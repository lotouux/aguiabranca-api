package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.enums.StatusProjeto;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AtualizarProjetoRequestDTO(
        String titulo,
        String descricao,
        String area,
        String observacao,
        Long responsavelId,
        StatusProjeto status,
        LocalDate dataInicio,
        LocalDate dataPrevistaConclusao,
        @PositiveOrZero BigDecimal investimento,
        @PositiveOrZero BigDecimal economiaAnualEstimada,
        @PositiveOrZero BigDecimal economiaAnualRealizada,
        @PositiveOrZero Integer horasEconomizadasMes
) {}
