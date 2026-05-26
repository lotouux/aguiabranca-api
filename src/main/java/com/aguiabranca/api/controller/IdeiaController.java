package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.AtualizarIdeiaRequestDTO;
import com.aguiabranca.api.dto.AtualizarMarcoRequestDTO;
import com.aguiabranca.api.dto.IdeiaRequestDTO;
import com.aguiabranca.api.dto.IdeiaResponseDTO;
import com.aguiabranca.api.dto.MarcoProjetoDTO;
import com.aguiabranca.api.model.Ideia;
import com.aguiabranca.api.model.MarcoProjeto;
import com.aguiabranca.api.repository.IdeiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ideias")
public class IdeiaController {

    @Autowired
    private IdeiaRepository repository;

    @GetMapping
    public ResponseEntity<List<IdeiaResponseDTO>> listarIdeias() {
        List<IdeiaResponseDTO> ideias = repository.findAll().stream().map(i -> new IdeiaResponseDTO(
                i.getId(), i.getTitulo(), i.getDescricao(), i.getStatus(),
                i.getArea(), i.getData(), i.getAutor(), i.getBaseKM(),
                i.isStrategicBonus(), i.getImpacto(), i.getEsforco(),
                i.getPrioridade(), i.getPrazo(), i.getRoiEsperado(),
                i.getInvestimento(), i.getRetorno(), i.getObservacaoProgresso(),
                i.getMarcos() != null ? i.getMarcos().stream().map(m -> new MarcoProjetoDTO(
                        m.getId(), m.getTitulo(), m.isCompleto(), m.getDataCompleto())).collect(Collectors.toList())
                        : new ArrayList<>(),
                i.getResponsavel())).collect(Collectors.toList());

        return ResponseEntity.ok(ideias);
    }

    @PostMapping
    public ResponseEntity<String> criarIdeia(@RequestBody IdeiaRequestDTO dto) {
        Ideia novaIdeia = new Ideia();
        novaIdeia.setTitulo(dto.titulo());
        novaIdeia.setDescricao(dto.descricao());
        novaIdeia.setArea(dto.area());
        novaIdeia.setAutor(dto.autor());
        novaIdeia.setBaseKM(100);
        novaIdeia.setImpacto(dto.impacto());
        novaIdeia.setEsforco(dto.esforco());
        novaIdeia.setData(dto.data());
        novaIdeia.setPrazo(dto.prazo());
        novaIdeia.setRoiEsperado(0f);
        novaIdeia.setInvestimento(0f);
        novaIdeia.setRetorno(0f);
        novaIdeia.setObservacaoProgresso("");
        novaIdeia.setMarcos(null);
        novaIdeia.setPrioridade("Baixa");
        novaIdeia.setStatus("Enviada");
        novaIdeia.setResponsavel("");

        repository.save(novaIdeia);
        return ResponseEntity.status(201).body("Ideia submetida com sucesso!");
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> atualizarIdeia(
            @PathVariable String id,
            @RequestBody AtualizarIdeiaRequestDTO dto) {
        Ideia ideia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ideia não encontrada"));

        if (dto.status() != null) {
            ideia.setStatus(dto.status());
        }

        if (dto.responsavel() != null) {
            ideia.setResponsavel(dto.responsavel());
        }

        if (dto.prioridade() != null) {
            ideia.setPrioridade(dto.prioridade());
        }

        if (dto.isStrategicBonus() != null) {
            ideia.setStrategicBonus(dto.isStrategicBonus());
        }

        if (dto.prazo() != null) {
            ideia.setPrazo(dto.prazo());
        }

        if (dto.investimento() != null) {
            ideia.setInvestimento(dto.investimento());
        }

        if (dto.retorno() != null) {
            ideia.setRetorno(dto.retorno());
        }

        if (dto.roiEsperado() != null) {
            ideia.setRoiEsperado(dto.roiEsperado());
        }

        repository.save(ideia);

        return ResponseEntity.ok("Ideia atualizada com sucesso!");
    }

    @PostMapping("/{id}/marcos")
    public ResponseEntity<String> adicionarMarco(
            @PathVariable String id,
            @RequestBody MarcoProjetoDTO dto) {
        Ideia ideia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ideia não encontrada"));

        if (ideia.getMarcos() == null) {
            ideia.setMarcos(new ArrayList<>());
        }

        MarcoProjeto marco = new MarcoProjeto();
        marco.setTitulo(dto.titulo());
        marco.setCompleto(false);
        marco.setDataCompleto("");
        marco.setIdeia(ideia);

        ideia.getMarcos().add(marco);

        repository.save(ideia);

        return ResponseEntity.ok("Marco adicionado!");
    }

    @PatchMapping("/{id}/marcos/{marcoId}")
    public ResponseEntity<String> atualizarMarco(
            @PathVariable String id,
            @PathVariable int marcoId,
            @RequestBody AtualizarMarcoRequestDTO dto) {

        Ideia ideia = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ideia não encontrada"));

        for (MarcoProjeto m : ideia.getMarcos()) {
            if (m.getId() == marcoId) {
                m.setCompleto(!m.isCompleto());

                if (m.isCompleto()) {
                    m.setDataCompleto(
                            java.time.LocalDate.now()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } else {
                    m.setDataCompleto("");
                }
            }
        }

        ideia.setObservacaoProgresso(dto.observacao());

        repository.save(ideia);

        return ResponseEntity.ok("Marco atualizado!");
    }
}