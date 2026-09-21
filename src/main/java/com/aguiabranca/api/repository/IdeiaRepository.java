package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Ideia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface IdeiaRepository extends MongoRepository<Ideia, String> {
    List<Ideia> findByAutorId(String autorId);

    Optional<Ideia> findByTitulo(String titulo);

    List<Ideia> findByAutorMatricula(String matricula);
}