package com.aguiabranca.api.dto;

import java.time.Instant;
import java.util.List;

public record ErroResponseDTO(
        Instant timestamp,
        int status,
        String erro,
        String mensagem,
        String path,
        List<CampoErroDTO> campos
) {

    public static ErroResponseDTO of(Instant timestamp, int status, String erro, String mensagem, String path) {
        return new ErroResponseDTO(timestamp, status, erro, mensagem, path, List.of());
    }

    public static ErroResponseDTO of(Instant timestamp, int status, String erro, String mensagem, String path,
            List<CampoErroDTO> campos) {
        return new ErroResponseDTO(timestamp, status, erro, mensagem, path, campos);
    }
}
