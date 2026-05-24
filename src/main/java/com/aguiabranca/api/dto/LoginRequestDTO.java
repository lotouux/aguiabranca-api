package com.aguiabranca.api.dto;

public record LoginRequestDTO(
        String email,
        String senha
) {}