package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.*;
import com.aguiabranca.api.security.UsuarioAutenticado;
import com.aguiabranca.api.service.IdeiaService;
import com.aguiabranca.api.service.MarcoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideias")
@RequiredArgsConstructor
public class IdeiaController {

    private final IdeiaService ideiaService;
    private final MarcoService marcoService;

    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<IdeiaResponseDTO>> listar(@AuthenticationPrincipal UsuarioAutenticado principal) {
        return ResponseEntity.ok(ideiaService.listar(principal));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<IdeiaResponseDTO> buscarPorId(@PathVariable Long id,
            @AuthenticationPrincipal UsuarioAutenticado principal) {
        return ResponseEntity.ok(ideiaService.buscarPorId(id, principal));
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<IdeiaResponseDTO> criar(@Valid @RequestBody CriarIdeiaDTO dto,
            @AuthenticationPrincipal UsuarioAutenticado principal) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ideiaService.criar(dto, principal));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<IdeiaResponseDTO> atualizar(@PathVariable Long id,
            @Valid @RequestBody AtualizarIdeiaRequestDTO dto) {
        return ResponseEntity.ok(ideiaService.atualizar(id, dto));
    }

    // --- Marcos ---

    @GetMapping("/{id}/marcos")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<MarcoDTO>> listarMarcos(@PathVariable Long id) {
        return ResponseEntity.ok(marcoService.listar(id));
    }

    @PostMapping("/{id}/marcos")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<MarcoDTO> criarMarco(@PathVariable Long id,
            @Valid @RequestBody CriarMarcoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marcoService.criar(id, dto));
    }

    @PatchMapping("/{id}/marcos/{marcoId}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<MarcoDTO> atualizarMarco(@PathVariable Long id,
            @PathVariable String marcoId,
            @RequestBody AtualizarMarcoDTO dto) {
        return ResponseEntity.ok(marcoService.atualizar(id, marcoId, dto));
    }
}