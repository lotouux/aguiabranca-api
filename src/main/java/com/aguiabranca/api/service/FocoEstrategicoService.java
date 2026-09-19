package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.AtualizarFocoDTO;
import com.aguiabranca.api.dto.CriarFocoDTO;
import com.aguiabranca.api.dto.FocoEstrategicoDTO;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.FocoEstrategico;
import com.aguiabranca.api.repository.FocoEstrategicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FocoEstrategicoService {

    private final FocoEstrategicoRepository focoRepository;

    @Transactional(readOnly = true)
    public List<FocoEstrategicoDTO> listar() {
        return focoRepository.findAll().stream().map(this::toDTO).toList();
    }

    public FocoEstrategicoDTO criar(CriarFocoDTO dto) {
        FocoEstrategico foco = new FocoEstrategico();
        foco.setMes(dto.mes());
        foco.setTitulo(dto.titulo());
        foco.setDescricao(dto.descricao());
        foco.setAreasPotenciais(dto.areasPotenciais());
        foco.setAtivo(false);
        return toDTO(focoRepository.save(foco));
    }

    public FocoEstrategicoDTO atualizar(String id, AtualizarFocoDTO dto) {
        FocoEstrategico foco = buscarOuFalhar(id);
        foco.setMes(dto.mes());
        foco.setTitulo(dto.titulo());
        foco.setDescricao(dto.descricao());
        foco.setAreasPotenciais(dto.areasPotenciais());
        return toDTO(focoRepository.save(foco));
    }

    public FocoEstrategicoDTO ativar(String id) {
        buscarOuFalhar(id);
        focoRepository.desativarTodos();
        focoRepository.ativarPorId(id);
        return toDTO(buscarOuFalhar(id));
    }

    public void deletar(String id) {
        buscarOuFalhar(id);
        focoRepository.deleteById(id);
    }

    private FocoEstrategico buscarOuFalhar(String id) {
        return focoRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Foco estratégico não encontrado."));
    }

    private FocoEstrategicoDTO toDTO(FocoEstrategico foco) {
        return new FocoEstrategicoDTO(foco.getId(), foco.getMes(), foco.getTitulo(), foco.getDescricao(),
                foco.getAreasPotenciais(), foco.isAtivo());
    }
}
