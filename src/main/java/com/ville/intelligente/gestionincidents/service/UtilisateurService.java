package com.ville.intelligente.gestionincidents.service;

import com.ville.intelligente.gestionincidents.dto.CreateAdminAgentRequest;
import com.ville.intelligente.gestionincidents.dto.RegisterRequest;
import com.ville.intelligente.gestionincidents.model.CategorieIncident;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.Role;
import com.ville.intelligente.gestionincidents.repository.UtilisateurRepository;
import com.ville.intelligente.gestionincidents.dao.CategorieIncidentDAO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final CategorieIncidentDAO categorieIncidentDAO;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UtilisateurService(
            UtilisateurRepository utilisateurRepository,
            CategorieIncidentDAO categorieIncidentDAO,
            PasswordEncoder passwordEncoder,
            EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.categorieIncidentDAO = categorieIncidentDAO;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // =========================
    // INSCRIPTION CITOYEN
    // =========================
    public Utilisateur inscrireCitoyen(RegisterRequest request) {

        if (!request.getMotDePasse().equals(request.getConfirmMotDePasse())) {
            throw new RuntimeException("Les mots de passe ne correspondent pas");
        }

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(Role.ROLE_CITIZEN)
                .dateInscription(LocalDateTime.now())
                .actif(false)
                .emailVerifie(false)
                .tokenVerificationEmail(UUID.randomUUID().toString())
                .build();

        utilisateurRepository.save(utilisateur);

        String lienVerification = "http://localhost:8080/verify?token=" + utilisateur.getTokenVerificationEmail();

        String contenu = "<p>Bonjour " + utilisateur.getPrenom() + ",</p>" +
                "<p>Merci de vous être inscrit.</p>" +
                "<p>Cliquez sur le lien ci-dessous pour activer votre compte :</p>" +
                "<p><a href=\"" + lienVerification + "\">Activer mon compte</a></p>";

        emailService.envoyerEmail(
                utilisateur.getEmail(),
                "Activation de votre compte",
                contenu);

        return utilisateur;
    }

    // =========================
    // CREATION ADMIN / AGENT
    // =========================
    public Utilisateur creerUtilisateurParSuperAdmin(CreateAdminAgentRequest request) {

        if (request.getRole() == Role.ROLE_CITIZEN) {
            throw new IllegalArgumentException("Un citoyen doit passer par l'inscription publique");
        }

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        // Récupération du département
        CategorieIncident departement = categorieIncidentDAO.findById(request.getDepartementId())
                .orElseThrow(() -> new RuntimeException("Département introuvable"));

        // CAS : CREATION ADMIN
        if (request.getRole() == Role.ROLE_ADMIN && departement.getAdmin() != null) {
            throw new RuntimeException("Ce département possède déjà un administrateur");
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .motDePasse(passwordEncoder.encode(request.getMotDePasse()))
                .role(request.getRole())
                .dateInscription(LocalDateTime.now())
                .actif(true)
                .emailVerifie(true)
                .tokenVerificationEmail(null)
                .departement(departement)
                .build();

        utilisateurRepository.save(utilisateur);

        // AFFECTATION ADMIN → DEPARTEMENT
        if (request.getRole() == Role.ROLE_ADMIN) {
            departement.setAdmin(utilisateur);
            categorieIncidentDAO.save(departement);
        }

        return utilisateur;
    }

    // =========================
    // VERIFICATION EMAIL
    // =========================
    public void verifierEmail(String token) {
        Utilisateur utilisateur = utilisateurRepository
                .findByTokenVerificationEmail(token)
                .orElseThrow(() -> new RuntimeException("Token de vérification invalide"));

        utilisateur.setEmailVerifie(true);
        utilisateur.setActif(true);
        utilisateur.setTokenVerificationEmail(null);

        utilisateurRepository.save(utilisateur);
    }

    // =========================
    // RECHERCHE
    // =========================
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }
}
