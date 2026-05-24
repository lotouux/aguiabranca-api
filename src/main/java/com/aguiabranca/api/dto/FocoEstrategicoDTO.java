package com.aguiabranca.api.dto;

import java.util.List;

// O formato exato do JSON que a API vai devolver ou receber do app
public record FocoEstrategicoDTO(
        String id,
        String mes,
        String titulo,
        String descricao,
        List<String> areasPotenciais,
        boolean ativo
) {}