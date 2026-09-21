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

    public TarefaDTO criar(Long projetoId, CriarTarefaDTO dto) {
        Projeto projeto = buscarProjeto(projetoId);
        garantirProjetoAberto(projeto);
        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(dto.titulo());
        tarefa.setStatus(StatusTarefa.PENDENTE);
        tarefa.setProjeto(projeto);
        return TarefaDTO.from(tarefaRepository.save(tarefa));
    }

    public TarefaDTO atualizar(Long projetoId, Long tarefaId, AtualizarTarefaRequestDTO dto) {
        Tarefa tarefa = carregarTarefaDo(projetoId, tarefaId);
        garantirProjetoAberto(tarefa.getProjeto());
        tarefa.setStatus(dto.status());
        tarefa.setDataConclusao(dto.status() == StatusTarefa.CONCLUIDA ? LocalDate.now(clock) : null);
        return TarefaDTO.from(tarefaRepository.save(tarefa));
    }

    public void excluir(Long projetoId, Long tarefaId) {
        Tarefa tarefa = carregarTarefaDo(projetoId, tarefaId);
        garantirProjetoAberto(tarefa.getProjeto());
        tarefaRepository.delete(tarefa);
    }

    private Projeto buscarProjeto(Long projetoId) {
        return projetoRepository.findById(projetoId)
                .orElseThrow(() -> new NotFoundException("Projeto não encontrado."));
    }

    private void garantirProjetoAberto(Projeto projeto) {
        if (projeto.getStatus() == StatusProjeto.CONCLUIDO || projeto.getStatus() == StatusProjeto.CANCELADO) {
            throw new ConflictException("Projeto encerrado não permite alterações em tarefas.");
        }
    }

    private Tarefa carregarTarefaDo(Long projetoId, Long tarefaId) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada."));
        if (!tarefa.getProjeto().getId().equals(projetoId)) {
            throw new NotFoundException("Tarefa não encontrada.");
        }
        return tarefa;
    }
}