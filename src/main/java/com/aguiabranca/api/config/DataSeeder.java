package com.aguiabranca.api.config;

import com.aguiabranca.api.model.*;
import com.aguiabranca.api.model.enums.*;
import com.aguiabranca.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final FocoEstrategicoRepository focoRepository;
    private final IdeiaRepository ideiaRepository;
    private final ProjetoRepository projetoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsuarios();
        seedFocos();
        seedIdeias();
        seedProjetos();
    }

    private void seedUsuarios() {
        if (usuarioRepository.count() > 0) {
            return;
        }
        String senha = passwordEncoder.encode("123");
        usuarioRepository.save(new Usuario(1L, "Pedro Miranda", "OP001", senha, TipoPerfil.OPERADOR));
        usuarioRepository.save(new Usuario(2L, "Leonardo Martin", "GS001", senha, TipoPerfil.GESTOR));
        usuarioRepository.save(new Usuario(3L, "Beatriz Camargo", "LD001", senha, TipoPerfil.LIDERANCA));
        log.info("Usuários criados");
    }

    private void seedFocos() {
        if (focoRepository.count() > 0) {
            return;
        }
        focoRepository.save(new FocoEstrategico(1L, "Jun", "Redução de Emissões",
                "Foco em ideias que reduzam a pegada de carbono da frota em 15%.",
                List.of("Logística", "Operação"),
                true));
        focoRepository.save(new FocoEstrategico(2L, "Jul", "Eficiência em Logística",
                "Otimizar processos de carga e descarga para reduzir tempo em 20%.",
                List.of("Logística"), false));
        log.info("Focos Estratégicos criados");
    }

    private void seedIdeias() {
        if (ideiaRepository.count() > 0) {
            return;
        }
        Usuario autor = usuarioRepository.findByMatricula("OP001").orElseThrow();

        ideiaRepository.save(novaIdeia("1", autor, "Sistema de Roteirização Inteligente",
                "Otimização de rotas via IA.", StatusIdeia.CONCLUIDA, "Logística", false,
                Nivel.ALTO, Nivel.ALTO, Prioridade.ALTA, LocalDate.of(2026, 6, 29),
                new BigDecimal("150000.00"), new BigDecimal("450000.00")));

        ideiaRepository.save(novaIdeia("2", autor, "App de Check-in Rápido",
                "Implementar IA para otimizar rotas de entregas, reduzindo tempo e combustível.",
                StatusIdeia.EM_EXECUCAO, "Logística", true,
                Nivel.ALTO, Nivel.MEDIO, Prioridade.ALTA, LocalDate.of(2026, 7, 5),
                new BigDecimal("150000.00"), new BigDecimal("420000.00")));

        ideiaRepository.save(novaIdeia("3", autor, "Programa de Fidelidade B2B",
                "Benefícios para clientes de carga regulares.",
                StatusIdeia.EM_EXECUCAO, "Comércio", false,
                Nivel.MEDIO, Nivel.BAIXO, Prioridade.MEDIA, LocalDate.of(2027, 12, 30),
                null, null));

        ideiaRepository.save(novaIdeia("4", autor, "Monitoramento de Pneus IoT",
                "Sensores para monitorar pressão e temperatura dos pneus em tempo real.",
                StatusIdeia.ENVIADA, "Logística", false,
                null, null, null, null, null, null));

        ideiaRepository.save(novaIdeia("5", autor, "Sistema de Feedback Automatizado",
                "Coleta automática de feedback pós-viagem com análise de sentimento.",
                StatusIdeia.APROVADA, "Passageiros", false,
                Nivel.ALTO, Nivel.MEDIO, Prioridade.ALTA, LocalDate.of(2026, 6, 20),
                new BigDecimal("85000.00"), new BigDecimal("289000.00")));

        ideiaRepository.save(novaIdeia("6", autor, "Substituição de Frota por Veículos Elétricos",
                "Eletrificação gradual da frota urbana.",
                StatusIdeia.REJEITADA, "Logística", false,
                Nivel.ALTO, Nivel.ALTO, Prioridade.BAIXA, null,
                null, null));

        log.info("Ideias criadas");
    }

    private Ideia novaIdeia(String id, Usuario autor, String titulo, String descricao, StatusIdeia status, String area,
            boolean strategicBonus, Nivel impacto, Nivel esforco, Prioridade prioridade, LocalDate prazo,
            BigDecimal investimento, BigDecimal retorno) {
        Ideia ideia = new Ideia();
        ideia.setId(id);
        ideia.setAutor(autor);
        ideia.setTitulo(titulo);
        ideia.setDescricao(descricao);
        ideia.setStatus(status);
        ideia.setArea(area);
        ideia.setStrategicBonus(strategicBonus);
        ideia.setImpacto(impacto);
        ideia.setEsforco(esforco);
        ideia.setPrioridade(prioridade);
        ideia.setPrazo(prazo);
        ideia.setInvestimento(investimento);
        ideia.setRetorno(retorno);
        return ideia;
    }

    private void seedProjetos() {
        if (projetoRepository.count() > 0) {
            return;
        }
        Usuario responsavel = usuarioRepository.findByMatricula("GS001").orElseThrow();
        Map<String, Ideia> ideiasPorTitulo = ideiaRepository.findAll().stream()
                .collect(Collectors.toMap(Ideia::getTitulo, Function.identity()));

        Projeto roteirizacao = novoProjeto(1L, "Plataforma de Roteirização",
                "Roteirização inteligente das rotas de entrega.", "Logística", responsavel,
                StatusProjeto.CONCLUIDO, LocalDate.of(2026, 1, 5), LocalDate.of(2026, 6, 15),
                new BigDecimal("100000.00"), new BigDecimal("400000.00"), new BigDecimal("350000.00"), 120);
        roteirizacao.getIdeias().add(ideiasPorTitulo.get("Sistema de Roteirização Inteligente"));
        roteirizacao.setTarefas(List.of(
                novaTarefa(1L, "Análise de Requisitos", StatusTarefa.CONCLUIDA, LocalDate.of(2026, 1, 20), roteirizacao),
                novaTarefa(2L, "MVP desenvolvido", StatusTarefa.CONCLUIDA, LocalDate.of(2026, 3, 1), roteirizacao),
                novaTarefa(3L, "Testes piloto", StatusTarefa.CONCLUIDA, LocalDate.of(2026, 4, 15), roteirizacao),
                novaTarefa(4L, "Rollout completo", StatusTarefa.CONCLUIDA, LocalDate.of(2026, 6, 10), roteirizacao)));
        projetoRepository.save(roteirizacao);

        Projeto checkin = novoProjeto(2L, "Check-in Digital",
                "App de check-in rápido para reduzir tempo de espera.", "Logística", responsavel,
                StatusProjeto.EM_ANDAMENTO, LocalDate.of(2026, 2, 1), LocalDate.of(2026, 8, 1),
                new BigDecimal("200000.00"), new BigDecimal("500000.00"), null, 80);
        checkin.getIdeias().add(ideiasPorTitulo.get("App de Check-in Rápido"));
        checkin.setTarefas(List.of(
                novaTarefa(5L, "Planejamento e levantamento de requisitos", StatusTarefa.CONCLUIDA,
                        LocalDate.of(2026, 2, 12), checkin),
                novaTarefa(6L, "Desenvolvimento do backend de rastreamento", StatusTarefa.EM_ANDAMENTO, null, checkin),
                novaTarefa(7L, "Implementação do dashboard mobile", StatusTarefa.PENDENTE, null, checkin),
                novaTarefa(8L, "Testes finais e publicação", StatusTarefa.PENDENTE, null, checkin)));
        projetoRepository.save(checkin);

        Projeto fidelidade = novoProjeto(3L, "Fidelidade B2B",
                "Programa de fidelidade para clientes de carga regulares.", "Comércio", responsavel,
                StatusProjeto.EM_ANDAMENTO, LocalDate.of(2026, 3, 1), LocalDate.of(2026, 12, 1),
                new BigDecimal("0.00"), new BigDecimal("50000.00"), null, 0);
        fidelidade.getIdeias().add(ideiasPorTitulo.get("Programa de Fidelidade B2B"));
        fidelidade.setTarefas(List.of(
                novaTarefa(9L, "Definição de regras de pontuação", StatusTarefa.PENDENTE, null, fidelidade),
                novaTarefa(10L, "Integração com sistema de faturamento", StatusTarefa.PENDENTE, null, fidelidade),
                novaTarefa(11L, "Piloto com clientes-chave", StatusTarefa.PENDENTE, null, fidelidade)));
        projetoRepository.save(fidelidade);

        log.info("Projetos criados");
    }

    private Projeto novoProjeto(Long id, String titulo, String descricao, String area, Usuario responsavel,
            StatusProjeto status, LocalDate dataInicio, LocalDate dataPrevistaConclusao, BigDecimal investimento,
            BigDecimal economiaAnualEstimada, BigDecimal economiaAnualRealizada, Integer horasEconomizadasMes) {
        Projeto projeto = new Projeto();
        projeto.setId(id);
        projeto.setTitulo(titulo);
        projeto.setDescricao(descricao);
        projeto.setArea(area);
        projeto.setResponsavel(responsavel);
        projeto.setStatus(status);
        projeto.setDataInicio(dataInicio);
        projeto.setDataPrevistaConclusao(dataPrevistaConclusao);
        projeto.setInvestimento(investimento);
        projeto.setEconomiaAnualEstimada(economiaAnualEstimada);
        projeto.setEconomiaAnualRealizada(economiaAnualRealizada);
        projeto.setHorasEconomizadasMes(horasEconomizadasMes);
        return projeto;
    }

    private Tarefa novaTarefa(Long id, String titulo, StatusTarefa status, LocalDate dataConclusao, Projeto projeto) {
        Tarefa tarefa = new Tarefa();
        tarefa.setId(id);
        tarefa.setTitulo(titulo);
        tarefa.setStatus(status);
        tarefa.setDataConclusao(dataConclusao);
        tarefa.setProjeto(projeto);
        return tarefa;
    }
}