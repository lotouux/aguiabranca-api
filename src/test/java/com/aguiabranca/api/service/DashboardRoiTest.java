package com.aguiabranca.api.service;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class DashboardRoiTest {

    @Test
    void roiEDerivadoComoEconomiaMenosInvestimentoSobreInvestimento() {
        BigDecimal roi = DashboardService.calcularRoi(new BigDecimal("900000.00"), new BigDecimal("300000.00"));

        assertThat(roi).isEqualTo(new BigDecimal("2.0000"));
    }

    @Test
    void roiArredondaParaCimaNaQuartaCasaDecimal() {
        BigDecimal roi = DashboardService.calcularRoi(new BigDecimal("350000.00"), new BigDecimal("300000.00"));

        assertThat(roi).isEqualTo(new BigDecimal("0.1667"));
    }

    @Test
    void investimentoElegivelNuloRetornaRoiNulo() {
        assertThat(DashboardService.calcularRoi(new BigDecimal("100000.00"), null)).isNull();
    }

    @Test
    void investimentoElegivelZeroRetornaRoiNuloEmVezDeDividirPorZero() {
        assertThat(DashboardService.calcularRoi(new BigDecimal("100000.00"), BigDecimal.ZERO)).isNull();
    }
}
