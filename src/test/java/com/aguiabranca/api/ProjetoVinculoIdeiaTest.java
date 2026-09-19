package com.aguiabranca.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ProjetoVinculoIdeiaTest extends AbstractApiIntegrationTest {

    @Test
    void loteComUmaIdeiaInvalidaNaoVinculaNenhuma() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Check-in Digital", bearerGestor);
        String idIdeiaAprovada = ideiaIdPorTitulo("Sistema de Feedback Automatizado", bearerGestor);

        mockMvc.perform(post("/api/projetos/" + idProjeto + "/ideias")
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ideiaIds\":[\"" + idIdeiaAprovada + "\",\"nao-existe\"]}"))
                .andExpect(status().isNotFound());

        MvcResult resultado = mockMvc.perform(get("/api/projetos/" + idProjeto).header("Authorization", bearerGestor))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode projeto = objectMapper.readTree(resultado.getResponse().getContentAsString());
        assertThat(projeto.get("ideiaIds")).hasSize(1);
    }

    @Test
    void vincularIdeiaNaoAprovadaRetorna409() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Check-in Digital", bearerGestor);
        String idIdeiaEnviada = ideiaIdPorTitulo("Monitoramento de Pneus IoT", bearerGestor);

        mockMvc.perform(post("/api/projetos/" + idProjeto + "/ideias")
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ideiaIds\":[\"" + idIdeiaEnviada + "\"]}"))
                .andExpect(status().isConflict());
    }

    @Test
    void reanexarIdeiaJaNoProjetoEIdempotente() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Check-in Digital", bearerGestor);
        String idIdeiaJaAnexada = ideiaIdPorTitulo("App de Check-in Rápido", bearerGestor);

        mockMvc.perform(post("/api/projetos/" + idProjeto + "/ideias")
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"ideiaIds\":[\"" + idIdeiaJaAnexada + "\"]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ideiaIds", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void desvincularIdeiaDevolveParaAprovadaERemoveDoProjeto() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Check-in Digital", bearerGestor);
        String idIdeia = ideiaIdPorTitulo("App de Check-in Rápido", bearerGestor);

        mockMvc.perform(delete("/api/projetos/" + idProjeto + "/ideias/" + idIdeia)
                        .header("Authorization", bearerGestor))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/ideias/" + idIdeia).header("Authorization", bearerGestor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADA"));

        mockMvc.perform(get("/api/projetos/" + idProjeto).header("Authorization", bearerGestor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ideiaIds", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    void desvincularIdeiaNaoVinculadaRetorna404() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Fidelidade B2B", bearerGestor);
        String idIdeiaDeOutroProjeto = ideiaIdPorTitulo("Sistema de Roteirização Inteligente", bearerGestor);

        mockMvc.perform(delete("/api/projetos/" + idProjeto + "/ideias/" + idIdeiaDeOutroProjeto)
                        .header("Authorization", bearerGestor))
                .andExpect(status().isNotFound());
    }
}
