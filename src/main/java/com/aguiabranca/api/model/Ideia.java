package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.Nivel;
import com.aguiabranca.api.model.enums.Prioridade;
import com.aguiabranca.api.model.enums.StatusIdeia;
import com.aguiabranca.api.util.CalculadoraRoi;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Ideia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;

    private String titulo;

    private String descricao;

    @Enumerated(EnumType.STRING)
    private StatusIdeia status;

    private String area;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @ToString.Exclude
    private Usuario autor;

    private boolean isStrategicBonus;

    @Enumerated(EnumType.STRING)
    private Nivel impacto;

    @Enumerated(EnumType.STRING)
    private Nivel esforco;

    @Enumerated(EnumType.STRING)
    private Prioridade prioridade;

    private LocalDate prazo;

    @Column(precision = 15, scale = 2)
    private BigDecimal investimento;

    @Column(precision = 15, scale = 2)
    private BigDecimal retorno;

    @CreatedDate
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    /**
     * Derived, never stored - delegates to the same {@link CalculadoraRoi} the Dashboard uses, so
     * the two can never disagree. {@code null} when {@code retorno} or {@code investimento} is
     * null, or {@code investimento} is zero.
     */
    @Transient
    public BigDecimal getRoiEsperado() {
        if (retorno == null) {
            return null;
        }
        return CalculadoraRoi.calcular(retorno, investimento);
    }
}
