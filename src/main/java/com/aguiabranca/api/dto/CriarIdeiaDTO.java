package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CriarIdeiaDTO(
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotBlank String area,
        LocalDate prazo,
        BigDecimal investimento,
        BigDecimal retorno,
        Integer baseKM
) {}
