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
@RequestMapping({"/api/focos-estrategicos", "/api/estrategia/focos"})
@RequiredArgsConstructor

public class EstrategiaController {
    private final FocoEstrategicoService focoEstrategicoService;

    @GetMapping
    public ResponseEntity<List<FocoEstrategicoDTO>> listar() {
        return ResponseEntity.ok(focoEstrategicoService.listar());
    }

    @PreAuthorize("hasRole('LIDERANCA')")
    @PostMapping
    public ResponseEntity<FocoEstrategicoDTO> criar(@Valid @RequestBody CriarFocoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(focoEstrategicoService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FocoEstrategicoDTO> atualizar(@PathVariable String id, @Valid @RequestBody AtualizarFocoDTO dto) {
        return ResponseEntity.ok(focoEstrategicoService.atualizar(id, dto));
    }

    @PreAuthorize("hasRole('LIDERANCA')")
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<FocoEstrategicoDTO> ativar(@PathVariable String id) {
        return ResponseEntity.ok(focoEstrategicoService.ativar(id));
    }

    @PreAuthorize("hasRole('LIDERANCA')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        focoEstrategicoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}