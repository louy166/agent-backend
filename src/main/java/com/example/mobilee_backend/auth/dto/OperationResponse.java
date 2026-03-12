package com.example.mobilee_backend.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OperationResponse {
    private Long id;
    private String type;
    private Double montant;
    private Double commission;
    private String nomClient;
    private String date;
    private String message;
}