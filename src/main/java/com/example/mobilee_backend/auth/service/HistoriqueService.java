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
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HistoriqueResponse getHistoriqueGlobal(Long agentId) {
        List<OperationEntity> ops = operationRepository.findByAgentIdOrderByCreatedAtDesc(agentId);
        Double totalCommissions = operationRepository.totalCommissions(agentId);
        Double totalMontant     = operationRepository.totalMontant(agentId);
        Double totalRetraits    = operationRepository.totalMontantParTypeGlobal(agentId, OperationEntity.TypeOperation.RETRAIT);
        Double totalDepots      = operationRepository.totalMontantParTypeGlobal(agentId, OperationEntity.TypeOperation.DEPOT);
        return buildResponse(ops, totalCommissions, totalMontant, totalRetraits, totalDepots);
    }

    public HistoriqueResponse getHistoriqueJour(Long agentId, LocalDate date) {
        LocalDateTime debut = date.atStartOfDay();
        LocalDateTime fin   = debut.plusDays(1).minusSeconds(1);
        return getHistoriquePeriode(agentId, debut, fin);
    }

    public HistoriqueResponse getHistoriqueMois(Long agentId, int annee, int mois) {
        LocalDateTime debut = LocalDate.of(annee, mois, 1).atStartOfDay();
        LocalDateTime fin   = debut.plusMonths(1).minusSeconds(1);
        return getHistoriquePeriode(agentId, debut, fin);
    }

    public HistoriqueResponse getHistoriquePeriode(Long agentId, LocalDateTime debut, LocalDateTime fin) {
        List<OperationEntity> ops = operationRepository
                .findByAgentIdAndCreatedAtBetweenOrderByCreatedAtDesc(agentId, debut, fin);
        Double totalCommissions = operationRepository.totalCommissionsJour(agentId, debut, fin);
        Double totalMontant     = operationRepository.totalMontantJour(agentId, debut, fin);
        Double totalRetraits    = operationRepository.totalMontantParType(agentId, OperationEntity.TypeOperation.RETRAIT, debut, fin);
        Double totalDepots      = operationRepository.totalMontantParType(agentId, OperationEntity.TypeOperation.DEPOT, debut, fin);
        return buildResponse(ops, totalCommissions, totalMontant, totalRetraits, totalDepots);
    }

    private HistoriqueResponse buildResponse(List<OperationEntity> ops,
                                             Double totalCommissions,
                                             Double totalMontant,
                                             Double totalRetraits,
                                             Double totalDepots) {
        List<HistoriqueResponse.OperationDto> dtos = ops.stream()
                .map(op -> HistoriqueResponse.OperationDto.builder()
                        .id(op.getId())
                        .reference(op.getReference())
                        .type(op.getType().name())
                        .montant(op.getMontant())
                        .commission(op.getCommission())
                        .nomClient(op.getNomClient())
                        .telephoneClient(op.getTelephoneClient())
                        .date(op.getCreatedAt().format(FORMATTER))
                        .build())
                .collect(Collectors.toList());

        return HistoriqueResponse.builder()
                .operations(dtos)
                .totalCommissions(totalCommissions)
                .totalMontant(totalMontant)
                .totalOperations((long) ops.size())
                .totalRetraits(totalRetraits)
                .totalDepots(totalDepots)
                .build();
    }
}