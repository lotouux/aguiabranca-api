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
@RequestMapping("/api/estrategia/focos")
@RequiredArgsConstructor
public class EstrategiaController {

    private final FocoEstrategicoService service;

    @GetMapping
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<List<FocoEstrategicoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<FocoEstrategicoDTO> criar(@Valid @RequestBody CriarFocoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<FocoEstrategicoDTO> atualizar(@PathVariable String id,
            @Valid @RequestBody AtualizarFocoDTO dto) {
        return ResponseEntity.ok(service.atualizar(id, dto));
    }

    @PatchMapping("/{id}/ativar")
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<FocoEstrategicoDTO> ativar(@PathVariable String id) {
        return ResponseEntity.ok(service.ativar(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<Void> deletar(@PathVariable String id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
