package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.SerieCreationDTO;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    // Trouver un utilisateur par id
    public Utilisateur trouverUtilisateurParId(int id){
        return utilisateurRepository.findById(id).orElseThrow();
    }
}
