package com.aguiabranca.api.dto;

import java.util.List;

public record CriarFocoDTO(
        String mes,
        String titulo,
        String descricao,
        List<String> areasPotenciais,
        boolean ativo
) {}
