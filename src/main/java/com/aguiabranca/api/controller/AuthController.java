package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.LoginRequestDTO;
import com.aguiabranca.api.dto.LoginResponseDTO;
import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByMatricula(loginRequest.matricula());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            if (usuario.getSenha().equals(loginRequest.senha())) {
                return ResponseEntity.ok(new LoginResponseDTO(
                        "token-fake-" + usuario.getId(),
                        usuario.getNome(),
                        usuario.getPerfil().name()
                ));
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}