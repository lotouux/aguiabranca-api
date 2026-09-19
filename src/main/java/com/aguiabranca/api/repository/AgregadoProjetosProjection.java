package com.aguiabranca.api.repository;

import java.math.BigDecimal;

/**
 * Raw sums/counts across all non-{@code CANCELADO} projects. ROI itself is computed in
 * {@code DashboardService} - {@link BigDecimal} division needs an explicit scale/rounding mode
 * that JPQL cannot express safely. "Elegivel" sums only include projects whose {@code
 * investimento} is present and non-zero; the plain totals include every project.
 */
public interface AgregadoProjetosProjection {

    long getTotalProjetos();

    long getProjetosConcluidos();

    long getProjetosEmAndamento();

    BigDecimal getInvestimentoTotal();

    BigDecimal getEconomiaEstimadaTotal();

    BigDecimal getEconomiaRealizadaTotal();

    long getHorasEconomizadasMes();

    long getProjetosSemInvestimento();

    BigDecimal getInvestimentoElegivel();

    BigDecimal getEconomiaEstimadaElegivel();

    BigDecimal getEconomiaRealizadaElegivel();
}
