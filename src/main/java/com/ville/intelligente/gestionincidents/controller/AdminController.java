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

import com.ville.intelligente.gestionincidents.metier.StatistiqueService;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.service.UtilisateurService;
import com.ville.intelligente.gestionincidents.dao.IncidentDAO;
import com.ville.intelligente.gestionincidents.model.enums.Role;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;
import com.ville.intelligente.gestionincidents.service.EmailService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final StatistiqueService statistiqueService;
    private final UtilisateurService utilisateurService;
    private final IncidentDAO incidentDAO;
    private final EmailService emailService;



    public AdminController(StatistiqueService statistiqueService,
            UtilisateurService utilisateurService,
            IncidentDAO incidentDAO, EmailService emailService) {
        this.statistiqueService = statistiqueService;
        this.utilisateurService = utilisateurService;
        this.incidentDAO = incidentDAO;
        this.emailService=emailService;
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
    
    @GetMapping("/assigner-incident/{id}")
    public String afficherPageAssignation(@PathVariable Long id,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        Utilisateur admin = utilisateurService.findByEmail(userDetails.getUsername());
        Incident incident = incidentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé"));

        // Vérifier que l'incident est bien dans le département de l'admin
        if (admin.getDepartement() == null ||
                incident.getCategorie() == null ||
                !incident.getCategorie().getId().equals(admin.getDepartement().getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à gérer cet incident");
        }

        // Récupérer les agents de son département
        List<Utilisateur> agentsDepartement = utilisateurService.findAll().stream()
                .filter(u -> u.getDepartement() != null &&
                        u.getDepartement().getId().equals(admin.getDepartement().getId()) &&
                        u.getRole() == Role.ROLE_AGENT)
                .collect(Collectors.toList());

        model.addAttribute("incident", incident);
        model.addAttribute("agents", agentsDepartement);

        return "admin/assigner-incident";
    }
    // ✅ NOUVELLE MÉTHODE : Traiter l'assignation
    @PostMapping("/assigner-incident/{id}")
    public String assignerIncident(
            @PathVariable Long id,
            @RequestParam Long agentId,
            @RequestParam int priorite,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        
        Utilisateur admin = utilisateurService.findByEmail(userDetails.getUsername());
        Incident incident = incidentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé"));
        Utilisateur agent = utilisateurService.findAll().stream()
                .filter(u -> u.getId().equals(agentId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Agent non trouvé"));
        
        // Vérifier que l'incident est bien dans le département de l'admin
        if (admin.getDepartement() == null || 
            incident.getCategorie() == null ||
            !incident.getCategorie().getId().equals(admin.getDepartement().getId())) {
            throw new RuntimeException("Vous n'êtes pas autorisé à gérer cet incident");
        }
        
        // Assigner l'incident
        incident.setAgentAssigne(agent);
        incident.setPriorite(priorite);
        incident.setStatut(StatutIncident.PRIS_EN_CHARGE);
        incidentDAO.save(incident);
        
        // ✅ ENVOI DES EMAILS
        // Email à l'agent
        String sujetAgent = "Nouvel incident assigné";
        String contenuAgent = "<h2>Bonjour " + agent.getPrenom() + ",</h2>" +
                "<p>Un nouvel incident vous a été assigné :</p>" +
                "<ul>" +
                "<li><strong>Titre :</strong> " + incident.getTitre() + "</li>" +
                "<li><strong>Description :</strong> " + incident.getDescription() + "</li>" +
                "<li><strong>Priorité :</strong> " + 
                    (priorite == 1 ? "Faible" : priorite == 2 ? "Moyenne" : priorite == 3 ? "Haute" : "Critique") + "</li>" +
                "<li><strong>Adresse :</strong> " + incident.getAdresse() + "</li>" +
                "</ul>" +
                "<p>Veuillez vous en occuper dès que possible.</p>" +
                "<p><a href='http://localhost:8080/incident/" + incident.getId() + "'>Voir l'incident</a></p>";
        
        emailService.envoyerEmail(agent.getEmail(), sujetAgent, contenuAgent);
        
        // Email au citoyen
        if (incident.getCitoyen() != null) {
            String sujetCitoyen = "Votre incident a été pris en charge";
            String contenuCitoyen = "<h2>Bonjour " + incident.getCitoyen().getPrenom() + ",</h2>" +
                    "<p>Bonne nouvelle ! Votre incident <strong>\"" + incident.getTitre() + "\"</strong> a été pris en charge.</p>" +
                    "<p>Un agent va s'en occuper très prochainement.</p>" +
                    "<p><a href='http://localhost:8080/incident/" + incident.getId() + "'>Suivre l'évolution de votre incident</a></p>";
            
            emailService.envoyerEmail(incident.getCitoyen().getEmail(), sujetCitoyen, contenuCitoyen);
        }
        
        redirectAttributes.addFlashAttribute("success", 
            "Incident assigné à " + agent.getPrenom() + " " + agent.getNom() + " avec succès ! Emails envoyés.");
        
        return "redirect:/admin/dashboard";
    }

     
        
}