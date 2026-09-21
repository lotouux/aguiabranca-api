package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarMarcoDTO(
        @NotBlank String titulo,
        String observacao
) {}
