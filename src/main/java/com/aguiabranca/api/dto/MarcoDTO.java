package com.aguiabranca.api.dto;

import com.aguiabranca.api.model.Marco;

public record MarcoDTO(
        String id,
        String titulo,
        boolean isCompleto,
        String observacao,
        String ideiaId
) {

    public static MarcoDTO from(Marco marco) {
        return new MarcoDTO(
                marco.getId(),
                marco.getTitulo(),
                marco.isCompleto(),
                marco.getObservacao(),
                marco.getIdeia() != null ? marco.getIdeia().getId() : null
        );
    }
}
