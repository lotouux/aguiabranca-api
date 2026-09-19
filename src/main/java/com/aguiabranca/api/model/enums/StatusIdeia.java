package com.aguiabranca.api.model.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

/**
 * The membership-triggered edges ({@code APROVADA -> EM_EXECUCAO}, {@code EM_EXECUCAO ->
 * APROVADA} on detach, {@code EM_EXECUCAO -> CONCLUIDA} on project conclusion) are only ever
 * driven by {@code ProjetoService}, never by a direct status PATCH - otherwise an idea could sit
 * in {@code EM_EXECUCAO} with no project. {@code REJEITADA} is terminal.
 */
public enum StatusIdeia {
    ENVIADA,
    APROVADA,
    REJEITADA,
    EM_EXECUCAO,
    CONCLUIDA;

    private static final Map<StatusIdeia, Set<StatusIdeia>> TRANSICOES_PERMITIDAS = new EnumMap<>(StatusIdeia.class);

    static {
        TRANSICOES_PERMITIDAS.put(ENVIADA, EnumSet.of(APROVADA, REJEITADA));
        TRANSICOES_PERMITIDAS.put(APROVADA, EnumSet.of(EM_EXECUCAO));
        TRANSICOES_PERMITIDAS.put(REJEITADA, EnumSet.noneOf(StatusIdeia.class));
        TRANSICOES_PERMITIDAS.put(EM_EXECUCAO, EnumSet.of(APROVADA, CONCLUIDA));
        TRANSICOES_PERMITIDAS.put(CONCLUIDA, EnumSet.noneOf(StatusIdeia.class));
    }

    public boolean permiteTransicaoPara(StatusIdeia destino) {
        return TRANSICOES_PERMITIDAS.get(this).contains(destino);
    }
}
