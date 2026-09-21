package com.aguiabranca.api.security;

import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.TipoPerfil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final Duration EXPIRACAO = Duration.ofHours(2);

    private static final String CLAIM_MATRICULA = "matricula";
    private static final String CLAIM_NOME = "nome";
    private static final String CLAIM_PERFIL = "perfil";

    private final JwtProperties properties;
    private final Clock clock;
    private final SecretKey chave;

    public JwtService(JwtProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
        this.chave = construirChave(properties.getSecret());
    }

    private SecretKey construirChave(String secretBase64) {
        if (secretBase64 == null || secretBase64.isBlank() || secretBase64.startsWith("${")) {
            throw new IllegalStateException(
                    "app.jwt.secret não está configurado. Defina a variável de ambiente JWT_SECRET.");
        }
        try {
            byte[] bytes = Decoders.BASE64.decode(secretBase64);
            return Keys.hmacShaKeyFor(bytes);
        } catch (WeakKeyException e) {
            throw new IllegalStateException(
                    "app.jwt.secret precisa decodificar (Base64) para pelo menos 256 bits.", e);
        } catch (io.jsonwebtoken.io.DecodingException e) {
            throw new IllegalStateException("app.jwt.secret precisa ser um valor Base64 válido.", e);
        }
    }

    public String gerarToken(Usuario usuario) {
        Instant agora = clock.instant();
        return Jwts.builder()
                .issuer(properties.getIssuer())
                .subject(String.valueOf(usuario.getId()))
                .claim(CLAIM_MATRICULA, usuario.getMatricula())
                .claim(CLAIM_NOME, usuario.getNome())
                .claim(CLAIM_PERFIL, usuario.getPerfil().name())
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(EXPIRACAO)))
                .signWith(chave, Jwts.SIG.HS256)
                .compact();
    }

    public UsuarioAutenticado validarToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(chave)
                .requireIssuer(properties.getIssuer())
                .clock(() -> Date.from(clock.instant()))
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String perfil = claims.get(CLAIM_PERFIL, String.class);
        if (perfil == null) {
            // Enum.valueOf(null) throws NullPointerException, which JwtAuthenticationFilter does
            // not catch - a validly-signed token missing this claim must still fail as a JwtException
            // (401), not surface as an uncaught 500.
            throw new MalformedJwtException("Claim '" + CLAIM_PERFIL + "' ausente no token.");
        }

        return new UsuarioAutenticado(
                claims.getSubject(),
                claims.get(CLAIM_MATRICULA, String.class),
                claims.get(CLAIM_NOME, String.class),
                TipoPerfil.valueOf(perfil));
    }
}
