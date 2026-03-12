package com.example.mobilee_backend.auth.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "operations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOperation type; // RETRAIT ou DEPOT

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private Double commission; // calculée automatiquement

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentEntity agent;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        // Calcul automatique commission
        if (this.type == TypeOperation.RETRAIT) {
            this.commission = this.montant * 0.50; // 50% pour retrait
        } else {
            this.commission = this.montant * 0.05; // 5% pour dépôt
        }
    }

    public enum TypeOperation {
        RETRAIT, DEPOT
    }
}