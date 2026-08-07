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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
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
        // Série avec 2 livres LU et 1 en PAL : correspond au cas "presque finie dans la PAL"
        serie = new Serie("Le Seigneur des anneaux", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        livre1 = new Livre("J. R. R. Tolkien", "La fraternité de l'anneau", "1234567891234", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                null, serie);
        livre2 = new Livre("J. R. R. Tolkien", "Les Deux Tours", "1235467891234", 2, StatutLivre.LU, FormatLivre.EBOOK, null, null, serie);
        livre3 = new Livre("J. R. R. Tolkien", "Le Retour du Roi", "1234567819234", 3, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serie);

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

    @Test
    @DisplayName("Doit retourner les séries presque finies avec uniquement des livres en PAL")
    void trouverSeriesPresqueFinieDansLaPal_returnSerieAvecLivresEnPal() {
        List<Serie> resultat = serieRepository.trouverSeriesPresqueFinieDansLaPal(2);

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst()).isEqualTo(serie);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série ayant un livre à acheter parmi les livres manquants")
    void trouverSeriesPresqueFinieDansLaPal_excludesSerieAvecLivreAAcheter() {
        Utilisateur autreUtilisateur = new Utilisateur("Rowling", "Joanne", "JoJo", "jo@email.fr");
        autreUtilisateur.setMdp("Azerty123");
        Serie autreSerie = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 2);
        Livre livre4 = new Livre("J. K. Rowling", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null, autreSerie);
        Livre livre5 = new Livre("J. K. Rowling", "Tome 2", "2222222222222", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                autreSerie);

        entityManager.persist(autreUtilisateur);
        entityManager.persist(autreSerie);
        entityManager.persist(livre4);
        entityManager.persist(livre5);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesPresqueFinieDansLaPal(2);

        assertThat(resultat).hasSize(1);
        assertThat(resultat).containsOnly(serie);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série entièrement lue")
    void trouverSeriesPresqueFinieDansLaPal_excludesSerieEntierementLue() {
        livre3.setStatutLivre(StatutLivre.LU);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesPresqueFinieDansLaPal(2);

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les séries avec des livres à acheter, triées par nombre croissant")
    void trouverSeriesAvecLivresAAcheter_returnSeriesTrieesParNombreCroissant() {
        Utilisateur autreUtilisateur = new Utilisateur("Rowling", "Joanne", "JoJo", "jo@email.fr");
        autreUtilisateur.setMdp("Azerty123");

        Serie serieDeuxAAcheter = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 2);
        Livre livre4 = new Livre("J. K. Rowling", "Tome 1", "1111111111111", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieDeuxAAcheter);
        Livre livre5 = new Livre("J. K. Rowling", "Tome 2", "2222222222222", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieDeuxAAcheter);

        Serie serieUnAAcheter = new Serie("Percy Jackson", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livre6 = new Livre("Rick Riordan", "Tome 1", "3333333333333", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieUnAAcheter);

        entityManager.persist(autreUtilisateur);
        entityManager.persist(serieDeuxAAcheter);
        entityManager.persist(livre4);
        entityManager.persist(livre5);
        entityManager.persist(serieUnAAcheter);
        entityManager.persist(livre6);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<Serie> resultat = serieRepository.trouverSeriesAvecLivresAAcheter(pageable);

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0)).isEqualTo(serieUnAAcheter);
        assertThat(resultat.get(1)).isEqualTo(serieDeuxAAcheter);
    }

    @Test
    @DisplayName("Doit respecter la limite imposée par le Pageable")
    void trouverSeriesAvecLivresAAcheter_respectePageableLimit() {
        Utilisateur autreUtilisateur = new Utilisateur("Rowling", "Joanne", "JoJo", "jo@email.fr");
        autreUtilisateur.setMdp("Azerty123");
        Serie serieA = new Serie("Percy Jackson", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Serie serieB = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livre4 = new Livre("Rick Riordan", "Tome 1", "3333333333333", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null, serieA);
        Livre livre5 = new Livre("J. K. Rowling", "Tome 1", "4444444444444", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null, serieB);

        entityManager.persist(autreUtilisateur);
        entityManager.persist(serieA);
        entityManager.persist(serieB);
        entityManager.persist(livre4);
        entityManager.persist(livre5);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 1);
        List<Serie> resultat = serieRepository.trouverSeriesAvecLivresAAcheter(pageable);

        assertThat(resultat).hasSize(1);
    }

    @Test
    @DisplayName("Doit compter les séries dont la date de fin est comprise dans la plage donnée")
    void countByDateFinBetween_returnsBonCompte() {
        serie.setDateFin(LocalDate.of(2026, 3, 15));
        entityManager.flush();

        long resultat = serieRepository.countByDateFinBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertThat(resultat).isEqualTo(1);
    }

    @Test
    @DisplayName("Ne doit pas compter une série dont la date de fin est hors de la plage donnée")
    void countByDateFinBetween_excludesSerieHorsPlage() {
        serie.setDateFin(LocalDate.of(2025, 3, 15));
        entityManager.flush();

        long resultat = serieRepository.countByDateFinBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31));

        assertThat(resultat).isEqualTo(0);
    }

    @Test
    @DisplayName("Doit retourner les séries selon le statut donné")
    void findByStatutSerie_returnSeriesAvecCeStatut(){
        Serie serieTerminee = new Serie("Harry Potter", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 1);
        entityManager.persist(serieTerminee);
        entityManager.flush();

        List<Serie> resultat = serieRepository.findByStatutSerie(StatutSerie.TERMINEE);

        assertThat(resultat).hasSize(1);
        assertThat(resultat).containsOnly(serieTerminee);
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucune série n'a le statut donné")
    void findByStatutSerie_aucuneSerieAvecCeStatut_returnListeVide(){
        List<Serie> resultat = serieRepository.findByStatutSerie(StatutSerie.ABANDONNEE);

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les séries qui n'ont pas le statut exclu")
    void findByStatutSerieNot_excludesSerieAvecStatutDonne(){
        Serie serieAbandonnee = new Serie("Harry Potter", utilisateur, StatutSerie.ABANDONNEE, StatutPublication.TERMINEE, 1);
        entityManager.persist(serieAbandonnee);
        entityManager.flush();

        List<Serie> resultat = serieRepository.findByStatutSerieNot(StatutSerie.ABANDONNEE);

        assertThat(resultat).hasSize(1);
        assertThat(resultat).containsOnly(serie);
    }

    @Test
    @DisplayName("Doit retourner les séries en cours avec au moins un ebook dans la PAL")
    void trouverSeriesAvecEbooksDansLaPal_returnSeriesAvecEbookEnPal(){
        // La série du setup est EN_COURS avec livre3 en DANS_PAL/EBOOK : elle doit ressortir telle quelle.
        List<Serie> resultat = serieRepository.trouverSeriesAvecEbooksDansLaPal();

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat).containsOnly(serie);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série dont la PAL ne contient que des livres papier")
    void trouverSeriesAvecEbooksDansLaPal_excludesSerieAvecSeulementDuPapierEnPal(){
        Serie serieSansEbook = new Serie("Harry Potter", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livrePapier = new Livre("J. K. Rowling", "Tome 1", "5555555555555", 1, StatutLivre.DANS_PAL, FormatLivre.PAPIER, null, null,
                serieSansEbook);

        entityManager.persist(serieSansEbook);
        entityManager.persist(livrePapier);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesAvecEbooksDansLaPal();

        assertThat(resultat).doesNotContain(serieSansEbook);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série qui n'est pas EN_COURS même si elle a un ebook en PAL")
    void trouverSeriesAvecEbooksDansLaPal_excludesSerieNonEnCours(){
        Serie serieTerminee = new Serie("Harry Potter", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 1);
        Livre livreEbook = new Livre("J. K. Rowling", "Tome 1", "5555555555555", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieTerminee);

        entityManager.persist(serieTerminee);
        entityManager.persist(livreEbook);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesAvecEbooksDansLaPal();

        assertThat(resultat).doesNotContain(serieTerminee);
    }
}
