package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.TipoPerfil;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String nome;

    @Column(unique = true)
    private String matricula;

    @ToString.Exclude
    private String senha;

    @Enumerated(EnumType.STRING)
    private TipoPerfil perfil; // OPERADOR, GESTOR, LIDERANCA
}
