package com.example.mobilee_backend.auth.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProfileResponse {
    private Long   id;
    private String nom;
    private String telephone;
    private String email;
    private String nomAgence;
    private String nni;
    private String adresse;
    private String typePersonne;
    private String typeOuverture;
    private String statut;
    private String role;
    private String createdAt;
    private String message;
}