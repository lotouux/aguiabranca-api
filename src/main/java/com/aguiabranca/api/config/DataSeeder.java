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
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final UsuarioRepository usuarioRepository;
    private final FocoEstrategicoRepository focoRepository;
    private final IdeiaRepository ideiaRepository;
    private final ProjetoRepository projetoRepository;
    private final TarefaRepository tarefaRepository;
    private final PasswordEncoder passwordEncoder;

    private void limparBanco() {
        projetoRepository.deleteAll();
        tarefaRepository.deleteAll();
        ideiaRepository.deleteAll();
        focoRepository.deleteAll();
        usuarioRepository.deleteAll();
        log.info("Banco limpo para execução do seeder");
    }

    @Override
    public void run(String... args) {
        seedUsuarios();
        seedFocos();
        seedIdeias();
        seedProjetos();
    }

    private void seedUsuarios() {
        String senha = passwordEncoder.encode("123");

        criarUsuarioSeNaoExistir(
                "OP001",
                "Pedro Miranda",
                senha,
                TipoPerfil.OPERADOR
        );

        criarUsuarioSeNaoExistir(
                "GS001",
                "Leonardo Martin",
                senha,
                TipoPerfil.GESTOR
        );

        criarUsuarioSeNaoExistir(
                "LD001",
                "Beatriz Camargo",
                senha,
                TipoPerfil.LIDERANCA
        );
    }

    private void criarUsuarioSeNaoExistir(
            String matricula,
            String nome,
            String senha,
            TipoPerfil perfil
    ) {
        if (usuarioRepository.findByMatricula(matricula).isPresent()) {
            log.info("Usuário {} já existe", matricula);
            return;
        }

        usuarioRepository.save(
                new Usuario(null, nome, matricula, senha, perfil)
        );

        log.info("Usuário {} criado", matricula);
    }

    private void seedFocos() {

        criarFocoSeNaoExistir(
                "Jun",
                "Redução de Emissões",
                "Foco em ideias que reduzam a pegada de carbono da frota em 15%.",
                List.of("Logística", "Operação"),
                true
        );

        criarFocoSeNaoExistir(
                "Jul",
                "Eficiência em Logística",
                "Otimizar processos de carga e descarga para reduzir tempo em 20%.",
                List.of("Logística"),
                false
        );
    }

    private void criarFocoSeNaoExistir(
            String mes,
            String titulo,
            String descricao,
            List<String> areas,
            boolean ativo
    ) {
        if (focoRepository.findByMes(mes).isPresent()) {
            log.info("Foco {} já existe", mes);
            return;
        }

        focoRepository.save(new FocoEstrategico(
                null,
                mes,
                titulo,
                descricao,
                areas,
                ativo
        ));

        log.info("Foco {} criado", mes);
    }

    private void seedIdeias() {

        Usuario autor = usuarioRepository
                .findByMatricula("OP001")
                .orElseThrow();

        criarIdeiaSeNaoExistir(
                autor,
                "Sistema de Roteirização Inteligente",
                "Otimização de rotas via IA.",
                StatusIdeia.CONCLUIDA,
                "Logística",
                false,
                Nivel.ALTO,
                Nivel.ALTO,
                Prioridade.ALTA,
                LocalDate.of(2026, 6, 29),
                new BigDecimal("150000.00"),
                new BigDecimal("450000.00")
        );

        criarIdeiaSeNaoExistir(
                autor,
                "App de Check-in Rápido",
                "Implementar IA para otimizar rotas de entregas, reduzindo tempo e combustível.",
                StatusIdeia.EM_EXECUCAO,
                "Logística",
                true,
                Nivel.ALTO,
                Nivel.MEDIO,
                Prioridade.ALTA,
                LocalDate.of(2026, 7, 5),
                new BigDecimal("150000.00"),
                new BigDecimal("420000.00")
        );

        criarIdeiaSeNaoExistir(
                autor,
                "Programa de Fidelidade B2B",
                "Benefícios para clientes de carga regulares.",
                StatusIdeia.EM_EXECUCAO,
                "Comércio",
                false,
                Nivel.MEDIO,
                Nivel.BAIXO,
                Prioridade.MEDIA,
                LocalDate.of(2027, 12, 30),
                null,
                null
        );

        criarIdeiaSeNaoExistir(
                autor,
                "Monitoramento de Pneus IoT",
                "Sensores para monitorar pressão e temperatura dos pneus em tempo real.",
                StatusIdeia.ENVIADA,
                "Logística",
                false,
                Nivel.ALTO,
                Nivel.ALTO,
                Prioridade.MEDIA,
                null,
                null,
                null
        );

        criarIdeiaSeNaoExistir(
                autor,
                "Sistema de Feedback Automatizado",
                "Coleta automática de feedback pós-viagem com análise de sentimento.",
                StatusIdeia.APROVADA,
                "Passageiros",
                false,
                Nivel.ALTO,
                Nivel.MEDIO,
                Prioridade.ALTA,
                LocalDate.of(2026, 6, 20),
                new BigDecimal("85000.00"),
                new BigDecimal("289000.00")
        );

        criarIdeiaSeNaoExistir(
                autor,
                "Substituição de Frota por Veículos Elétricos",
                "Eletrificação gradual da frota urbana.",
                StatusIdeia.REJEITADA,
                "Logística",
                false,
                Nivel.ALTO,
                Nivel.ALTO,
                Prioridade.BAIXA,
                null,
                null,
                null
        );
    }

    private Ideia criarIdeiaSeNaoExistir(
            Usuario autor,
            String titulo,
            String descricao,
            StatusIdeia status,
            String area,
            boolean strategicBonus,
            Nivel impacto,
            Nivel esforco,
            Prioridade prioridade,
            LocalDate prazo,
            BigDecimal investimento,
            BigDecimal retorno
    ) {
        return ideiaRepository.findByTitulo(titulo)
                .orElseGet(() -> {
                    Ideia ideia = novaIdeia(
                            autor,
                            titulo,
                            descricao,
                            status,
                            area,
                            strategicBonus,
                            impacto,
                            esforco,
                            prioridade,
                            prazo,
                            investimento,
                            retorno
                    );

                    Ideia salva = ideiaRepository.save(ideia);

                    log.info(
                            "Ideia '{}' criada | ID: {}",
                            salva.getTitulo(),
                            salva.getId()
                    );

                    return salva;
                });
    }

    private Ideia novaIdeia(
            Usuario autor,
            String titulo,
            String descricao,
            StatusIdeia status,
            String area,
            boolean strategicBonus,
            Nivel impacto,
            Nivel esforco,
            Prioridade prioridade,
            LocalDate prazo,
            BigDecimal investimento,
            BigDecimal retorno
    ) {
        Ideia ideia = new Ideia();

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

        Usuario responsavel = usuarioRepository
                .findByMatricula("GS001")
                .orElseThrow();

        Ideia roteirizacaoIdeia = ideiaRepository
                .findByTitulo("Sistema de Roteirização Inteligente")
                .orElseThrow();

        Ideia checkinIdeia = ideiaRepository
                .findByTitulo("App de Check-in Rápido")
                .orElseThrow();

        Ideia fidelidadeIdeia = ideiaRepository
                .findByTitulo("Programa de Fidelidade B2B")
                .orElseThrow();

        Projeto roteirizacao = criarProjetoSeNaoExistir(
                "Plataforma de Roteirização",
                "Roteirização inteligente das rotas de entrega.",
                "Logística",
                responsavel,
                StatusProjeto.CONCLUIDO,
                LocalDate.of(2026, 1, 5),
                LocalDate.of(2026, 6, 15),
                new BigDecimal("100000.00"),
                new BigDecimal("400000.00"),
                new BigDecimal("350000.00"),
                120
        );

        adicionarIdeiaSeNaoExistir(roteirizacao, roteirizacaoIdeia);

        adicionarTarefaSeNaoExistir(
                roteirizacao,
                "Análise de Requisitos",
                StatusTarefa.CONCLUIDA,
                LocalDate.of(2026, 1, 20)
        );

        adicionarTarefaSeNaoExistir(
                roteirizacao,
                "MVP desenvolvido",
                StatusTarefa.CONCLUIDA,
                LocalDate.of(2026, 3, 1)
        );

        adicionarTarefaSeNaoExistir(
                roteirizacao,
                "Testes piloto",
                StatusTarefa.CONCLUIDA,
                LocalDate.of(2026, 4, 15)
        );

        adicionarTarefaSeNaoExistir(
                roteirizacao,
                "Rollout completo",
                StatusTarefa.CONCLUIDA,
                LocalDate.of(2026, 6, 10)
        );

        projetoRepository.save(roteirizacao);

        Projeto checkin = criarProjetoSeNaoExistir(
                "Check-in Digital",
                "App de check-in rápido para reduzir tempo de espera.",
                "Logística",
                responsavel,
                StatusProjeto.EM_ANDAMENTO,
                LocalDate.of(2026, 2, 1),
                LocalDate.of(2026, 8, 1),
                new BigDecimal("200000.00"),
                new BigDecimal("500000.00"),
                null,
                80
        );

        adicionarIdeiaSeNaoExistir(checkin, checkinIdeia);

        adicionarTarefaSeNaoExistir(
                checkin,
                "Planejamento e levantamento de requisitos",
                StatusTarefa.CONCLUIDA,
                LocalDate.of(2026, 2, 12)
        );

        adicionarTarefaSeNaoExistir(
                checkin,
                "Desenvolvimento do backend de rastreamento",
                StatusTarefa.EM_ANDAMENTO,
                null
        );

        adicionarTarefaSeNaoExistir(
                checkin,
                "Implementação do dashboard mobile",
                StatusTarefa.PENDENTE,
                null
        );

        adicionarTarefaSeNaoExistir(
                checkin,
                "Testes finais e publicação",
                StatusTarefa.PENDENTE,
                null
        );

        projetoRepository.save(checkin);

        Projeto fidelidade = criarProjetoSeNaoExistir(
                "Fidelidade B2B",
                "Programa de fidelidade para clientes de carga regulares.",
                "Comércio",
                responsavel,
                StatusProjeto.EM_ANDAMENTO,
                LocalDate.of(2026, 3, 1),
                LocalDate.of(2026, 12, 1),
                new BigDecimal("0.00"),
                new BigDecimal("50000.00"),
                null,
                0
        );

        adicionarIdeiaSeNaoExistir(fidelidade, fidelidadeIdeia);

        adicionarTarefaSeNaoExistir(
                fidelidade,
                "Definição de regras de pontuação",
                StatusTarefa.PENDENTE,
                null
        );

        adicionarTarefaSeNaoExistir(
                fidelidade,
                "Integração com sistema de faturamento",
                StatusTarefa.PENDENTE,
                null
        );

        adicionarTarefaSeNaoExistir(
                fidelidade,
                "Piloto com clientes-chave",
                StatusTarefa.PENDENTE,
                null
        );

        projetoRepository.save(fidelidade);

        log.info("Projetos verificados/criados");
    }

    private Projeto criarProjetoSeNaoExistir(
            String titulo,
            String descricao,
            String area,
            Usuario responsavel,
            StatusProjeto status,
            LocalDate dataInicio,
            LocalDate dataPrevistaConclusao,
            BigDecimal investimento,
            BigDecimal economiaAnualEstimada,
            BigDecimal economiaAnualRealizada,
            Integer horasEconomizadasMes
    ) {
        return projetoRepository.findByTitulo(titulo)
                .orElseGet(() -> {

                    Projeto projeto = novoProjeto(
                            titulo,
                            descricao,
                            area,
                            responsavel,
                            status,
                            dataInicio,
                            dataPrevistaConclusao,
                            investimento,
                            economiaAnualEstimada,
                            economiaAnualRealizada,
                            horasEconomizadasMes
                    );

                    Projeto salvo = projetoRepository.save(projeto);

                    log.info(
                            "Projeto '{}' criado | ID: {}",
                            salvo.getTitulo(),
                            salvo.getId()
                    );

                    return salvo;
                });
    }

    private void adicionarIdeiaSeNaoExistir(
            Projeto projeto,
            Ideia ideia
    ) {
        if (ideia == null) {
            return;
        }

        if (projeto.getIdeias() == null) {
            projeto.setIdeias(new java.util.HashSet<>());
        }

        boolean jaExiste = projeto.getIdeias()
                .stream()
                .anyMatch(i -> i.getId().equals(ideia.getId()));

        if (!jaExiste) {
            projeto.getIdeias().add(ideia);
            log.info(
                    "Ideia '{}' adicionada ao projeto '{}'",
                    ideia.getTitulo(),
                    projeto.getTitulo()
            );
        }
    }

    private void adicionarTarefaSeNaoExistir(
            Projeto projeto,
            String titulo,
            StatusTarefa status,
            LocalDate dataConclusao
    ) {
        if (projeto.getTarefas() == null) {
            projeto.setTarefas(new ArrayList<>());
        }

        boolean jaExiste = projeto.getTarefas()
                .stream()
                .anyMatch(t -> titulo.equals(t.getTitulo()));

        if (jaExiste) {
            log.info(
                    "Tarefa '{}' já existe no projeto '{}'",
                    titulo,
                    projeto.getTitulo()
            );
            return;
        }

        Tarefa tarefa = new Tarefa();
        tarefa.setTitulo(titulo);
        tarefa.setStatus(status);
        tarefa.setDataConclusao(dataConclusao);
        tarefa.setProjeto(projeto);

        Tarefa salva = tarefaRepository.save(tarefa);

        projeto.getTarefas().add(salva);

        log.info(
                "Tarefa '{}' criada | Projeto: {} | ID: {}",
                salva.getTitulo(),
                projeto.getTitulo(),
                salva.getId()
        );
    }

    private Projeto novoProjeto(
            String titulo,
            String descricao,
            String area,
            Usuario responsavel,
            StatusProjeto status,
            LocalDate dataInicio,
            LocalDate dataPrevistaConclusao,
            BigDecimal investimento,
            BigDecimal economiaAnualEstimada,
            BigDecimal economiaAnualRealizada,
            Integer horasEconomizadasMes
    ) {
        Projeto projeto = new Projeto();

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
        projeto.setTarefas(new ArrayList<>());

        return projeto;
    }
}