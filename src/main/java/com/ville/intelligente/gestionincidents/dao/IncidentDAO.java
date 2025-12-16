package com.ville.intelligente.gestionincidents.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;

import java.sql.Date;
import java.util.List;

public interface IncidentDAO extends JpaRepository<Incident, Long> {



    // Compter par statut
    long countByStatut(StatutIncident statut);
    
    List<Incident> findByCategorie_Id(Long departementId);

    List<Incident> findByCitoyen(Utilisateur citoyen);
    
    List<Incident> findByAgentAssigne(Utilisateur agent);

    // Compter par catégorie
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.categorie.nom = ?1")
    long countByCategorie(String categorieNom);

    // Compter par quartier
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.quartier.nom = ?1")
    long countByQuartier(String quartierNom);

    // Récupérer tous par statut
    List<Incident> findByStatut(StatutIncident statut);

    // Récupérer par catégorie
    @Query("SELECT i FROM Incident i WHERE i.categorie.nom = ?1")
    List<Incident> findByCategorie(String categorieNom);


    @Query("SELECT i.categorie.nom, COUNT(i) FROM Incident i GROUP BY i.categorie.nom")
    List<Object[]> countByCategorie();

    @Query("SELECT i.quartier.nom, COUNT(i) FROM Incident i GROUP BY i.quartier.nom ORDER BY COUNT(i) DESC")
    List<Object[]> countByQuartier();

    // Pour le graphique temporel
    @Query("SELECT FUNCTION('DATE', i.dateDeclaration), COUNT(i) FROM Incident i GROUP BY FUNCTION('DATE', i.dateDeclaration) ORDER BY FUNCTION('DATE', i.dateDeclaration)")
    List<Object[]> countByDate();

    // Recherche avec filtres - Compare seulement la DATE (pas l'heure)
    @Query("SELECT i FROM Incident i WHERE " +
            "(:statut IS NULL OR i.statut = :statut) AND " +
            "(:categorie IS NULL OR :categorie = '' OR i.categorie.nom = :categorie) AND " +
            "(:quartier IS NULL OR :quartier = '' OR i.quartier.nom = :quartier) AND " +
            "(:dateDeclaration IS NULL OR DATE(i.dateDeclaration) = :dateDeclaration)")
    List<Incident> findByFilters(
            @Param("statut") StatutIncident statut,
            @Param("categorie") String categorie,
            @Param("quartier") String quartier,
            @Param("dateDeclaration")  Date dateDeclaration);

    }