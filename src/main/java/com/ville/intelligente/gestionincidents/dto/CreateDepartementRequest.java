package com.ville.intelligente.gestionincidents.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateDepartementRequest {

    @NotBlank(message = "Le nom du département est obligatoire")
    @Size(min = 3, message = "Le nom du département doit contenir au moins 3 caractères")
    private String nom;
}
