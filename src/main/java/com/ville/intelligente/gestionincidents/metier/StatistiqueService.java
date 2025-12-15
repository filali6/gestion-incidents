package com.ville.intelligente.gestionincidents.metier;

import java.util.Map;

public interface StatistiqueService {
    // Compter le total d'incidents
    long compterTotalIncidents();

    // Statistiques par statut
    Map<String, Long> getStatistiquesParStatut();

    // Statistiques par catégorie
    Map<String, Long> getStatistiquesParCategorie();

    // Statistiques par quartier (top 5)
    Map<String, Long> getTop5Quartiers();
    
}
