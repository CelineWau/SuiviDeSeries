package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.ObjectifAnnuel;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.ObjectifAnnuelRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class ObjectifAnnuelService {

    private final ObjectifAnnuelRepository objectifAnnuelRepository;

    public ObjectifAnnuelService(ObjectifAnnuelRepository objectifAnnuelRepository) {
        this.objectifAnnuelRepository = objectifAnnuelRepository;
    }

    public ObjectifAnnuel definirObjectifAnnuel(Utilisateur utilisateur, int valeurObjectif) {

        //Validation métier
        if(valeurObjectif < 1 || valeurObjectif > 50) {
            throw new BusinessException("La valeur de l'objectif doit être comprise entre 1 et 50 séries par an.");
        }

        if(utilisateur == null) {
            throw new BusinessException("L'objectif annuel doit être associé à un utilisateur.");
        }

        int annee = LocalDate.now().getYear();
        Optional<ObjectifAnnuel> objectifExistant = objectifAnnuelRepository.findByUtilisateurAndAnnee(utilisateur, annee);

        if(objectifExistant.isPresent()) {
            ObjectifAnnuel objectif = objectifExistant.get();
            objectif.setValeurObjectif(valeurObjectif);
            return objectifAnnuelRepository.save(objectif);
        } else {
            ObjectifAnnuel objectif = new ObjectifAnnuel(annee, valeurObjectif,  utilisateur);
            return objectifAnnuelRepository.save(objectif);
        }
    }

    public Optional<ObjectifAnnuel> recupererObjectifAnnuel(Utilisateur utilisateur) {
        int annee = LocalDate.now().getYear();
        return objectifAnnuelRepository.findByUtilisateurAndAnnee(utilisateur, annee);
    }
}
