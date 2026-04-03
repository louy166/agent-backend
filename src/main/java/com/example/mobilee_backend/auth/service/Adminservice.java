package com.example.mobilee_backend.auth.service;

import com.example.mobilee_backend.auth.dto.AdminAgentRequest;
import com.example.mobilee_backend.auth.model.AgentEntity;
import com.example.mobilee_backend.auth.repository.AgentRepository;
import com.example.mobilee_backend.auth.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AgentRepository agentRepository;
    private final OperationRepository operationRepository;
    private final PasswordEncoder passwordEncoder;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Liste tous les agents ─────────────────────────────────────────────────
    public List<Map<String, Object>> getAllAgents() {
        return agentRepository.findAll().stream()
                .map(this::agentToMap)
                .collect(Collectors.toList());
    }

    // ── Détail d'un agent ─────────────────────────────────────────────────────
    public Map<String, Object> getAgent(Long id) {
        AgentEntity a = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable"));
        return agentToMap(a);
    }

    // ── Créer un agent (admin) ────────────────────────────────────────────────
    public Map<String, Object> creerAgent(AdminAgentRequest req) {
        if (agentRepository.existsByEmail(req.getEmail()))
            throw new IllegalArgumentException("Email déjà utilisé");
        if (agentRepository.existsByTelephone(req.getTelephone()))
            throw new IllegalArgumentException("Téléphone déjà utilisé");
        if (req.getNni() != null && !req.getNni().isBlank() && agentRepository.existsByNni(req.getNni()))
            throw new IllegalArgumentException("NNI déjà utilisé");

        AgentEntity agent = AgentEntity.builder()
                .nom(req.getNom())
                .telephone(req.getTelephone())
                .email(req.getEmail())
                .motDePasse(passwordEncoder.encode(
                        req.getMotDePasse() != null ? req.getMotDePasse() : "masriva123"))
                .nomAgence(req.getNomAgence())
                .nni(req.getNni())
                .adresse(req.getAdresse())
                .typePersonne(parseEnum(AgentEntity.TypePersonne.class, req.getTypePersonne()))
                .typeOuverture(parseEnum(AgentEntity.TypeOuverture.class, req.getTypeOuverture()))
                .statut(req.getStatut() != null
                        ? parseEnum(AgentEntity.Statut.class, req.getStatut())
                        : AgentEntity.Statut.ACTIF)
                .role(req.getRole() != null
                        ? parseEnum(AgentEntity.Role.class, req.getRole())
                        : AgentEntity.Role.AGENT)
                .build();

        return agentToMap(agentRepository.save(agent));
    }

    // ── Modifier un agent ─────────────────────────────────────────────────────
    public Map<String, Object> modifierAgent(Long id, AdminAgentRequest req) {
        AgentEntity agent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable"));

        if (req.getNom()       != null) agent.setNom(req.getNom());
        if (req.getTelephone() != null) agent.setTelephone(req.getTelephone());
        if (req.getEmail()     != null) agent.setEmail(req.getEmail());
        if (req.getNomAgence() != null) agent.setNomAgence(req.getNomAgence());
        if (req.getNni()       != null) agent.setNni(req.getNni());
        if (req.getAdresse()   != null) agent.setAdresse(req.getAdresse());
        if (req.getTypePersonne() != null)
            agent.setTypePersonne(parseEnum(AgentEntity.TypePersonne.class, req.getTypePersonne()));
        if (req.getTypeOuverture() != null)
            agent.setTypeOuverture(parseEnum(AgentEntity.TypeOuverture.class, req.getTypeOuverture()));
        if (req.getStatut() != null)
            agent.setStatut(parseEnum(AgentEntity.Statut.class, req.getStatut()));
        if (req.getRole() != null)
            agent.setRole(parseEnum(AgentEntity.Role.class, req.getRole()));
        if (req.getMotDePasse() != null && !req.getMotDePasse().isBlank())
            agent.setMotDePasse(passwordEncoder.encode(req.getMotDePasse()));

        return agentToMap(agentRepository.save(agent));
    }

    // ── Supprimer un agent ────────────────────────────────────────────────────
    public void supprimerAgent(Long id) {
        if (!agentRepository.existsById(id))
            throw new IllegalArgumentException("Agent introuvable");
        agentRepository.deleteById(id);
    }

    // ── Changer statut ────────────────────────────────────────────────────────
    public Map<String, Object> changerStatut(Long id, String statut) {
        AgentEntity agent = agentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable"));
        agent.setStatut(parseEnum(AgentEntity.Statut.class, statut));
        return agentToMap(agentRepository.save(agent));
    }

    // ── Toutes les transactions ───────────────────────────────────────────────
    public List<Map<String, Object>> getAllOperations() {
        return operationRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::opToMap)
                .collect(Collectors.toList());
    }

    // ── Statistiques globales admin ───────────────────────────────────────────
    public Map<String, Object> getStats() {
        long totalAgents = agentRepository.count();
        long agentsActifs = agentRepository.findAll().stream()
                .filter(a -> a.getStatut() == AgentEntity.Statut.ACTIF).count();
        long totalOps = operationRepository.count();
        Double totalCommissions = operationRepository.totalCommissionsAdmin();
        Double totalMontant     = operationRepository.totalMontantAdmin();

        return Map.of(
                "totalAgents", totalAgents,
                "agentsActifs", agentsActifs,
                "totalOperations", totalOps,
                "totalCommissions", totalCommissions,
                "totalMontant", totalMontant
        );
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private Map<String, Object> agentToMap(AgentEntity a) {
        return Map.ofEntries(
                Map.entry("id",            a.getId()),
                Map.entry("nom",           a.getNom()),
                Map.entry("telephone",     a.getTelephone()),
                Map.entry("email",         a.getEmail()),
                Map.entry("nomAgence",     a.getNomAgence()     != null ? a.getNomAgence()    : ""),
                Map.entry("nni",           a.getNni()           != null ? a.getNni()          : ""),
                Map.entry("adresse",       a.getAdresse()       != null ? a.getAdresse()      : ""),
                Map.entry("typePersonne",  a.getTypePersonne()  != null ? a.getTypePersonne().name() : ""),
                Map.entry("typeOuverture", a.getTypeOuverture() != null ? a.getTypeOuverture().name() : ""),
                Map.entry("statut",        a.getStatut()        != null ? a.getStatut().name() : "ACTIF"),
                Map.entry("role",          a.getRole()          != null ? a.getRole().name()  : "AGENT"),
                Map.entry("createdAt",     a.getCreatedAt()     != null ? a.getCreatedAt().format(FMT) : "")
        );
    }

    private Map<String, Object> opToMap(OperationEntity op) {
        return Map.ofEntries(
                Map.entry("id",              op.getId()),
                Map.entry("reference",       op.getReference() != null ? op.getReference() : ""),
                Map.entry("type",            op.getType().name()),
                Map.entry("montant",         op.getMontant()),
                Map.entry("commission",      op.getCommission()),
                Map.entry("nomClient",       op.getNomClient()       != null ? op.getNomClient()       : ""),
                Map.entry("telephoneClient", op.getTelephoneClient() != null ? op.getTelephoneClient() : ""),
                Map.entry("date",            op.getCreatedAt()       != null ? op.getCreatedAt().format(FMT) : ""),
                Map.entry("agentNom",        op.getAgent()           != null ? op.getAgent().getNom()  : ""),
                Map.entry("agentId",         op.getAgent()           != null ? op.getAgent().getId()   : 0)
        );
    }

    private <T extends Enum<T>> T parseEnum(Class<T> cls, String val) {
        try { return Enum.valueOf(cls, val.toUpperCase()); }
        catch (Exception e) { return null; }
    }
}