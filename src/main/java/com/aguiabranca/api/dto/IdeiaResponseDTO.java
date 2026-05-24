package com.aguiabranca.api.dto;

import java.util.List;

public record IdeiaResponseDTO(
        String id,
        String titulo,
        String descricao,
        String status,
        String area,
        String data,
        String autor,
        Integer baseKM,
        boolean isStrategicBonus,
        String impacto,
        String esforco,
        String prioridade,
        String prazo,
        Float roiEsperado,
        Float investimento,
        Float retorno,
        String observacaoProgresso,
        List<MarcoProjetoDTO> marcos,
        String responsavel
) {}