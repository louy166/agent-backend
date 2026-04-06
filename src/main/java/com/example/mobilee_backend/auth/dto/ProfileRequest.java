package com.example.mobilee_backend.auth.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProfileRequest {

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 2, message = "Le nom doit contenir au moins 2 caractères")
    private String nom;

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[0-9]{8}$", message = "Le téléphone doit contenir 8 chiffres")
    private String telephone;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    private String nomAgence;
    private String adresse;
    private String nni;
    private String typePersonne;   // MORAL, PHYSIQUE
    private String typeOuverture;  // MOBILE, WEB

    // Changement de mot de passe (optionnel)
    private String ancienMotDePasse;
    private String nouveauMotDePasse;
    private String confirmerMotDePasse;
}