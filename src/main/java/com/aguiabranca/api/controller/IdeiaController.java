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
    public ResponseEntity<IdeiaResponseDTO> buscarPorId(@PathVariable String id,
            @AuthenticationPrincipal UsuarioAutenticado principal) {
        return ResponseEntity.ok(ideiaService.buscarPorId(id, principal));
    }

    @GetMapping("/minhas")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<IdeiaResponseDTO>> minhasIdeias(
            @AuthenticationPrincipal UsuarioAutenticado usuario
    ) {
        return ResponseEntity.ok(
                ideiaService.listarMinhasIdeias(usuario.id())
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<IdeiaResponseDTO> criar(@Valid @RequestBody CriarIdeiaDTO dto,
            @AuthenticationPrincipal UsuarioAutenticado principal) {

        return ResponseEntity.status(HttpStatus.CREATED).body(ideiaService.criar(dto, principal));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<IdeiaResponseDTO> atualizar(@PathVariable String id,
            @Valid @RequestBody AtualizarIdeiaRequestDTO dto) {
        return ResponseEntity.ok(ideiaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        ideiaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // --- Marcos ---

    @GetMapping("/{id}/marcos")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<MarcoDTO>> listarMarcos(@PathVariable String id) {
        return ResponseEntity.ok(marcoService.listar(id));
    }

    @PostMapping("/{id}/marcos")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<MarcoDTO> criarMarco(@PathVariable String id,
            @Valid @RequestBody CriarMarcoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marcoService.criar(id, dto));
    }

    @PatchMapping("/{id}/marcos/{marcoId}")
    @PreAuthorize("hasRole('GESTOR')")
    public ResponseEntity<MarcoDTO> atualizarMarco(@PathVariable String id,
            @PathVariable String marcoId,
            @RequestBody AtualizarMarcoDTO dto) {
        return ResponseEntity.ok(marcoService.atualizar(id, marcoId, dto));
    }
}