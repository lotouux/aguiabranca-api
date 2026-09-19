package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.enums.TipoPerfil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CriarUsuarioDTO(
        @NotBlank String nome,
        @NotBlank String matricula,
        @NotBlank String senha,
        @NotNull TipoPerfil perfil
) {}
