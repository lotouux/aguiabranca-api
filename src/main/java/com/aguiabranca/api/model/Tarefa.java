package com.aguiabranca.api.model;

import com.aguiabranca.api.model.enums.StatusTarefa;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "tarefas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
public class Tarefa {

    @Id
    @EqualsAndHashCode.Include
    private Long id; // Alterado de Integer para String para compatibilidade com o ObjectId do Mongo

    private String titulo;

    private StatusTarefa status;

    private LocalDate dataConclusao;

    @DBRef
    @ToString.Exclude
    private Projeto projeto;
}