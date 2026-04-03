package com.example.mobilee_backend.auth.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false, unique = true)
    private String telephone;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String motDePasse;

    @Column(name = "nom_agence")
    private String nomAgence;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;


    @Column(unique = true)
    private String nni;                     // Numéro National d'Identification

    @Column
    private String adresse;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_personne")
    private TypePersonne typePersonne;      // MORAL ou PHYSIQUE

    @Enumerated(EnumType.STRING)
    @Column(name = "statut")
    @Builder.Default
    private Statut statut = Statut.ACTIF;   // ACTIF, BLOQUE, FERME, SUSPENDU

    @Enumerated(EnumType.STRING)
    @Column(name = "type_ouverture")
    private TypeOuverture typeOuverture;

    @PrePersist
    public void prePersist() {
        if (this.role == null) this.role = Role.AGENT;
        if (this.statut == null) this.statut = Statut.ACTIF;
        if (this.createdAt == null) this.createdAt = LocalDateTime.now();
    }

    public enum Role {
        AGENT, ADMIN
    }

    public enum TypePersonne { MORAL, PHYSIQUE }
    public enum Statut       { ACTIF, BLOQUE, FERME, SUSPENDU }
    public enum TypeOuverture { MOBILE, WEB }
}