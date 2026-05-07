package com.example.mobilee_backend.auth.service;

import com.example.mobilee_backend.auth.dto.OperationRequest;
import com.example.mobilee_backend.auth.dto.OperationResponse;
import com.example.mobilee_backend.auth.model.AgentEntity;
import com.example.mobilee_backend.auth.model.OperationEntity;
import com.example.mobilee_backend.auth.repository.AgentRepository;
import com.example.mobilee_backend.auth.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class OperationService {

    private final OperationRepository operationRepository;
    private final AgentRepository agentRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public OperationResponse creerOperation(OperationRequest request) {

        // Vérifier le type
        OperationEntity.TypeOperation type;
        try {
            type = OperationEntity.TypeOperation.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Type invalide. Utilisez RETRAIT ou DEPOT");
        }

        // Récupérer l'agent
        AgentEntity agent = agentRepository.findById(request.getAgentId())
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable"));

        // ── Vérification du code pour les retraits ────────────────────────────
        if (type == OperationEntity.TypeOperation.RETRAIT) {
            if (request.getCodeAgent() == null || request.getCodeAgent().isBlank())
                throw new IllegalArgumentException("Le code est requis pour effectuer un retrait");

            if (!passwordEncoder.matches(request.getCodeAgent(), agent.getMotDePasse()))
                throw new IllegalArgumentException("Code incorrect. Retrait refusé");
        }

        // Créer l'opération
        OperationEntity operation = OperationEntity.builder()
                .type(type)
                .montant(request.getMontant())
                .nomClient(request.getNomClient())
                .telephoneClient(request.getTelephoneClient())
                .agent(agent)
                .build();

        OperationEntity saved = operationRepository.save(operation);

        return OperationResponse.builder()
                .id(saved.getId())
                .reference(saved.getReference())
                .type(saved.getType().name())
                .montant(saved.getMontant())
                .commission(saved.getCommission())
                .nomClient(saved.getNomClient())
                .telephoneClient(saved.getTelephoneClient())
                .date(saved.getCreatedAt().format(FORMATTER))
                .message("Opération enregistrée avec succès")
                .build();
    }
}