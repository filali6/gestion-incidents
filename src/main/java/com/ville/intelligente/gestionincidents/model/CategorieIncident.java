package com.ville.intelligente.gestionincidents.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories_incident")

public class CategorieIncident {
    @Id
    @GeneratedValue
    private Long id;
    private String nom;

    @OneToMany(mappedBy = "categorie", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Incident> incidents = new HashSet<>();

    @OneToOne
    @JoinColumn(name = "admin_id", unique = true)
    private Utilisateur admin;

    @OneToMany(mappedBy = "departement")
    private Set<Utilisateur> agents = new HashSet<>();

    public CategorieIncident(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "CategorieIncident [id=" + id + ", nom=" + nom + "]";

    }

}
