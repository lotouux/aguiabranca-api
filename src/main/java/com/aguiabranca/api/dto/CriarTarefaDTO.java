package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarTarefaDTO(
        @NotBlank String titulo
) {}
