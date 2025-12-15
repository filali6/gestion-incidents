package com.ville.intelligente.gestionincidents.metier;

import org.springframework.web.multipart.MultipartFile;

import com.ville.intelligente.gestionincidents.model.Incident;

public interface IncidentService {
    Incident saveIncident(Incident incident);
    
    public Incident saveIncidentWithPhoto(Incident incident, MultipartFile photoFile,String categorieNom,
            String quartierNom, String quartierVille, int quartierCodePostal);
}
