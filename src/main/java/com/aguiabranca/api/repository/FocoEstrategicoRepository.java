package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.FocoEstrategico;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import java.util.Optional;

public interface FocoEstrategicoRepository extends MongoRepository<FocoEstrategico, Long> {

    long countByAtivoTrue();

    @Query("{}")
    @Update("{ '$set': { 'ativo': false } }")
    void desativarTodos();

    @Query("{ '_id': ?0 }")
    @Update("{ '$set': { 'ativo': true } }")
    void ativarPorId(Long id);

    Optional<FocoEstrategico> findByMes(String mes);
}