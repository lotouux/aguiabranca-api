package com.aguiabranca.api;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LoginTest extends AbstractApiIntegrationTest {

    @Test
    void loginComCredenciaisValidasRetornaTokenRealENomeEPerfil() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"OP001\",\"senha\":\"123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.nome").value("Pedro Miranda"))
                .andExpect(jsonPath("$.perfil").value("OPERADOR"));
    }

    @Test
    void tokenEmitidoTemTresSegmentosENaoEOTokenFalsoAntigo() throws Exception {
        String token = token("OP001");

        assertThat(token.split("\\.")).hasSize(3);
        assertThat(token).doesNotStartWith("token-fake-");
    }

    @Test
    void matriculaDesconhecidaEsenhaErradaRetornamCorpoIdentico() throws Exception {
        MvcResult matriculaDesconhecida = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"NAO_EXISTE\",\"senha\":\"123\"}"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        MvcResult senhaErrada = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"OP001\",\"senha\":\"senha-errada\"}"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertThat(semTimestamp(matriculaDesconhecida.getResponse().getContentAsString()))
                .isEqualTo(semTimestamp(senhaErrada.getResponse().getContentAsString()));
    }

    private String semTimestamp(String corpo) {
        return corpo.replaceAll("\"timestamp\":\"[^\"]*\"", "\"timestamp\":\"X\"");
    }

    @Test
    void meRetornaOUsuarioDoTokenSemSenha() throws Exception {
        mockMvc.perform(get("/api/auth/me").header("Authorization", bearer("GS001")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matricula").value("GS001"))
                .andExpect(jsonPath("$.senha").doesNotExist());
    }
}
