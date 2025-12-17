package com.ville.intelligente.gestionincidents.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ville.intelligente.gestionincidents.metier.IncidentService;
import com.ville.intelligente.gestionincidents.metier.StatistiqueService;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;
import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.service.EmailService;
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
    private final IncidentService incidentService;
    private final EmailService emailService;

    public AgentController(StatistiqueService statistiqueService,
            UtilisateurService utilisateurService,
            IncidentDAO incidentDAO, IncidentService incidentService, EmailService emailService) {
        this.statistiqueService = statistiqueService;
        this.utilisateurService = utilisateurService;
        this.incidentDAO = incidentDAO;
        this.incidentService=incidentService;
        this.emailService=emailService;
    }

    @GetMapping("/dashboard")
    public String afficherDashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur agent = utilisateurService.findByEmail(userDetails.getUsername());

        List<Incident> mesIncidents = incidentDAO.findByAgentAssigne(agent);

        long totalMesIncidents = mesIncidents.size();

        model.addAttribute("totalIncidents", totalMesIncidents);
        model.addAttribute("mesIncidents", mesIncidents);
        model.addAttribute("statsStatut", statistiqueService.getStatistiquesParStatutPourAgent(agent));
        model.addAttribute("userRole", "AGENT");
        model.addAttribute("agent", agent);

        return "dashboard";
    }
    @PostMapping("/incident/{id}/changer-statut")
    public String changerStatut(@PathVariable Long id,
                                @RequestParam("statut") String nouveauStatutStr,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
             
            Utilisateur agent = utilisateurService.findByEmail(userDetails.getUsername());
            
             
            StatutIncident nouveauStatut = StatutIncident.valueOf(nouveauStatutStr);
            Incident incident = incidentDAO.findById(id)
                    .orElseThrow(() -> new RuntimeException("Incident non trouvé"));
            
             
            incidentService.changerStatut(id, nouveauStatut, agent);

            if (nouveauStatut == StatutIncident.RESOLU && incident.getCitoyen() != null) {
                String sujet = "Votre incident a été résolu !";
                String contenu = "<h2>Bonjour " + incident.getCitoyen().getPrenom() + ",</h2>" +
                        "<p>Bonne nouvelle ! Votre incident <strong>\"" + incident.getTitre()
                        + "\"</strong> a été résolu.</p>" +
                        "<p>Nous vous invitons à consulter la résolution et à nous donner votre feedback.</p>" +
                        "<p><a href='http://localhost:8080/incident/" + incident.getId()
                        + "'>Donner mon feedback</a></p>" +
                        "<p>Merci de votre confiance !</p>";

                emailService.envoyerEmail(incident.getCitoyen().getEmail(), sujet, contenu);
            }
            
            
            redirectAttributes.addFlashAttribute("success", "Statut changé avec succès !");
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Statut invalide");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        
         
        return "redirect:/incident/" + id;
    }
}