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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ville.intelligente.gestionincidents.dao.IncidentDAO;
import com.ville.intelligente.gestionincidents.metier.IncidentService;
import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;
import com.ville.intelligente.gestionincidents.service.UtilisateurService;

@Controller
@RequestMapping("/incident")
public class IncidentController {

    private final IncidentService incidentService;
    private final IncidentDAO incidentDAO;
    private final UtilisateurService utilisateurService;

    public IncidentController(IncidentService incidentService,IncidentDAO incidentDAO,
            UtilisateurService utilisateurService) {
        this.incidentService = incidentService;
        this.incidentDAO = incidentDAO;
        this.utilisateurService=utilisateurService;
        
    }

    //  afficher le formulaire
    @GetMapping("/declarer")
    public String showForm(Model model) {
        model.addAttribute("incident", new Incident());
        return "incident-form";
    }

    //   traiter la soumission du formulaire
    @PostMapping("/save")
    public String saveIncident(
        Incident incident,
        @RequestParam("photoFile") MultipartFile photoFile,@RequestParam("categorieNom") String categorieNom,      // ⭐ AJOUTE
    @RequestParam("quartierNom") String quartierNom,@RequestParam("quartierVille") String quartierVille,        // ⭐ AJOUTE
    @RequestParam("quartierCodePostal") int quartierCodePostal, @AuthenticationPrincipal UserDetails userDetails,RedirectAttributes redirectAttributes) 
          {
              Utilisateur citoyen = utilisateurService.findByEmail(userDetails.getUsername());
              incident.setCitoyen(citoyen);
              incident.setPriorite(2);

    Incident savedIncident =
            incidentService.saveIncidentWithPhoto(incident, photoFile, categorieNom, quartierNom, quartierVille,
                    quartierCodePostal);

    return "redirect:/citoyen/dashboard"  ;
}
    @GetMapping("/{id}")
    public String viewIncident(@PathVariable Long id, Model model) {
        Incident incident = incidentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé"));

        model.addAttribute("incident", incident);
        return "incident-detail";
    }
@PostMapping("/{id}/feedback")
@PreAuthorize("hasRole('CITIZEN')")
public String soumettFeedback(
        @PathVariable Long id,
        @RequestParam String feedbackCitoyen,
        @AuthenticationPrincipal UserDetails userDetails,
        RedirectAttributes redirectAttributes) {
    
    // Récupérer l'incident
    Incident incident = incidentDAO.findById(id)
            .orElseThrow(() -> new RuntimeException("Incident non trouvé"));
    
    // Vérifier que c'est bien le citoyen propriétaire
    Utilisateur citoyen = utilisateurService.findByEmail(userDetails.getUsername());
    if (!incident.getCitoyen().getId().equals(citoyen.getId())) {
        throw new RuntimeException("Vous n'êtes pas autorisé à donner un feedback sur cet incident");
    }
    
    // Vérifier que l'incident est bien RESOLU
    if (incident.getStatut() != StatutIncident.RESOLU) {
        throw new RuntimeException("Le feedback n'est possible que pour les incidents résolus");
    }
    
    // Enregistrer le feedback et clôturer
    incident.setFeedbackCitoyen(feedbackCitoyen);
    incident.setStatut(StatutIncident.CLOTURE);
    incidentDAO.save(incident);
    
    redirectAttributes.addFlashAttribute("success", "Merci pour votre feedback ! L'incident est maintenant clôturé.");
    return "redirect:/citoyen/dashboard";
}
    
     
    
}
