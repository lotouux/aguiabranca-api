package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.Tarefa;
import com.aguiabranca.api.model.enums.StatusTarefa;

import java.time.LocalDate;

public record TarefaDTO(
        Long id,
        String titulo,
        StatusTarefa status,
        LocalDate dataConclusao
) {

    public static TarefaDTO from(Tarefa tarefa) {
        return new TarefaDTO(tarefa.getId(), tarefa.getTitulo(), tarefa.getStatus(), tarefa.getDataConclusao());
    }
}