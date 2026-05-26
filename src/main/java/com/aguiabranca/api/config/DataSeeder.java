package com.aguiabranca.api.config;

import com.aguiabranca.api.model.*;
import com.aguiabranca.api.model.enums.TipoPerfil;
import com.aguiabranca.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

        @Autowired
        private UsuarioRepository usuarioRepository;

        @Autowired
        private FocoEstrategicoRepository focoRepository;

        @Autowired
        private IdeiaRepository ideiaRepository;

        @Override
        public void run(String... args) throws Exception {

                // 1. Seed de Usuários
                if (usuarioRepository.count() == 0) {
                        usuarioRepository.save(new Usuario(null, "Pedro Miranda", "OP001", "123", TipoPerfil.OPERADOR));
                        usuarioRepository.save(new Usuario(null, "Leonardo Martin", "GS001", "123", TipoPerfil.GESTOR));
                        usuarioRepository.save(
                                        new Usuario(null, "Beatriz Camargo", "LD001", "123", TipoPerfil.LIDERANCA));
                        System.out.println("Usuários criados");
                }

                // 2. Seed de Focos Estratégicos
                if (focoRepository.count() == 0) {
                        focoRepository.save(new FocoEstrategico(null, "Junho", "Redução de Emissões",
                                        "Foco em ideias que reduzam a pegada de carbono da frota em 15%.",
                                        List.of("Logística", "Operação"),
                                        true));
                        focoRepository.save(new FocoEstrategico(null, "Julho", "Eficiência em Logística",
                                        "Otimizar processos de carga e descarga para reduzir tempo em 20%.",
                                        List.of("Logística"), false));
                        System.out.println("Focos Estratégicos criados");
                }

                // 3. Seed de Ideias e Marcos
                if (ideiaRepository.count() == 0) {
                        Ideia ideia1 = new Ideia();

                        ideia1.setTitulo("Sistema de Roteirização Inteligente");
                        ideia1.setDescricao("Otimização de rotas via IA.");
                        ideia1.setStatus("Em Execução");
                        ideia1.setArea("Logística");
                        ideia1.setData("12 ago");
                        ideia1.setAutor("Pedro Miranda");
                        ideia1.setBaseKM(200);
                        ideia1.setStrategicBonus(false);
                        ideia1.setImpacto("Alto");
                        ideia1.setEsforco("Alto");
                        ideia1.setPrioridade("Alta");
                        ideia1.setPrazo("29/06/2026");
                        ideia1.setRoiEsperado(2.0f);
                        ideia1.setInvestimento(150000f);
                        ideia1.setRetorno(450000f);
                        ideia1.setObservacaoProgresso("");
                        ideia1.setResponsavel("Larissa Linguiça");

                        MarcoProjeto m1 = new MarcoProjeto(null, "Análise de Requisitos", true, "20/03/2026", ideia1);
                        MarcoProjeto m2 = new MarcoProjeto(null, "MVP desenvolvido", true, "29/03/2026", ideia1);
                        MarcoProjeto m3 = new MarcoProjeto(null, "Testes piloto", false, "", ideia1);
                        MarcoProjeto m4 = new MarcoProjeto(null, "Rollout completo", false, "", ideia1);

                        ideia1.setMarcos(List.of(m1, m2, m3, m4));

                        ideiaRepository.save(ideia1);

                        // ==========================================================

                        Ideia ideia2 = new Ideia();

                        ideia2.setTitulo("App de Check-in Rápido");
                        ideia2.setDescricao(
                                        "Implementar IA para otimizar rotas de entregas, reduzindo tempo e combustível.");
                        ideia2.setStatus("Aprovada");
                        ideia2.setArea("Logística");
                        ideia2.setData("12 ago");
                        ideia2.setAutor("Pedro Miranda");
                        ideia2.setBaseKM(200);
                        ideia2.setStrategicBonus(true);
                        ideia2.setImpacto("Alto");
                        ideia2.setEsforco("Médio");
                        ideia2.setPrioridade("Alta");
                        ideia2.setPrazo("05/07/2026");
                        ideia2.setRoiEsperado(2.8f);
                        ideia2.setInvestimento(150000f);
                        ideia2.setRetorno(420000f);
                        ideia2.setObservacaoProgresso("Integração inicial concluída.");
                        ideia2.setResponsavel("Larissa Linguiça");

                        MarcoProjeto m5 = new MarcoProjeto(null, "Planejamento e levantamento de requisitos", true,
                                        "12/02/2026", ideia2);
                        MarcoProjeto m6 = new MarcoProjeto(null, "Desenvolvimento do backend de rastreamento", false,
                                        "", ideia2);
                        MarcoProjeto m7 = new MarcoProjeto(null, "Implementação do dashboard mobile", false, "",
                                        ideia2);
                        MarcoProjeto m8 = new MarcoProjeto(null, "Testes finais e publicação", false, "", ideia2);

                        ideia2.setMarcos(List.of(m5, m6, m7, m8));

                        ideiaRepository.save(ideia2);

                        // ==========================================================

                        Ideia ideia3 = new Ideia();

                        ideia3.setTitulo("Monitoramento de Pneus IoT");
                        ideia3.setDescricao("Sensores para monitorar pressão e temperatura dos pneus em tempo real.");
                        ideia3.setStatus("Enviada");
                        ideia3.setArea("Logística");
                        ideia3.setData("12 ago");
                        ideia3.setAutor("Pedro Miranda");
                        ideia3.setBaseKM(200);
                        ideia3.setStrategicBonus(false);
                        ideia3.setImpacto("Alto");
                        ideia3.setEsforco("Médio");
                        ideia3.setPrioridade("Alta");
                        ideia3.setPrazo("");
                        ideia3.setRoiEsperado(0f);
                        ideia3.setInvestimento(0f);
                        ideia3.setRetorno(0f);
                        ideia3.setObservacaoProgresso("");
                        ideia3.setResponsavel("");
                        ideia3.setMarcos(new ArrayList<>());

                        ideiaRepository.save(ideia3);

                        // ==========================================================

                        Ideia ideia4 = new Ideia();

                        ideia4.setTitulo("Programa de Fidelidade B2B");
                        ideia4.setDescricao("Benefícios para clientes de carga regulares.");
                        ideia4.setStatus("Em Execução");
                        ideia4.setArea("Comércio");
                        ideia4.setData("12 ago");
                        ideia4.setAutor("Pedro Miranda");
                        ideia4.setBaseKM(200);
                        ideia4.setStrategicBonus(false);
                        ideia4.setImpacto("Médio");
                        ideia4.setEsforco("Baixo");
                        ideia4.setPrioridade("Média");
                        ideia4.setPrazo("");
                        ideia4.setRoiEsperado(0f);
                        ideia4.setInvestimento(0f);
                        ideia4.setRetorno(0f);
                        ideia4.setObservacaoProgresso("");
                        ideia4.setResponsavel("");
                        ideia4.setMarcos(new ArrayList<>());

                        ideiaRepository.save(ideia4);

                        // ==========================================================

                        Ideia ideia5 = new Ideia();

                        ideia5.setTitulo("Sistema de Feedback Automatizado");
                        ideia5.setDescricao("Coleta automática de feedback pós-viagem com análise de sentimento.");
                        ideia5.setStatus("Concluída");
                        ideia5.setArea("Passageiros");
                        ideia5.setData("12 ago");
                        ideia5.setAutor("Pedro Miranda");
                        ideia5.setBaseKM(200);
                        ideia5.setStrategicBonus(false);
                        ideia5.setImpacto("Alto");
                        ideia5.setEsforco("Médio");
                        ideia5.setPrioridade("Alta");
                        ideia5.setPrazo("20/06/2026");
                        ideia5.setRoiEsperado(3.4f);
                        ideia5.setInvestimento(85000f);
                        ideia5.setRetorno(289000f);
                        ideia5.setObservacaoProgresso("Projeto concluído e integrado ao sistema principal.");
                        ideia5.setResponsavel("Larissa Linguiça");

                        MarcoProjeto m9 = new MarcoProjeto(null, "Definição dos fluxos de coleta de feedback", true,
                                        "10/01/2026", ideia5);
                        MarcoProjeto m10 = new MarcoProjeto(null, "Integração com serviços de envio automático", true,
                                        "05/03/2026", ideia5);
                        MarcoProjeto m11 = new MarcoProjeto(null, "Implementação da análise de sentimento", true,
                                        "28/04/2026", ideia5);
                        MarcoProjeto m12 = new MarcoProjeto(null, "Testes finais e implantação", true, "15/06/2026",
                                        ideia5);

                        ideia5.setMarcos(List.of(m9, m10, m11, m12));

                        ideiaRepository.save(ideia5);

                        System.out.println("Ideias e Marcos criados.");
                }
        }
}
