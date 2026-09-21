package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Projeto;
import com.aguiabranca.api.model.enums.StatusProjeto;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ProjetoRepository extends MongoRepository<Projeto, String> {

    List<Projeto> findByStatus(StatusProjeto status);

    @Aggregation(pipeline = {
        "{ '$match': { 'status': { '$ne': 'CANCELADO' } } }",
        "{ '$group': { " +
            "'_id': null, " +
            "'totalProjetos': { '$sum': 1 }, " +
            "'projetosConcluidos': { '$sum': { '$cond': [ { '$eq': ['$status', 'CONCLUIDO'] }, 1, 0 ] } }, " +
            "'projetosEmAndamento': { '$sum': { '$cond': [ { '$eq': ['$status', 'EM_ANDAMENTO'] }, 1, 0 ] } }, " +
            "'investimentoTotal': { '$sum': '$investimento' }, " +
            "'economiaEstimadaTotal': { '$sum': '$economiaAnualEstimada' }, " +
            "'economiaRealizadaTotal': { '$sum': '$economiaAnualRealizada' }, " +
            "'horasEconomizadasMes': { '$sum': '$horasEconomizadasMes' }, " +
            "'projetosSemInvestimento': { '$sum': { '$cond': [ { '$or': [ { '$eq': ['$investimento', null] }, { '$eq': ['$investimento', 0] } ] }, 1, 0 ] } }, " +
            "'investimentoElegivel': { '$sum': { '$cond': [ { '$and': [ { '$ne': ['$investimento', null] }, { '$ne': ['$investimento', 0] } ] }, '$investimento', 0 ] } }, " +
            "'economiaEstimadaElegivel': { '$sum': { '$cond': [ { '$and': [ { '$ne': ['$investimento', null] }, { '$ne': ['$investimento', 0] } ] }, '$economiaAnualEstimada', 0 ] } }, " +
            "'economiaRealizadaElegivel': { '$sum': { '$cond': [ { '$and': [ { '$ne': ['$investimento', null] }, { '$ne': ['$investimento', 0] } ] }, '$economiaAnualRealizada', 0 ] } } " +
        "} }"
    })
    AgregadoProjetosProjection buscarAgregado();

    Optional<Projeto> findByTitulo(String titulo);
}