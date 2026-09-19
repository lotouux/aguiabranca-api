package com.aguiabranca.api;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Every figure here is a literal constant taken from the seed, never the ROI/progress formula
 * re-evaluated in the test - otherwise this would pass against an implementation that shares the
 * same bug as the one it is meant to catch.
 */
class DashboardTest extends AbstractApiIntegrationTest {

    @Test
    void numerosDoDashboardBatemComOSeed() throws Exception {
        mockMvc.perform(get("/api/dashboard").header("Authorization", bearer("LD001")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalProjetos").value(3))
                .andExpect(jsonPath("$.projetosConcluidos").value(1))
                .andExpect(jsonPath("$.projetosEmAndamento").value(2))
                .andExpect(jsonPath("$.investimentoTotal").value(300000.00))
                .andExpect(jsonPath("$.economiaEstimadaTotal").value(950000.00))
                .andExpect(jsonPath("$.economiaRealizadaTotal").value(350000.00))
                .andExpect(jsonPath("$.roiEstimado").value(2.0000))
                .andExpect(jsonPath("$.roiRealizado").value(0.1667))
                .andExpect(jsonPath("$.horasEconomizadasMes").value(200))
                .andExpect(jsonPath("$.projetosSemInvestimento").value(1));
    }

    @Test
    void gestorNaoAcessaODashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard").header("Authorization", bearer("GS001")))
                .andExpect(status().isForbidden());
    }
}
