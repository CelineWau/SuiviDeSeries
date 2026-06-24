package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutSerie;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@DataJpaTest
public class SerieRepositoryTest {

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Serie serie;
    private Utilisateur utilisateur;
    private Livre livre1;
    private Livre livre2;
    private Livre livre3;

    @BeforeEach
    void setup() {
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        utilisateur.setMdp("Azerty123");
        serie = new Serie("Le Seigneur des anneaux",utilisateur, StatutSerie.EN_COURS, 3);
        livre1 = new Livre("J. R. R. Tolken", "La fraternité de l'anneau", "1234567891234", StatutLivre.LU, serie);
        livre2 = new Livre("J. R. R. Tolken", "Les Deux Tours", "1235467891234", StatutLivre.LU, serie);
        livre3 = new Livre("J. R. R. Tolken", "Le Retour du Roi", "1234567819234", StatutLivre.DANS_PAL, serie);

        entityManager.persist(utilisateur);
        entityManager.persist(serie);
        entityManager.persist(livre1);
        entityManager.persist(livre2);
        entityManager.persist(livre3);
        entityManager.flush();
    }

    // Test FindByNom
    @Test
    @DisplayName("Doit retourner la serie par le nom donné")
    void findByNom_returnSerie() {
        Optional<Serie> resultat = serieRepository.findByNom("Le Seigneur des anneaux");

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getNom()).isEqualTo("Le Seigneur des anneaux");

    }

    // Test trouverSeriesParNombreLivresManquants

    @Test
    @DisplayName("Doit retourner le nombre de livre manquant dans toutes les séries")
    void trouverSeriesParNombreLivresManquants_returnLivresManquant() {
        List<Serie> resultat = serieRepository.trouverSeriesParNombreLivresManquants(1);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getFirst()).isEqualTo(serie);
        assertThat(resultat).hasSize(1);
    }
}
