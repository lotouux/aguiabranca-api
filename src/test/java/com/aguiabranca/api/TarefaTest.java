package com.aguiabranca.api;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TarefaTest extends AbstractApiIntegrationTest {

    @Test
    void concluirTarefaRegistraDataDoRelogioFixo() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Check-in Digital", bearerGestor);
        int idTarefa = tarefaIdPorTitulo(idProjeto, "Desenvolvimento do backend de rastreamento", bearerGestor);

        mockMvc.perform(patch("/api/projetos/" + idProjeto + "/tarefas/" + idTarefa)
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONCLUIDA\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONCLUIDA"))
                .andExpect(jsonPath("$.dataConclusao").value("2026-06-15"));
    }

    @Test
    void reabrirTarefaConcluidaLimpaADataDeConclusao() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Check-in Digital", bearerGestor);
        int idTarefa = tarefaIdPorTitulo(idProjeto,
                "Planejamento e levantamento de requisitos", bearerGestor);

        mockMvc.perform(patch("/api/projetos/" + idProjeto + "/tarefas/" + idTarefa)
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PENDENTE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataConclusao").doesNotExist());
    }

    @Test
    void alterarTarefaDeProjetoConcluidoRetorna409() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Plataforma de Roteirização", bearerGestor);
        int idTarefa = tarefaIdPorTitulo(idProjeto, "Rollout completo", bearerGestor);

        mockMvc.perform(patch("/api/projetos/" + idProjeto + "/tarefas/" + idTarefa)
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"PENDENTE\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(post("/api/projetos/" + idProjeto + "/tarefas")
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Nova tarefa\"}"))
                .andExpect(status().isConflict());

        mockMvc.perform(delete("/api/projetos/" + idProjeto + "/tarefas/" + idTarefa)
                        .header("Authorization", bearerGestor))
                .andExpect(status().isConflict());
    }

    @Test
    void atualizarTarefaDeOutroProjetoRetorna404() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjetoA = projetoIdPorTitulo("Check-in Digital", bearerGestor);
        String idProjetoB = projetoIdPorTitulo("Fidelidade B2B", bearerGestor);
        int idTarefaDoProjetoA = tarefaIdPorTitulo(idProjetoA,
                "Planejamento e levantamento de requisitos", bearerGestor);

        mockMvc.perform(patch("/api/projetos/" + idProjetoB + "/tarefas/" + idTarefaDoProjetoA)
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONCLUIDA\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void tarefaDesconhecidaRetorna404() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Fidelidade B2B", bearerGestor);

        mockMvc.perform(patch("/api/projetos/" + idProjeto + "/tarefas/999999")
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONCLUIDA\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void criarEExcluirTarefa() throws Exception {
        String bearerGestor = bearer("GS001");
        String idProjeto = projetoIdPorTitulo("Fidelidade B2B", bearerGestor);

        MvcResult criada = mockMvc.perform(post("/api/projetos/" + idProjeto + "/tarefas")
                        .header("Authorization", bearerGestor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"titulo\":\"Nova tarefa\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDENTE"))
                .andReturn();
        int idTarefa = objectMapper.readTree(criada.getResponse().getContentAsString()).get("id").asInt();

        mockMvc.perform(delete("/api/projetos/" + idProjeto + "/tarefas/" + idTarefa)
                        .header("Authorization", bearerGestor))
                .andExpect(status().isNoContent());
    }

    private int tarefaIdPorTitulo(String idProjeto, String titulo, String bearerToken) throws Exception {
        MvcResult resultado = mockMvc.perform(get("/api/projetos/" + idProjeto).header("Authorization", bearerToken))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode projeto = objectMapper.readTree(resultado.getResponse().getContentAsString());
        for (JsonNode tarefa : projeto.get("tarefas")) {
            if (tarefa.get("titulo").asText().equals(titulo)) {
                return tarefa.get("id").asInt();
            }
        }
        throw new IllegalStateException("Tarefa não encontrada no seed: " + titulo);
    }
}
