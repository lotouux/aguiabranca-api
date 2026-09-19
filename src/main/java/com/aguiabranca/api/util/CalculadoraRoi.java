package com.aguiabranca.api.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * The single implementation of {@code (economia - investimento) / investimento}, shared by
 * {@code Ideia.getRoiEsperado()} and {@code DashboardService} so the two can never disagree -
 * exactly the bug the seed data used to have (idea 2 stored 2.8 vs. the correct 1.8).
 */
public final class CalculadoraRoi {

    private static final int ESCALA = 4;

    private CalculadoraRoi() {}

    /**
     * {@code null} when {@code investimento} is null or zero, never a divide-by-zero.
     */
    public static BigDecimal calcular(BigDecimal economia, BigDecimal investimento) {
        if (investimento == null || investimento.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }
        return economia.subtract(investimento).divide(investimento, ESCALA, RoundingMode.HALF_UP);
    }
}
