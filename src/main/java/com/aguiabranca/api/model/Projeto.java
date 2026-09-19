package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.StatusProjeto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * No {@code cascade} on {@link #ideias}: {@code CascadeType.ALL} there would delete the Ideias
 * themselves when a project is deleted, destroying authors' submissions. {@code unique = true} on
 * the join table's {@code ideia_id} is the only backstop against two managers concurrently
 * attaching the same idea to different projects. Never bulk-delete a {@code Projeto} - always go
 * through {@code deleteById} so Hibernate clears the {@code projeto_ideia} rows first.
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Projeto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;

    private String titulo;

    private String descricao;

    private String area;

    private String observacao;

    @Enumerated(EnumType.STRING)
    private StatusProjeto status;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private Usuario responsavel;

    private LocalDate dataInicio;

    private LocalDate dataPrevistaConclusao;

    @Column(precision = 15, scale = 2)
    private BigDecimal investimento;

    @Column(precision = 15, scale = 2)
    private BigDecimal economiaAnualEstimada;

    @Column(precision = 15, scale = 2)
    private BigDecimal economiaAnualRealizada;

    private Integer horasEconomizadasMes;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "projeto_ideia",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "ideia_id", unique = true))
    @ToString.Exclude
    private Set<Ideia> ideias = new HashSet<>();

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id asc")
    @ToString.Exclude
    private List<Tarefa> tarefas = new java.util.ArrayList<>();

    @Version
    private Long version;

    @CreatedDate
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    /**
     * Derived, never stored: {@code round(concluidas / total * 100)}, HALF_UP, {@code 0} when
     * there are no tasks.
     */
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
