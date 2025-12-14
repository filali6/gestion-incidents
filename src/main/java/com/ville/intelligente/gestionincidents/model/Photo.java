package com.ville.intelligente.gestionincidents.model;

import java.util.Date;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
@Table(name = "photos")
public class Photo {
    @Id
    @GeneratedValue
    private long id ;
    private String nomFichier ; 
    private String chemin;
    private String type ;
    private Date dateUpload ;

     
    @OneToOne
    @JoinColumn(name = "incident_id")
    private Incident incident;
    
    public Photo(String nomFichier, String type, Date dateUpload ) {
        this.nomFichier = nomFichier;
        this.type = type;
        this.dateUpload = dateUpload;
        
    }


    
}
