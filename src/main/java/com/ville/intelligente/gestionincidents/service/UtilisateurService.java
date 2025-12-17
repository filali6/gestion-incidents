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

    
    public Utilisateur creerUtilisateurParSuperAdmin(CreateAdminAgentRequest request) {

        if (request.getRole() == Role.ROLE_CITIZEN) {
            throw new IllegalArgumentException("Un citoyen doit passer par l'inscription publique");
        }

        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email déjà utilisé");
        }

        CategorieIncident departement = categorieIncidentDAO.findById(request.getDepartementId())
                .orElseThrow(() -> new RuntimeException("Département introuvable"));

        if (request.getRole() == Role.ROLE_ADMIN && departement.getAdmin() != null) {
            throw new RuntimeException("Ce département possède déjà un administrateur");
        }

        
        String motDePasse = request.getMotDePasse();
        if (motDePasse == null || motDePasse.isEmpty()) {
            motDePasse = UUID.randomUUID().toString().substring(0, 8);  
        }

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .email(request.getEmail())
                .telephone(request.getTelephone())
                .motDePasse(passwordEncoder.encode(motDePasse))
                .role(request.getRole())
                .dateInscription(LocalDateTime.now())
                .actif(true)
                .emailVerifie(true)
                .tokenVerificationEmail(null)
                .departement(departement)
                .build();

        utilisateurRepository.save(utilisateur);

        if (request.getRole() == Role.ROLE_ADMIN) {
            departement.setAdmin(utilisateur);
            categorieIncidentDAO.save(departement);
        }

     
        String contenu = "<p>Bonjour " + utilisateur.getPrenom() + ",</p>" +
                "<p>Votre compte a été créé sur la plateforme Gestion des Incidents.</p>" +
                "<p>Voici vos identifiants pour vous connecter :</p>" +
                "<ul>" +
                "<li>Email : " + utilisateur.getEmail() + "</li>" +
                "<li>Mot de passe : " + motDePasse + "</li>" +
                "</ul>" +
                "<p>Nous vous recommandons de changer votre mot de passe dès votre première connexion.</p>" +
                "<p><a href=\"http://localhost:8080/login\">Se connecter</a></p>";

        emailService.envoyerEmail(
                utilisateur.getEmail(),
                "Création de votre compte - Gestion des Incidents",
                contenu);

        return utilisateur;
    }

    
    public void verifierEmail(String token) {
        Utilisateur utilisateur = utilisateurRepository
                .findByTokenVerificationEmail(token)
                .orElseThrow(() -> new RuntimeException("Token de vérification invalide"));

        utilisateur.setEmailVerifie(true);
        utilisateur.setActif(true);
        utilisateur.setTokenVerificationEmail(null);

        utilisateurRepository.save(utilisateur);
    }

     
    public Utilisateur findByEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

    public List<Utilisateur> findAll() {
        return utilisateurRepository.findAll();
    }

    public void supprimerUtilisateur(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (utilisateur.getRole() == Role.ROLE_SUPER_ADMIN) {
            throw new RuntimeException("Impossible de supprimer un super administrateur");
        }

        if (utilisateur.getDepartement() != null) {
            CategorieIncident dept = utilisateur.getDepartement();
            if (dept.getAdmin() != null && dept.getAdmin().getId().equals(id)) {
                dept.setAdmin(null);
            }
        }

        utilisateurRepository.deleteById(id);
    }

    public List<Utilisateur> getAgentsParDepartement(Long departementId) {
        return utilisateurRepository.findByDepartementIdAndRole(departementId, Role.ROLE_AGENT);
    }

    
    public Utilisateur getProfilUtilisateur(String emailConnecte) {
        return utilisateurRepository.findByEmail(emailConnecte)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
    }

     
    public Utilisateur mettreAJourProfil(
            String emailConnecte,
            String nom,
            String prenom,
            String telephone) {

        Utilisateur utilisateur = getProfilUtilisateur(emailConnecte);

        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setTelephone(telephone);

        return utilisateurRepository.save(utilisateur);
    }

     
    public void changerMotDePasse(
            String emailConnecte,
            String ancienMotDePasse,
            String nouveauMotDePasse,
            String confirmationMotDePasse) {

        Utilisateur utilisateur = getProfilUtilisateur(emailConnecte);

        if (!passwordEncoder.matches(ancienMotDePasse, utilisateur.getMotDePasse())) {
            throw new RuntimeException("Ancien mot de passe incorrect");
        }

        if (!nouveauMotDePasse.equals(confirmationMotDePasse)) {
            throw new RuntimeException("Les nouveaux mots de passe ne correspondent pas");
        }

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
    }
}
