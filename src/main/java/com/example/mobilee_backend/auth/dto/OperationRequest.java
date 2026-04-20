package com.example.mobilee_backend.auth.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OperationRequest {

    @NotNull(message = "Le type est obligatoire")
    private String type;

    @NotNull(message = "Le montant est obligatoire")
    @Min(value = 1, message = "Le montant doit être supérieur à 0")
    private Double montant;

    private String nomClient;

    private String telephoneClient;   // nouveau champ

    @NotNull(message = "L'agent ID est obligatoire")
    private Long agentId;
}