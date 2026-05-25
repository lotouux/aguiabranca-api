package com.aguiabranca.api.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FocoEstrategico {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String mes;

    private String titulo;

    private String descricao;

    @ElementCollection
    private List<String> areasPotenciais;

    private boolean ativo;
}