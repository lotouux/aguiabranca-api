package com.aguiabranca.api;

import com.aguiabranca.api.repository.FocoEstrategicoRepository;
import com.aguiabranca.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class DataSeederTest extends AbstractApiIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FocoEstrategicoRepository focoEstrategicoRepository;

    @Test
    void senhasSeedadasSaoHashesBcryptNaoTextoPlano() {
        usuarioRepository.findAll().forEach(usuario -> assertThat(usuario.getSenha()).startsWith("$2"));
    }

    @Test
    void loginComASenhaOriginalAindaFunciona() throws Exception {
        token("OP001");
        token("GS001");
        token("LD001");
    }

    @Test
    void noMaximoUmFocoEstrategicoEstaAtivo() {
        assertThat(focoEstrategicoRepository.countByAtivoTrue()).isLessThanOrEqualTo(1);
    }

    @Test
    void matriculasSeedadasSaoUnicas() {
        var matriculas = usuarioRepository.findAll().stream().map(u -> u.getMatricula()).distinct().toList();
        assertThat(matriculas).hasSameSizeAs(usuarioRepository.findAll());
    }
}
