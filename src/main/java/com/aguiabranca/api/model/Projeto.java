package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.StatusProjeto;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document(collection = "projetos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Projeto {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String titulo;

    private String descricao;

    private String area;

    private String observacao;

    private StatusProjeto status;

    @DBRef
    @ToString.Exclude
    private Usuario responsavel;

    private LocalDate dataInicio;

    private LocalDate dataPrevistaConclusao;

    private BigDecimal investimento;

    private BigDecimal economiaAnualEstimada;

    private BigDecimal economiaAnualRealizada;

    private Integer horasEconomizadasMes;

    @DBRef
    @ToString.Exclude
    private Set<Ideia> ideias = new HashSet<>();

    @DBRef
    @ToString.Exclude
    private List<Tarefa> tarefas = new java.util.ArrayList<>();

    @Version
    private Long version;

    @CreatedDate
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    @Transient
    public int getProgresso() {
        if (tarefas == null || tarefas.isEmpty()) {
            return 0;
        }
        long concluidas = tarefas.stream()
                .filter(t -> t.getStatus() == com.aguiabranca.api.model.enums.StatusTarefa.CONCLUIDA)
                .count();
        return BigDecimal.valueOf(concluidas)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(tarefas.size()), 0, RoundingMode.HALF_UP)
                .intValue();
    }
}