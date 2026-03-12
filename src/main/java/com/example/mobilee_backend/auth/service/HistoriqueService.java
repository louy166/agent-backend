package com.example.mobilee_backend.auth.service;

import com.example.mobilee_backend.auth.dto.HistoriqueResponse;
import com.example.mobilee_backend.auth.model.OperationEntity;
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
public class HistoriqueService {

    private final OperationRepository operationRepository;

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ─── Toutes les opérations ────────────────────────────────────────────────
    public HistoriqueResponse getHistoriqueGlobal(Long agentId) {
        List<OperationEntity> operations =
                operationRepository.findByAgentIdOrderByCreatedAtDesc(agentId);

        Double totalCommissions = operationRepository.totalCommissions(agentId);
        Double totalRetraits = operationRepository.totalMontantParTypeGlobal(
                agentId, OperationEntity.TypeOperation.RETRAIT);
        Double totalDepots = operationRepository.totalMontantParTypeGlobal(
                agentId, OperationEntity.TypeOperation.DEPOT);

        return buildResponse(operations, totalCommissions, totalRetraits, totalDepots);
    }

    // ─── Filtrer par jour ─────────────────────────────────────────────────────
    public HistoriqueResponse getHistoriqueJour(Long agentId, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin = debut.plusDays(1).minusSeconds(1);
        return getHistoriquePeriode(agentId, debut, fin);
    }

    // ─── Filtrer par mois ─────────────────────────────────────────────────────
    public HistoriqueResponse getHistoriqueMois(Long agentId, int annee, int mois) {
        LocalDateTime debut = LocalDate.of(annee, mois, 1).atStartOfDay();
        LocalDateTime fin = debut.plusMonths(1).minusSeconds(1);
        return getHistoriquePeriode(agentId, debut, fin);
    }

    // ─── Filtrer par période personnalisée ────────────────────────────────────
    public HistoriqueResponse getHistoriquePeriode(Long agentId,
                                                   LocalDateTime debut,
                                                   LocalDateTime fin) {
        List<OperationEntity> operations =
                operationRepository.findByAgentIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        agentId, debut, fin);

        Double totalCommissions = operationRepository.totalCommissionsJour(agentId, debut, fin);
        Double totalRetraits = operationRepository.totalMontantParType(
                agentId, OperationEntity.TypeOperation.RETRAIT, debut, fin);
        Double totalDepots = operationRepository.totalMontantParType(
                agentId, OperationEntity.TypeOperation.DEPOT, debut, fin);

        return buildResponse(operations, totalCommissions, totalRetraits, totalDepots);
    }

    // ─── Helper ───────────────────────────────────────────────────────────────
    private HistoriqueResponse buildResponse(List<OperationEntity> operations,
                                             Double totalCommissions,
                                             Double totalRetraits,
                                             Double totalDepots) {
        List<HistoriqueResponse.OperationDto> dtos = operations.stream()
                .map(op -> HistoriqueResponse.OperationDto.builder()
                        .id(op.getId())
                        .type(op.getType().name())
                        .montant(op.getMontant())
                        .commission(op.getCommission())
                        .nomClient(op.getNomClient())
                        .date(op.getCreatedAt().format(FORMATTER))
                        .build())
                .collect(Collectors.toList());

        return HistoriqueResponse.builder()
                .operations(dtos)
                .totalCommissions(totalCommissions)
                .totalOperations((long) operations.size())
                .totalRetraits(totalRetraits)
                .totalDepots(totalDepots)
                .build();
    }
}