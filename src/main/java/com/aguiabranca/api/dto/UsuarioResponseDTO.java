package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.TipoPerfil;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String matricula,
        TipoPerfil perfil
) {

    public static UsuarioResponseDTO from(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNome(), usuario.getMatricula(),
                usuario.getPerfil());
    }
}
