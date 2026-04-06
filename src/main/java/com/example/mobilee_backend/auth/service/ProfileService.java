package com.example.mobilee_backend.auth.service;


import com.example.mobilee_backend.auth.dto.ProfileRequest;
import com.example.mobilee_backend.auth.dto.ProfileResponse;
import com.example.mobilee_backend.auth.model.AgentEntity;
import com.example.mobilee_backend.auth.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Récupérer le profil ───────────────────────────────────────────────────
    public ProfileResponse getProfil(Long agentId) {
        AgentEntity agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable"));
        return toResponse(agent, "");
    }

    // ── Modifier le profil ────────────────────────────────────────────────────
    public ProfileResponse modifierProfil(Long agentId, ProfileRequest request) {

        AgentEntity agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable"));

        // Vérifier unicité email (si changé)
        if (!agent.getEmail().equals(request.getEmail()) &&
                agentRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Cet email est déjà utilisé");

        // Vérifier unicité téléphone (si changé)
        if (!agent.getTelephone().equals(request.getTelephone()) &&
                agentRepository.existsByTelephone(request.getTelephone()))
            throw new IllegalArgumentException("Ce numéro est déjà utilisé");

        // Vérifier unicité NNI (si changé)
        if (request.getNni() != null && !request.getNni().isBlank()
                && !request.getNni().equals(agent.getNni())
                && agentRepository.existsByNni(request.getNni()))
            throw new IllegalArgumentException("Ce NNI est déjà utilisé");

        // Mise à jour des champs
        agent.setNom(request.getNom());
        agent.setTelephone(request.getTelephone());
        agent.setEmail(request.getEmail());
        if (request.getNomAgence()    != null) agent.setNomAgence(request.getNomAgence());
        if (request.getAdresse()      != null) agent.setAdresse(request.getAdresse());
        if (request.getNni()          != null) agent.setNni(request.getNni());
        if (request.getTypePersonne() != null) {
            try { agent.setTypePersonne(AgentEntity.TypePersonne.valueOf(request.getTypePersonne())); }
            catch (Exception ignored) {}
        }
        if (request.getTypeOuverture() != null) {
            try { agent.setTypeOuverture(AgentEntity.TypeOuverture.valueOf(request.getTypeOuverture())); }
            catch (Exception ignored) {}
        }

        // Changement de mot de passe (optionnel)
        if (request.getNouveauMotDePasse() != null && !request.getNouveauMotDePasse().isBlank()) {
            if (request.getAncienMotDePasse() == null || request.getAncienMotDePasse().isBlank())
                throw new IllegalArgumentException("L'ancien mot de passe est requis");
            if (!passwordEncoder.matches(request.getAncienMotDePasse(), agent.getMotDePasse()))
                throw new IllegalArgumentException("Ancien mot de passe incorrect");
            if (!request.getNouveauMotDePasse().equals(request.getConfirmerMotDePasse()))
                throw new IllegalArgumentException("Les nouveaux mots de passe ne correspondent pas");
            if (request.getNouveauMotDePasse().length() < 6)
                throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
            agent.setMotDePasse(passwordEncoder.encode(request.getNouveauMotDePasse()));
        }

        return toResponse(agentRepository.save(agent), "Profil mis à jour avec succès");
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private ProfileResponse toResponse(AgentEntity a, String message) {
        return ProfileResponse.builder()
                .id(a.getId())
                .nom(a.getNom())
                .telephone(a.getTelephone())
                .email(a.getEmail())
                .nomAgence(a.getNomAgence())
                .nni(a.getNni())
                .adresse(a.getAdresse())
                .typePersonne(a.getTypePersonne()  != null ? a.getTypePersonne().name()  : null)
                .typeOuverture(a.getTypeOuverture() != null ? a.getTypeOuverture().name() : null)
                .statut(a.getStatut()   != null ? a.getStatut().name()   : "ACTIF")
                .role(a.getRole()       != null ? a.getRole().name()     : "AGENT")
                .createdAt(a.getCreatedAt() != null ? a.getCreatedAt().format(FMT) : "")
                .message(message)
                .build();
    }
}