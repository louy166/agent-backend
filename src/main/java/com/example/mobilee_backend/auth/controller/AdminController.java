package com.example.mobilee_backend.auth.controller;

import com.example.mobilee_backend.auth.dto.AdminAgentRequest;
import com.example.mobilee_backend.auth.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /** GET /api/admin/stats */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        try { return ResponseEntity.ok(adminService.getStats()); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage())); }
    }

    /** GET /api/admin/agents */
    @GetMapping("/agents")
    public ResponseEntity<?> getAllAgents() {
        try { return ResponseEntity.ok(adminService.getAllAgents()); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage())); }
    }

    /** GET /api/admin/agents/{id} */
    @GetMapping("/agents/{id}")
    public ResponseEntity<?> getAgent(@PathVariable Long id) {
        try { return ResponseEntity.ok(adminService.getAgent(id)); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage())); }
    }

    /** POST /api/admin/agents */
    @PostMapping("/agents")
    public ResponseEntity<?> creerAgent(@RequestBody AdminAgentRequest req) {
        try { return ResponseEntity.status(HttpStatus.CREATED).body(adminService.creerAgent(req)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage())); }
    }

    /** PUT /api/admin/agents/{id} */
    @PutMapping("/agents/{id}")
    public ResponseEntity<?> modifierAgent(@PathVariable Long id, @RequestBody AdminAgentRequest req) {
        try { return ResponseEntity.ok(adminService.modifierAgent(id, req)); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage())); }
    }

    /** DELETE /api/admin/agents/{id} */
    @DeleteMapping("/agents/{id}")
    public ResponseEntity<?> supprimerAgent(@PathVariable Long id) {
        try { adminService.supprimerAgent(id); return ResponseEntity.ok(Map.of("message", "Agent supprimé")); }
        catch (IllegalArgumentException e) { return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage())); }
    }

    /** PATCH /api/admin/agents/{id}/statut */
    @PatchMapping("/agents/{id}/statut")
    public ResponseEntity<?> changerStatut(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try { return ResponseEntity.ok(adminService.changerStatut(id, body.get("statut"))); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage())); }
    }

    /** GET /api/admin/operations */
    @GetMapping("/operations")
    public ResponseEntity<?> getAllOperations() {
        try { return ResponseEntity.ok(adminService.getAllOperations()); }
        catch (Exception e) { return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage())); }
    }
}