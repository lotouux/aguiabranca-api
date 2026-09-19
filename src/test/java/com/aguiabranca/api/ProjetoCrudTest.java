package com.aguiabranca.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjetoCrudTest extends AbstractApiIntegrationTest {

    @Test
    void criarProjetoComecaComoPlanejado() throws Exception {
        mockMvc.perform(post("/api/projetos")
                        .header("Authorization", bearer("GS001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Novo Projeto\",\"descricao\":\"desc\",\"area\":\"Logística\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PLANEJADO"))
                .andExpect(jsonPath("$.progresso").value(0));
    }

    @Test
    void atualizarApenasEconomiaRealizadaNaoAlteraOutrosCampos() throws Exception {
        String bearerGestor = bearer("GS001");
        String id = projetoIdPorTitulo("Check-in Digital", bearerGestor);

        mockMvc.perform(patch("/api/projetos/" + id)
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"economiaAnualRealizada\":123456.78}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.economiaAnualRealizada").value(123456.78))
                .andExpect(jsonPath("$.titulo").value("Check-in Digital"))
                .andExpect(jsonPath("$.status").value("EM_ANDAMENTO"))
                .andExpect(jsonPath("$.investimento").value(200000.00))
                .andExpect(jsonPath("$.economiaAnualEstimada").value(500000.00));
    }

    @Test
    void excluirProjetoConcluidoRetorna409() throws Exception {
        String bearerGestor = bearer("GS001");
        String id = projetoIdPorTitulo("Plataforma de Roteirização", bearerGestor);

        mockMvc.perform(delete("/api/projetos/" + id).header("Authorization", bearerGestor))
                .andExpect(status().isConflict());
    }

    @Test
    void excluirProjetoDevolveIdeiasParaAprovada() throws Exception {
        String bearerGestor = bearer("GS001");
        String idIdeiaAprovada = ideiaIdPorTitulo("Sistema de Feedback Automatizado", bearerGestor);

        MvcResult criado = mockMvc.perform(post("/api/projetos")
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Projeto Temporário\",\"descricao\":\"desc\",\"area\":\"Passageiros\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String idProjeto = objectMapper.readTree(criado.getResponse().getContentAsString()).get("id").asText();

        mockMvc.perform(post("/api/projetos/" + idProjeto + "/ideias")
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ideiaIds\":[\"" + idIdeiaAprovada + "\"]}"))
                .andExpect(status().isOk());

        assertStatusDaIdeia(idIdeiaAprovada, bearerGestor, "EM_EXECUCAO");

        mockMvc.perform(delete("/api/projetos/" + idProjeto).header("Authorization", bearerGestor))
                .andExpect(status().isNoContent());

        assertStatusDaIdeia(idIdeiaAprovada, bearerGestor, "APROVADA");
    }

    private void assertStatusDaIdeia(String idIdeia, String bearerToken, String statusEsperado) throws Exception {
        MvcResult resultado = mockMvc.perform(get("/api/ideias/" + idIdeia).header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode ideia = objectMapper.readTree(resultado.getResponse().getContentAsString());
        assertThat(ideia.get("status").asText()).isEqualTo(statusEsperado);
    }
}
