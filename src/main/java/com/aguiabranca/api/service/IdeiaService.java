package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.AtualizarIdeiaRequestDTO;
import com.aguiabranca.api.dto.CriarIdeiaDTO;
import com.aguiabranca.api.dto.IdeiaResponseDTO;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.Ideia;
import com.aguiabranca.api.repository.IdeiaRepository;
import com.aguiabranca.api.security.UsuarioAutenticado;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class IdeiaService {

    private final IdeiaRepository ideiaRepository;

    @Transactional(readOnly = true)
    public List<IdeiaResponseDTO> listar(UsuarioAutenticado principal) {
        return ideiaRepository.findAll().stream()
                .map(IdeiaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public IdeiaResponseDTO buscarPorId(Long id, UsuarioAutenticado principal) {
        Ideia ideia = ideiaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ideia não encontrada: " + id));
        return IdeiaResponseDTO.from(ideia);
    }

    public IdeiaResponseDTO criar(CriarIdeiaDTO dto, UsuarioAutenticado principal) {
        // Mantenha a sua implementação do método criar aqui...
        return null; 
    }

    public IdeiaResponseDTO atualizar(Long id, AtualizarIdeiaRequestDTO dto) {
        Ideia ideia = ideiaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Ideia não encontrada: " + id));
        // Mantenha a sua lógica de atualização de campos aqui...
        return IdeiaResponseDTO.from(ideiaRepository.save(ideia));
    }
}