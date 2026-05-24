package com.aguiabranca.api.dto;

public record MarcoProjetoDTO(
        Integer id,
        String titulo,
        boolean isCompleto,
        String dataCompleto
) {}