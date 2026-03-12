package com.example.mobilee_backend.auth.dto;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HistoriqueResponse {

    private List<OperationDto> operations;
    private Double totalCommissions;
    private Long totalOperations;
    private Double totalRetraits;
    private Double totalDepots;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class OperationDto {
        private Long id;
        private String type;
        private Double montant;
        private Double commission;
        private String nomClient;
        private String date;
    }
}