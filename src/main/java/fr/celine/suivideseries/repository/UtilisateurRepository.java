package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByPseudo(String pseudo);
}
