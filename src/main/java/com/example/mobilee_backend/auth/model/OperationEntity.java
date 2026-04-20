package com.example.mobilee_backend.auth.model;

import com.example.mobilee_backend.auth.model.AgentEntity;
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
        // Référence unique : 10 chiffres aléatoires
        if (this.reference == null) {
            long ref = 1_000_000_000L + (long)(Math.random() * 9_000_000_000L);
            this.reference = String.valueOf(ref);
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