package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.AtualizarUsuarioDTO;
import com.aguiabranca.api.dto.CriarUsuarioDTO;
import com.aguiabranca.api.dto.UsuarioResponseDTO;
import com.aguiabranca.api.security.UsuarioAutenticado;
import com.aguiabranca.api.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
@PreAuthorize("hasRole('LIDERANCA')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listar() {
        return ResponseEntity.ok(usuarioService.listar());
    }

    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> criar(@Valid @RequestBody CriarUsuarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.criar(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id,
            @RequestBody AtualizarUsuarioDTO dto, @AuthenticationPrincipal UsuarioAutenticado principal) {
        return ResponseEntity.ok(usuarioService.atualizar(id, dto, principal.id()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado principal) {
        usuarioService.deletar(id, principal.id());
        return ResponseEntity.noContent().build();
    }
}
