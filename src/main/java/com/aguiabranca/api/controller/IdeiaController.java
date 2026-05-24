package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.IdeiaRequestDTO;
import com.aguiabranca.api.dto.IdeiaResponseDTO;
import com.aguiabranca.api.dto.MarcoProjetoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideias")
public class IdeiaController {

    // Rota para o App listar todas as ideias (Usado pelo Gestor e Operador)
    @GetMapping
    public ResponseEntity<List<IdeiaResponseDTO>> listarIdeias() {

        // Mock reproduzindo exatamente a "Ideia 1" do seu arquivo Kotlin
        IdeiaResponseDTO ideia1 = new IdeiaResponseDTO(
                "1", "Sistema de Roteirização Inteligente", "Otimização de rotas via IA.",
                "Em Execução", "Logística", "12 ago", "Pedro Miranda", 200, false,
                "Alto", "Alto", "A", "29/06/2026", 2.0f, 150000f, 450000f, "",
                List.of(
                        new MarcoProjetoDTO(0, "Análise de Requisitos", true, "20/03/2026"),
                        new MarcoProjetoDTO(1, "MVP desenvolvido", true, "29/03/2026"),
                        new MarcoProjetoDTO(2, "Testes piloto", false, ""),
                        new MarcoProjetoDTO(3, "Rollout completo", false, "")
                ),
                "Larissa Linguiça"
        );

        return ResponseEntity.ok(List.of(ideia1));
    }

    // Rota para o Operador enviar uma nova ideia
    @PostMapping
    public ResponseEntity<String> criarIdeia(@RequestBody IdeiaRequestDTO novaIdeia) {
        System.out.println("Nova ideia recebida: " + novaIdeia.titulo() + " da área de " + novaIdeia.area());

        // Retorna status 201 (Created)
        return ResponseEntity.status(201).body("Ideia submetida com sucesso!");
    }
}