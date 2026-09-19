package com.aguiabranca.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * A {@code @PreAuthorize} denial is thrown by the AOP interceptor inside {@code
 * DispatcherServlet}, before {@code ExceptionTranslationFilter} would normally see it - without a
 * dedicated {@code AccessDeniedException} handler in {@code GlobalExceptionHandler}, every one of
 * these 403s becomes a 500 instead. This grid is the regression for that, across the whole
 * permission matrix.
 */
class AutorizacaoPorPerfilTest extends AbstractApiIntegrationTest {

    static Stream<Arguments> casos() {
        return Stream.of(
                Arguments.of("OP001", "GET", "/api/ideias", 200),
                Arguments.of("GS001", "GET", "/api/ideias", 200),
                Arguments.of("LD001", "GET", "/api/ideias", 200),

                Arguments.of("OP001", "GET", "/api/projetos", 403),
                Arguments.of("GS001", "GET", "/api/projetos", 200),
                Arguments.of("LD001", "GET", "/api/projetos", 200),

                Arguments.of("OP001", "GET", "/api/dashboard", 403),
                Arguments.of("GS001", "GET", "/api/dashboard", 403),
                Arguments.of("LD001", "GET", "/api/dashboard", 200),

                Arguments.of("OP001", "GET", "/api/usuarios", 403),
                Arguments.of("GS001", "GET", "/api/usuarios", 403),
                Arguments.of("LD001", "GET", "/api/usuarios", 200),

                Arguments.of("OP001", "GET", "/api/estrategia/focos", 200),
                Arguments.of("GS001", "GET", "/api/estrategia/focos", 200),
                Arguments.of("LD001", "GET", "/api/estrategia/focos", 200),

                Arguments.of("OP001", "POST", "/api/projetos", 403),
                Arguments.of("GS001", "POST", "/api/projetos", 201),
                Arguments.of("LD001", "POST", "/api/projetos", 201),

                Arguments.of("OP001", "POST", "/api/usuarios", 403),
                Arguments.of("GS001", "POST", "/api/usuarios", 403)
        );
    }

    private static final String CORPO_PROJETO = """
            {"titulo":"Projeto de teste","descricao":"desc","area":"Logística"}""";
    private static final String CORPO_USUARIO = """
            {"nome":"Teste","matricula":"ZZZ999","senha":"123","perfil":"OPERADOR"}""";

    @ParameterizedTest
    @MethodSource("casos")
    void matrizDePermissoes(String matricula, String metodo, String caminho, int statusEsperado) throws Exception {
        MockHttpServletRequestBuilder requisicao = switch (metodo) {
            case "GET" -> get(caminho);
            case "POST" -> post(caminho).contentType(MediaType.APPLICATION_JSON).content(corpoPara(caminho));
            default -> throw new IllegalArgumentException("Método não suportado no grid: " + metodo);
        };
        requisicao.header("Authorization", bearer(matricula));

        int statusObtido = mockMvc.perform(requisicao).andReturn().getResponse().getStatus();

        assertThat(statusObtido).isEqualTo(statusEsperado);
    }

    private String corpoPara(String caminho) {
        return caminho.equals("/api/usuarios") ? CORPO_USUARIO : CORPO_PROJETO;
    }

    @Test
    void nenhumaNegacaoRetorna500() throws Exception {
        int status = mockMvc.perform(get("/api/dashboard").header("Authorization", bearer("OP001")))
                .andReturn().getResponse().getStatus();

        assertThat(status).isEqualTo(403).isNotEqualTo(500);
    }

    @Test
    void escritaEmFocosEstrategicosSoParaLideranca() throws Exception {
        String corpoValido = """
                {"mes":"Ago","titulo":"Foco de teste","descricao":"desc","areasPotenciais":["Logística"]}""";

        mockMvc.perform(post("/api/estrategia/focos")
                        .header("Authorization", bearer("OP001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoValido))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/estrategia/focos")
                        .header("Authorization", bearer("GS001"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corpoValido))
                .andExpect(status().isForbidden());
    }
}
