package com.aguiabranca.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ideia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String titulo;

    private String descricao;

    private String status;

    private String area;

    private String data;

    private String autor;

    private Integer baseKM;

    private boolean isStrategicBonus;

    private String impacto;

    private String esforco;

    private String prioridade;

    private String prazo;

    private Float roiEsperado;

    private Float investimento;

    private Float retorno;

    private String observacaoProgresso;

    private String responsavel;

    @OneToMany(mappedBy = "ideia", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<MarcoProjeto> marcos = new ArrayList<>();
}
