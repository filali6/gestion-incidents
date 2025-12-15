package com.ville.intelligente.gestionincidents.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ville.intelligente.gestionincidents.dao.QuartierDAO;
import com.ville.intelligente.gestionincidents.metier.IncidentService;
import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;

@Controller
public class IncidentListController {

    private final IncidentService incidentService;
    private final QuartierDAO quartierDAO;

    public IncidentListController(IncidentService incidentService, QuartierDAO quartierDAO) {
        this.incidentService = incidentService;
        this.quartierDAO=quartierDAO;
    }

    @GetMapping("/incidents/liste")
    public String listeIncidents(
        @RequestParam(required = false) String statut,
        @RequestParam(required = false) String categorie,
        @RequestParam(required = false) String quartier,
        @RequestParam(required = false) String dateDeclaration ,
     
    
        Model model) {
            StatutIncident statutEnum = (statut != null && !statut.isEmpty())
                ? StatutIncident.valueOf(statut)
                : null;
            Date sqlDate = null;

            if (dateDeclaration != null && !dateDeclaration.isEmpty()) {
                sqlDate = Date.valueOf(dateDeclaration); // conversion String -> java.sql.Date
            }
        List<Incident> incidents;
        
        
         if (statutEnum != null || (categorie != null && !categorie.isEmpty()) ||
                (quartier != null && !quartier.isEmpty()) || dateDeclaration != null) {

            incidents = incidentService.findByFilters(statutEnum, categorie, quartier, sqlDate);
        } else {
            incidents = incidentService.findAll();
        }

        model.addAttribute("incidents", incidents);
        model.addAttribute("quartiers", quartierDAO.findAll());

        return "incident-list";
         
    }
}