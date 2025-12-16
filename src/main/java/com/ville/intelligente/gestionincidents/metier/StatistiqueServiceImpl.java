package com.ville.intelligente.gestionincidents.metier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ville.intelligente.gestionincidents.dao.IncidentDAO;
import com.ville.intelligente.gestionincidents.model.Incident;
import com.ville.intelligente.gestionincidents.model.Utilisateur;
import com.ville.intelligente.gestionincidents.model.enums.StatutIncident;

@Service
public class StatistiqueServiceImpl implements StatistiqueService {
    private final IncidentDAO incidentDao;

    public StatistiqueServiceImpl ( IncidentDAO incidentDao){
        this.incidentDao=incidentDao;
    }
     @Override
    public long compterTotalIncidents() {
        return incidentDao.count();
    }

    @Override
    public Map<String, Long> getStatistiquesParStatut() {
        Map<String, Long> stats = new HashMap<>();
        
        stats.put("SIGNALE", incidentDao.countByStatut(StatutIncident.SIGNALE));
        stats.put("PRIS_EN_CHARGE", incidentDao.countByStatut(StatutIncident.PRIS_EN_CHARGE));
        stats.put("EN_RESOLUTION", incidentDao.countByStatut(StatutIncident.EN_RESOLUTION));
        stats.put("RESOLU", incidentDao.countByStatut(StatutIncident.RESOLU));
        stats.put("CLOTURE", incidentDao.countByStatut(StatutIncident.CLOTURE));
        
        return stats;
    }

    // @Override
    // public Map<String, Long> getStatistiquesParCategorie() {
    //     Map<String, Long> stats = new HashMap<>();
        
    //     stats.put("Infrastructure", incidentDao.countByCategorie("Infrastructure"));
    //     stats.put("Propreté", incidentDao.countByCategorie("Propreté"));
    //     stats.put("Sécurité", incidentDao.countByCategorie("Sécurité"));
    //     stats.put("Éclairage", incidentDao.countByCategorie("Éclairage"));
    //     stats.put("Transport", incidentDao.countByCategorie("Transport"));
    //     stats.put("Environnement", incidentDao.countByCategorie("Environnement"));
    //     stats.put("Autre", incidentDao.countByCategorie("Autre"));
        
    //     return stats;
    // }
    @Override
    public Map<String, Long> getStatistiquesParCategorie() {
        Map<String, Long> stats = new HashMap<>();

        List<Object[]> results = incidentDao.countByCategorie();
        for (Object[] result : results) {
            String categorie = (String) result[0];
            Long count = (Long) result[1];
            stats.put(categorie, count);
        }

        return stats;
    }

    @Override
    public Map<String, Long> getTop5Quartiers() {
        Map<String, Long> stats = new HashMap<>();
        
        List<Object[]> results = incidentDao.countByQuartier();
        int count = 0;
        for (Object[] result : results) {
            if (count >= 5) break;
            String quartier = (String) result[0];
            Long nombre = (Long) result[1];
            stats.put(quartier, nombre);
            count++;
        }
        
        return stats;
    }
    @Override
public long compterIncidentsParCitoyen(Utilisateur citoyen) {
    return incidentDao.findByCitoyen(citoyen).size();
}

@Override
public Map<StatutIncident, Long> getStatistiquesParStatutPourCitoyen(Utilisateur citoyen) {
    List<Incident> incidents = incidentDao.findByCitoyen(citoyen);
    return incidents.stream()
            .collect(Collectors.groupingBy(
                    Incident::getStatut,
                    Collectors.counting()
            ));
}

@Override
public long compterIncidentsParAgent(Utilisateur agent) {
    return incidentDao.findByAgentAssigne(agent).size();
}

@Override
public Map<StatutIncident, Long> getStatistiquesParStatutPourAgent(Utilisateur agent) {
    List<Incident> incidents = incidentDao.findByAgentAssigne(agent);
    return incidents.stream()
            .collect(Collectors.groupingBy(
                    Incident::getStatut,
                    Collectors.counting()));
}

@Override
public long compterIncidentsParDepartement(Long departementId) {
    return (long) incidentDao.findByCategorie_Id(departementId).size();
}

@Override
public Map<StatutIncident, Long> getStatistiquesParStatutPourDepartement(Long departementId) {
    List<Incident> incidents = incidentDao.findByCategorie_Id(departementId);
    return incidents.stream()
            .collect(Collectors.groupingBy(
                    Incident::getStatut,
                    Collectors.counting()));
}
}
