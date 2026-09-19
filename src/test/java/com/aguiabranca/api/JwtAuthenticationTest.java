package com.aguiabranca.api;

import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.TipoPerfil;
import com.aguiabranca.api.security.JwtProperties;
import com.aguiabranca.api.security.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class JwtAuthenticationTest extends AbstractApiIntegrationTest {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private Clock clock;

    @Test
    void semTokenRetorna401NoFormatoPadrao() throws Exception {
        mockMvc.perform(get("/api/ideias"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.erro").value("NAO_AUTENTICADO"))
                .andExpect(jsonPath("$.path").value("/api/ideias"));
    }

    @Test
    void tokenExpiradoRetorna401ComCodigoTokenExpirado() throws Exception {
        Instant passado = clock.instant().minus(Duration.ofHours(3));
        JwtService jwtServiceNoPassado = new JwtService(jwtProperties, Clock.fixed(passado, ZoneOffset.UTC));
        String tokenExpirado = jwtServiceNoPassado.gerarToken(usuarioFicticio());

        mockMvc.perform(get("/api/ideias").header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("TOKEN_EXPIRADO"));
    }

    @Test
    void tokenAdulteradoRetorna401ComCodigoTokenInvalido() throws Exception {
        String token = token("OP001");
        String adulterado = token.substring(0, token.length() - 4) + "aaaa";

        mockMvc.perform(get("/api/ideias").header("Authorization", "Bearer " + adulterado))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("TOKEN_INVALIDO"));
    }

    @Test
    void cabecalhoSemEsquemaBearerRetorna401ComCodigoTokenAusente() throws Exception {
        mockMvc.perform(get("/api/ideias").header("Authorization", "Basic algumacoisa"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.erro").value("TOKEN_AUSENTE"));
    }

    @Test
    void negacaoDeAcessoEDeAutenticacaoUsamOMesmoFormatoDeCorpo() throws Exception {
        mockMvc.perform(get("/api/dashboard").header("Authorization", bearer("GS001")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.erro").value("ACESSO_NEGADO"))
                .andExpect(jsonPath("$.path").value("/api/dashboard"));
    }

    private Usuario usuarioFicticio() {
        return new Usuario(1L, "Pedro Miranda", "OP001", "hash-irrelevante", TipoPerfil.OPERADOR);
    }
}
