package com.ville.intelligente.gestionincidents.controller;

import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.service.UtilisateurService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UtilisateurService utilisateurService;

    public ProfileController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    /**
     * Affichage du profil utilisateur connecté
     */
    @GetMapping
    public String afficherProfil(Authentication authentication, Model model) {

        String emailConnecte = authentication.getName();
        Utilisateur utilisateur = utilisateurService.getProfilUtilisateur(emailConnecte);

        model.addAttribute("utilisateur", utilisateur);

        return "profile";
    }

    /**
     * Mise à jour des informations personnelles
     */
    @PostMapping("/update")
    public String mettreAJourProfil(
            Authentication authentication,
            @RequestParam @NotBlank String nom,
            @RequestParam @NotBlank String prenom,
            @RequestParam @NotBlank String telephone,
            RedirectAttributes redirectAttributes) {

        try {
            String emailConnecte = authentication.getName();

            utilisateurService.mettreAJourProfil(
                    emailConnecte,
                    nom,
                    prenom,
                    telephone);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Profil mis à jour avec succès");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
        }

        return "redirect:/profile";
    }

    /**
     * Changement du mot de passe
     */
    @PostMapping("/change-password")
    public String changerMotDePasse(
            Authentication authentication,
            @RequestParam String ancienMotDePasse,
            @RequestParam String nouveauMotDePasse,
            @RequestParam String confirmationMotDePasse,
            RedirectAttributes redirectAttributes) {

        try {
            String emailConnecte = authentication.getName();

            utilisateurService.changerMotDePasse(
                    emailConnecte,
                    ancienMotDePasse,
                    nouveauMotDePasse,
                    confirmationMotDePasse);

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Mot de passe modifié avec succès");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    e.getMessage());
        }

        return "redirect:/profile";
    }
}
