package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.LoginRequestDTO;
import com.aguiabranca.api.dto.LoginResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {

        // Lógica mockada simples: dependendo do email digitado, devolvemos um perfil diferente
        if (loginRequest.email().contains("operador")) {
            return ResponseEntity.ok(new LoginResponseDTO("token-fake-123", "Pedro Miranda", "OPERADOR"));
        }
        else if (loginRequest.email().contains("gestor")) {
            return ResponseEntity.ok(new LoginResponseDTO("token-fake-456", "Leonardo Martin", "GESTOR"));
        }
        else if (loginRequest.email().contains("lider")) {
            return ResponseEntity.ok(new LoginResponseDTO("token-fake-789", "Beatriz Camargo", "LIDERANCA"));
        }

        // Se a senha ou email estiverem "errados", retorna erro 401 Unauthorized
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}