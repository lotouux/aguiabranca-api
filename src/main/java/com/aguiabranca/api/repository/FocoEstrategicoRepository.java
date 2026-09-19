package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.FocoEstrategico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FocoEstrategicoRepository extends JpaRepository<FocoEstrategico, String> {

    long countByAtivoTrue();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FocoEstrategico f SET f.ativo = false WHERE f.ativo = true")
    void desativarTodos();

    @Modifying(clearAutomatically = true)
    @Query("UPDATE FocoEstrategico f SET f.ativo = true WHERE f.id = :id")
    void ativarPorId(@Param("id") String id);
}
