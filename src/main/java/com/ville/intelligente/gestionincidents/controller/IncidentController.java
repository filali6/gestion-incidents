package com.ville.intelligente.gestionincidents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ville.intelligente.gestionincidents.dao.IncidentDAO;
import com.ville.intelligente.gestionincidents.metier.IncidentService;
import com.ville.intelligente.gestionincidents.model.Incident;

@Controller
@RequestMapping("/incident")
public class IncidentController {

    private final IncidentService incidentService;
    private final IncidentDAO incidentDAO;

    public IncidentController(IncidentService incidentService,IncidentDAO incidentDAO) {
        this.incidentService = incidentService;
        this.incidentDAO = incidentDAO;
        
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
    @RequestParam("quartierCodePostal") int quartierCodePostal) 
          {

    Incident savedIncident =
            incidentService.saveIncidentWithPhoto(incident, photoFile, categorieNom, quartierNom, quartierVille,
                    quartierCodePostal);

    return "redirect:/incident/" + savedIncident.getId();
}
    @GetMapping("/{id}")
    public String viewIncident(@PathVariable Long id, Model model) {
        Incident incident = incidentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Incident non trouvé"));

        model.addAttribute("incident", incident);
        return "incident-detail";
    }
    
     
    
}
