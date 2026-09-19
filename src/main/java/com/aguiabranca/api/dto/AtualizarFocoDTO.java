package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AtualizarFocoDTO(
        @NotBlank String mes,
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotEmpty List<String> areasPotenciais
) {}
