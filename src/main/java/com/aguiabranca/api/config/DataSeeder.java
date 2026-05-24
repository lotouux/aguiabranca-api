package com.aguiabranca.api.config;

import com.aguiabranca.api.model.*;
import com.aguiabranca.api.model.enums.TipoPerfil;
import com.aguiabranca.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

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
            usuarioRepository.save(new Usuario(null, "Beatriz Camargo", "LD001", "123", TipoPerfil.LIDERANCA));
            System.out.println("Usuários criados");
        }

        // 2. Seed de Focos Estratégicos
        if (focoRepository.count() == 0) {
            focoRepository.save(new FocoEstrategico("1", "Maio", "Redução de Emissões", "Foco em ideias que reduzam a pegada de carbono da frota em 15%.", List.of("Logística", "Operação"), true));
            focoRepository.save(new FocoEstrategico("2", "Junho", "Eficiência em Logística", "Otimizar processos de carga e descarga para reduzir tempo em 20%.", List.of("Logística"), false));
            System.out.println("Focos Estratégicos criados");
        }

        // 3. Seed de Ideias e Marcos
        if (ideiaRepository.count() == 0) {
            Ideia ideia = new Ideia();

            ideia.setTitulo("Sistema de Roteirização Inteligente");
            ideia.setDescricao("Otimização de rotas via IA.");
            ideia.setStatus("Em Execução");
            ideia.setArea("Logística");
            ideia.setData("12 ago");
            ideia.setAutor("Pedro Miranda");
            ideia.setBaseKM(200);
            ideia.setStrategicBonus(false);
            ideia.setImpacto("Alto");
            ideia.setEsforco("Alto");
            ideia.setPrioridade("A");
            ideia.setPrazo("29/06/2026");
            ideia.setRoiEsperado(2.0f);
            ideia.setInvestimento(150000f);
            ideia.setRetorno(450000f);
            ideia.setObservacaoProgresso("");
            ideia.setResponsavel("Larissa Linguiça");

            MarcoProjeto m1 = new MarcoProjeto(null, "Análise de Requisitos", true, "20/03/2026", ideia);
            MarcoProjeto m2 = new MarcoProjeto(null, "MVP desenvolvido", true, "29/03/2026", ideia);
            MarcoProjeto m3 = new MarcoProjeto(null, "Testes piloto", false, "", ideia);
            MarcoProjeto m4 = new MarcoProjeto(null, "Rollout completo", false, "", ideia);

            ideia.setMarcos(List.of(m1, m2, m3, m4));

            ideiaRepository.save(ideia);
            System.out.println("Ideias e Marcos criados.");
        }
    }
}
