package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.Ideia;
import com.aguiabranca.api.model.enums.Nivel;
import com.aguiabranca.api.model.enums.Prioridade;
import com.aguiabranca.api.model.enums.StatusIdeia;

import java.math.BigDecimal;
import java.time.LocalDate;

public record IdeiaResponseDTO(
        String id,
        String titulo,
        String descricao,
        StatusIdeia status,
        String area,
        String autor,
        Long autorId,
        boolean isStrategicBonus,
        Nivel impacto,
        Nivel esforco,
        Prioridade prioridade,
        LocalDate prazo,
        BigDecimal roiEsperado,
        BigDecimal investimento,
        BigDecimal retorno,
        LocalDate dataSubmissao
) {

    public static IdeiaResponseDTO from(Ideia ideia) {
        return new IdeiaResponseDTO(
                ideia.getId(),
                ideia.getTitulo(),
                ideia.getDescricao(),
                ideia.getStatus(),
                ideia.getArea(),
                ideia.getAutor().getNome(),
                ideia.getAutor().getId(),
                ideia.isStrategicBonus(),
                ideia.getImpacto(),
                ideia.getEsforco(),
                ideia.getPrioridade(),
                ideia.getPrazo(),
                ideia.getRoiEsperado(),
                ideia.getInvestimento(),
                ideia.getRetorno(),
                ideia.getCriadoEm() != null ? ideia.getCriadoEm().toLocalDate() : null
        );
    }
}
