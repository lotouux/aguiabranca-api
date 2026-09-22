package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AtualizarFocoDTO(
        @NotBlank String mes,
        @NotBlank String titulo,
        @NotBlank String descricao,
        List<String> areasPotenciais,
        @NotNull Boolean ativo
) {}