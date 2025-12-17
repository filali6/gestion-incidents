package com.ville.intelligente.gestionincidents.metier;

 
import java.sql.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;

public interface IncidentService {
    Incident saveIncident(Incident incident);
    
    public Incident saveIncidentWithPhoto(Incident incident, MultipartFile photoFile,String categorieNom,
            String quartierNom, String quartierVille, int quartierCodePostal);
    
    List<Incident> findAll();
    List<Incident> findByFilters(StatutIncident statut, String categorie,String quartier, Date dateDeclaration);
    Incident changerStatut(Long incidentId, StatutIncident nouveauStatut, Utilisateur agent);
    
    List<Incident> getIncidentsParDepartement(Long departementId);

    List<Incident> getIncidentsByAgent(Utilisateur agent);

    List<Incident> getIncidentsByCitoyen(Utilisateur citoyen);
    
    Page<Incident> findAllWithPagination(Pageable pageable);

    Page<Incident> findByFiltersWithPagination(StatutIncident statut, String categorie, String quartier,
            Date dateDeclaration, Pageable pageable);
    
}
