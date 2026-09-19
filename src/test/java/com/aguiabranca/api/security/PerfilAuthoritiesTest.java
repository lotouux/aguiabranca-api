package com.aguiabranca.api.security;

import com.aguiabranca.api.model.enums.TipoPerfil;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PerfilAuthoritiesTest {

    @Test
    void operadorSoTemAAutoridadeDeOperador() {
        assertThat(nomes(TipoPerfil.OPERADOR)).containsExactlyInAnyOrder("ROLE_OPERADOR");
    }

    @Test
    void gestorHerdaAutoridadeDeOperador() {
        assertThat(nomes(TipoPerfil.GESTOR)).containsExactlyInAnyOrder("ROLE_GESTOR", "ROLE_OPERADOR");
    }

    @Test
    void liderancaHerdaAutoridadesDeGestorEOperador() {
        assertThat(nomes(TipoPerfil.LIDERANCA))
                .containsExactlyInAnyOrder("ROLE_LIDERANCA", "ROLE_GESTOR", "ROLE_OPERADOR");
    }

    private List<String> nomes(TipoPerfil perfil) {
        return PerfilAuthorities.authorities(perfil).stream().map(GrantedAuthority::getAuthority).toList();
    }
}
