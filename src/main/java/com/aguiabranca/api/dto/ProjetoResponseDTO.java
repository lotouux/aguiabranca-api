package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.Projeto;
import com.aguiabranca.api.model.enums.StatusProjeto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record ProjetoResponseDTO(
        String id,
        String titulo,
        String descricao,
        String area,
        String observacao,
        StatusProjeto status,
        Long responsavelId,
        String responsavelNome,
        LocalDate dataInicio,
        LocalDate dataPrevistaConclusao,
        BigDecimal investimento,
        BigDecimal economiaAnualEstimada,
        BigDecimal economiaAnualRealizada,
        Integer horasEconomizadasMes,
        int progresso,
        List<String> ideiaIds,
        List<TarefaDTO> tarefas
) {

    public static ProjetoResponseDTO from(Projeto projeto) {
        return new ProjetoResponseDTO(
                projeto.getId(),
                projeto.getTitulo(),
                projeto.getDescricao(),
                projeto.getArea(),
                projeto.getObservacao(),
                projeto.getStatus(),
                projeto.getResponsavel() != null ? projeto.getResponsavel().getId() : null,
                projeto.getResponsavel() != null ? projeto.getResponsavel().getNome() : null,
                projeto.getDataInicio(),
                projeto.getDataPrevistaConclusao(),
                projeto.getInvestimento(),
                projeto.getEconomiaAnualEstimada(),
                projeto.getEconomiaAnualRealizada(),
                projeto.getHorasEconomizadasMes(),
                projeto.getProgresso(),
                projeto.getIdeias().stream().map(com.aguiabranca.api.model.Ideia::getId).toList(),
                projeto.getTarefas().stream().map(TarefaDTO::from).toList()
        );
    }
}
