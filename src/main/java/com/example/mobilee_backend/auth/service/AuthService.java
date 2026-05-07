package com.example.mobilee_backend.auth.service;

import com.example.mobilee_backend.auth.dto.*;
import com.example.mobilee_backend.auth.model.AgentEntity;
import com.example.mobilee_backend.auth.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        if (!request.getMotDePasse().equals(request.getConfirmerMotDePasse()))
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        if (agentRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Cet email est déjà utilisé");
        if (agentRepository.existsByTelephone(request.getTelephone()))
            throw new IllegalArgumentException("Ce numéro de téléphone est déjà utilisé");
        if (request.getNni() != null && !request.getNni().isBlank()
                && agentRepository.existsByNni(request.getNni()))
            throw new IllegalArgumentException("Ce NNI est déjà utilisé");

        AgentEntity.TypePersonne typePersonne = null;
        if (request.getTypePersonne() != null && !request.getTypePersonne().isBlank()) {
            try { typePersonne = AgentEntity.TypePersonne.valueOf(request.getTypePersonne().toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }

        AgentEntity.TypeOuverture typeOuverture = null;
        if (request.getTypeOuverture() != null && !request.getTypeOuverture().isBlank()) {
            try { typeOuverture = AgentEntity.TypeOuverture.valueOf(request.getTypeOuverture().toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }

        AgentEntity.Statut statut = AgentEntity.Statut.ACTIF;
        if (request.getStatut() != null && !request.getStatut().isBlank()) {
            try { statut = AgentEntity.Statut.valueOf(request.getStatut().toUpperCase()); }
            catch (IllegalArgumentException ignored) {}
        }

        AgentEntity agent = AgentEntity.builder()
                .nom(request.getNom())
                .telephone(request.getTelephone())
                .email(request.getEmail())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .nomAgence(request.getNomAgence())
                .nni(request.getNni())
                .adresse(request.getAdresse())
                .typePersonne(typePersonne)
                .typeOuverture(typeOuverture)
                .statut(statut)
                .build();

        return buildResponse("Compte créé avec succès", agentRepository.save(agent));
    }

    public AuthResponse login(LoginRequest request) {
        if (request.getEmail() == null || request.getMotDePasse() == null)
            throw new IllegalArgumentException("Données de connexion incomplètes");

        AgentEntity agent = agentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe incorrect"));

        if (agent.getStatut() == AgentEntity.Statut.BLOQUE)
            throw new IllegalArgumentException("Votre compte est bloqué. Contactez l'administration.");
        if (agent.getStatut() == AgentEntity.Statut.FERME)
            throw new IllegalArgumentException("Votre compte est fermé.");

        if (!passwordEncoder.matches(request.getMotDePasse(), agent.getMotDePasse()))
            throw new IllegalArgumentException("Email ou mot de passe incorrect");

        return buildResponse("Connexion réussie", agent);
    }

    // ─── Connexion par téléphone ──────────────────────────────────────────────
    public AuthResponse loginByPhone(LoginByPhoneRequest request) {
        AgentEntity agent = agentRepository.findByTelephone(request.getTelephone())
                .orElseThrow(() -> new IllegalArgumentException("Numéro ou mot de passe incorrect"));

        if (agent.getStatut() == AgentEntity.Statut.BLOQUE)
            throw new IllegalArgumentException("Votre compte est bloqué. Contactez l'administration.");
        if (agent.getStatut() == AgentEntity.Statut.FERME)
            throw new IllegalArgumentException("Votre compte est fermé.");

        if (!passwordEncoder.matches(request.getMotDePasse(), agent.getMotDePasse()))
            throw new IllegalArgumentException("Numéro ou mot de passe incorrect");

        return buildResponse("Connexion réussie", agent);
    }

    public void verifierEmail(ForgotPasswordRequest request) {
        if (!agentRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Aucun compte associé à cet email");
    }

    public void reinitialiserMotDePasse(ResetPasswordRequest request) {
        if (!request.getNouveauMotDePasse().equals(request.getConfirmerMotDePasse()))
            throw new IllegalArgumentException("Les mots de passe ne correspondent pas");
        AgentEntity agent = agentRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Compte introuvable"));
        agent.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        agentRepository.save(agent);
    }

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