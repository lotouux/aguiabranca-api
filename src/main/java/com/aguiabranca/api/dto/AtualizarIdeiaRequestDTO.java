package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.enums.Nivel;
import com.aguiabranca.api.model.enums.Prioridade;
import com.aguiabranca.api.model.enums.StatusIdeia;

public record AtualizarIdeiaRequestDTO(
        StatusIdeia status,
        Prioridade prioridade,
        Nivel impacto,
        Nivel esforco,
        Boolean isStrategicBonus
) {}
