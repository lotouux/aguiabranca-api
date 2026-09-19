package com.aguiabranca.api.repository;

import com.aguiabranca.api.config.ClockConfig;
import com.aguiabranca.api.model.Projeto;
import com.aguiabranca.api.model.enums.StatusProjeto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Seeder-free (the {@code @DataJpaTest} slice never runs {@code DataSeeder}'s
 * {@code CommandLineRunner}), so this can build fixtures the seed cannot: an empty database, a
 * project whose investimento is exactly zero, and a {@code CANCELADO} project.
 *
 * <p>{@code @DataJpaTest} excludes plain {@code @Configuration} beans by default, so {@link
 * ClockConfig} - which provides the {@code auditingDateTimeProvider} that {@code
 * ApiApplication}'s {@code @EnableJpaAuditing} requires - must be imported back explicitly.
 */
@DataJpaTest
@Import(ClockConfig.class)
class DashboardAgregacaoTest {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Test
    void bancoVazioRetornaZerosENuncaNull() {
        AgregadoProjetosProjection agregado = projetoRepository.buscarAgregado();

        assertThat(agregado.getTotalProjetos()).isZero();
        assertThat(agregado.getProjetosConcluidos()).isZero();
        assertThat(agregado.getProjetosEmAndamento()).isZero();
        assertThat(agregado.getInvestimentoTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(agregado.getEconomiaEstimadaTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(agregado.getEconomiaRealizadaTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(agregado.getHorasEconomizadasMes()).isZero();
        assertThat(agregado.getProjetosSemInvestimento()).isZero();
        assertThat(agregado.getInvestimentoElegivel()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void projetoComInvestimentoZeroContaComoSemInvestimentoMasEntraNosTotais() {
        salvar(StatusProjeto.EM_ANDAMENTO, new BigDecimal("0.00"), new BigDecimal("10000.00"), null, 0);

        AgregadoProjetosProjection agregado = projetoRepository.buscarAgregado();

        assertThat(agregado.getTotalProjetos()).isEqualTo(1);
        assertThat(agregado.getProjetosSemInvestimento()).isEqualTo(1);
        assertThat(agregado.getEconomiaEstimadaTotal()).isEqualByComparingTo(new BigDecimal("10000.00"));
        assertThat(agregado.getInvestimentoElegivel()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(agregado.getEconomiaEstimadaElegivel()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void projetoCanceladoEExcluidoInteiramenteDoAgregado() {
        salvar(StatusProjeto.CANCELADO, new BigDecimal("999999.00"), new BigDecimal("999999.00"), null, 999);

        AgregadoProjetosProjection agregado = projetoRepository.buscarAgregado();

        assertThat(agregado.getTotalProjetos()).isZero();
        assertThat(agregado.getInvestimentoTotal()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void economiaRealizadaNulaComEconomiaSomaComoZero() {
        salvar(StatusProjeto.EM_ANDAMENTO, new BigDecimal("50000.00"), new BigDecimal("80000.00"), null, 10);

        AgregadoProjetosProjection agregado = projetoRepository.buscarAgregado();

        assertThat(agregado.getEconomiaRealizadaTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(agregado.getEconomiaRealizadaElegivel()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    private void salvar(StatusProjeto status, BigDecimal investimento, BigDecimal estimada, BigDecimal realizada,
            Integer horas) {
        Projeto projeto = new Projeto();
        projeto.setTitulo("Projeto de teste");
        projeto.setDescricao("desc");
        projeto.setArea("Logística");
        projeto.setStatus(status);
        projeto.setInvestimento(investimento);
        projeto.setEconomiaAnualEstimada(estimada);
        projeto.setEconomiaAnualRealizada(realizada);
        projeto.setHorasEconomizadasMes(horas);
        projetoRepository.save(projeto);
    }
}
