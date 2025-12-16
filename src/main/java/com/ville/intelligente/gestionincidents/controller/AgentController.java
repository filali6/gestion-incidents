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

import java.util.List;

@Controller
@RequestMapping("/agent")
@PreAuthorize("hasRole('AGENT')")
public class AgentController {

    private final StatistiqueService statistiqueService;
    private final UtilisateurService utilisateurService;
    private final IncidentDAO incidentDAO;

    public AgentController(StatistiqueService statistiqueService,
            UtilisateurService utilisateurService,
            IncidentDAO incidentDAO) {
        this.statistiqueService = statistiqueService;
        this.utilisateurService = utilisateurService;
        this.incidentDAO = incidentDAO;
    }

    @GetMapping("/dashboard")
    public String afficherDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        // Récupérer l'agent connecté
        Utilisateur agent = utilisateurService.findByEmail(userDetails.getUsername());

        // Récupérer UNIQUEMENT les incidents assignés à cet agent
        List<Incident> mesIncidents = incidentDAO.findByAgentAssigne(agent);

        // Stats personnalisées
        long totalMesIncidents = mesIncidents.size();

        model.addAttribute("totalIncidents", totalMesIncidents);
        model.addAttribute("mesIncidents", mesIncidents);
        model.addAttribute("statsStatut", statistiqueService.getStatistiquesParStatutPourAgent(agent));
        model.addAttribute("userRole", "AGENT");
        model.addAttribute("agent", agent);

        return "dashboard";
    }
}