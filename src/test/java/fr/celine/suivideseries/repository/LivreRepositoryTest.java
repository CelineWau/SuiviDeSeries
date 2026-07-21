package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.FormatLivre;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutPublication;
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

import static org.assertj.core.api.Assertions.assertThat;


@Transactional
@DataJpaTest
public class LivreRepositoryTest {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Utilisateur utilisateur;
    private Serie serie;
    private Livre livre;

    @BeforeEach
    void setup(){
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        utilisateur.setMdp("Azerty123");
        serie = new Serie("Le puits des mémoires", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        livre = new Livre("Gabriel Katz", "La traque", "1234567891234", 1, StatutLivre.LU, FormatLivre.PAPIER, null, serie);

        entityManager.persist(utilisateur);
        entityManager.persist(serie);
        entityManager.persist(livre);
        entityManager.flush();
    }

    @Test
    @DisplayName("Doit retourner le livre par un isbn donné")
    void findByIsbn_ReturnLivre(){
        Optional<Livre> resultat = livreRepository.findByIsbn("1234567891234");

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getIsbn()).isEqualTo("1234567891234");
    }

    @Test
    @DisplayName("Doit retourner le livre en fonction de son numéro dans la série")
    void findByNumeroDansLaSerie_ReturnLivre(){
        Optional<Livre> resultat = livreRepository.findByNumeroDansLaSerieAndSerie(1, serie);

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getNumeroDansLaSerie()).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit retourner la liste des auteurs triée par ordre alphabétique, sans doublon")
    void trouverAuteurParOrdreAlphabetique_returnAuteursTriesSansDoublon(){
        // Deuxième livre du même auteur (pour vérifier le DISTINCT) + un auteur qui doit passer avant dans le tri
        Livre livre2 = new Livre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.DANS_PAL, FormatLivre.PAPIER, null, serie);
        Livre livre3 = new Livre("Alison Germain", "Le Souffle de Midas", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, serie);
        entityManager.persist(livre2);
        entityManager.persist(livre3);
        entityManager.flush();

        List<String> resultat = livreRepository.trouverAuteurParOrdreAlphabetique();

        assertThat(resultat).isNotNull();
        assertThat(resultat).containsExactly("Alison Germain", "Gabriel Katz");
    }
}
