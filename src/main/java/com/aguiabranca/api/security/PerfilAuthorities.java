package com.aguiabranca.api.security;

import com.aguiabranca.api.model.enums.TipoPerfil;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

/**
 * Role hierarchy expanded here, from the single {@code perfil} claim, rather than baked into the
 * token or configured as a Spring Security {@code RoleHierarchy} bean - for method security a
 * {@code RoleHierarchy} also needs a {@code static} {@code MethodSecurityExpressionHandler} bean,
 * and a non-static one silently yields a handler without the hierarchy.
 */
public final class PerfilAuthorities {

    private PerfilAuthorities() {}

    public static List<GrantedAuthority> authorities(TipoPerfil perfil) {
        return switch (perfil) {
            case OPERADOR -> List.of(autoridade("ROLE_OPERADOR"));
            case GESTOR -> List.of(autoridade("ROLE_GESTOR"), autoridade("ROLE_OPERADOR"));
            case LIDERANCA -> List.of(autoridade("ROLE_LIDERANCA"), autoridade("ROLE_GESTOR"),
                    autoridade("ROLE_OPERADOR"));
        };
    }

    private static GrantedAuthority autoridade(String nome) {
        return new SimpleGrantedAuthority(nome);
    }
}
