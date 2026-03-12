package com.example.mobilee_backend.auth.service;

import com.example.mobilee_backend.auth.dto.*;
import com.example.mobilee_backend.auth.model.AgentEntity;
import com.example.mobilee_backend.auth.repository.AgentRepository;


import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── Inscription ──────────────────────────────────────────────────────────
    public AuthResponse register(RegisterRequest request) {
        if (!request.getMotDePasse().equals(request.getConfirmerMotDePasse()))
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        if (agentRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        if (agentRepository.existsByTelephone(request.getTelephone()))
            throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé");

        AgentEntity agent = AgentEntity.builder()
                .nom(request.getNom())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .nomAgence(request.getNomAgence())
                .build();

        AgentEntity savedAgent = agentRepository.save(agent);

        return buildResponse("Compte créé avec succès", savedAgent);
    }

    // ─── Connexion ────────────────────────────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        // 1. Validation manuelle (évite l'annotation @Valid)
        if (request.getEmail() == null || request.getMotDePasse() == null) {
            throw new IllegalArgumentException("Données de connexion incomplètes");
        }

        // 2. Recherche de l'utilisateur
        AgentEntity agent = agentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        // 3. Vérification du mot de passe avec BCrypt classique
        // On compare le mot de passe en clair avec le hash stocké
        if (!BCrypt.checkpw(request.getMotDePasse(), agent.getMotDePasse())) {
            throw new IllegalArgumentException("Email ou mot de passe incorrect");
        }

        return buildResponse("Connexion réussie", agent);
    }

    // ─── Vérification email (étape 1) ─────────────────────────────────────────
    public void verifierEmail(ForgotPasswordRequest request) {
        if (!agentRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Aucun compte associé à cet email");
    }

    // ─── Réinitialisation mot de passe (étape 2) ──────────────────────────────
    public void reinitialiserMotDePasse(ResetPasswordRequest request) {
        if (!request.getNouveauMotDePasse().equals(request.getConfirmerMotDePasse()))
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");

        AgentEntity agent = agentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable"));

        agent.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        agentRepository.save(agent);
    }

    // ─── Helper ───────────────────────────────────────────────────────────────
    private AuthResponse buildResponse(String message, AgentEntity agent) {
        return AuthResponse.builder()
                .message(message)
                .agent(AuthResponse.AgentInfo.builder()
                        .id(agent.getId())
                        .nom(agent.getNom())
                        .telephone(agent.getTelephone())
                        .email(agent.getEmail())
                        .nomAgence(agent.getNomAgence())
                        .role(agent.getRole().name())
                        .build())
                .build();
    }
}