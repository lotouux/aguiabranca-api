package com.aguiabranca.api.dto;

public record IdeiaRequestDTO(
        String titulo,
        String descricao,
        String area,
        String impacto,
        String esforco,
        String prioridade
) {}