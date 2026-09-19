package com.aguiabranca.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PropriedadeDeIdeiaTest extends AbstractApiIntegrationTest {

    @Test
    void operadorVeExatamenteAsProprias() throws Exception {
        String tokenGestor = bearer("GS001");
        String idDaIdeiaDoGestor = criarIdeia(tokenGestor, "Ideia exclusiva do Gestor");

        JsonNode ideiasDoOperador = listarIdeias(bearer("OP001"));
        assertThat(ideiasDoOperador).hasSize(6);
        for (JsonNode ideia : ideiasDoOperador) {
            assertThat(ideia.get("titulo").asText()).isNotEqualTo("Ideia exclusiva do Gestor");
        }

        mockMvc.perform(get("/api/ideias/" + idDaIdeiaDoGestor).header("Authorization", bearer("OP001")))
                .andExpect(status().isForbidden());

        assertThat(listarIdeias(tokenGestor)).hasSize(7);
    }

    @Test
    void criarIdeiaIgnoraAutorDoCorpoEUsaOPrincipal() throws Exception {
        MvcResult me = mockMvc.perform(get("/api/auth/me").header("Authorization", bearer("OP001")))
                .andExpect(status().isOk()).andReturn();
        long idReal = objectMapper.readTree(me.getResponse().getContentAsString()).get("id").asLong();

        MvcResult criada = mockMvc.perform(post("/api/ideias")
                        .header("Authorization", bearer("OP001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"titulo":"Tentativa de impersonation","descricao":"desc","area":"Logística",
                                 "autor":"Outra Pessoa"}"""))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode ideia = objectMapper.readTree(criada.getResponse().getContentAsString());
        assertThat(ideia.get("autor").asText()).isEqualTo("Pedro Miranda");
        assertThat(ideia.get("autorId").asLong()).isEqualTo(idReal);
    }

    private String criarIdeia(String bearerToken, String titulo) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/api/ideias")
                        .header("Authorization", bearerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"" + titulo + "\",\"descricao\":\"desc\",\"area\":\"Logística\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        return objectMapper.readTree(resultado.getResponse().getContentAsString()).get("id").asText();
    }
}
