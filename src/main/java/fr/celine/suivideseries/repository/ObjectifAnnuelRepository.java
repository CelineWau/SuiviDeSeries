package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.ObjectifAnnuel;
import fr.celine.suivideseries.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ObjectifAnnuelRepository extends JpaRepository<ObjectifAnnuel, Integer> {

    Optional<ObjectifAnnuel> findByUtilisateurAndAnnee(Utilisateur utilisateur, int annee);
}
