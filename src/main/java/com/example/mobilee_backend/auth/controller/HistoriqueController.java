package com.example.mobilee_backend.auth.controller;

import com.example.mobilee_backend.auth.service.HistoriqueService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/historique")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HistoriqueController {

    private final HistoriqueService historiqueService;

    /**
     * GET /api/historique/{agentId}
     * Toutes les opérations
     */
    @GetMapping("/{agentId}")
    public ResponseEntity<?> getHistoriqueGlobal(@PathVariable Long agentId) {
        try {
            return ResponseEntity.ok(historiqueService.getHistoriqueGlobal(agentId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * GET /api/historique/{agentId}/jour?date=2024-01-15
     * Filtrer par jour
     */
    @GetMapping("/{agentId}/jour")
    public ResponseEntity<?> getHistoriqueJour(
            @PathVariable Long agentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            return ResponseEntity.ok(historiqueService.getHistoriqueJour(agentId, date));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * GET /api/historique/{agentId}/mois?annee=2024&mois=1
     * Filtrer par mois
     */
    @GetMapping("/{agentId}/mois")
    public ResponseEntity<?> getHistoriqueMois(
            @PathVariable Long agentId,
            @RequestParam int annee,
            @RequestParam int mois) {
        try {
            return ResponseEntity.ok(historiqueService.getHistoriqueMois(agentId, annee, mois));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /**
     * GET /api/historique/{agentId}/periode?debut=2024-01-01T00:00:00&fin=2024-01-31T23:59:59
     * Filtrer par période personnalisée
     */
    @GetMapping("/{agentId}/periode")
    public ResponseEntity<?> getHistoriquePeriode(
            @PathVariable Long agentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime debut,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fin) {
        try {
            return ResponseEntity.ok(historiqueService.getHistoriquePeriode(agentId, debut, fin));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}