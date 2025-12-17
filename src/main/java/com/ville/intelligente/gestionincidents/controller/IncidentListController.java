package com.ville.intelligente.gestionincidents.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        @RequestParam(defaultValue = "0") int page,  
        @RequestParam(defaultValue = "10") int size,  
        @RequestParam(defaultValue = "dateDeclaration") String sortBy,  
        @RequestParam(defaultValue = "desc") String sortDir,  
    
    
        Model model) {
            StatutIncident statutEnum = (statut != null && !statut.isEmpty())
                ? StatutIncident.valueOf(statut)
                : null;
            Date sqlDate = null;

            if (dateDeclaration != null && !dateDeclaration.isEmpty()) {
                sqlDate = Date.valueOf(dateDeclaration); // conversion String -> java.sql.Date
            }
        //tri
        Sort sort = sortDir.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();

        //pagination
        Pageable pageable = PageRequest.of(page, size, sort);

        // incidents avec pagination
        Page<Incident> incidentsPage;

        if (statutEnum != null || (categorie != null && !categorie.isEmpty()) ||
                (quartier != null && !quartier.isEmpty()) || dateDeclaration != null) {
            incidentsPage = incidentService.findByFiltersWithPagination(statutEnum, categorie, quartier, sqlDate, pageable);
        } else {
            incidentsPage = incidentService.findAllWithPagination(pageable);
        }

        model.addAttribute("incidents", incidentsPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", incidentsPage.getTotalPages());
        model.addAttribute("totalItems", incidentsPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("quartiers", quartierDAO.findAll());

        return "incident-list";
    }
}