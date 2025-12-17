package com.ville.intelligente.gestionincidents.repository;

import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    Optional<Utilisateur> findByEmail(String email);

    Optional<Utilisateur> findByTokenVerificationEmail(String token);

    boolean existsByEmail(String email);
     
    List<Utilisateur> findByDepartementIdAndRole(Long departementId, Role role);
    
    
    List<Utilisateur> findByDepartementId(Long departementId);
    
    
    List<Utilisateur> findByRole(Role role);
    
     
    @Query("SELECT u FROM Utilisateur u WHERE u.departement.id = :departementId AND u.role = :role")
    List<Utilisateur> findAgentsByDepartement(@Param("departementId") Long departementId, @Param("role") Role role);
}
