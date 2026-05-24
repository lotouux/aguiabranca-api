package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.FocoEstrategicoDTO;
import com.aguiabranca.api.model.FocoEstrategico;
import com.aguiabranca.api.repository.FocoEstrategicoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estrategia")
public class EstrategiaController {

    @Autowired
    private FocoEstrategicoRepository repository;

    @GetMapping("/focos")
    public ResponseEntity<List<FocoEstrategicoDTO>> listarFocos() {
        List<FocoEstrategicoDTO> focos = repository.findAll().stream()
                .map(f -> new FocoEstrategicoDTO(
                        f.getId(), f.getMes(), f.getTitulo(), f.getDescricao(),
                        f.getAreasPotenciais(), f.isAtivo()
                )).collect(Collectors.toList());

        return ResponseEntity.ok(focos);
    }

    @PostMapping("/focos")
    public ResponseEntity<String> criarFoco(@RequestBody FocoEstrategicoDTO dto) {
        FocoEstrategico foco = new FocoEstrategico(
                dto.id(), dto.mes(), dto.titulo(), dto.descricao(),
                dto.areasPotenciais(), dto.ativo()
        );
        repository.save(foco);
        return ResponseEntity.status(201).body("Foco estratégico criado com sucesso!");
    }
}