package com.aguiabranca.api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "focos_estrategicos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class FocoEstrategico {

    @Id
    @EqualsAndHashCode.Include
    private Long id;

    private String mes;

    private String titulo;

    private String descricao;

    private List<String> areasPotenciais;

    private boolean ativo;
}