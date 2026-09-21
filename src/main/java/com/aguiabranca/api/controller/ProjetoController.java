package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.*;
import com.aguiabranca.api.model.enums.StatusProjeto;
import com.aguiabranca.api.service.ProjetoService;
import com.aguiabranca.api.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projetos")
@RequiredArgsConstructor
@PreAuthorize("hasRole('GESTOR')")
public class ProjetoController {

    private final ProjetoService projetoService;
    private final TarefaService tarefaService;

    @GetMapping
    public ResponseEntity<List<ProjetoResponseDTO>> listar(@RequestParam(required = false) StatusProjeto status) {
        return ResponseEntity.ok(projetoService.listar(status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(@PathVariable String id) {
        return ResponseEntity.ok(projetoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ProjetoResponseDTO> criar(@Valid @RequestBody CriarProjetoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(projetoService.criar(dto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> atualizar(@PathVariable String id,
            @Valid @RequestBody AtualizarProjetoRequestDTO dto) {
        return ResponseEntity.ok(projetoService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        projetoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ideias")
    public ResponseEntity<ProjetoResponseDTO> vincularIdeias(@PathVariable String id,
            @Valid @RequestBody AttachIdeiasDTO dto) {
        return ResponseEntity.ok(projetoService.vincularIdeias(id, dto.ideiaIds()));
    }

    @DeleteMapping("/{id}/ideias/{ideiaId}")
    public ResponseEntity<Void> desvincularIdeia(@PathVariable String id, @PathVariable String ideiaId) {
        projetoService.desvincularIdeia(id, ideiaId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/tarefas")
    public ResponseEntity<TarefaDTO> criarTarefa(@PathVariable String id, @Valid @RequestBody CriarTarefaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tarefaService.criar(id, dto));
    }

    @PatchMapping("/{id}/tarefas/{tarefaId}")
    public ResponseEntity<TarefaDTO> atualizarTarefa(@PathVariable String id, @PathVariable String tarefaId,
            @Valid @RequestBody AtualizarTarefaRequestDTO dto) {
        return ResponseEntity.ok(tarefaService.atualizar(id, tarefaId, dto));
    }

    @DeleteMapping("/{id}/tarefas/{tarefaId}")
    public ResponseEntity<Void> excluirTarefa(@PathVariable String id, @PathVariable String tarefaId) {
        tarefaService.excluir(id, tarefaId);
        return ResponseEntity.noContent().build();
    }
}