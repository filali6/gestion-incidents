package com.ville.intelligente.gestionincidents.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ville.intelligente.gestionincidents.model.CategorieIncident;

public interface CategorieIncidentDAO extends JpaRepository<CategorieIncident, Long> {
    Optional<CategorieIncident> findByNom(String nom);
}
