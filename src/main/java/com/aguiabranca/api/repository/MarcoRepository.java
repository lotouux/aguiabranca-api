package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Marco;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MarcoRepository extends MongoRepository<Marco, String> {

    List<Marco> findByIdeiaId(String ideiaId);
}
