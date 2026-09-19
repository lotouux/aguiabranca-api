package com.aguiabranca.api.service;

import com.aguiabranca.api.dto.DashboardResponseDTO;
import com.aguiabranca.api.repository.AgregadoProjetosProjection;
import com.aguiabranca.api.repository.ProjetoRepository;
import com.aguiabranca.api.util.CalculadoraRoi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ProjetoRepository projetoRepository;

    @Transactional(readOnly = true)
    public DashboardResponseDTO gerar() {
        AgregadoProjetosProjection agregado = projetoRepository.buscarAgregado();

        return new DashboardResponseDTO(
                agregado.getTotalProjetos(),
                agregado.getProjetosConcluidos(),
                agregado.getProjetosEmAndamento(),
                agregado.getInvestimentoTotal(),
                agregado.getEconomiaEstimadaTotal(),
                agregado.getEconomiaRealizadaTotal(),
                calcularRoi(agregado.getEconomiaEstimadaElegivel(), agregado.getInvestimentoElegivel()),
                calcularRoi(agregado.getEconomiaRealizadaElegivel(), agregado.getInvestimentoElegivel()),
                agregado.getHorasEconomizadasMes(),
                agregado.getProjetosSemInvestimento());
    }

    /**
     * Delegates to {@link CalculadoraRoi}, over only the projects whose investimento is present
     * and non-zero - the same "eligible" set {@code investimentoElegivel} was summed over.
     */
    public static BigDecimal calcularRoi(BigDecimal economiaElegivel, BigDecimal investimentoElegivel) {
        return CalculadoraRoi.calcular(economiaElegivel, investimentoElegivel);
    }
}
