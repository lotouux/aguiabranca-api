package com.aguiabranca.api.repository;

import com.aguiabranca.api.config.ClockConfig;
import com.aguiabranca.api.model.Ideia;
import com.aguiabranca.api.model.Projeto;
import com.aguiabranca.api.model.Usuario;
import com.aguiabranca.api.model.enums.StatusIdeia;
import com.aguiabranca.api.model.enums.StatusProjeto;
import com.aguiabranca.api.model.enums.TipoPerfil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Inserts via the repositories directly, bypassing {@code ProjetoService}'s pre-check, so this
 * proves the database's {@code unique} join column actually exists as a backstop rather than
 * merely being intended. {@link ClockConfig} is imported back for the same reason as in {@link
 * DashboardAgregacaoTest}.
 */
@DataJpaTest
@Import(ClockConfig.class)
class ProjetoIdeiaUniqueConstraintTest {

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private IdeiaRepository ideiaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void mesmaIdeiaEmDoisProjetosViolaAConstraintUnicaAoFlush() {
        Ideia ideia = criarIdeiaAprovada();

        Projeto projetoA = novoProjeto("Projeto A");
        projetoA.getIdeias().add(ideia);
        projetoRepository.save(projetoA);
        projetoRepository.flush();

        Projeto projetoB = novoProjeto("Projeto B");
        projetoB.getIdeias().add(ideia);
        projetoRepository.save(projetoB);

        assertThatThrownBy(projetoRepository::flush).isInstanceOf(DataIntegrityViolationException.class);
    }

    private Ideia criarIdeiaAprovada() {
        Usuario autor = usuarioRepository.save(new Usuario(null, "Autor de Teste", "TST001", "hash",
                TipoPerfil.OPERADOR));
        Ideia ideia = new Ideia();
        ideia.setAutor(autor);
        ideia.setTitulo("Ideia de teste");
        ideia.setDescricao("desc");
        ideia.setArea("Logística");
        ideia.setStatus(StatusIdeia.APROVADA);
        return ideiaRepository.save(ideia);
    }

    private Projeto novoProjeto(String titulo) {
        Projeto projeto = new Projeto();
        projeto.setTitulo(titulo);
        projeto.setDescricao("desc");
        projeto.setArea("Logística");
        projeto.setStatus(StatusProjeto.PLANEJADO);
        return projeto;
    }
}
