package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Ideia;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface IdeiaRepository extends MongoRepository<Ideia, Long> {
    List<Ideia> findByAutorId(Long autorId);
}