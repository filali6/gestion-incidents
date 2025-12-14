package com.ville.intelligente.gestionincidents.dao;

 

import org.springframework.data.jpa.repository.JpaRepository;
import com.ville.intelligente.gestionincidents.model.Quartier;
import java.util.Optional;

public interface QuartierDAO extends JpaRepository<Quartier, Long> {
    Optional<Quartier> findByNom(String nom);
}
