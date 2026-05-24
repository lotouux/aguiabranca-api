package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.StrategicFocusDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/estrategia")
public class EstrategiaController {

    // Simula o App pedindo: "Quais são os focos estratégicos do mês?" (GET)
    @GetMapping("/focos")
    public ResponseEntity<List<StrategicFocusDTO>> listarFocos() {

        // Aqui, no futuro, buscaremos do banco de dados real.
        // Por enquanto, devolvemos um mock no formato exato que o Kotlin espera.
        StrategicFocusDTO foco1 = new StrategicFocusDTO(
                "1", "Maio", "Redução de Emissões",
                "Foco em ideias que reduzam a pegada de carbono da frota em 15%.",
                List.of("Logística", "Operação"), true
        );

        StrategicFocusDTO foco2 = new StrategicFocusDTO(
                "2", "Junho", "Eficiência em Logística",
                "Otimizar processos de carga e descarga para reduzir tempo em 20%.",
                List.of("Logística"), false
        );

        return ResponseEntity.ok(List.of(foco1, foco2));
    }

    // Simula o Líder criando um novo Foco Estratégico pelo App (POST)
    @PostMapping("/focos")
    public ResponseEntity<String> criarFoco(@RequestBody StrategicFocusDTO novoFoco) {

        // O Spring Boot pega o JSON que o Android enviou e transforma automaticamente no objeto novoFoco!
        System.out.println("Recebido do App Android: " + novoFoco.titulo());

        // Retornamos um status 201 (Created)
        return ResponseEntity.status(201).body("Foco estratégico criado com sucesso!");
    }
}