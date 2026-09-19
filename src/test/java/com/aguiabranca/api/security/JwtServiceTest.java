package com.aguiabranca.api.security;

import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.TipoPerfil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JwtServiceTest {

    private static final String SECRET = Base64.getEncoder()
            .encodeToString("uma-chave-de-teste-com-pelo-menos-256-bits-de-tamanho!!".getBytes());

    private final Instant agora = Instant.parse("2026-01-01T00:00:00Z");
    private final Usuario usuario = new Usuario(7L, "Ana Teste", "OP999",
            "$2a$10$hashIrrelevanteParaEsteTeste", TipoPerfil.GESTOR);

    @Test
    void gerarTokenProduzTresSegmentosEValidarTokenRecuperaOsClaims() {
        JwtService jwtService = jwtService(Clock.fixed(agora, ZoneOffset.UTC));
        String token = jwtService.gerarToken(usuario);

        assertThat(token.split("\\.")).hasSize(3);

        UsuarioAutenticado autenticado = jwtService.validarToken(token);
        assertThat(autenticado.id()).isEqualTo(7L);
        assertThat(autenticado.matricula()).isEqualTo("OP999");
        assertThat(autenticado.nome()).isEqualTo("Ana Teste");
        assertThat(autenticado.perfil()).isEqualTo(TipoPerfil.GESTOR);
    }

    @Test
    void tokenExpiraApos2Horas() {
        JwtService aoEmitir = jwtService(Clock.fixed(agora, ZoneOffset.UTC));
        String token = aoEmitir.gerarToken(usuario);

        JwtService duasHorasEUmSegundoDepois = jwtService(
                Clock.fixed(agora.plus(Duration.ofHours(2)).plusSeconds(1), ZoneOffset.UTC));

        assertThatThrownBy(() -> duasHorasEUmSegundoDepois.validarToken(token))
                .isInstanceOf(ExpiredJwtException.class);
    }

    @Test
    void tokenAssinadoComOutraChaveEInvalido() {
        JwtService emissor = jwtService(Clock.fixed(agora, ZoneOffset.UTC));
        String token = emissor.gerarToken(usuario);

        JwtProperties outraChave = new JwtProperties();
        outraChave.setIssuer("aguiabranca-api");
        outraChave.setSecret(Base64.getEncoder()
                .encodeToString("outra-chave-completamente-diferente-de-256-bits!!".getBytes()));
        JwtService validador = new JwtService(outraChave, Clock.fixed(agora, ZoneOffset.UTC));

        assertThatThrownBy(() -> validador.validarToken(token)).isInstanceOf(SignatureException.class);
    }

    @Test
    void chaveAusenteFalhaAoConstruirOServico() {
        JwtProperties semSegredo = new JwtProperties();
        semSegredo.setIssuer("aguiabranca-api");

        assertThatThrownBy(() -> new JwtService(semSegredo, Clock.fixed(agora, ZoneOffset.UTC)))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("app.jwt.secret");
    }

    private JwtService jwtService(Clock clock) {
        JwtProperties properties = new JwtProperties();
        properties.setIssuer("aguiabranca-api");
        properties.setSecret(SECRET);
        return new JwtService(properties, clock);
    }
}
