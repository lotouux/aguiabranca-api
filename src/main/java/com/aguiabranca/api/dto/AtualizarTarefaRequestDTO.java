package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.enums.StatusTarefa;
import jakarta.validation.constraints.NotNull;

public record AtualizarTarefaRequestDTO(
        @NotNull StatusTarefa status
) {}
