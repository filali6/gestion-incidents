package com.ville.intelligente.gestionincidents.metier;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ville.intelligente.gestionincidents.dao.IncidentDAO;
import com.ville.intelligente.gestionincidents.dao.PhotoDAO;
import com.ville.intelligente.gestionincidents.dao.CategorieIncidentDAO; 
import com.ville.intelligente.gestionincidents.dao.QuartierDAO;

import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.model.Photo;
import com.ville.intelligente.gestionincidents.model.CategorieIncident;  
import com.ville.intelligente.gestionincidents.model.Quartier;  
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident; 

@Service
public class IncidentServiceImpl implements IncidentService {

    private final IncidentDAO incidentDao;
    private final PhotoDAO photoDao;
    private final CategorieIncidentDAO categorieDao; 
    private final QuartierDAO quartierDao;

    public IncidentServiceImpl(IncidentDAO incidentDao, PhotoDAO photoDao, CategorieIncidentDAO categorieDao,
            QuartierDAO quartierDao) {  
                                                                                                              
        this.incidentDao = incidentDao;
        this.photoDao = photoDao;
        this.categorieDao = categorieDao; 
        this.quartierDao = quartierDao;

    }

    @Override
    public Incident saveIncident(Incident incident) {
        return incidentDao.save(incident);
    }
    
    // ⭐ AJOUTE CETTE MÉTHODE
    private boolean isImageValid(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return true; // Optionnel, donc valide si vide
        }

        // Vérifier la taille (5 MB max)
        if (file.getSize() > 5 * 1024 * 1024) {
            return false;
        }

        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        // Types autorisés : JPEG, PNG, GIF, WebP
        return contentType.equals("image/jpeg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/gif") ||
                contentType.equals("image/webp");
    }
   @Override

    public Incident saveIncidentWithPhoto(Incident incident, MultipartFile photoFile, String categorieNom,
            String quartierNom,String quartierVille, int quartierCodePostal) {
             
    
        // 1. Récupérer ou créer la catégorie
        CategorieIncident categorie = categorieDao.findByNom(categorieNom)
            .orElseGet(() -> categorieDao.save(new CategorieIncident(categorieNom)));
        incident.setCategorie(categorie);
        
        // 2. Récupérer ou créer le quartier
        Quartier quartier = quartierDao.findByNom(quartierNom)
            .orElseGet(() -> quartierDao.save(new Quartier(quartierNom, quartierVille, quartierCodePostal)));
        incident.setQuartier(quartier);
        
        // 3. Initialiser statut et date
        incident.setStatut(StatutIncident.SIGNALE);
        incident.setDateDeclaration(LocalDateTime.now());
    
    
        // 1. Sauvegarder l'incident d'abord
        Incident savedIncident = incidentDao.save(incident);

        // 2. Si un fichier est uploadé
        if (photoFile != null && !photoFile.isEmpty()) {
            try {
                if (!isImageValid(photoFile)) {
                    throw new IllegalArgumentException("Fichier non valide : type ou taille incorrecte");
                }
                // 2a. Créer un dossier 'uploads' dans resources/static si pas existant
                String uploadDir = "C:/Users/HP/Desktop/gestion-incidents/src/main/resources/static/uploads";
                File dir = new File(uploadDir);
                if (!dir.exists()) dir.mkdirs();

                // 2b. Générer un nom unique pour le fichier
                String fileName = System.currentTimeMillis() + "_" + photoFile.getOriginalFilename();

                // 2c. Sauvegarder le fichier sur le disque
                File file = new File(uploadDir + "/" + fileName);
                photoFile.transferTo(file);

                // 2d. Créer la photo et la lier à l'incident
                Photo photo = new Photo();
                photo.setNomFichier(fileName);
                photo.setType(photoFile.getContentType());
                photo.setDateUpload(LocalDateTime.now());
                photo.setChemin("/uploads/" + fileName);  // chemin relatif pour Thymeleaf
                photo.setIncident(savedIncident);

                savedIncident.setPhoto(photo);

                // 2e. Sauvegarder la photo en base
                photoDao.save(photo);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return savedIncident;
    }
    
    @Override
    public List<Incident> findAll() {
        return incidentDao.findAll();
    }
    
    @Override
    public List<Incident> findByFilters(StatutIncident statut, String categorie, String quartier, Date dateDeclaration
             ) {
        return incidentDao.findByFilters(statut, categorie,quartier,dateDeclaration);
    }
    
    
}
