package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.ObjectifAnnuel;
import fr.celine.suivideseries.entity.Utilisateur;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DataJpaTest
public class ObjectifAnnuelRepositoryTest {
    @Autowired
    private ObjectifAnnuelRepository objectifAnnuelRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Utilisateur utilisateur;
    private ObjectifAnnuel objectifAnnuel;

    @BeforeEach
    void setup() {
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        utilisateur.setMdp("Azerty123");
        objectifAnnuel = new ObjectifAnnuel(2026, 15, utilisateur);

        entityManager.persist(utilisateur);
        entityManager.persist(objectifAnnuel);
        entityManager.flush();
    }

    @Test
    @DisplayName("Doit retourner l'objectif d'un utilisateur pour une année donnée")
    void findByUtilisateurAndAnnee_returnObjectif() {
        Optional<ObjectifAnnuel> resultat = objectifAnnuelRepository.findByUtilisateurAndAnnee(utilisateur, 2026);

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getValeurObjectif()).isEqualTo(15);
    }

    @Test
    @DisplayName("Ne doit rien retourner si l'objectif n'existe pas pour cette année")
    void findByUtilisateurAndAnnee_anneeSansObjectif_returnEmpty() {
        Optional<ObjectifAnnuel> resultat = objectifAnnuelRepository.findByUtilisateurAndAnnee(utilisateur, 2027);

        assertThat(resultat).isEmpty();
    }
}