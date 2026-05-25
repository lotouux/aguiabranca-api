package com.aguiabranca.api.dto;

public record IdeiaRequestDTO(
        String titulo,
        String descricao,
        String area,
        String autor,
        String data,
        String prazo,
        String impacto,
        String esforco
) {}