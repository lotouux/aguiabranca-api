package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.CriarFocoDTO;
import com.aguiabranca.api.dto.FocoEstrategicoDTO;
import com.aguiabranca.api.model.FocoEstrategico;
import com.aguiabranca.api.repository.FocoEstrategicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estrategia/focos")
public class EstrategiaController {

    @Autowired
    private FocoEstrategicoRepository repository;

    // LISTAR
    @GetMapping
    public ResponseEntity<List<FocoEstrategicoDTO>> listarFocos() {
        List<FocoEstrategicoDTO> focos = repository.findAll().stream()
                .map(f -> new FocoEstrategicoDTO(
                        f.getId(),
                        f.getMes(),
                        f.getTitulo(),
                        f.getDescricao(),
                        f.getAreasPotenciais(),
                        f.isAtivo()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(focos);
    }

    // CRIAR
    @PostMapping
    public ResponseEntity<FocoEstrategicoDTO> criarFoco(@RequestBody CriarFocoDTO dto) {
        FocoEstrategico foco = new FocoEstrategico(
                null,
                dto.mes(),
                dto.titulo(),
                dto.descricao(),
                dto.areasPotenciais(),
                dto.ativo());
        FocoEstrategico salvo = repository.save(foco);

        return ResponseEntity.status(201).body(
                new FocoEstrategicoDTO(
                        salvo.getId(),
                        salvo.getMes(),
                        salvo.getTitulo(),
                        salvo.getDescricao(),
                        salvo.getAreasPotenciais(),
                        salvo.isAtivo()));
    }

    // ATUALIZAR
    @PatchMapping("/{id}")
    public ResponseEntity<FocoEstrategicoDTO> atualizarFoco(
            @PathVariable String id,
            @RequestBody FocoEstrategicoDTO dto) {
        return repository.findById(id)
                .map(foco -> {

                    foco.setMes(dto.mes());
                    foco.setTitulo(dto.titulo());
                    foco.setDescricao(dto.descricao());
                    foco.setAreasPotenciais(dto.areasPotenciais());
                    foco.setAtivo(dto.ativo());

                    FocoEstrategico atualizado = repository.save(foco);

                    FocoEstrategicoDTO response = new FocoEstrategicoDTO(
                            atualizado.getId(),
                            atualizado.getMes(),
                            atualizado.getTitulo(),
                            atualizado.getDescricao(),
                            atualizado.getAreasPotenciais(),
                            atualizado.isAtivo());

                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/focos/{id}/ativar")
    public ResponseEntity<Void> ativarFoco(@PathVariable String id) {

        repository.findAll().forEach(f -> {
            f.setAtivo(f.getId().equals(id));
            repository.save(f);
        });

        return ResponseEntity.ok().build();
    }

    // DELETAR
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarFoco(@PathVariable String id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}