package com.aguiabranca.api;

import com.aguiabranca.api.config.TestClockConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Every integration test performs a real login rather than faking a principal - the JWT filter
 * is the component most worth exercising end to end. One Spring context for the whole suite:
 * {@code spring.datasource.url} is fixed, so a class that diverges in configuration would get a
 * second context whose {@code create-drop} drops the tables under the first, still-cached one.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@Import(TestClockConfig.class)
@Transactional
public abstract class AbstractApiIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    protected String token(String matricula) throws Exception {
        return token(matricula, "123");
    }

    protected String token(String matricula, String senha) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"matricula\":\"" + matricula + "\",\"senha\":\"" + senha + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    protected String bearer(String matricula) throws Exception {
        return "Bearer " + token(matricula);
    }

    protected JsonNode listarIdeias(String bearerToken) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/ideias").header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    protected String ideiaIdPorTitulo(String titulo, String bearerToken) throws Exception {
        for (JsonNode ideia : listarIdeias(bearerToken)) {
            if (ideia.get("titulo").asText().equals(titulo)) {
                return ideia.get("id").asText();
            }
        }
        throw new IllegalStateException("Ideia não encontrada no seed: " + titulo);
    }

    protected JsonNode listarProjetos(String bearerToken) throws Exception {
        MvcResult result = mockMvc.perform(get("/api/projetos").header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    protected String projetoIdPorTitulo(String titulo, String bearerToken) throws Exception {
        for (JsonNode projeto : listarProjetos(bearerToken)) {
            if (projeto.get("titulo").asText().equals(titulo)) {
                return projeto.get("id").asText();
            }
        }
        throw new IllegalStateException("Projeto não encontrado no seed: " + titulo);
    }
}
