package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.TipoPerfil;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import lombok.*;

@Document(collection = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Usuario {

    @Id

    @EqualsAndHashCode.Include
    private String id;

    private String nome;

    @Indexed(unique = true)
    private String matricula;

    @ToString.Exclude
    private String senha;

    
    private TipoPerfil perfil; // OPERADOR, GESTOR, LIDERANCA
}
