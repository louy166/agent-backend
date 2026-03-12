package com.example.mobilee_backend.auth.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OperationRequest {

    @NotNull(message = "Le type est obligatoire")
    private String type; // RETRAIT ou DEPOT

    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 1, message = "Le montant doit être supérieur à 0")
    private Double montant;

    private String nomClient;

    @NotNull(message = "L'agent ID est obligatoire")
    private Long agentId;
}