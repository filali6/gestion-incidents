package com.ville.intelligente.gestionincidents.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ville.intelligente.gestionincidents.model.Incident;

public interface IncidentDAO extends JpaRepository<Incident, Long> {
}
