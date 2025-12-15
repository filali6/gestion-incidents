package com.ville.intelligente.gestionincidents.security;

import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.service.UtilisateurService;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurService utilisateurService;

    public CustomUserDetailsService(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {

        Utilisateur utilisateur = utilisateurService.findByEmail(email);

        if (!utilisateur.isEmailVerifie()) {
            throw new DisabledException("Email non vérifié");
        }

        return new CustomUserDetails(utilisateur);
    }
}
