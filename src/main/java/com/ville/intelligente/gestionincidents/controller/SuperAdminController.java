package com.ville.intelligente.gestionincidents.controller;

import com.ville.intelligente.gestionincidents.dto.CreateAdminAgentRequest;
import com.ville.intelligente.gestionincidents.dto.CreateDepartementRequest;
import com.ville.intelligente.gestionincidents.metier.StatistiqueService;
import com.ville.intelligente.gestionincidents.model.CategorieIncident;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.Role;
import com.ville.intelligente.gestionincidents.service.UtilisateurService;
import com.ville.intelligente.gestionincidents.service.DepartementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.stream.Collectors;
import com.ville.intelligente.gestionincidents.model.enums.Role;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
 

@Controller
@RequestMapping("/super-admin")
public class SuperAdminController {

    private final UtilisateurService utilisateurService;
    private final DepartementService departementService;
    private final StatistiqueService statistiqueService;

    public SuperAdminController(UtilisateurService utilisateurService,
            DepartementService departementService, StatistiqueService statistiqueService) {
        this.utilisateurService = utilisateurService;
        this.departementService = departementService;
        this.statistiqueService=statistiqueService;
    }

     
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<Utilisateur> utilisateurs = utilisateurService.findAll();
        List<CategorieIncident> departements = departementService.findAll();
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("departements", departements);
   
        model.addAttribute("totalIncidents", statistiqueService.compterTotalIncidents());
        model.addAttribute("statsStatut", statistiqueService.getStatistiquesParStatut());
        model.addAttribute("statsCategorie", statistiqueService.getStatistiquesParCategorie());
        model.addAttribute("statsQuartiers", statistiqueService.getTop5Quartiers());

        model.addAttribute("totalUtilisateurs", utilisateurs.size());
        model.addAttribute("totalDepartements", departements.size());
 
        model.addAttribute("userRole", "SUPER_ADMIN");
        return "dashboard";
    }
 
    @GetMapping("/create-user")
    public String showCreateUserForm(Model model) {
        model.addAttribute("createUserRequest", new CreateAdminAgentRequest());
        model.addAttribute("departements", departementService.findAll());
        model.addAttribute("roles", List.of(Role.ROLE_ADMIN, Role.ROLE_AGENT));
        return "super-admin/create-user";
    }

    @PostMapping("/create-user")
    public String createUser(@Valid @ModelAttribute CreateAdminAgentRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            
            model.addAttribute("createUserRequest", request);
            model.addAttribute("departements", departementService.findAll());
            model.addAttribute("roles", List.of(Role.ROLE_ADMIN, Role.ROLE_AGENT));
            return "super-admin/create-user";
        }

        try {
            utilisateurService.creerUtilisateurParSuperAdmin(request);
            model.addAttribute("successMessage", "Utilisateur créé avec succès !");
        } catch (RuntimeException e) {
            
            model.addAttribute("createUserRequest", request);
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("departements", departementService.findAll());
            model.addAttribute("roles", List.of(Role.ROLE_ADMIN, Role.ROLE_AGENT));
            return "super-admin/create-user";
        }

        return "dashboard";
    }

    
    @GetMapping("/create-departement")
    public String showCreateDepartementForm(Model model) {
        model.addAttribute("createDepartementRequest", new CreateDepartementRequest());
        return "super-admin/create-departement";
    }

    @PostMapping("/create-departement")
    public String createDepartement(@Valid @ModelAttribute CreateDepartementRequest request,
            BindingResult result,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("createDepartementRequest", request);
            return "super-admin/create-departement";
        }

        try {
            departementService.creerDepartement(request);
            model.addAttribute("successMessage", "Département créé avec succès !");
        } catch (RuntimeException e) {
            model.addAttribute("createDepartementRequest", request);
            model.addAttribute("errorMessage", e.getMessage());
            return "super-admin/create-departement";
        }

        return "dashboard";
    }
    @GetMapping("/utilisateurs")
    public String listeUtilisateurs(@RequestParam(required = false) String role, Model model) {
        List<Utilisateur> utilisateurs;
        
        // Filtrage par rôle si spécifié
        if (role != null && !role.isEmpty()) {
            Role roleEnum = Role.valueOf(role);
            utilisateurs = utilisateurService.findAll().stream()
                    .filter(u -> u.getRole() == roleEnum)
                    .collect(Collectors.toList());
        } else {
            utilisateurs = utilisateurService.findAll();
        }
        
        model.addAttribute("utilisateurs", utilisateurs);
        model.addAttribute("roleFiltre", role);
        model.addAttribute("roles", Role.values());
        
        return "super-admin/utilisateurs";
}
@GetMapping("/departements")
public String listeDepartements(Model model) {
    List<CategorieIncident> departements = departementService.findAll();
    
    // Compter le nombre d'agents par département
    Map<Long, Long> nbAgentsParDept = new HashMap<>();
    for (CategorieIncident dept : departements) {
        long nbAgents = utilisateurService.findAll().stream()
                .filter(u -> u.getDepartement() != null && 
                            u.getDepartement().getId().equals(dept.getId()) && 
                            u.getRole() == Role.ROLE_AGENT)
                .count();
        nbAgentsParDept.put(dept.getId(), nbAgents);
    }
    long nbAvecAdmin = departements.stream().filter(d -> d.getAdmin() != null).count();
    long nbSansAdmin = departements.stream().filter(d -> d.getAdmin() == null).count();
    
    model.addAttribute("departements", departements);
    model.addAttribute("nbAgentsParDept", nbAgentsParDept);
    model.addAttribute("nbAvecAdmin", nbAvecAdmin);
    model.addAttribute("nbSansAdmin", nbSansAdmin);
    
    return "super-admin/departements";
}
}
