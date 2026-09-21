package com.aguiabranca.api.repository;

import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.TipoPerfil;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UsuarioRepository extends MongoRepository<Usuario, String> {

    Optional<Usuario> findByMatricula(String matricula);

    long countByPerfil(TipoPerfil perfil);
}