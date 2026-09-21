package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.AtualizarUsuarioDTO;
import com.aguiabranca.api.dto.CriarUsuarioDTO;
import com.aguiabranca.api.dto.UsuarioResponseDTO;
import com.aguiabranca.api.exception.ConflictException;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.TipoPerfil;
import com.aguiabranca.api.repository.IdeiaRepository;
import com.aguiabranca.api.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final IdeiaRepository ideiaRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {
        return usuarioRepository.findAll().stream().map(UsuarioResponseDTO::from).toList();
    }

    public UsuarioResponseDTO criar(CriarUsuarioDTO dto) {
        garantirMatriculaDisponivel(dto.matricula());

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setMatricula(dto.matricula());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setPerfil(dto.perfil());
        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO atualizar(String id, AtualizarUsuarioDTO dto, String solicitanteId) {
        Usuario usuario = buscarOuFalhar(id);

        if (dto.perfil() != null && dto.perfil() != usuario.getPerfil()) {
            garantirTrocaDePerfilPermitida(usuario, dto.perfil(), solicitanteId);
            usuario.setPerfil(dto.perfil());
        }
        if (dto.nome() != null) {
            usuario.setNome(dto.nome());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }
        return UsuarioResponseDTO.from(usuarioRepository.save(usuario));
    }

    public void deletar(String id, String solicitanteId) {
        Usuario usuario = buscarOuFalhar(id);

        if (usuario.getId().equals(solicitanteId)) {
            throw new ConflictException("Você não pode excluir seu próprio usuário.");
        }
        garantirNaoEUltimoLider(usuario);
        if (!ideiaRepository.findByAutorId(usuario.getId()).isEmpty()) {
            throw new ConflictException("Não é possível excluir um usuário com ideias associadas.");
        }
        usuarioRepository.delete(usuario);
    }

    private void garantirTrocaDePerfilPermitida(Usuario usuario, TipoPerfil perfilDestino, String solicitanteId) {
        if (usuario.getId().equals(solicitanteId)) {
            throw new ConflictException("Você não pode alterar seu próprio perfil.");
        }
        if (perfilDestino != TipoPerfil.LIDERANCA) {
            garantirNaoEUltimoLider(usuario);
        }
    }

    private void garantirNaoEUltimoLider(Usuario usuario) {
        if (usuario.getPerfil() == TipoPerfil.LIDERANCA && usuarioRepository.countByPerfil(TipoPerfil.LIDERANCA) <= 1) {
            throw new ConflictException("Não é possível remover o último usuário com perfil de Liderança.");
        }
    }

    private void garantirMatriculaDisponivel(String matricula) {
        if (usuarioRepository.findByMatricula(matricula).isPresent()) {
            throw new ConflictException("Matrícula já cadastrada.");
        }
    }

    private Usuario buscarOuFalhar(String id) {
        return usuarioRepository.findById(id).orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
    }
}