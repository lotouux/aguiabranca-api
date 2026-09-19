package com.aguiabranca.api.security;

import com.aguiabranca.api.dto.ErroResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Clock;
import java.time.Instant;

/**
 * Filter-chain denials (no controller reached at all) never go through {@code
 * GlobalExceptionHandler} - this serializes the same {@link ErroResponseDTO} shape via the
 * injected {@link ObjectMapper} so the two paths agree.
 */
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private final Clock clock;

    public RestAuthenticationEntryPoint(ObjectMapper objectMapper, Clock clock) {
        this.objectMapper = objectMapper;
        this.clock = clock;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        String codigoErro = (String) request.getAttribute(JwtAuthenticationFilter.ATRIBUTO_CODIGO_ERRO);
        if (codigoErro == null) {
            codigoErro = "NAO_AUTENTICADO";
        }

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                ErroResponseDTO.of(Instant.now(clock), HttpServletResponse.SC_UNAUTHORIZED, codigoErro,
                        "Autenticação necessária.", request.getRequestURI()));
    }
}
