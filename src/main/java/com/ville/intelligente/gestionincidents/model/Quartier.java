package com.ville.intelligente.gestionincidents.model;

import java.util.HashSet;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "quartiers")
 
public class Quartier {
    @Id
    @GeneratedValue

    private long id ;

    private String nom ; 
    private String ville ; 
    private int codePostal;

    @OneToMany(mappedBy="quartier")
    private Set<Incident> incidents = new HashSet<>();

    public Quartier(String nom , String ville,int codePostal){
        this.nom=nom;
        this.ville=ville;
        this.codePostal=codePostal;
    }
    
}
