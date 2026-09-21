package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.AtualizarIdeiaRequestDTO;
import com.aguiabranca.api.dto.CriarIdeiaDTO;
import com.aguiabranca.api.dto.IdeiaResponseDTO;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.Ideia;
import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.Nivel;
import com.aguiabranca.api.model.enums.StatusIdeia;
import com.aguiabranca.api.repository.IdeiaRepository;
import com.aguiabranca.api.repository.UsuarioRepository;
import com.aguiabranca.api.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
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
    public List<IdeiaResponseDTO> listar(UsuarioAutenticado principal) {
        return ideiaRepository.findAll().stream()
                .map(IdeiaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public IdeiaResponseDTO buscarPorId(String id, UsuarioAutenticado principal) {
        Ideia ideia = ideiaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ideia não encontrada: " + id));
        return IdeiaResponseDTO.from(ideia);
    }

    public List<IdeiaResponseDTO> listarPorMatricula(String matricula) {
        return ideiaRepository.findByAutorMatricula(matricula)
                .stream()
                .map(IdeiaResponseDTO::from)
                .toList();
    }

    public IdeiaResponseDTO criar(CriarIdeiaDTO dto, UsuarioAutenticado principal) {
        Usuario autor = usuarioRepository.findById(principal.id())
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));

        Ideia ideia = new Ideia();
        ideia.setTitulo(dto.titulo());
        ideia.setDescricao(dto.descricao());
        ideia.setArea(dto.area());
        ideia.setPrazo(dto.prazo());
        ideia.setEsforco(Nivel.valueOf(dto.esforco().toUpperCase()));
        ideia.setImpacto(Nivel.valueOf(dto.impacto().toUpperCase()));
        ideia.setInvestimento(dto.investimento());
        ideia.setRetorno(dto.retorno());
        ideia.setBaseKM(dto.baseKM());
        ideia.setAutor(autor);
        ideia.setStatus(StatusIdeia.ENVIADA);
        ideia.setStrategicBonus(false);

        Ideia salva = ideiaRepository.save(ideia);

        return IdeiaResponseDTO.from(salva);
    }

    public IdeiaResponseDTO atualizar(String id, AtualizarIdeiaRequestDTO dto) {
        Ideia ideia = ideiaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ideia não encontrada: " + id));

        if (dto.status() != null) {
            ideia.setStatus(dto.status());
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
        if (dto.feedbackGestor() != null) {
            ideia.setFeedbackGestor(dto.feedbackGestor());
        }

        return IdeiaResponseDTO.from(ideiaRepository.save(ideia));
    }
}