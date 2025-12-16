package com.ville.intelligente.gestionincidents.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ville.intelligente.gestionincidents.metier.StatistiqueService;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {
    private final StatistiqueService statistiqueService;

    public DashboardController(StatistiqueService statistiqueService) {
        this.statistiqueService = statistiqueService;
    }

    @GetMapping
    public String afficherDashboard(Model model) {
        // Passer les stats au template
        model.addAttribute("totalIncidents", statistiqueService.compterTotalIncidents());
        model.addAttribute("statsStatut", statistiqueService.getStatistiquesParStatut());
        model.addAttribute("statsCategorie", statistiqueService.getStatistiquesParCategorie());
        model.addAttribute("statsQuartiers", statistiqueService.getTop5Quartiers());

        return "dashboard";
    }

}
