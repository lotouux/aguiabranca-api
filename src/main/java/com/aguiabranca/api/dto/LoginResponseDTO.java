package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginResponseDTO(
        String token,
        String nome,
        String perfil,
        String aiKey
) {}
