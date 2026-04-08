package com.example.mobilee_backend.auth.dto;

import lombok.*;
import java.util.List;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class DashboardResponse {

    private AgentStats stats;
    private List<OperationDto> dernieresOperations;

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class AgentStats {
        private String nomAgent;
        private String nomAgence;
        // Commissions
        private Double totalCommissionsGlobal;
        private Double totalCommissionsAujourdhui;
        // Montants
        private Double totalMontantGlobal;
        private Double totalMontantAujourdhui;
        // Compteurs
        private Long nombreOperationsAujourdhui;
        private Long nombreOperationsTotal;
    }

    @Data @AllArgsConstructor @NoArgsConstructor @Builder
    public static class OperationDto {
        private Long id;
        private String reference;
        private String type;
        private Double montant;
        private Double commission;
        private String nomClient;
        private String telephoneClient;
        private String date;
    }
}