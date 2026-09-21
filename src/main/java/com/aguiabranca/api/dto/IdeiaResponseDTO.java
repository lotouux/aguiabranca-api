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
        String autorId,
        String responsavel,
        boolean strategicBonus,
        Nivel impacto,
        Nivel esforco,
        Prioridade prioridade,
        LocalDate prazo,
        BigDecimal roiEsperado,
        BigDecimal investimento,
        BigDecimal retorno,
        Integer baseKM,
        String feedbackGestor,
        LocalDate data
) {

    public static IdeiaResponseDTO from(Ideia ideia) {
        return new IdeiaResponseDTO(
                ideia.getId(),
                ideia.getTitulo(),
                ideia.getDescricao(),
                ideia.getStatus(),
                ideia.getArea(),
                ideia.getAutor() != null ? ideia.getAutor().getNome() : null,
                ideia.getAutor() != null ? ideia.getAutor().getId() : null,
                ideia.getResponsavel(),
                ideia.isStrategicBonus(),
                ideia.getImpacto(),
                ideia.getEsforco(),
                ideia.getPrioridade(),
                ideia.getPrazo(),
                ideia.getRoiEsperado(),
                ideia.getInvestimento(),
                ideia.getRetorno(),
                ideia.getBaseKM(),
                ideia.getFeedbackGestor(),
                ideia.getCriadoEm() != null ? ideia.getCriadoEm().toLocalDate() : null
        );
    }
}
