package com.example.mobilee_backend.auth.controller;

import com.example.mobilee_backend.auth.dto.OperationRequest;
import com.example.mobilee_backend.auth.dto.OperationResponse;
import com.example.mobilee_backend.auth.service.OperationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/operations")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class OperationController {

    private final OperationService operationService;

    /**
     * POST /api/operations
     * Créer une nouvelle opération (retrait ou dépôt)
     */
    @PostMapping
    public ResponseEntity<?> creerOperation(@Valid @RequestBody OperationRequest request) {
        try {
            OperationResponse response = operationService.creerOperation(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("erreur", e.getMessage()));
        }
    }
}