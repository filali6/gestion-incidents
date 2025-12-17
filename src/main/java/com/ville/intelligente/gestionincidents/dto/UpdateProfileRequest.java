package com.ville.intelligente.gestionincidents.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {

    @NotBlank
    private String nom;

    @NotBlank
    private String prenom;

    @Email
    @NotBlank
    private String email;

    private String telephone;

    private String nouveauMotDePasse;
    private String confirmationMotDePasse;
}
