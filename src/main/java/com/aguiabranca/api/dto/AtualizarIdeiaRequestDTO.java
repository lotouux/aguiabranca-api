package com.aguiabranca.api.dto;

public record AtualizarIdeiaRequestDTO(
        String status,
        String prioridade,
        Boolean isStrategicBonus,
        String responsavel,
        String prazo,
        Float investimento,
        Float retorno,
        Float roiEsperado
) {}