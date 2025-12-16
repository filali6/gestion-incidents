package com.ville.intelligente.gestionincidents.controller;

import com.ville.intelligente.gestionincidents.dto.CreateAdminAgentRequest;
import com.ville.intelligente.gestionincidents.dto.CreateDepartementRequest;
import com.ville.intelligente.gestionincidents.model.CategorieIncident;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.Role;
import com.ville.intelligente.gestionincidents.service.UtilisateurService;
import com.ville.intelligente.gestionincidents.service.DepartementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Controller
@RequestMapping("/super-admin")
public class SuperAdminController {

    private final UtilisateurService utilisateurService;
    private final DepartementService departementService;

    public SuperAdminController(UtilisateurService utilisateurService,
            DepartementService departementService) {
        this.utilisateurService = utilisateurService;
        this.departementService = departementService;
    }

    // =========================
    // DASHBOARD SUPER ADMIN
    // =========================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Utilisateur> utilisateurs = utilisateurService.findAll();
        List<CategorieIncident> departements = departementService.findAll();
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("departements", departements);
        return "super-admin/dashboard";
    }

    // =========================
    // CREATION UTILISATEUR (ADMIN / AGENT)
    // =========================
    @GetMapping("/create-user")
    public String showCreateUserForm(Model model) {
        model.addAttribute("createUserRequest", new CreateAdminAgentRequest());
        model.addAttribute("departements", departementService.findAll());
        model.addAttribute("roles", List.of(Role.ROLE_ADMIN, Role.ROLE_AGENT));
        return "super-admin/create-user";
    }

    @PostMapping("/create-user")
    public String createUser(@Valid @ModelAttribute CreateAdminAgentRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            // ✅ Remettre createUserRequest pour Thymeleaf
            model.addAttribute("createUserRequest", request);
            model.addAttribute("departements", departementService.findAll());
            model.addAttribute("roles", List.of(Role.ROLE_ADMIN, Role.ROLE_AGENT));
            return "super-admin/create-user";
        }

        try {
            utilisateurService.creerUtilisateurParSuperAdmin(request);
            model.addAttribute("successMessage", "Utilisateur créé avec succès !");
        } catch (RuntimeException e) {
            // ✅ Remettre createUserRequest pour Thymeleaf
            model.addAttribute("createUserRequest", request);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departements", departementService.findAll());
            model.addAttribute("roles", List.of(Role.ROLE_ADMIN, Role.ROLE_AGENT));
            return "super-admin/create-user";
        }

        return "redirect:/super-admin/dashboard";
    }

    // =========================
    // CREATION DEPARTEMENT
    // =========================
    @GetMapping("/create-departement")
    public String showCreateDepartementForm(Model model) {
        model.addAttribute("createDepartementRequest", new CreateDepartementRequest());
        return "super-admin/create-departement";
    }

    @PostMapping("/create-departement")
    public String createDepartement(@Valid @ModelAttribute CreateDepartementRequest request,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("createDepartementRequest", request);
            return "super-admin/create-departement";
        }

        try {
            departementService.creerDepartement(request);
            model.addAttribute("successMessage", "Département créé avec succès !");
        } catch (RuntimeException e) {
            model.addAttribute("createDepartementRequest", request);
            model.addAttribute("errorMessage", e.getMessage());
            return "super-admin/create-departement";
        }

        return "redirect:/super-admin/dashboard";
    }
}
