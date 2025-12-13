package com.ville.intelligente.gestionincidents.model;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "rapports")
public class Rapport {
    @Id
    @GeneratedValue
    private long id;

    private String titre;
    private String contenu;
    private Date dateCreation;

    @ManyToOne
    private Incident incident;

    public Rapport(String titre, String contenu, Date dateCreation, Incident incident) {
        this.titre = titre;
        this.contenu = contenu;
        this.dateCreation = dateCreation;
        this.incident = incident;
    }
    
}
