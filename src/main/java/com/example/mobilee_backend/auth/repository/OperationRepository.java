package com.example.mobilee_backend.auth.repository;

import com.example.mobilee_backend.auth.model.OperationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OperationRepository extends JpaRepository<OperationEntity, Long> {

    List<OperationEntity> findByAgentIdOrderByCreatedAtDesc(Long agentId);
    List<OperationEntity> findAllByOrderByCreatedAtDesc();

    List<OperationEntity> findByAgentIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long agentId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(o.commission), 0) FROM OperationEntity o WHERE o.agent.id = :agentId")
    Double totalCommissions(@Param("agentId") Long agentId);

    @Query("SELECT COALESCE(SUM(o.commission), 0) FROM OperationEntity o WHERE o.agent.id = :agentId AND o.createdAt BETWEEN :debut AND :fin")
    Double totalCommissionsJour(@Param("agentId") Long agentId,
                                @Param("debut") LocalDateTime debut,
                                @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(o.montant), 0) FROM OperationEntity o WHERE o.agent.id = :agentId")
    Double totalMontant(@Param("agentId") Long agentId);

    @Query("SELECT COALESCE(SUM(o.montant), 0) FROM OperationEntity o WHERE o.agent.id = :agentId AND o.createdAt BETWEEN :debut AND :fin")
    Double totalMontantJour(@Param("agentId") Long agentId,
                            @Param("debut") LocalDateTime debut,
                            @Param("fin") LocalDateTime fin);

    Long countByAgentIdAndCreatedAtBetween(Long agentId, LocalDateTime debut, LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(o.montant), 0) FROM OperationEntity o WHERE o.agent.id = :agentId AND o.type = :type AND o.createdAt BETWEEN :debut AND :fin")
    Double totalMontantParType(@Param("agentId") Long agentId,
                               @Param("type") OperationEntity.TypeOperation type,
                               @Param("debut") LocalDateTime debut,
                               @Param("fin") LocalDateTime fin);

    @Query("SELECT COALESCE(SUM(o.montant), 0) FROM OperationEntity o WHERE o.agent.id = :agentId AND o.type = :type")
    Double totalMontantParTypeGlobal(@Param("agentId") Long agentId,
                                     @Param("type") OperationEntity.TypeOperation type);

    // Admin : totaux globaux toutes opérations
    @Query("SELECT COALESCE(SUM(o.commission), 0) FROM OperationEntity o")
    Double totalCommissionsAdmin();

    @Query("SELECT COALESCE(SUM(o.montant), 0) FROM OperationEntity o")
    Double totalMontantAdmin();
}