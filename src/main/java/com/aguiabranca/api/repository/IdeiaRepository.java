package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Ideia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IdeiaRepository extends JpaRepository<Ideia, String> {

    List<Ideia> findByAutorId(Long autorId);
}
