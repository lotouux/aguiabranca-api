package com.aguiabranca.api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarcoProjeto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String titulo;

    private boolean isCompleto;

    private String dataCompleto;

    @ManyToOne
    @JoinColumn(name = "ideia_id")
    private Ideia ideia;
}
