package com.ville.intelligente.gestionincidents.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ville.intelligente.gestionincidents.metier.StatistiqueService;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.service.UtilisateurService;
import com.ville.intelligente.gestionincidents.dao.IncidentDAO;
import com.ville.intelligente.gestionincidents.model.enums.Role;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final StatistiqueService statistiqueService;
    private final UtilisateurService utilisateurService;
    private final IncidentDAO incidentDAO;

    public AdminController(StatistiqueService statistiqueService,
            UtilisateurService utilisateurService,
            IncidentDAO incidentDAO) {
        this.statistiqueService = statistiqueService;
        this.utilisateurService = utilisateurService;
        this.incidentDAO = incidentDAO;
    }

    @GetMapping("/dashboard")
    public String afficherDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur admin = utilisateurService.findByEmail(userDetails.getUsername());

        if (admin.getDepartement() == null) {
            model.addAttribute("erreur", "Aucun département assigné");
            return "dashboard";
        }

        Long deptId = admin.getDepartement().getId();

        // Incidents du département
        List<Incident> incidentsDept = incidentDAO.findByCategorie_Id(deptId);

        // Agents du département
        List<Utilisateur> agentsDept = utilisateurService.findAll().stream()
                .filter(u -> u.getDepartement() != null &&
                        u.getDepartement().getId().equals(deptId) &&
                        u.getRole() == Role.ROLE_AGENT)
                .collect(Collectors.toList());

        model.addAttribute("totalIncidents", incidentsDept.size());
        model.addAttribute("mesIncidents", incidentsDept);
        model.addAttribute("mesAgents", agentsDept);
        model.addAttribute("statsStatut", statistiqueService.getStatistiquesParStatutPourDepartement(deptId));
        model.addAttribute("userRole", "ADMIN");
        model.addAttribute("admin", admin);
        model.addAttribute("departement", admin.getDepartement());

        return "dashboard";
    }
}