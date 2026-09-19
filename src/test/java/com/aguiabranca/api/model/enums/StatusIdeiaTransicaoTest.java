package com.aguiabranca.api.model.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StatusIdeiaTransicaoTest {

    @Test
    void enviadaPodeSerAprovadaOuRejeitada() {
        assertThat(StatusIdeia.ENVIADA.permiteTransicaoPara(StatusIdeia.APROVADA)).isTrue();
        assertThat(StatusIdeia.ENVIADA.permiteTransicaoPara(StatusIdeia.REJEITADA)).isTrue();
    }

    @Test
    void enviadaNaoPodePularDiretoParaExecucaoOuConcluida() {
        assertThat(StatusIdeia.ENVIADA.permiteTransicaoPara(StatusIdeia.EM_EXECUCAO)).isFalse();
        assertThat(StatusIdeia.ENVIADA.permiteTransicaoPara(StatusIdeia.CONCLUIDA)).isFalse();
    }

    @Test
    void rejeitadaETerminal() {
        for (StatusIdeia destino : StatusIdeia.values()) {
            assertThat(StatusIdeia.REJEITADA.permiteTransicaoPara(destino)).isFalse();
        }
    }

    @Test
    void concluidaETerminal() {
        for (StatusIdeia destino : StatusIdeia.values()) {
            assertThat(StatusIdeia.CONCLUIDA.permiteTransicaoPara(destino)).isFalse();
        }
    }

    @Test
    void aprovadaSoTransicionaParaExecucaoPorVinculoDeProjeto() {
        assertThat(StatusIdeia.APROVADA.permiteTransicaoPara(StatusIdeia.EM_EXECUCAO)).isTrue();
        assertThat(StatusIdeia.APROVADA.permiteTransicaoPara(StatusIdeia.REJEITADA)).isFalse();
        assertThat(StatusIdeia.APROVADA.permiteTransicaoPara(StatusIdeia.CONCLUIDA)).isFalse();
    }

    @Test
    void emExecucaoVoltaParaAprovadaAoDesvincularOuVaiParaConcluidaAoEncerrarProjeto() {
        assertThat(StatusIdeia.EM_EXECUCAO.permiteTransicaoPara(StatusIdeia.APROVADA)).isTrue();
        assertThat(StatusIdeia.EM_EXECUCAO.permiteTransicaoPara(StatusIdeia.CONCLUIDA)).isTrue();
        assertThat(StatusIdeia.EM_EXECUCAO.permiteTransicaoPara(StatusIdeia.ENVIADA)).isFalse();
        assertThat(StatusIdeia.EM_EXECUCAO.permiteTransicaoPara(StatusIdeia.REJEITADA)).isFalse();
    }
}
