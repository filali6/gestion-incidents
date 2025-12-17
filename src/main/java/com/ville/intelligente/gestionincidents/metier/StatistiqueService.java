package com.ville.intelligente.gestionincidents.metier;

import java.util.Map;

import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;

public interface StatistiqueService {
     
    long compterTotalIncidents();

    Double getDelaiMoyenResolution();

     
    Map<String, Long> getStatistiquesParStatut();

     
    Map<String, Long> getStatistiquesParCategorie();

     
    Map<String, Long> getTop5Quartiers();
    long compterIncidentsParCitoyen(Utilisateur citoyen);
    Map<String, Long> getStatistiquesParStatutPourCitoyen(Utilisateur citoyen);

    long compterIncidentsParAgent(Utilisateur agent);

    Map<String, Long> getStatistiquesParStatutPourAgent(Utilisateur agent);

    long compterIncidentsParDepartement(Long departementId);

    Map<String, Long> getStatistiquesParStatutPourDepartement(Long departementId);
    
}
