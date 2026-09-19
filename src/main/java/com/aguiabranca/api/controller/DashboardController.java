package com.aguiabranca.api.controller;

import com.aguiabranca.api.dto.DashboardResponseDTO;
import com.aguiabranca.api.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @PreAuthorize("hasRole('LIDERANCA')")
    public ResponseEntity<DashboardResponseDTO> gerar() {
        return ResponseEntity.ok(dashboardService.gerar());
    }
}
