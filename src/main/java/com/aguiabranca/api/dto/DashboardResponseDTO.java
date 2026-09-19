package com.aguiabranca.api.dto;

import java.math.BigDecimal;

public record DashboardResponseDTO(
        long totalProjetos, long projetosConcluidos, long projetosEmAndamento,
        BigDecimal investimentoTotal,
        BigDecimal economiaEstimadaTotal, BigDecimal economiaRealizadaTotal,
        BigDecimal roiEstimado, BigDecimal roiRealizado,
        long horasEconomizadasMes,
        long projetosSemInvestimento) {}
