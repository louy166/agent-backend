package com.example.mobilee_backend.auth.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(nullable = false, unique = true)
    private String reference;               // référence unique auto-générée

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOperation type;             // RETRAIT ou DEPOT

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private Double commission;              // calculée automatiquement

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "telephone_client")
    private String telephoneClient;         // nouveau champ

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentEntity agent;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
        // Référence unique : OP-YYYYMMDD-UUID(8 chars)
        if (this.reference == null) {
            String date = java.time.format.DateTimeFormatter
                    .ofPattern("yyyyMMdd").format(this.createdAt);
            this.reference = "OP-" + date + "-" +
                    UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        // Calcul automatique commission
        if (this.type == TypeOperation.RETRAIT) {
            this.commission = this.montant * 0.50;
        } else {
            this.commission = this.montant * 0.05;
        }
    }

    public enum TypeOperation { RETRAIT, DEPOT }
}