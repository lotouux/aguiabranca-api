package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Ideia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdeiaRepository extends JpaRepository<Ideia, String> {}
