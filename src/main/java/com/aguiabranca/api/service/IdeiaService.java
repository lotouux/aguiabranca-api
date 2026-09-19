package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.AtualizarIdeiaRequestDTO;
import com.aguiabranca.api.dto.CriarIdeiaDTO;
import com.aguiabranca.api.dto.IdeiaResponseDTO;
import com.aguiabranca.api.exception.ConflictException;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.Ideia;
import com.aguiabranca.api.model.enums.StatusIdeia;
import com.aguiabranca.api.model.enums.TipoPerfil;
import com.aguiabranca.api.repository.IdeiaRepository;
import com.aguiabranca.api.repository.UsuarioRepository;
import com.aguiabranca.api.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IdeiaService {

    private final IdeiaRepository ideiaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<IdeiaResponseDTO> listar(UsuarioAutenticado solicitante) {
        List<Ideia> ideias = solicitante.perfil() == TipoPerfil.OPERADOR
                ? ideiaRepository.findByAutorId(solicitante.id())
                : ideiaRepository.findAll();
        return ideias.stream().map(IdeiaResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public IdeiaResponseDTO buscarPorId(String id, UsuarioAutenticado solicitante) {
        Ideia ideia = buscarOuFalhar(id);
        garantirAcesso(ideia, solicitante);
        return IdeiaResponseDTO.from(ideia);
    }

    public IdeiaResponseDTO criar(CriarIdeiaDTO dto, UsuarioAutenticado solicitante) {
        Ideia ideia = new Ideia();
        ideia.setAutor(usuarioRepository.getReferenceById(solicitante.id()));
        ideia.setTitulo(dto.titulo());
        ideia.setDescricao(dto.descricao());
        ideia.setArea(dto.area());
        ideia.setPrazo(dto.prazo());
        ideia.setInvestimento(dto.investimento());
        ideia.setRetorno(dto.retorno());
        ideia.setStatus(StatusIdeia.ENVIADA);
        ideia.setStrategicBonus(false);
        return IdeiaResponseDTO.from(ideiaRepository.save(ideia));
    }

    public IdeiaResponseDTO atualizar(String id, AtualizarIdeiaRequestDTO dto) {
        Ideia ideia = buscarOuFalhar(id);

        if (dto.status() != null) {
            aplicarTransicaoManual(ideia, dto.status());
        }
        if (dto.prioridade() != null) {
            ideia.setPrioridade(dto.prioridade());
        }
        if (dto.impacto() != null) {
            ideia.setImpacto(dto.impacto());
        }
        if (dto.esforco() != null) {
            ideia.setEsforco(dto.esforco());
        }
        if (dto.isStrategicBonus() != null) {
            ideia.setStrategicBonus(dto.isStrategicBonus());
        }
        return IdeiaResponseDTO.from(ideiaRepository.save(ideia));
    }

    /**
     * {@code EM_EXECUCAO}/{@code CONCLUIDA} are membership-triggered only ({@link
     * com.aguiabranca.api.service.ProjetoService}) - a manual PATCH may only move an idea out of
     * {@code ENVIADA}, which {@link StatusIdeia#permiteTransicaoPara} already limits to {@code
     * APROVADA}/{@code REJEITADA}.
     */
    private void aplicarTransicaoManual(Ideia ideia, StatusIdeia destino) {
        if (ideia.getStatus() != StatusIdeia.ENVIADA || !ideia.getStatus().permiteTransicaoPara(destino)) {
            throw new ConflictException("Transição de status inválida para esta ideia.");
        }
        ideia.setStatus(destino);
    }

    private void garantirAcesso(Ideia ideia, UsuarioAutenticado solicitante) {
        if (solicitante.perfil() == TipoPerfil.OPERADOR && !ideia.getAutor().getId().equals(solicitante.id())) {
            throw new AccessDeniedException("Você não tem acesso a esta ideia.");
        }
    }

    private Ideia buscarOuFalhar(String id) {
        return ideiaRepository.findById(id).orElseThrow(() -> new NotFoundException("Ideia não encontrada."));
    }
}
