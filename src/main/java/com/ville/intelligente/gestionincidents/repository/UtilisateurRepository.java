package com.ville.intelligente.gestionincidents.repository;

import com.ville.intelligente.gestionincidents.model.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findByTokenVerificationEmail(String token);

    boolean existsByEmail(String email);
}
