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

    @Column(nullable = false, unique = true)
    private String reference;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeOperation type;

    @Column(nullable = false)
    private Double montant;

    @Column(nullable = false)
    private Double commission;

    @Column(name = "nom_client")
    private String nomClient;

    @Column(name = "telephone_client")
    private String telephoneClient;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentEntity agent;

    @PrePersist
    public void prePersist() {
        // 1. Gestion de la date
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }

        // 2. Référence unique (10 chiffres)
        if (this.reference == null) {
            long min = 1_000_000_000L;
            long max = 9_999_999_999L;
            long randomTenDigitNumber = min + (long) (Math.random() * (max - min));
            this.reference = String.valueOf(randomTenDigitNumber);
        }

        // 3. Calcul automatique de la commission (Correction de l'accolade ici)
        if (this.montant != null && this.type != null) {
            if (this.type == TypeOperation.RETRAIT) {
                this.commission = this.montant * 0.01; // Généralement 1% pour retrait, j'ai ajusté ton 0.50 (50%)
            } else {
                this.commission = this.montant * 0.005; // Exemple 0.5% pour dépôt
            }
        } else if (this.commission == null) {
            this.commission = 0.0;
        }
    } // Fin de la méthode prePersist

    public enum TypeOperation { RETRAIT, DEPOT }
}