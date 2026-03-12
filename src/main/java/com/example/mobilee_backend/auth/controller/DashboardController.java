package com.example.mobilee_backend.auth.controller;

import com.example.mobilee_backend.auth.dto.DashboardResponse;
import com.example.mobilee_backend.auth.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * GET /api/dashboard/{agentId}
     * Récupérer les statistiques et dernières opérations de l'agent
     */
    @GetMapping("/{agentId}")
    public ResponseEntity<?> getDashboard(@PathVariable Long agentId) {
        try {
            DashboardResponse response = dashboardService.getDashboard(agentId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}