package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.TipoPerfil;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    private String matricula;

    private String senha;

    @Enumerated(EnumType.STRING)
    private TipoPerfil perfil; // OPERADOR, GESTOR, LIDERANCA
}
