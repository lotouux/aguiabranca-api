package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.LoginRequestDTO;
import com.aguiabranca.api.dto.LoginResponseDTO;
import com.aguiabranca.api.dto.UsuarioResponseDTO;
import com.aguiabranca.api.security.UsuarioAutenticado;
import com.aguiabranca.api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto.matricula(), dto.senha()));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(@AuthenticationPrincipal UsuarioAutenticado principal) {
        return ResponseEntity.ok(authService.me(principal.id()));
    }
}
