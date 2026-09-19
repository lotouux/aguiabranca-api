package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank String matricula,
        @NotBlank String senha
) {}
