package com.aguiabranca.api.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class FocoEstrategico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    private String id;

    private String mes;

    private String titulo;

    private String descricao;

    @ElementCollection
    private List<String> areasPotenciais;

    private boolean ativo;
}
