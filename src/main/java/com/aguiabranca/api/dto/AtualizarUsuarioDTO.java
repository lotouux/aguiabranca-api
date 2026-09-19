package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.enums.TipoPerfil;

public record AtualizarUsuarioDTO(
        String nome,
        TipoPerfil perfil,
        String senha
) {}
