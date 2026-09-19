package com.aguiabranca.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class IdeiaFluxoTest extends AbstractApiIntegrationTest {

    @Test
    void ideiaSubmetidaComecaComoEnviadaSemPrioridadeOuImpacto() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/ideias")
                        .header("Authorization", bearer("OP001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Nova ideia\",\"descricao\":\"desc\",\"area\":\"Logística\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("ENVIADA"))
                .andExpect(jsonPath("$.prioridade").doesNotExist())
                .andExpect(jsonPath("$.impacto").doesNotExist())
                .andReturn();

        assertThat(objectMapper.readTree(resultado.getResponse().getContentAsString()).get("investimento").isNull())
                .isTrue();
    }

    @Test
    void gestorAprovaIdeiaEnviada() throws Exception {
        String id = criarIdeia();

        mockMvc.perform(patch("/api/ideias/" + id)
                        .header("Authorization", bearer("GS001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APROVADA\",\"prioridade\":\"ALTA\",\"impacto\":\"ALTO\",\"esforco\":\"MEDIO\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"))
                .andExpect(jsonPath("$.prioridade").value("ALTA"));
    }

    @Test
    void gestorRejeitaIdeiaEnviadaETornaTransicaoTerminal() throws Exception {
        String id = criarIdeia();

        mockMvc.perform(patch("/api/ideias/" + id)
                        .header("Authorization", bearer("GS001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"REJEITADA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJEITADA"));

        mockMvc.perform(patch("/api/ideias/" + id)
                        .header("Authorization", bearer("GS001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APROVADA\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void corpoDaAtualizacaoNaoAceitaCamposDeInvestimento() throws Exception {
        String id = criarIdeia();

        MvcResult resultado = mockMvc.perform(patch("/api/ideias/" + id)
                        .header("Authorization", bearer("GS001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"APROVADA\",\"investimento\":999999.99}"))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode ideia = objectMapper.readTree(resultado.getResponse().getContentAsString());
        assertThat(ideia.get("investimento").isNull()).isTrue();
    }

    private String criarIdeia() throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/ideias")
                        .header("Authorization", bearer("OP001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Ideia de teste\",\"descricao\":\"desc\",\"area\":\"Logística\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asText();
    }
}
