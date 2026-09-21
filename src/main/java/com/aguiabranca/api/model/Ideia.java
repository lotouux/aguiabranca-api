package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.Nivel;
import com.aguiabranca.api.model.enums.Prioridade;
import com.aguiabranca.api.model.enums.StatusIdeia;
import com.aguiabranca.api.util.CalculadoraRoi;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Document(collection = "ideias")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Ideia {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String titulo;

    private String descricao;

    private StatusIdeia status;

    private String responsavel;

    private String area;

    @DBRef
    @ToString.Exclude
    private Usuario autor;

    private boolean isStrategicBonus;

    private Nivel impacto;

    private Nivel esforco;

    private Prioridade prioridade;

    private LocalDate prazo;

    private BigDecimal investimento;

    private BigDecimal retorno;

    private Integer baseKM;

    private String feedbackGestor;

    @CreatedDate
    private LocalDateTime criadoEm;

    @LastModifiedDate
    private LocalDateTime atualizadoEm;

    @Transient
    public BigDecimal getRoiEsperado() {
        if (retorno == null || investimento == null) {
            return null;
        }

        return CalculadoraRoi.calcular(retorno, investimento);
    }
}