package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.AtualizarFocoDTO;
import com.aguiabranca.api.dto.CriarFocoDTO;
import com.aguiabranca.api.dto.FocoEstrategicoDTO;
import com.aguiabranca.api.service.FocoEstrategicoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/focos-estrategicos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GESTOR')")
public class EstrategiaController {

    private final FocoEstrategicoService focoEstrategicoService;

    @GetMapping
    public ResponseEntity<List<FocoEstrategicoDTO>> listar() {
        return ResponseEntity.ok(focoEstrategicoService.listar());
    }

    @PostMapping
    public ResponseEntity<FocoEstrategicoDTO> criar(@Valid @RequestBody CriarFocoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(focoEstrategicoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FocoEstrategicoDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarFocoDTO dto) {
        return ResponseEntity.ok(focoEstrategicoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/ativar")
    public ResponseEntity<FocoEstrategicoDTO> ativar(@PathVariable Long id) {
        return ResponseEntity.ok(focoEstrategicoService.ativar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        focoEstrategicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}