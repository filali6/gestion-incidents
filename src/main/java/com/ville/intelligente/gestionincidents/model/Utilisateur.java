package com.ville.intelligente.gestionincidents.model;

import com.ville.intelligente.gestionincidents.model.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "utilisateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String prenom;

    @Email(message = "Email invalide")
    @NotBlank(message = "L'email est obligatoire")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String telephone;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    @Builder.Default
    @Column(nullable = false)
    private boolean actif = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean emailVerifie = false;
    private String tokenVerificationEmail;

    // Incidents déclarés (citoyen)
    @OneToMany(mappedBy = "citoyen", fetch = FetchType.LAZY)
    private List<Incident> incidentsDeclares;

    // Incidents assignés (agent)
    @OneToMany(mappedBy = "agentAssigne", fetch = FetchType.LAZY)
    private List<Incident> incidentsAssignes;

    // Notifications reçues
    @OneToMany(mappedBy = "utilisateur", fetch = FetchType.LAZY)
    private List<Notification> notifications;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "departement_id")
    private CategorieIncident departement;
}
