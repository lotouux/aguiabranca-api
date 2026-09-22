package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.enums.Nivel;
import com.aguiabranca.api.model.enums.Prioridade;
import com.aguiabranca.api.model.enums.StatusIdeia;

public record AtualizarIdeiaRequestDTO(
        StatusIdeia status,
        Prioridade prioridade,
        String responsavel,
        Nivel impacto,
        Nivel esforco,
        Boolean isStrategicBonus,
        String feedbackGestor,
        String prazo,
        Float investimento,
        Float retorno,
        Float roiEsperado
) {}