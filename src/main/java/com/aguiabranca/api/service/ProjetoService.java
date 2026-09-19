package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.AtualizarProjetoRequestDTO;
import com.aguiabranca.api.dto.CriarProjetoDTO;
import com.aguiabranca.api.dto.ProjetoResponseDTO;
import com.aguiabranca.api.exception.ConflictException;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.Ideia;
import com.aguiabranca.api.model.Projeto;
import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.StatusIdeia;
import com.aguiabranca.api.model.enums.StatusProjeto;
import com.aguiabranca.api.repository.IdeiaRepository;
import com.aguiabranca.api.repository.ProjetoRepository;
import com.aguiabranca.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjetoService {

    private final ProjetoRepository projetoRepository;
    private final IdeiaRepository ideiaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<ProjetoResponseDTO> listar(StatusProjeto status) {
        List<Projeto> projetos = status != null ? projetoRepository.findByStatus(status) : projetoRepository.findAll();
        return projetos.stream().map(ProjetoResponseDTO::from).toList();
    }

    @Transactional(readOnly = true)
    public ProjetoResponseDTO buscarPorId(String id) {
        return ProjetoResponseDTO.from(buscarOuFalhar(id));
    }

    public ProjetoResponseDTO criar(CriarProjetoDTO dto) {
        Projeto projeto = new Projeto();
        projeto.setTitulo(dto.titulo());
        projeto.setDescricao(dto.descricao());
        projeto.setArea(dto.area());
        projeto.setObservacao(dto.observacao());
        projeto.setResponsavel(referenciaOuNulo(dto.responsavelId()));
        projeto.setStatus(StatusProjeto.PLANEJADO);
        projeto.setDataInicio(dto.dataInicio());
        projeto.setDataPrevistaConclusao(dto.dataPrevistaConclusao());
        projeto.setInvestimento(dto.investimento());
        projeto.setEconomiaAnualEstimada(dto.economiaAnualEstimada());
        projeto.setEconomiaAnualRealizada(dto.economiaAnualRealizada());
        projeto.setHorasEconomizadasMes(dto.horasEconomizadasMes());
        return ProjetoResponseDTO.from(projetoRepository.save(projeto));
    }

    public ProjetoResponseDTO atualizar(String id, AtualizarProjetoRequestDTO dto) {
        Projeto projeto = buscarOuFalhar(id);

        if (dto.titulo() != null) {
            projeto.setTitulo(dto.titulo());
        }
        if (dto.descricao() != null) {
            projeto.setDescricao(dto.descricao());
        }
        if (dto.area() != null) {
            projeto.setArea(dto.area());
        }
        if (dto.observacao() != null) {
            projeto.setObservacao(dto.observacao());
        }
        if (dto.responsavelId() != null) {
            projeto.setResponsavel(referenciaOuNulo(dto.responsavelId()));
        }
        if (dto.dataInicio() != null) {
            projeto.setDataInicio(dto.dataInicio());
        }
        if (dto.dataPrevistaConclusao() != null) {
            projeto.setDataPrevistaConclusao(dto.dataPrevistaConclusao());
        }
        if (dto.investimento() != null) {
            projeto.setInvestimento(dto.investimento());
        }
        if (dto.economiaAnualEstimada() != null) {
            projeto.setEconomiaAnualEstimada(dto.economiaAnualEstimada());
        }
        if (dto.economiaAnualRealizada() != null) {
            projeto.setEconomiaAnualRealizada(dto.economiaAnualRealizada());
        }
        if (dto.horasEconomizadasMes() != null) {
            projeto.setHorasEconomizadasMes(dto.horasEconomizadasMes());
        }
        if (dto.status() != null) {
            aplicarTransicaoStatus(projeto, dto.status());
        }
        return ProjetoResponseDTO.from(projetoRepository.save(projeto));
    }

    /**
     * Deletion is restricted to {@code PLANEJADO}: a {@code CONCLUIDO} project is the source of
     * truth for every historical Dashboard figure, so deleting it would silently rewrite them.
     * The close-out path for an unwanted project is {@code CANCELADO} instead.
     */
    public void excluir(String id) {
        Projeto projeto = buscarOuFalhar(id);
        if (projeto.getStatus() != StatusProjeto.PLANEJADO) {
            throw new ConflictException("Somente projetos em planejamento podem ser excluídos.");
        }
        devolverIdeiasParaAprovada(projeto);
        projetoRepository.deleteById(id);
    }

    /**
     * Atomic batch attach: every id is validated before any mutation happens, so one invalid
     * entry attaches none of them. Re-attaching an idea already on this project is a no-op
     * (idempotent). An idea already attached elsewhere is never {@code APROVADA} any more (it is
     * {@code EM_EXECUCAO}), so the "not APROVADA" check below also rejects that case.
     */
    public ProjetoResponseDTO vincularIdeias(String projetoId, List<String> ideiaIds) {
        Projeto projeto = buscarOuFalhar(projetoId);
        garantirProjetoNaoEncerrado(projeto);

        List<Ideia> ideias = new ArrayList<>();
        for (String ideiaId : ideiaIds) {
            Ideia ideia = ideiaRepository.findById(ideiaId)
                    .orElseThrow(() -> new NotFoundException("Ideia não encontrada: " + ideiaId));
            if (!projeto.getIdeias().contains(ideia) && ideia.getStatus() != StatusIdeia.APROVADA) {
                throw new ConflictException("Ideia " + ideiaId + " não está aprovada.");
            }
            ideias.add(ideia);
        }

        for (Ideia ideia : ideias) {
            if (projeto.getIdeias().add(ideia)) {
                ideia.setStatus(StatusIdeia.EM_EXECUCAO);
            }
        }
        return ProjetoResponseDTO.from(projetoRepository.save(projeto));
    }

    public void desvincularIdeia(String projetoId, String ideiaId) {
        Projeto projeto = buscarOuFalhar(projetoId);
        garantirProjetoNaoEncerrado(projeto);
        Ideia ideia = ideiaRepository.findById(ideiaId)
                .orElseThrow(() -> new NotFoundException("Ideia não encontrada."));

        if (!projeto.getIdeias().remove(ideia)) {
            throw new NotFoundException("Ideia não está vinculada a este projeto.");
        }
        if (ideia.getStatus() == StatusIdeia.EM_EXECUCAO) {
            ideia.setStatus(StatusIdeia.APROVADA);
        }
        projetoRepository.save(projeto);
    }

    /**
     * {@code CONCLUIDO}/{@code CANCELADO} are terminal - once reached, no further status change
     * is permitted. Without this, a {@code CONCLUIDO} project could be patched back to {@code
     * PLANEJADO} and then deleted, silently rewriting historical Dashboard figures despite the
     * {@code excluir()} guard below.
     */
    private void aplicarTransicaoStatus(Projeto projeto, StatusProjeto destino) {
        garantirProjetoNaoEncerrado(projeto);
        if (destino == StatusProjeto.CONCLUIDO) {
            concluirIdeias(projeto);
        }
        projeto.setStatus(destino);
    }

    private void garantirProjetoNaoEncerrado(Projeto projeto) {
        if (projeto.getStatus() == StatusProjeto.CONCLUIDO || projeto.getStatus() == StatusProjeto.CANCELADO) {
            throw new ConflictException("Projeto encerrado não pode ser alterado.");
        }
    }

    private void concluirIdeias(Projeto projeto) {
        for (Ideia ideia : projeto.getIdeias()) {
            if (ideia.getStatus() == StatusIdeia.EM_EXECUCAO) {
                ideia.setStatus(StatusIdeia.CONCLUIDA);
            }
        }
    }

    private void devolverIdeiasParaAprovada(Projeto projeto) {
        for (Ideia ideia : projeto.getIdeias()) {
            if (ideia.getStatus() == StatusIdeia.EM_EXECUCAO) {
                ideia.setStatus(StatusIdeia.APROVADA);
            }
        }
    }

    /**
     * {@code getReferenceById} returns an uninitialized proxy with no query - existence must be
     * checked explicitly here, otherwise an invalid id only fails later when something (e.g.
     * {@code ProjetoResponseDTO.from}) dereferences the proxy, surfacing as an uncaught {@code
     * EntityNotFoundException} (500) instead of a clean 404.
     */
    private Usuario referenciaOuNulo(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new NotFoundException("Usuário responsável não encontrado: " + usuarioId);
        }
        return usuarioRepository.getReferenceById(usuarioId);
    }

    private Projeto buscarOuFalhar(String id) {
        return projetoRepository.findById(id).orElseThrow(() -> new NotFoundException("Projeto não encontrado."));
    }
}
