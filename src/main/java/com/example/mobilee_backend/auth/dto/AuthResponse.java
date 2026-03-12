package com.example.mobilee_backend.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
    private String message;
    private AgentInfo agent;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AgentInfo {
        private Long id;
        private String nom;
        private String telephone;
        private String email;
        private String nomAgence;
        private String role;
    }
}