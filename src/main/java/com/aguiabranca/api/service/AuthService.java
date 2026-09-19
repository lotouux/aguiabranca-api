package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.LoginResponseDTO;
import com.aguiabranca.api.dto.UsuarioResponseDTO;
import com.aguiabranca.api.exception.NotFoundException;
import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.repository.UsuarioRepository;
import com.aguiabranca.api.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String MENSAGEM_CREDENCIAIS_INVALIDAS = "Matrícula ou senha inválidos.";

    /**
     * A fixed dummy hash compared against on the not-found branch, so a login attempt against an
     * unknown matrícula takes the same time as one against a real matrícula with a wrong senha -
     * otherwise timing tells an attacker which matrículas exist.
     */
    private static final String HASH_FIXO_PARA_TIMING = new BCryptPasswordEncoder()
            .encode("senha-fixa-usada-apenas-para-equalizar-o-tempo-de-resposta");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional(readOnly = true)
    public LoginResponseDTO login(String matricula, String senha) {
        Usuario usuario = usuarioRepository.findByMatricula(matricula).orElse(null);
        String hashParaComparar = usuario != null ? usuario.getSenha() : HASH_FIXO_PARA_TIMING;
        boolean senhaValida = passwordEncoder.matches(senha, hashParaComparar);

        if (usuario == null || !senhaValida) {
            throw new BadCredentialsException(MENSAGEM_CREDENCIAIS_INVALIDAS);
        }

        String token = jwtService.gerarToken(usuario);
        return new LoginResponseDTO(token, usuario.getNome(), usuario.getPerfil().name());
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO me(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado."));
        return UsuarioResponseDTO.from(usuario);
    }
}
