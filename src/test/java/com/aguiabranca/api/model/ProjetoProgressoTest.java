package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.StatusTarefa;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ProjetoProgressoTest {

    @Test
    void semTarefasProgressoEZero() {
        Projeto projeto = new Projeto();
        projeto.setTarefas(List.of());

        assertThat(projeto.getProgresso()).isZero();
    }

    @Test
    void umaDeQuatroConcluidaEVinteECincoPorCento() {
        Projeto projeto = comTarefas(StatusTarefa.CONCLUIDA, StatusTarefa.PENDENTE, StatusTarefa.PENDENTE,
                StatusTarefa.PENDENTE);

        assertThat(projeto.getProgresso()).isEqualTo(25);
    }

    @Test
    void quatroDeQuatroConcluidasECemPorCento() {
        Projeto projeto = comTarefas(StatusTarefa.CONCLUIDA, StatusTarefa.CONCLUIDA, StatusTarefa.CONCLUIDA,
                StatusTarefa.CONCLUIDA);

        assertThat(projeto.getProgresso()).isEqualTo(100);
    }

    @Test
    void umaDeTresConcluidaArredondaParaTrintaETres() {
        Projeto projeto = comTarefas(StatusTarefa.CONCLUIDA, StatusTarefa.PENDENTE, StatusTarefa.PENDENTE);

        assertThat(projeto.getProgresso()).isEqualTo(33);
    }

    @Test
    void duasDeTresConcluidasArredondaParaSessentaESete() {
        Projeto projeto = comTarefas(StatusTarefa.CONCLUIDA, StatusTarefa.CONCLUIDA, StatusTarefa.PENDENTE);

        assertThat(projeto.getProgresso()).isEqualTo(67);
    }

    private Projeto comTarefas(StatusTarefa... statuses) {
        Projeto projeto = new Projeto();
        List<Tarefa> tarefas = List.of(statuses).stream().map(status -> {
            Tarefa tarefa = new Tarefa();
            tarefa.setStatus(status);
            return tarefa;
        }).toList();
        projeto.setTarefas(tarefas);
        return projeto;
    }
}
