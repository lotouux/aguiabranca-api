package com.aguiabranca.api.dto;

public record LoginResponseDTO(
        String token,
        String nome,
        String perfil
) {}
