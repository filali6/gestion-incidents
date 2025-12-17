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
@RequestMapping("/citoyen")
@PreAuthorize("hasRole('CITIZEN')")
public class CitoyenController {

    private final StatistiqueService statistiqueService;
    private final UtilisateurService utilisateurService;
    private final IncidentDAO incidentDAO;

    public CitoyenController(StatistiqueService statistiqueService, UtilisateurService utilisateurService,
            IncidentDAO incidentDAO) {
        this.statistiqueService = statistiqueService;
        this.incidentDAO=incidentDAO;
        this.utilisateurService=utilisateurService;
    }

    @GetMapping("/dashboard")
    public String afficherDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {

        
        Utilisateur citoyen = utilisateurService.findByEmail(userDetails.getUsername()); 

       
        List<Incident> mesIncidents = incidentDAO.findByCitoyen(citoyen);

       
        long totalMesIncidents = mesIncidents.size();
         
        model.addAttribute("totalIncidents", totalMesIncidents);
        model.addAttribute("mesIncidents", mesIncidents);
        model.addAttribute("statsStatut", statistiqueService.getStatistiquesParStatutPourCitoyen(citoyen));
        model.addAttribute("userRole", "CITIZEN");
        model.addAttribute("citoyen", citoyen);

        return "dashboard";
    }
}