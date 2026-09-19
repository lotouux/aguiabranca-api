package com.aguiabranca.api.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AttachIdeiasDTO(
        @NotEmpty List<String> ideiaIds
) {}
