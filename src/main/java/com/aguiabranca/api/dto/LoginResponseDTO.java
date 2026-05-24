package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.enums.TipoPerfil;

public record LoginResponseDTO(
        String token,   // No futuro, usaremos JWT aqui
        String nome,
        String perfil
) {}