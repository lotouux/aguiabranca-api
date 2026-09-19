package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.AtualizarTarefaRequestDTO;
import com.aguiabranca.api.dto.CriarTarefaDTO;
import com.aguiabranca.api.dto.TarefaDTO;
import com.aguiabranca.api.exception.ConflictException;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.Projeto;
import com.aguiabranca.api.model.Tarefa;
import com.aguiabranca.api.model.enums.StatusProjeto;
import com.aguiabranca.api.model.enums.StatusTarefa;
import com.aguiabranca.api.repository.ProjetoRepository;
import com.aguiabranca.api.repository.TarefaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class TarefaService {

    private final ProjetoRepository projetoRepository;
    private final TarefaRepository tarefaRepository;
    private final Clock clock;

    public TarefaDTO criar(String projetoId, CriarTarefaDTO dto) {
        Projeto projeto = buscarProjeto(projetoId);
        garantirProjetoAberto(projeto);
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setProjeto(projeto);
        return TarefaDTO.from(tarefaRepository.save(tarefa));
    }

    public TarefaDTO atualizar(String projetoId, Integer tarefaId, AtualizarTarefaRequestDTO dto) {
        Tarefa tarefa = carregarTarefaDo(projetoId, tarefaId);
        garantirProjetoAberto(tarefa.getProjeto());
        tarefa.setStatus(dto.status());
        tarefa.setDataConclusao(dto.status() == StatusTarefa.CONCLUIDA ? LocalDate.now(clock) : null);
        return TarefaDTO.from(tarefaRepository.save(tarefa));
    }

    public void excluir(String projetoId, Integer tarefaId) {
        Tarefa tarefa = carregarTarefaDo(projetoId, tarefaId);
        garantirProjetoAberto(tarefa.getProjeto());
        tarefaRepository.delete(tarefa);
    }

    private Projeto buscarProjeto(String projetoId) {
        return projetoRepository.findById(projetoId)
                .orElseThrow(() -> new NotFoundException("Projeto não encontrado."));
    }

    /**
     * Mirrors {@code ProjetoService}'s terminal-status guard: a {@code CONCLUIDO}/{@code
     * CANCELADO} project is a frozen historical record, so its tasks - and therefore its derived
     * {@code progresso} - must not change either.
     */
    private void garantirProjetoAberto(Projeto projeto) {
        if (projeto.getStatus() == StatusProjeto.CONCLUIDO || projeto.getStatus() == StatusProjeto.CANCELADO) {
            throw new ConflictException("Projeto encerrado não permite alterações em tarefas.");
        }
    }

    /**
     * Scoped lookup - without checking that the task actually belongs to {@code projetoId}, a
     * {@code PATCH /api/projetos/{A}/tarefas/{taskOfB}} would succeed and edit another project's
     * task.
     */
    private Tarefa carregarTarefaDo(String projetoId, Integer tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));
        if (!tarefa.getProjeto().getId().equals(projetoId)) {
            throw new NotFoundException("Tarefa não encontrada.");
        }
        return tarefa;
    }
}
