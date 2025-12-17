package com.ville.intelligente.gestionincidents.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Query(value = "SELECT AVG(DATEDIFF(date_resolution, date_declaration)) FROM incidents WHERE date_resolution IS NOT NULL", nativeQuery = true)
    Double getDelaiMoyenResolution();
    
     

    // Count par catégorie
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.categorie.nom = ?1")
    long countByCategorie(String categorieNom);

    // Count par quartier
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.quartier.nom = ?1")
    long countByQuartier(String quartierNom);

    // find par statut
    List<Incident> findByStatut(StatutIncident statut);

    // Récupérer par catégorie
    @Query("SELECT i FROM Incident i WHERE i.categorie.nom = ?1")
    List<Incident> findByCategorie(String categorieNom);


    @Query("SELECT i.categorie.nom, COUNT(i) FROM Incident i GROUP BY i.categorie.nom")
    List<Object[]> countByCategorie();

    @Query("SELECT i.quartier.nom, COUNT(i) FROM Incident i GROUP BY i.quartier.nom ORDER BY COUNT(i) DESC")
    List<Object[]> countByQuartier();

     

    //  filtres - Compare seulement la DATE  
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

    Page<Incident> findAll(Pageable pageable);
    
    @Query("SELECT i FROM Incident i " +
                    "LEFT JOIN i.categorie c " +
                    "LEFT JOIN i.quartier q " +
                    "WHERE (:statut IS NULL OR i.statut = :statut) " +
                    "AND (:categorie IS NULL OR :categorie = '' OR c.nom = :categorie) " +
                    "AND (:quartier IS NULL OR :quartier = '' OR q.nom = :quartier) " +
                    "AND (:dateDeclaration IS NULL OR CAST(i.dateDeclaration AS date) = :dateDeclaration)")
    Page<Incident> findByFiltersWithPageable(
                    @Param("statut") StatutIncident statut,
                    @Param("categorie") String categorie,
                    @Param("quartier") String quartier,
                    @Param("dateDeclaration") Date dateDeclaration,
                    Pageable pageable);


    }