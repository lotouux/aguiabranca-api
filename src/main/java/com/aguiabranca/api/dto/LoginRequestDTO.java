package com.aguiabranca.api.dto;

public record LoginRequestDTO(
        String matricula,
        String senha
) {}