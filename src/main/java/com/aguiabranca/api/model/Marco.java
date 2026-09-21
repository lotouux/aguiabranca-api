package com.aguiabranca.api.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "marcos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Marco {

    @Id
    @EqualsAndHashCode.Include
    private String id;

    private String titulo;

    private boolean completo;

    private String observacao;

    @DBRef
    @ToString.Exclude
    private Ideia ideia;
}
