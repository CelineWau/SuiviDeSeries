package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.dto.AuteursSeriesEnCoursDTO;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        livre = new Livre("Gabriel Katz", "La traque", "1234567891234", 1, StatutLivre.LU, FormatLivre.PAPIER, null, null, serie);

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
        Livre livre2 = new Livre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.DANS_PAL, FormatLivre.PAPIER, null,
                null,serie);
        Livre livre3 = new Livre("Alison Germain", "Le Souffle de Midas", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null,
                serie);
        entityManager.persist(livre2);
        entityManager.persist(livre3);
        entityManager.flush();

        List<String> resultat = livreRepository.trouverAuteurParOrdreAlphabetique();

        assertThat(resultat).isNotNull();
        assertThat(resultat).containsExactly("Alison Germain", "Gabriel Katz");
    }

    @Test
    @DisplayName("Doit compter les livres selon leur statut et leur format")
    void countByStatutLivreAndFormatLivre_returnsBonCompte(){
        Livre livre2 = new Livre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.LU, FormatLivre.EBOOK, null, null,serie);
        Livre livre3 = new Livre("Gabriel Katz", "La traque 3", "9876543219877", 3, StatutLivre.LU, FormatLivre.EBOOK, null, null, serie);
        entityManager.persist(livre2);
        entityManager.persist(livre3);
        entityManager.flush();

        long resultat = livreRepository.countByStatutLivreAndFormatLivre(StatutLivre.LU, FormatLivre.EBOOK);

        assertThat(resultat).isEqualTo(2);
    }

    @Test
    @DisplayName("Doit retourner les auteurs triés par nombre de séries en cours décroissant, puis par ordre alphabétique")
    void trouverAuteursParNombreSerieEnCours_returnAuteursTriesParNombreDecroissant(){
        // "Gabriel Katz" a déjà 1 série EN_COURS via le setup (livre + serie).
        // On lui ajoute une deuxième série EN_COURS pour qu'il passe devant "Alison Germain".
        Serie serieBis = new Serie("Le fils de la lune", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livreGabrielBis = new Livre("Gabriel Katz", "Tome unique", "9999999999999", 1, StatutLivre.LU, FormatLivre.PAPIER, null, null,
                serieBis);

        Serie serieAlison = new Serie("Le Souffle de Midas", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livreAlison = new Livre("Alison Germain", "Tome 1", "8888888888888", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null, serieAlison);

        entityManager.persist(serieBis);
        entityManager.persist(livreGabrielBis);
        entityManager.persist(serieAlison);
        entityManager.persist(livreAlison);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 5);
        List<AuteursSeriesEnCoursDTO> resultat = livreRepository.trouverAuteursParNombreSerieEnCours(pageable);

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getAuteur()).isEqualTo("Gabriel Katz");
        assertThat(resultat.get(0).getNombreSeries()).isEqualTo(2);
        assertThat(resultat.get(1).getAuteur()).isEqualTo("Alison Germain");
        assertThat(resultat.get(1).getNombreSeries()).isEqualTo(1);
    }

    @Test
    @DisplayName("Ne doit pas compter une série qui n'est pas EN_COURS")
    void trouverAuteursParNombreSerieEnCours_excludesSerieNonEnCours(){
        // Deuxième série du même auteur mais TERMINEE : ne doit pas augmenter son compte.
        Serie serieTerminee = new Serie("Le fils de la lune", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 1);
        Livre livreTermine = new Livre("Gabriel Katz", "Tome unique", "9999999999999", 1, StatutLivre.LU, FormatLivre.PAPIER, null, null,
                serieTerminee);

        entityManager.persist(serieTerminee);
        entityManager.persist(livreTermine);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 5);
        List<AuteursSeriesEnCoursDTO> resultat = livreRepository.trouverAuteursParNombreSerieEnCours(pageable);

        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst().getAuteur()).isEqualTo("Gabriel Katz");
        assertThat(resultat.getFirst().getNombreSeries()).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit respecter la limite imposée par le Pageable")
    void trouverAuteursParNombreSerieEnCours_respectePageableLimit(){
        Serie serieAlison = new Serie("Le Souffle de Midas", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livreAlison = new Livre("Alison Germain", "Tome 1", "8888888888888", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null, serieAlison);

        entityManager.persist(serieAlison);
        entityManager.persist(livreAlison);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 1);
        List<AuteursSeriesEnCoursDTO> resultat = livreRepository.trouverAuteursParNombreSerieEnCours(pageable);

        assertThat(resultat).hasSize(1);
    }
}
