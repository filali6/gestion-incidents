package com.ville.intelligente.gestionincidents.metier;

import java.util.Map;

import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;

public interface StatistiqueService {
    // Compter le total d'incidents
    long compterTotalIncidents();

    // Statistiques par statut
    Map<String, Long> getStatistiquesParStatut();

    // Statistiques par catégorie
    Map<String, Long> getStatistiquesParCategorie();

    // Statistiques par quartier (top 5)
    Map<String, Long> getTop5Quartiers();
    long compterIncidentsParCitoyen(Utilisateur citoyen);
    Map<StatutIncident, Long> getStatistiquesParStatutPourCitoyen(Utilisateur citoyen);

    long compterIncidentsParAgent(Utilisateur agent);

    Map<StatutIncident, Long> getStatistiquesParStatutPourAgent(Utilisateur agent);

    long compterIncidentsParDepartement(Long departementId);

    Map<StatutIncident, Long> getStatistiquesParStatutPourDepartement(Long departementId);
    
}
