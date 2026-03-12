package com.example.mobilee_backend.auth.service;

import com.example.mobilee_backend.auth.dto.DashboardResponse;
import com.example.mobilee_backend.auth.model.AgentEntity;
import com.example.mobilee_backend.auth.model.OperationEntity;
import com.example.mobilee_backend.auth.repository.AgentRepository;
import com.example.mobilee_backend.auth.repository.OperationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AgentRepository agentRepository;
    private final OperationRepository operationRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public DashboardResponse getDashboard(Long agentId) {

        // Récupérer l'agent
        AgentEntity agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent introuvable"));

        // Bornes de la journée
        LocalDateTime debutJour = LocalDate.now().atStartOfDay();
        LocalDateTime finJour = debutJour.plusDays(1).minusSeconds(1);

        // Statistiques
        Double commissionsGlobal = operationRepository.totalCommissions(agentId);
        Double commissionsAujourdhui = operationRepository.totalCommissionsJour(agentId, debutJour, finJour);
        Long opsAujourdhui = operationRepository.countByAgentIdAndCreatedAtBetween(agentId, debutJour, finJour);
        Long opsTotal = operationRepository.countByAgentIdAndCreatedAtBetween(agentId,
                LocalDateTime.of(2000, 1, 1, 0, 0), LocalDateTime.now());

        // 5 dernières opérations
        List<OperationEntity> operations = operationRepository
                .findByAgentIdOrderByCreatedAtDesc(agentId)
                .stream().limit(5).collect(Collectors.toList());

        List<DashboardResponse.OperationDto> dernieres = operations.stream()
                .map(op -> DashboardResponse.OperationDto.builder()
                        .id(op.getId())
                        .type(op.getType().name())
                        .montant(op.getMontant())
                        .commission(op.getCommission())
                        .nomClient(op.getNomClient())
                        .date(op.getCreatedAt().format(FORMATTER))
                        .build())
                .collect(Collectors.toList());

        return DashboardResponse.builder()
                .stats(DashboardResponse.AgentStats.builder()
                        .nomAgent(agent.getNom())
                        .nomAgence(agent.getNomAgence())
                        .totalCommissionsGlobal(commissionsGlobal)
                        .totalCommissionsAujourdhui(commissionsAujourdhui)
                        .nombreOperationsAujourdhui(opsAujourdhui)
                        .nombreOperationsTotal(opsTotal)
                        .build())
                .dernieresOperations(dernieres)
                .build();
    }
}