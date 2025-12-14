package com.ville.intelligente.gestionincidents.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ville.intelligente.gestionincidents.model.Photo;

public interface PhotoDAO extends JpaRepository<Photo, Long> {
}