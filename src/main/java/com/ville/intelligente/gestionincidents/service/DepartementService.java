package com.ville.intelligente.gestionincidents.service;

import com.ville.intelligente.gestionincidents.dao.CategorieIncidentDAO;
import com.ville.intelligente.gestionincidents.dto.CreateDepartementRequest;
import com.ville.intelligente.gestionincidents.model.CategorieIncident;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartementService {

    private final CategorieIncidentDAO categorieIncidentDAO;

    public DepartementService(CategorieIncidentDAO categorieIncidentDAO) {
        this.categorieIncidentDAO = categorieIncidentDAO;
    }

     

    public CategorieIncident creerDepartement(CreateDepartementRequest request) {

        if (categorieIncidentDAO.existsByNom(request.getNom())) {
            throw new RuntimeException("Un département avec ce nom existe déjà");
        }

        CategorieIncident departement = new CategorieIncident();
        departement.setNom(request.getNom());

        return categorieIncidentDAO.save(departement);
    }

   

    public List<CategorieIncident> findAll() {
        return categorieIncidentDAO.findAll();
    }

    public CategorieIncident findById(Long id) {
        return categorieIncidentDAO.findById(id)
                .orElseThrow(() -> new RuntimeException("Département introuvable"));
    }
}
