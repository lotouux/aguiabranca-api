package com.aguiabranca.api.security;

import com.aguiabranca.api.model.enums.TipoPerfil;

public record UsuarioAutenticado(
        String id,
        String matricula,
        String nome,
        TipoPerfil perfil
) {}
