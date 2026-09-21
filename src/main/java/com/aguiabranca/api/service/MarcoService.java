package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.AtualizarMarcoDTO;
import com.aguiabranca.api.dto.CriarMarcoDTO;
import com.aguiabranca.api.dto.MarcoDTO;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.Ideia;
import com.aguiabranca.api.model.Marco;
import com.aguiabranca.api.repository.IdeiaRepository;
import com.aguiabranca.api.repository.MarcoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MarcoService {

    private final MarcoRepository marcoRepository;
    private final IdeiaRepository ideiaRepository;

    @Transactional(readOnly = true)
    public List<MarcoDTO> listar(String ideiaId) {
        buscarIdeiaOuFalhar(ideiaId);
        return marcoRepository.findByIdeiaId(ideiaId).stream()
                .map(MarcoDTO::from)
                .toList();
    }

    public MarcoDTO criar(String ideiaId, CriarMarcoDTO dto) {
        Ideia ideia = buscarIdeiaOuFalhar(ideiaId);

        Marco marco = new Marco();
        marco.setTitulo(dto.titulo());
        marco.setObservacao(dto.observacao());
        marco.setCompleto(false);
        marco.setIdeia(ideia);

        return MarcoDTO.from(marcoRepository.save(marco));
    }

    public MarcoDTO atualizar(String ideiaId, String marcoId, AtualizarMarcoDTO dto) {
        buscarIdeiaOuFalhar(ideiaId);
        Marco marco = marcoRepository.findById(marcoId)
                .orElseThrow(() -> new NotFoundException("Marco não encontrado: " + marcoId));

        if (!marco.getIdeia().getId().equals(ideiaId)) {
            throw new NotFoundException("Marco não pertence a esta ideia.");
        }
        if (dto.titulo() != null) {
            marco.setTitulo(dto.titulo());
        }
        if (dto.isCompleto() != null) {
            marco.setCompleto(dto.isCompleto());
        }
        if (dto.observacao() != null) {
            marco.setObservacao(dto.observacao());
        }

        return MarcoDTO.from(marcoRepository.save(marco));
    }

    private Ideia buscarIdeiaOuFalhar(String ideiaId) {
        return ideiaRepository.findById(ideiaId)
                .orElseThrow(() -> new NotFoundException("Ideia não encontrada: " + ideiaId));
    }
}
