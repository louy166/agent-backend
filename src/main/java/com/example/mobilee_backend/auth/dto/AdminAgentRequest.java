package com.example.mobilee_backend.auth.dto;

import lombok.Data;

@Data
public class AdminAgentRequest {
    private String nom;
    private String telephone;
    private String email;
    private String motDePasse;
    private String nomAgence;
    private String nni;
    private String adresse;
    private String typePersonne;   // MORAL, PHYSIQUE
    private String statut;         // ACTIF, BLOQUE, FERME, SUSPENDU
    private String typeOuverture;  // MOBILE, WEB
    private String role;           // AGENT, ADMIN
}