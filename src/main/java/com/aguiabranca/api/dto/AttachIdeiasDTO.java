package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record AttachIdeiasDTO(
        @NotEmpty(message = "A lista de IDs das ideias não pode estar vazia")
        List<String> ideiaIds
) {}