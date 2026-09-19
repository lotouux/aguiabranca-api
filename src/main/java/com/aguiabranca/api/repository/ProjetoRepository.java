package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Projeto;
import com.aguiabranca.api.model.enums.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Never bulk-delete a {@code Projeto} (e.g. {@code @Query("delete from Projeto ...")}) - it
 * would leave orphan {@code projeto_ideia} rows or hit an FK violation. Always go through {@code
 * deleteById}, which loads the entity first so Hibernate clears the join table.
 */
public interface ProjetoRepository extends JpaRepository<Projeto, String> {

    List<Projeto> findByStatus(StatusProjeto status);

    @Query("""
            SELECT
              COALESCE(COUNT(p), 0) as totalProjetos,
              COALESCE(SUM(CASE WHEN p.status = com.aguiabranca.api.model.enums.StatusProjeto.CONCLUIDO THEN 1 ELSE 0 END), 0) as projetosConcluidos,
              COALESCE(SUM(CASE WHEN p.status = com.aguiabranca.api.model.enums.StatusProjeto.EM_ANDAMENTO THEN 1 ELSE 0 END), 0) as projetosEmAndamento,
              COALESCE(SUM(p.investimento), 0) as investimentoTotal,
              COALESCE(SUM(p.economiaAnualEstimada), 0) as economiaEstimadaTotal,
              COALESCE(SUM(p.economiaAnualRealizada), 0) as economiaRealizadaTotal,
              COALESCE(SUM(p.horasEconomizadasMes), 0) as horasEconomizadasMes,
              COALESCE(SUM(CASE WHEN p.investimento IS NULL OR p.investimento = 0 THEN 1 ELSE 0 END), 0) as projetosSemInvestimento,
              COALESCE(SUM(CASE WHEN p.investimento IS NOT NULL AND p.investimento <> 0 THEN p.investimento ELSE 0 END), 0) as investimentoElegivel,
              COALESCE(SUM(CASE WHEN p.investimento IS NOT NULL AND p.investimento <> 0 THEN p.economiaAnualEstimada ELSE 0 END), 0) as economiaEstimadaElegivel,
              COALESCE(SUM(CASE WHEN p.investimento IS NOT NULL AND p.investimento <> 0 THEN p.economiaAnualRealizada ELSE 0 END), 0) as economiaRealizadaElegivel
            FROM Projeto p
            WHERE p.status <> com.aguiabranca.api.model.enums.StatusProjeto.CANCELADO
            """)
    AgregadoProjetosProjection buscarAgregado();
}
