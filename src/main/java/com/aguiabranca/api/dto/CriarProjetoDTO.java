package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CriarProjetoDTO(
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotBlank String area,
        String observacao,
        Long responsavelId,
        LocalDate dataInicio,
        LocalDate dataPrevistaConclusao,
        @PositiveOrZero BigDecimal investimento,
        @PositiveOrZero BigDecimal economiaAnualEstimada,
        @PositiveOrZero BigDecimal economiaAnualRealizada,
        @PositiveOrZero Integer horasEconomizadasMes
) {}
