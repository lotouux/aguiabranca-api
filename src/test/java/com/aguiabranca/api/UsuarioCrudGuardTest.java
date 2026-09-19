package com.aguiabranca.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UsuarioCrudGuardTest extends AbstractApiIntegrationTest {

    @Test
    void listaDeUsuariosNuncaExpoeSenha() throws Exception {
        mockMvc.perform(get("/api/usuarios").header("Authorization", bearer("LD001")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].senha").doesNotExist());
    }

    @Test
    void criarUsuarioComMatriculaJaExistenteRetorna409() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .header("Authorization", bearer("LD001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Duplicado\",\"matricula\":\"OP001\",\"senha\":\"123\",\"perfil\":\"OPERADOR\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void liderNaoPodeExcluirAPropriaConta() throws Exception {
        long idLider = idDoUsuarioAutenticado("LD001");

        mockMvc.perform(delete("/api/usuarios/" + idLider).header("Authorization", bearer("LD001")))
                .andExpect(status().isConflict());
    }

    @Test
    void liderNaoPodeAlterarOProprioPerfil() throws Exception {
        long idLider = idDoUsuarioAutenticado("LD001");

        mockMvc.perform(patch("/api/usuarios/" + idLider)
                        .header("Authorization", bearer("LD001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"perfil\":\"GESTOR\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void removerOUltimoLiderRetorna409EDepoisDeCriarUmSegundoFunciona() throws Exception {
        long idPrimeiroLider = idDoUsuarioAutenticado("LD001");

        mockMvc.perform(delete("/api/usuarios/" + idPrimeiroLider).header("Authorization", bearer("LD001")))
                .andExpect(status().isConflict());

        MvcResult segundoLider = mockMvc.perform(post("/api/usuarios")
                        .header("Authorization", bearer("LD001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Segunda Liderança\",\"matricula\":\"LD002\",\"senha\":\"123\",\"perfil\":\"LIDERANCA\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        String tokenSegundoLider = bearer("LD002");

        mockMvc.perform(delete("/api/usuarios/" + idPrimeiroLider).header("Authorization", tokenSegundoLider))
                .andExpect(status().isNoContent());
    }

    @Test
    void excluirUsuarioComIdeiasAssociadasRetorna409() throws Exception {
        long idOperador = idDoUsuarioAutenticado("OP001");

        mockMvc.perform(delete("/api/usuarios/" + idOperador).header("Authorization", bearer("LD001")))
                .andExpect(status().isConflict());
    }

    private long idDoUsuarioAutenticado(String matricula) throws Exception {
        MvcResult resultado = mockMvc.perform(get("/api/auth/me").header("Authorization", bearer(matricula)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode corpo = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return corpo.get("id").asLong();
    }
}
