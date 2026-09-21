package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Tarefa;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TarefaRepository extends MongoRepository<Tarefa, String> {
}