package com.ville.intelligente.gestionincidents.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "incidents")
public class Incident {
    @Id
    @GeneratedValue
    private long id;

    private String titre;
    private String description;
    private Date dateDeclaration;
    private String adresse;
    private String feedbackCitoyen;
    private int priorite;
    private Double latitude;
    private Double longitude;

    @Enumerated(EnumType.STRING)
    private StatutIncident statut;

    @ManyToOne
    private Quartier quartier;

    @ManyToOne
    private CategorieIncident categorie;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rapport> rapports = new HashSet<>();

    @OneToOne(mappedBy = "incident", cascade = CascadeType.ALL)
    private Photo photo;
     


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "citoyen_id")
    private Utilisateur citoyen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private Utilisateur agentAssigne;

    @OneToMany(mappedBy = "incident", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notification> notifications = new HashSet<>();

    public Incident(String titre, String description, Date dateDeclaration, String adresse, StatutIncident statut,
            CategorieIncident categorie, Quartier quartier) {
        this.titre = titre;
        this.description = description;
        this.dateDeclaration = dateDeclaration;
        this.adresse = adresse;
        this.statut = statut;
        this.categorie = categorie;
        this.quartier = quartier;
    }

}
