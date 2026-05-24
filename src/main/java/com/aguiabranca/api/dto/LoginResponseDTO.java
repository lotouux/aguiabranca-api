package com.aguiabranca.api.dto;

public record LoginResponseDTO(
        String token,   // No futuro, usaremos JWT aqui
        String nome,
        String perfil   // "OPERADOR", "LIDERANCA" ou "GESTOR"
) {}