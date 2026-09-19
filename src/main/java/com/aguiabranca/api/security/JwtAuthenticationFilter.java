package com.aguiabranca.api.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Claims (including {@code perfil}) are trusted for the life of the token and never re-checked
 * against the database - so for up to 2 hours after issuance, a deleted user keeps access, a
 * demoted Manager keeps {@code ROLE_GESTOR}, and a password change does not invalidate
 * outstanding tokens. Every {@code /api/usuarios} guard is only eventually consistent.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_CODIGO_ERRO = "jwtErro";

    private static final String PREFIXO_BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header == null || header.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        if (!header.startsWith(PREFIXO_BEARER) || header.length() == PREFIXO_BEARER.length()) {
            request.setAttribute(ATRIBUTO_CODIGO_ERRO, "TOKEN_AUSENTE");
            filterChain.doFilter(request, response);
            return;
        }

        autenticar(request, header.substring(PREFIXO_BEARER.length()));
        filterChain.doFilter(request, response);
    }

    private void autenticar(HttpServletRequest request, String token) {
        try {
            UsuarioAutenticado usuario = jwtService.validarToken(token);
            var authorities = PerfilAuthorities.authorities(usuario.perfil());
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            request.setAttribute(ATRIBUTO_CODIGO_ERRO, "TOKEN_EXPIRADO");
        } catch (JwtException | IllegalArgumentException e) {
            request.setAttribute(ATRIBUTO_CODIGO_ERRO, "TOKEN_INVALIDO");
        }
    }
}
