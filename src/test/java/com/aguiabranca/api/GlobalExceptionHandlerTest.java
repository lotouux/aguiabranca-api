package com.aguiabranca.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTest extends AbstractApiIntegrationTest {

    @Test
    void ideiaInexistenteRetorna404ComCorpoPadrao() throws Exception {
        mockMvc.perform(get("/api/ideias/nao-existe").header("Authorization", bearer("OP001")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.erro").value("NAO_ENCONTRADO"));
    }

    @Test
    void corpoInvalidoRetorna400ComListaDeCampos() throws Exception {
        mockMvc.perform(post("/api/ideias")
                        .header("Authorization", bearer("OP001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"\",\"descricao\":\"\",\"area\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("REQUISICAO_INVALIDA"))
                .andExpect(jsonPath("$.campos").isNotEmpty());
    }

    @Test
    void jsonMalformadoRetorna400EmVezDe500() throws Exception {
        mockMvc.perform(post("/api/ideias")
                        .header("Authorization", bearer("OP001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ isto nao e json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void valorDeEnumDesconhecidoNaQueryRetorna400EmVezDe500() throws Exception {
        mockMvc.perform(get("/api/projetos?status=NAO_EXISTE").header("Authorization", bearer("GS001")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erro").value("REQUISICAO_INVALIDA"));
    }

    @Test
    void rotaInexistenteRetorna404() throws Exception {
        mockMvc.perform(get("/api/rota-que-nao-existe").header("Authorization", bearer("OP001")))
                .andExpect(status().isNotFound());
    }
}
