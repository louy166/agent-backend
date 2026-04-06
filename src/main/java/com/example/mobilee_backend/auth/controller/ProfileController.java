package com.example.mobilee_backend.auth.controller;

import com.example.mobilee_backend.auth.dto.ProfileRequest;
import com.example.mobilee_backend.auth.dto.ProfileResponse;
import com.example.mobilee_backend.auth.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /** GET /api/profile/{agentId} */
    @GetMapping("/{agentId}")
    public ResponseEntity<?> getProfil(@PathVariable Long agentId) {
        try {
            return ResponseEntity.ok(profileService.getProfil(agentId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }

    /** PUT /api/profile/{agentId} */
    @PutMapping("/{agentId}")
    public ResponseEntity<?> modifierProfil(
            @PathVariable Long agentId,
            @Valid @RequestBody ProfileRequest request) {
        try {
            ProfileResponse response = profileService.modifierProfil(agentId, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}