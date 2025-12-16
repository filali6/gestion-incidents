package com.ville.intelligente.gestionincidents.controller;

import com.ville.intelligente.gestionincidents.dto.RegisterRequest;
import com.ville.intelligente.gestionincidents.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UtilisateurService utilisateurService;

    public AuthController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    // LOGIN

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    // REGISTER

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("registerRequest") RegisterRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            utilisateurService.inscrireCitoyen(request);
            model.addAttribute(
                    "successMessage",
                    "Inscription réussie ! Vérifiez votre email pour activer votre compte.");
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "auth/register";
        }

        return "redirect:/login";
    }

    // EMAIL VERIFICATION

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token) {
        utilisateurService.verifierEmail(token);
        return "redirect:/login?verified";
    }
}
