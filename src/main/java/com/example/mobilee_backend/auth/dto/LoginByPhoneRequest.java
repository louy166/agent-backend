package com.example.mobilee_backend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginByPhoneRequest {

    @NotBlank(message = "Le téléphone est obligatoire")
    @Pattern(regexp = "^[234][0-9]{7}$", message = "Le téléphone doit commencer par 2, 3 ou 4 et contenir 8 chiffres")
    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    private String motDePasse;
}