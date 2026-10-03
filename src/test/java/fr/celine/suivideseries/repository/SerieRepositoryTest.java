package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.dto.RepartitionCategorieDTO;
import fr.celine.suivideseries.entity.Genre;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.*;
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
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Transactional
@DataJpaTest
class SerieRepositoryTest {

    @Autowired
    private SerieRepository serieRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Serie serie;
    private Utilisateur utilisateur;
    private Utilisateur autreUtilisateur;
    private Livre livre3;

    @BeforeEach
    void setup() {
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        utilisateur.setMdp("Azerty123");
        autreUtilisateur = new Utilisateur("Waucheul", "Céline", "Amaterasu", "monemail@email.fr");
        autreUtilisateur.setMdp("Azerty123");
        // Série avec 2 livres LU et 1 en PAL : correspond au cas "presque fini dans la PAL"
        serie = new Serie("Le Seigneur des anneaux", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre livre1 = new Livre("J. R. R. Tolkien", "La fraternité de l'anneau", "1234567891234", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                null, serie);
        Livre livre2 = new Livre("J. R. R. Tolkien", "Les Deux Tours", "1235467891234", 2, StatutLivre.LU, FormatLivre.EBOOK, null, null, serie);
        livre3 = new Livre("J. R. R. Tolkien", "Le Retour du Roi", "1234567819234", 3, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serie);

        entityManager.persist(utilisateur);
        entityManager.persist(autreUtilisateur);
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
        List<Integer> resultat = serieRepository.trouverIdsSeriesParNombreLivresManquants(1);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getFirst()).isEqualTo(serie.getIdSerie());
        assertThat(resultat).hasSize(1);
    }

    @Test
    @DisplayName("Doit retourner les séries presque finies avec uniquement des livres en PAL")
    void trouverSeriesPresqueFinieDansLaPal_returnSerieAvecLivresEnPal() {
        // Série identique (presque finie dans la PAL) mais appartenant à un autre utilisateur
        Serie serieAutreUtilisateur = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre livreA = new Livre("J.K. Rowling", "Harry Potter 1", "9999999999991", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null, serieAutreUtilisateur);
        Livre livreB = new Livre("J.K. Rowling", "Harry Potter 2", "9999999999992", 2, StatutLivre.LU, FormatLivre.EBOOK, null, null, serieAutreUtilisateur);
        Livre livreC = new Livre("J.K. Rowling", "Harry Potter 3", "9999999999993", 3, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null, serieAutreUtilisateur);
        entityManager.persist(serieAutreUtilisateur);
        entityManager.persist(livreA);
        entityManager.persist(livreB);
        entityManager.persist(livreC);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSeriesPresqueFinieDansLaPal(2, utilisateur);

        assertThat(resultat).isNotNull().hasSize(1).doesNotContain(serieAutreUtilisateur.getIdSerie());
        assertThat(resultat.getFirst()).isEqualTo(serie.getIdSerie());
    }

    @Test
    @DisplayName("Ne doit pas retourner une série ayant un livre à acheter parmi les livres manquants")
    void trouverSeriesPresqueFinieDansLaPal_excludesSerieAvecLivreAAcheter() {
        Serie autreSerie = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 2);
        Livre livre4 = new Livre("J. K. Rowling", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null, autreSerie);
        Livre livre5 = new Livre("J. K. Rowling", "Tome 2", "2222222222222", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                autreSerie);

        entityManager.persist(autreSerie);
        entityManager.persist(livre4);
        entityManager.persist(livre5);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSeriesPresqueFinieDansLaPal(2, utilisateur);

        assertThat(resultat).hasSize(1).containsOnly(serie.getIdSerie());
    }

    @Test
    @DisplayName("Ne doit pas retourner une série entièrement lue")
    void trouverSeriesPresqueFinieDansLaPal_excludesSerieEntierementLue() {
        livre3.setStatutLivre(StatutLivre.LU);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSeriesPresqueFinieDansLaPal(2, utilisateur);

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les séries avec des livres à acheter, triées par nombre croissant")
    void trouverSeriesAvecLivresAAcheter_returnSeriesTrieesParNombreCroissant() {
        Serie serieDeuxAAcheter = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 2);
        Livre livre4 = new Livre("J. K. Rowling", "Tome 1", "1111111111111", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieDeuxAAcheter);
        Livre livre5 = new Livre("J. K. Rowling", "Tome 2", "2222222222222", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieDeuxAAcheter);

        Serie serieUnAAcheter = new Serie("Percy Jackson", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livre6 = new Livre("Rick Riordan", "Tome 1", "3333333333333", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieUnAAcheter);

        entityManager.persist(serieDeuxAAcheter);
        entityManager.persist(livre4);
        entityManager.persist(livre5);
        entityManager.persist(serieUnAAcheter);
        entityManager.persist(livre6);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        List<Integer> ids = serieRepository.trouverIdsSeriesAvecLivresAAcheter(pageable, autreUtilisateur);

        assertThat(ids).containsExactly(serieUnAAcheter.getIdSerie(), serieDeuxAAcheter.getIdSerie());

        List<Serie> resultat = serieRepository.trouverSeriesAvecDetailsParIds(ids);

        assertThat(resultat).hasSize(2).containsExactlyInAnyOrder(serieUnAAcheter, serieDeuxAAcheter);
    }

    @Test
    @DisplayName("Doit respecter la limite imposée par le Pageable")
    void trouverSeriesAvecLivresAAcheter_respectePageableLimit() {
        Serie serieA = new Serie("Percy Jackson", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Serie serieB = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livre4 = new Livre("Rick Riordan", "Tome 1", "3333333333333", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null, serieA);
        Livre livre5 = new Livre("J. K. Rowling", "Tome 1", "4444444444444", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null, serieB);

        entityManager.persist(serieA);
        entityManager.persist(serieB);
        entityManager.persist(livre4);
        entityManager.persist(livre5);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 1);
        List<Integer> ids = serieRepository.trouverIdsSeriesAvecLivresAAcheter(pageable, autreUtilisateur);
        List<Serie> resultat = serieRepository.trouverSeriesAvecDetailsParIds(ids);

        assertThat(resultat).hasSize(1);
    }

    @Test
    @DisplayName("Doit compter les séries dont la date de fin est comprise dans la plage donnée")
    void countByDateFinBetween_returnsBonCompte() {
        serie.setDateFin(LocalDate.of(2026, Month.MARCH, 15));

        Serie serieAutreUtilisateur = new Serie("Harry Potter", autreUtilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 1);
        serieAutreUtilisateur.setDateFin(LocalDate.of(2026, Month.MARCH, 15));
        entityManager.persist(serieAutreUtilisateur);
        entityManager.flush();

        long resultat = serieRepository.countByDateFinBetweenAndUtilisateur(LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.DECEMBER, 31), utilisateur);

        assertThat(resultat).isEqualTo(1);
    }

    @Test
    @DisplayName("Ne doit pas compter une série dont la date de fin est hors de la plage donnée")
    void countByDateFinBetween_excludesSerieHorsPlage() {
        serie.setDateFin(LocalDate.of(2025, Month.MARCH, 15));
        entityManager.flush();

        long resultat = serieRepository.countByDateFinBetweenAndUtilisateur(LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.DECEMBER, 31), utilisateur);

        assertThat(resultat).isZero();
    }

    @Test
    @DisplayName("Doit retourner les séries selon le statut donné")
    void findByStatutSerie_returnSeriesAvecCeStatut(){
        Serie serieTerminee = new Serie("Harry Potter", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 1);
        Serie serieTerminee1 = new Serie("Eragon", autreUtilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 1);
        entityManager.persist(serieTerminee);
        entityManager.persist(serieTerminee1);
        entityManager.flush();

        List<Serie> resultat = serieRepository.findByStatutSerieAndUtilisateur(StatutSerie.TERMINEE, utilisateur);

        assertThat(resultat).hasSize(1).containsOnly(serieTerminee);
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucune série n'a le statut donné")
    void findByStatutSerie_aucuneSerieAvecCeStatut_returnListeVide(){
        List<Serie> resultat = serieRepository.findByStatutSerieAndUtilisateur(StatutSerie.ABANDONNEE, utilisateur);

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner les séries qui n'ont pas le statut exclu")
    void findByStatutSerieNot_excludesSerieAvecStatutDonne(){
        Serie serieAbandonnee = new Serie("Harry Potter", utilisateur, StatutSerie.ABANDONNEE, StatutPublication.TERMINEE, 1);
        Serie serieTerminee = new Serie("Eragon", autreUtilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 1);
        entityManager.persist(serieAbandonnee);
        entityManager.persist(serieTerminee);
        entityManager.flush();

        List<Serie> resultat = serieRepository.findByStatutSerieNotAndUtilisateur(StatutSerie.ABANDONNEE, utilisateur);

        assertThat(resultat).hasSize(1).containsOnly(serie);
    }

    @Test
    @DisplayName("Doit retourner les séries en cours avec au moins un ebook dans la PAL")
    void trouverSeriesAvecEbooksDansLaPal_returnSeriesAvecEbookEnPal(){
        Serie serieAutreUtilisateur = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livreEbookAutreUtilisateur = new Livre("J. K. Rowling", "Tome 1", "6666666666666", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieAutreUtilisateur);
        entityManager.persist(serieAutreUtilisateur);
        entityManager.persist(livreEbookAutreUtilisateur);
        entityManager.flush();

        // La série du setup est EN_COURS avec livre3 en DANS_PAL/EBOOK : elle doit ressortir telle quelle.
        List<Serie> resultat = serieRepository.trouverSeriesAvecEbooksDansLaPal(utilisateur);

        assertThat(resultat).isNotNull().hasSize(1).containsOnly(serie);
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

        // La série du setup est EN_COURS avec livre3 en DANS_PAL/EBOOK : elle doit ressortir seule.
        List<Serie> resultat = serieRepository.trouverSeriesAvecEbooksDansLaPal(utilisateur);

        assertThat(resultat).containsOnly(serie);
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

        // La série du setup est EN_COURS avec livre3 en DANS_PAL/EBOOK : elle doit ressortir seule.
        List<Serie> resultat = serieRepository.trouverSeriesAvecEbooksDansLaPal(utilisateur);

        assertThat(resultat).containsOnly(serie);
    }

    @Test
    @DisplayName("Doit retourner les séries en cours dont la publication est en cours et pas encore entièrement lues")
    void trouverSerieASurveiller_returnSeriesASurveiller(){
        Serie serieASurveiller = new Serie("Cosmere", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 20);
        Livre livreNonLu = new Livre("Brandon Sanderson", "Tome 2", "1111111111111", 2, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieASurveiller);
        Serie serieAutreUtilisateur = new Serie("Stormlight Archive", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 10);
        Livre livreNonLuAutreUtilisateur = new Livre("Brandon Sanderson", "Tome 1", "9999999999999", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieAutreUtilisateur);
        entityManager.persist(serieASurveiller);
        entityManager.persist(livreNonLuAutreUtilisateur);
        entityManager.persist(serieAutreUtilisateur);
        entityManager.persist(livreNonLu);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSerieASurveiller(utilisateur);

        assertThat(resultat).hasSize(1).containsOnly(serieASurveiller.getIdSerie());
    }

    @Test
    @DisplayName("Ne doit pas retourner une série dont la publication est déjà terminée")
    void trouverSerieASurveiller_excludesPublicationTerminee(){
        // La série du setup a déjà statutPublication = TERMINEE, donc elle ne doit pas ressortir.
        List<Integer> resultat = serieRepository.trouverIdsSerieASurveiller(utilisateur);

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Ne doit pas retourner une série abandonnée même si sa publication est en cours")
    void trouverSerieASurveiller_excludesSerieAbandonnee(){
        Serie serieAbandonnee = new Serie("Cosmere", utilisateur, StatutSerie.ABANDONNEE, StatutPublication.EN_COURS, 20);
        Livre livreNonLu = new Livre("Brandon Sanderson", "Tome 2", "1111111111111", 2, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieAbandonnee);
        entityManager.persist(serieAbandonnee);
        entityManager.persist(livreNonLu);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSerieASurveiller(utilisateur);

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Ne doit pas retourner une série dont tous les livres sont déjà lus (déjà à jour)")
    void trouverSerieASurveiller_excludesSerieDejaAJour(){
        Serie serieAJour = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre livre10 = new Livre("Ilona Andrews", "Tome 1", "2222222222222", 1, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026,
                Month.JANUARY, 1), serieAJour);
        Livre livre20 = new Livre("Ilona Andrews", "Tome 2", "3333333333333", 2, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026,
                Month.FEBRUARY, 1), serieAJour);

        Serie serieASurveiller = new Serie("Cosmere", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 20);
        Livre livreNonLu = new Livre("Brandon Sanderson", "Tome 2", "1111111111111", 2, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieASurveiller);

        entityManager.persist(serieAJour);
        entityManager.persist(livre10);
        entityManager.persist(livre20);
        entityManager.persist(serieASurveiller);
        entityManager.persist(livreNonLu);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSerieASurveiller(utilisateur);

        assertThat(resultat).containsOnly(serieASurveiller.getIdSerie());
    }

    @Test
    @DisplayName("Doit retourner les séries avec livres à acheter triées par dernière lecture croissante")
    void trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture_returnTriesParDateCroissante(){
        Serie serieRecente = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 12);
        Livre livreLuRecent = new Livre("Ilona Andrews", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JUNE, 1), serieRecente);
        Livre livreAAcheterRecent = new Livre("Ilona Andrews", "Tome 2", "2222222222222", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null,
                null, serieRecente);

        Serie serieAncienne = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 5);
        Livre livreLuAncien = new Livre("Patricia Briggs", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2022, Month.JANUARY, 1), serieAncienne);
        Livre livreAAcheterAncien = new Livre("Patricia Briggs", "Tome 2", "4444444444444", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieAncienne);

        Serie serieAutreUtilisateur = new Serie("Riyria", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 6);
        Livre livreLuAutreUtilisateur = new Livre("Michael J. Sullivan", "Tome 1", "5555555555555", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2020, Month.JANUARY, 1), serieAutreUtilisateur);
        Livre livreAAcheterAutreUtilisateur = new Livre("Michael J. Sullivan", "Tome 2", "6666666666666", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieAutreUtilisateur);

        entityManager.persist(serieRecente);
        entityManager.persist(livreLuRecent);
        entityManager.persist(livreAAcheterRecent);
        entityManager.persist(serieAncienne);
        entityManager.persist(livreLuAncien);
        entityManager.persist(livreAAcheterAncien);
        entityManager.persist(serieAutreUtilisateur);
        entityManager.persist(livreLuAutreUtilisateur);
        entityManager.persist(livreAAcheterAutreUtilisateur);
        entityManager.flush();

        List<Integer> ids = serieRepository.trouverIdsSeriesAvecLivresAAcheterTrieesParDerniereLecture(utilisateur);

        assertThat(ids).containsExactly(serieAncienne.getIdSerie(), serieRecente.getIdSerie());

        List<Serie> resultat = serieRepository.trouverSeriesAvecDetailsParIds(ids);

        assertThat(resultat).hasSize(2).containsExactlyInAnyOrder(serieAncienne, serieRecente);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série sans aucun livre à acheter")
    void trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture_excludesSerieSansLivreAAcheter(){
        Serie serieSansAchat = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 12);
        Livre livreLu = new Livre("Ilona Andrews", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JUNE, 1), serieSansAchat);

        Serie serieAvecAchat = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 5);
        Livre livreLuAvecAchat = new Livre("Patricia Briggs", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2022, Month.JANUARY, 1), serieAvecAchat);
        Livre livreAAcheter = new Livre("Patricia Briggs", "Tome 2", "4444444444444", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieAvecAchat);

        entityManager.persist(serieSansAchat);
        entityManager.persist(livreLu);
        entityManager.persist(serieAvecAchat);
        entityManager.persist(livreLuAvecAchat);
        entityManager.persist(livreAAcheter);
        entityManager.flush();

        List<Integer> ids = serieRepository.trouverIdsSeriesAvecLivresAAcheterTrieesParDerniereLecture(utilisateur);
        List<Serie> resultat = serieRepository.trouverSeriesAvecDetailsParIds(ids);

        assertThat(resultat).containsOnly(serieAvecAchat);
    }

    @Test
    @DisplayName("Doit départager par nombre de tomes à acheter en cas d'égalité de dernière lecture")
    void trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture_departageParNombreAAcheter(){
        LocalDate memeDate = LocalDate.of(2023, Month.MARCH, 1);

        Serie serieAvecPlusATrouver = new Serie("Havrefer", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 6);
        Livre livreLu1 = new Livre("Richard Ford", "Tome 1", "5555555555555", 1, StatutLivre.LU, FormatLivre.PAPIER, null, memeDate, serieAvecPlusATrouver);
        Livre livreAAcheter1 = new Livre("Richard Ford", "Tome 2", "6666666666666", 2, StatutLivre.A_ACHETER, FormatLivre.PAPIER, null, null,
                serieAvecPlusATrouver);
        Livre livreAAcheter2 = new Livre("Richard Ford", "Tome 3", "7777777777777", 3, StatutLivre.A_ACHETER, FormatLivre.PAPIER, null, null,
                serieAvecPlusATrouver);

        Serie serieAvecMoinsATrouver = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 6);
        Livre livreLu2 = new Livre("Jacqueline Carey", "Tome 1", "8888888888888", 1, StatutLivre.LU, FormatLivre.PAPIER, null, memeDate,
                serieAvecMoinsATrouver);
        Livre livreAAcheter3 = new Livre("Jacqueline Carey", "Tome 2", "9999999999999", 2, StatutLivre.A_ACHETER, FormatLivre.PAPIER, null, null,
                serieAvecMoinsATrouver);

        entityManager.persist(serieAvecPlusATrouver);
        entityManager.persist(livreLu1);
        entityManager.persist(livreAAcheter1);
        entityManager.persist(livreAAcheter2);
        entityManager.persist(serieAvecMoinsATrouver);
        entityManager.persist(livreLu2);
        entityManager.persist(livreAAcheter3);
        entityManager.flush();

        List<Integer> ids = serieRepository.trouverIdsSeriesAvecLivresAAcheterTrieesParDerniereLecture(utilisateur);

        assertThat(ids).containsExactly(serieAvecMoinsATrouver.getIdSerie(), serieAvecPlusATrouver.getIdSerie());

        List<Serie> resultat = serieRepository.trouverSeriesAvecDetailsParIds(ids);

        assertThat(resultat).hasSize(2).containsExactlyInAnyOrder(serieAvecMoinsATrouver, serieAvecPlusATrouver);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série sans aucune date de lecture connue")
    void trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture_excludesSerieJamaisCommencee(){
        Serie serieJamaisCommencee = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 5);
        Livre livreEnPal = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieJamaisCommencee);
        Livre livreAAcheterJamaisCommencee = new Livre("Patricia Briggs", "Tome 3", "2222222222222", 3, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieJamaisCommencee);

        Serie serieDejaCommencee = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 12);
        Livre livreLu = new Livre("Ilona Andrews", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JUNE, 1), serieDejaCommencee);
        Livre livreAAcheterDejaCommencee = new Livre("Ilona Andrews", "Tome 2", "4444444444444", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieDejaCommencee);

        entityManager.persist(serieJamaisCommencee);
        entityManager.persist(livreEnPal);
        entityManager.persist(livreAAcheterJamaisCommencee);
        entityManager.persist(serieDejaCommencee);
        entityManager.persist(livreLu);
        entityManager.persist(livreAAcheterDejaCommencee);
        entityManager.flush();

        List<Integer> ids = serieRepository.trouverIdsSeriesAvecLivresAAcheterTrieesParDerniereLecture(utilisateur);
        List<Serie> resultat = serieRepository.trouverSeriesAvecDetailsParIds(ids);

        assertThat(resultat).containsOnly(serieDejaCommencee);
    }

    @Test
    @DisplayName("Doit retourner les séries dont le tome 1 a été lu dans la période donnée")
    void trouverSeriesAvecTome1LuDansAnnee_tome1LuDansLaPeriode_returnsSerie(){
        Serie serieCandidate = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 5);
        Livre tome1 = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.MARCH, 1), serieCandidate);

        Serie serieAutreUtilisateur = new Serie("Riyria", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 5);
        Livre tome1AutreUtilisateur = new Livre("Michael J. Sullivan", "Tome 1", "2222222222222", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.MARCH, 1), serieAutreUtilisateur);

        entityManager.persist(serieCandidate);
        entityManager.persist(tome1);
        entityManager.persist(serieAutreUtilisateur);
        entityManager.persist(tome1AutreUtilisateur);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesAvecTome1LuDansAnnee(LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.DECEMBER, 31), utilisateur);

        assertThat(resultat).containsOnly(serieCandidate);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série dont le tome 1 a été lu hors de la période")
    void trouverSeriesAvecTome1LuDansAnnee_tome1LuHorsPeriode_excludesSerie(){
        Serie serieHorsPeriode = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 5);
        Livre tome1HorsPeriode = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2022, Month.MARCH, 1), serieHorsPeriode);

        Serie serieDansLaPeriode = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 5);
        Livre tome1DansLaPeriode = new Livre("Jacqueline Carey", "Tome 1", "2222222222222", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.MARCH, 1), serieDansLaPeriode);

        entityManager.persist(serieHorsPeriode);
        entityManager.persist(tome1HorsPeriode);
        entityManager.persist(serieDansLaPeriode);
        entityManager.persist(tome1DansLaPeriode);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesAvecTome1LuDansAnnee(LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.DECEMBER, 31), utilisateur);

        assertThat(resultat).containsOnly(serieDansLaPeriode);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série dont le tome 1 n'est pas lu")
    void trouverSeriesAvecTome1LuDansAnnee_tome1NonLu_excludesSerie(){
        Serie serieTome1NonLu = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 5);
        Livre tome1 = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieTome1NonLu);

        Serie serieTome1Lu = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 5);
        Livre tome1Lu = new Livre("Jacqueline Carey", "Tome 1", "2222222222222", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.MARCH, 1), serieTome1Lu);

        entityManager.persist(serieTome1NonLu);
        entityManager.persist(tome1);
        entityManager.persist(serieTome1Lu);
        entityManager.persist(tome1Lu);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesAvecTome1LuDansAnnee(LocalDate.of(2026, Month.JANUARY, 1), LocalDate.of(2026, Month.DECEMBER, 31), utilisateur);

        assertThat(resultat).containsOnly(serieTome1Lu);
    }

    @Test
    @DisplayName("Doit retourner une série en cours sans aucun livre lu")
    void trouverSeriesJamaisCommencees_serieSansLivreLu_returnSerie(){
        Serie serieJamaisCommencee = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre livreNonLu = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieJamaisCommencee);

        Serie serieAutreUtilisateur = new Serie("Riyria", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre livreNonLuAutreUtilisateur = new Livre("Michael J. Sullivan", "Tome 1", "2222222222222", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieAutreUtilisateur);

        entityManager.persist(serieJamaisCommencee);
        entityManager.persist(livreNonLu);
        entityManager.persist(serieAutreUtilisateur);
        entityManager.persist(livreNonLuAutreUtilisateur);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesJamaisCommencees(utilisateur);

        assertThat(resultat).containsOnly(serieJamaisCommencee);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série avec au moins un livre lu")
    void trouverSeriesJamaisCommencees_serieAvecLivreLu_excludesSerie(){
        Serie serieCommencee = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre livreLu = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieCommencee);

        Serie serieJamaisCommencee = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre livreNonLu = new Livre("Jacqueline Carey", "Tome 1", "2222222222222", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieJamaisCommencee);

        entityManager.persist(serieCommencee);
        entityManager.persist(livreLu);
        entityManager.persist(serieJamaisCommencee);
        entityManager.persist(livreNonLu);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesJamaisCommencees(utilisateur);

        assertThat(resultat).containsOnly(serieJamaisCommencee);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série terminée sans lecture")
    void trouverSeriesJamaisCommencees_serieTermineeSansLecture_excludesSerie(){
        Serie serieTerminee = new Serie("Alpha & Omega", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 3);
        Livre livreNonLu = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieTerminee);

        Serie serieJamaisCommencee = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre livreNonLuJamaisCommencee = new Livre("Jacqueline Carey", "Tome 1", "2222222222222", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieJamaisCommencee);

        entityManager.persist(serieTerminee);
        entityManager.persist(livreNonLu);
        entityManager.persist(serieJamaisCommencee);
        entityManager.persist(livreNonLuJamaisCommencee);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesJamaisCommencees(utilisateur);

        assertThat(resultat).containsOnly(serieJamaisCommencee);
    }

    @Test
    @DisplayName("Ne doit pas retournée une série abandonnée sans aucune lecture")
    void trouverSeriesJamaisCommencees_serieAbandonneSansLecture_excludesSerie(){
        Serie serieAbandonnee = new Serie("Chasseuse de la nuit", utilisateur, StatutSerie.ABANDONNEE, StatutPublication.TERMINEE, 8);
        Livre livreNonLu = new Livre("Jeaniene Forst", "Au bord de la tombe", "123456789123", 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null,
                null, serieAbandonnee);

        Serie serieJamaisCommencee = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre livreNonLuJamaisCommencee = new Livre("Jacqueline Carey", "Tome 1", "2222222222222", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieJamaisCommencee);

        entityManager.persist(serieAbandonnee);
        entityManager.persist(livreNonLu);
        entityManager.persist(serieJamaisCommencee);
        entityManager.persist(livreNonLuJamaisCommencee);
        entityManager.flush();

        List<Serie> resultat = serieRepository.trouverSeriesJamaisCommencees(utilisateur);

        assertThat(resultat).containsOnly(serieJamaisCommencee);
    }

    @Test
    @DisplayName("Doit retourner une série dont tous les tomes (sur le total réel) sont lus")
    void trouverSeriesAJour_touteLaSerieLue_returnSerie(){
        Serie serieAJour = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre tome1 = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieAJour);
        Livre tome2 = new Livre("Patricia Briggs", "Tome 2", "2222222222222", 2, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.FEBRUARY, 1), serieAJour);

        Serie serieAutreUtilisateur = new Serie("Riyria", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre tome1AutreUtilisateur = new Livre("Michael J. Sullivan", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieAutreUtilisateur);
        Livre tome2AutreUtilisateur = new Livre("Michael J. Sullivan", "Tome 2", "4444444444444", 2, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.FEBRUARY, 1), serieAutreUtilisateur);

        entityManager.persist(serieAJour);
        entityManager.persist(tome1);
        entityManager.persist(tome2);
        entityManager.persist(serieAutreUtilisateur);
        entityManager.persist(tome1AutreUtilisateur);
        entityManager.persist(tome2AutreUtilisateur);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSeriesAJour(utilisateur);

        assertThat(resultat).containsOnly(serieAJour.getIdSerie());
    }

    @Test
    @DisplayName("Ne doit pas retourner une série dont il manque des tomes non encore enregistrés (bug Bourbon Kid)")
    void trouverSeriesAJour_tomesManquantsNonEnregistres_excludesSerie(){
        Serie bourbonKid = new Serie("Bourbon Kid", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 11);
        Livre tome1 = new Livre("Anonyme", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), bourbonKid);
        Livre tome2 = new Livre("Anonyme", "Tome 2", "4444444444444", 2, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 15), bourbonKid);

        // Seuls 2 tomes sur les 11 sont enregistrés, tous les deux LUS — le bug faisait ressortir cette série à tort.
        Serie serieAJour = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre tome1AJour = new Livre("Patricia Briggs", "Tome 1", "5555555555555", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieAJour);
        Livre tome2AJour = new Livre("Patricia Briggs", "Tome 2", "6666666666666", 2, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.FEBRUARY, 1), serieAJour);

        entityManager.persist(bourbonKid);
        entityManager.persist(tome1);
        entityManager.persist(tome2);
        entityManager.persist(serieAJour);
        entityManager.persist(tome1AJour);
        entityManager.persist(tome2AJour);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSeriesAJour(utilisateur);

        assertThat(resultat).containsOnly(serieAJour.getIdSerie());
    }

    @Test
    @DisplayName("Ne doit pas retourner une série avec un tome non lu enregistré")
    void trouverSeriesAJour_tomeNonLuEnregistre_excludesSerie(){
        Serie serieEnCours = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre tome1 = new Livre("Ilona Andrews", "Tome 1", "5555555555555", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieEnCours);
        Livre tome2 = new Livre("Ilona Andrews", "Tome 2", "6666666666666", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                serieEnCours);

        Serie serieAJour = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre tome1AJour = new Livre("Patricia Briggs", "Tome 1", "7777777777777", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieAJour);
        Livre tome2AJour = new Livre("Patricia Briggs", "Tome 2", "8888888888888", 2, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.FEBRUARY, 1), serieAJour);

        entityManager.persist(serieEnCours);
        entityManager.persist(tome1);
        entityManager.persist(tome2);
        entityManager.persist(serieAJour);
        entityManager.persist(tome1AJour);
        entityManager.persist(tome2AJour);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSeriesAJour(utilisateur);

        assertThat(resultat).containsOnly(serieAJour.getIdSerie());
    }

    @Test
    @DisplayName("Ne doit pas retourner une série dont la publication est terminée")
    void trouverSeriesAJour_publicationTerminee_excludesSerie(){
        Serie serieTerminee = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 2);
        Livre tome1 = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieTerminee);
        Livre tome2 = new Livre("Patricia Briggs", "Tome 2", "2222222222222", 2, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.FEBRUARY, 1), serieTerminee);

        Serie serieAJour = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre tome1AJour = new Livre("Ilona Andrews", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieAJour);
        Livre tome2AJour = new Livre("Ilona Andrews", "Tome 2", "4444444444444", 2, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.FEBRUARY, 1), serieAJour);

        entityManager.persist(serieTerminee);
        entityManager.persist(tome1);
        entityManager.persist(tome2);
        entityManager.persist(serieAJour);
        entityManager.persist(tome1AJour);
        entityManager.persist(tome2AJour);
        entityManager.flush();

        List<Integer> resultat = serieRepository.trouverIdsSeriesAJour(utilisateur);

        assertThat(resultat).containsOnly(serieAJour.getIdSerie());
    }

    @Test
    @DisplayName("Doit retourner les séries avec leurs livres et leur genre à partir d'une liste d'ids")
    void trouverSeriesAvecDetailsParIds_idsValides_returnSeriesAvecLivresEtGenre(){
        Genre fantasy = new Genre("Fantasy");
        Serie serieBitLit = new Serie("Alpha & Omega", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 1);
        serieBitLit.setGenre(fantasy);
        Livre tome1 = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null,
                LocalDate.of(2026, Month.JANUARY, 1), serieBitLit);

        entityManager.persist(fantasy);
        entityManager.persist(serieBitLit);
        entityManager.persist(tome1);
        entityManager.flush();
        entityManager.clear();

        List<Serie> resultat = serieRepository.trouverSeriesAvecDetailsParIds(List.of(serieBitLit.getIdSerie()));

        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst().getLivres()).containsExactly(tome1);
        assertThat(resultat.getFirst().getGenre()).isEqualTo(fantasy);
    }

    @Test
    @DisplayName("Doit retourner les séries en cours marquées à lire en anglais")
    void findByLireEnAnglaisAndStatutSerie_serieEnCoursMarquee_returnSerie(){
        Serie serieAnglais = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        serieAnglais.setLireEnAnglais(true);

        Serie serieAnglaisAutreUtilisateur = new Serie("Riyria", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        serieAnglaisAutreUtilisateur.setLireEnAnglais(true);

        entityManager.persist(serieAnglais);
        entityManager.persist(serieAnglaisAutreUtilisateur);
        entityManager.flush();

        List<Serie> resultat = serieRepository.findByLireEnAnglaisAndStatutSerie(true, StatutSerie.EN_COURS, utilisateur);

        assertThat(resultat).containsOnly(serieAnglais);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série non marquée à lire en anglais")
    void findByLireEnAnglaisAndStatutSerie_serieNonMarquee_excludesSerie(){
        Serie serieAnglais = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        serieAnglais.setLireEnAnglais(true);
        entityManager.persist(serieAnglais);
        entityManager.flush();

        // La série du setup a lireEnAnglais = false par défaut.
        List<Serie> resultat = serieRepository.findByLireEnAnglaisAndStatutSerie(true, StatutSerie.EN_COURS, utilisateur);

        assertThat(resultat).containsOnly(serieAnglais);
    }

    @Test
    @DisplayName("Ne doit pas retourner une série terminée même marquée à lire en anglais")
    void findByLireEnAnglaisAndStatutSerie_serieTerminee_excludesSerie(){
        Serie serieTerminee = new Serie("Red Rising", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 6);
        serieTerminee.setLireEnAnglais(true);

        Serie serieEnCoursAnglais = new Serie("Kushiel", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        serieEnCoursAnglais.setLireEnAnglais(true);

        entityManager.persist(serieTerminee);
        entityManager.persist(serieEnCoursAnglais);
        entityManager.flush();

        List<Serie> resultat = serieRepository.findByLireEnAnglaisAndStatutSerie(true, StatutSerie.EN_COURS, utilisateur);

        assertThat(resultat).containsOnly(serieEnCoursAnglais);
    }

    @Test
    @DisplayName("Doit retourner les séries délaissées (dernière lecture trop ancienne et pas totalement lues)")
    void trouverIdsSeriesDelaissees_returnSeriesAncienneLectureNonTerminee() {
        // Série délaissée : dernière lecture il y a plus d'un an, pas terminée
        Serie serieDelaissee = new Serie("Le Trône de Fer", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre livreLuAncien = new Livre("George R. R. Martin", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.PAPIER, null,
                LocalDate.of(2023, Month.JANUARY, 1), serieDelaissee);
        Livre livreNonLu = new Livre("George R. R. Martin", "Tome 2", "2222222222222", 2, StatutLivre.DANS_PAL, FormatLivre.PAPIER, null,
                null, serieDelaissee);

        // Série récemment lue : ne doit PAS apparaître
        Serie serieRecente = new Serie("Mistborn", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre livreLuRecent = new Livre("Brandon Sanderson", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.PAPIER, null,
                LocalDate.now().minusMonths(1), serieRecente);
        Livre livreNonLu2 = new Livre("Brandon Sanderson", "Tome 2", "4444444444444", 2, StatutLivre.DANS_PAL, FormatLivre.PAPIER, null,
                null, serieRecente);

        // Série délaissée chez un autre utilisateur (ici le "premier" utilisateur du setup) : ne doit PAS apparaître pour autreUtilisateur
        Serie serieDelaisseeUtilisateur = new Serie("Malazan", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 2);
        Livre livreLuAncienUtilisateur = new Livre("Steven Erikson", "Tome 1", "5555555555555", 1, StatutLivre.LU, FormatLivre.PAPIER, null,
                LocalDate.of(2023, Month.JANUARY, 1), serieDelaisseeUtilisateur);
        Livre livreNonLuUtilisateur = new Livre("Steven Erikson", "Tome 2", "6666666666666", 2, StatutLivre.DANS_PAL, FormatLivre.PAPIER, null,
                null, serieDelaisseeUtilisateur);

        entityManager.persist(serieDelaissee);
        entityManager.persist(livreLuAncien);
        entityManager.persist(livreNonLu);
        entityManager.persist(serieRecente);
        entityManager.persist(livreLuRecent);
        entityManager.persist(livreNonLu2);
        entityManager.persist(serieDelaisseeUtilisateur);
        entityManager.persist(livreLuAncienUtilisateur);
        entityManager.persist(livreNonLuUtilisateur);
        entityManager.flush();

        LocalDate dateSeuil = LocalDate.now().minusYears(1);
        Pageable pageable = PageRequest.of(0, 15);

        List<Integer> ids = serieRepository.trouverIdsSeriesDelaissees(dateSeuil, autreUtilisateur, pageable);

        assertThat(ids).containsExactly(serieDelaissee.getIdSerie());
    }

    @Test
    @DisplayName("Doit compter les séries par genre, en excluant les abandonnées et en regroupant celles sans genre sous 'Sans genre'")
    void compterSeriesParGenre_donneesVariees_returnRepartitionCorrecte(){
        Genre fantasy = new Genre("Fantasy");
        Genre comics = new Genre("Comics");

        Serie serieFantasy1 = new Serie("Kushiel's Legacy", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        serieFantasy1.setGenre(fantasy);
        Serie serieFantasy2 = new Serie("Mercy Thompson", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 10);
        serieFantasy2.setGenre(fantasy);
        Serie serieComics = new Serie("Saga", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 5);
        serieComics.setGenre(comics);
        Serie serieAbandonnee = new Serie("Abandon", utilisateur, StatutSerie.ABANDONNEE, StatutPublication.TERMINEE, 2);
        serieAbandonnee.setGenre(fantasy);

        Serie serieFantasyAutreUtilisateur = new Serie("Riyria", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 6);
        serieFantasyAutreUtilisateur.setGenre(fantasy);

        entityManager.persist(fantasy);
        entityManager.persist(comics);
        entityManager.persist(serieFantasy1);
        entityManager.persist(serieFantasy2);
        entityManager.persist(serieComics);
        entityManager.persist(serieAbandonnee);
        entityManager.persist(serieFantasyAutreUtilisateur);
        entityManager.flush();

        List<RepartitionCategorieDTO> resultat = serieRepository.compterSeriesParGenre(utilisateur);

        assertThat(resultat)
                .extracting(RepartitionCategorieDTO::getNom, RepartitionCategorieDTO::getNombreSerie)
                .containsExactlyInAnyOrder(
                        tuple("Fantasy", 2L),
                        tuple("Comics", 1L),
                        tuple("Sans genre", 1L) // la série du setup, qui n'a pas de genre
                );
    }

    @Test
    @DisplayName("Doit compter les séries par nature, en excluant les abandonnées")
    void compterSeriesParNature_donneesVariees_returnRepartitionCorrecte(){
        Serie serieManga1 = new Serie("One Piece", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 100);
        serieManga1.setNatureSerie(NatureSerie.MANGA);
        Serie serieManga2 = new Serie("Naruto", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 72);
        serieManga2.setNatureSerie(NatureSerie.MANGA);
        Serie serieRoman = new Serie("Kushiel's Legacy", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        serieRoman.setNatureSerie(NatureSerie.ROMAN);
        Serie serieAbandonnee = new Serie("Abandon", utilisateur, StatutSerie.ABANDONNEE, StatutPublication.TERMINEE, 2);
        serieAbandonnee.setNatureSerie(NatureSerie.MANGA);

        Serie serieMangaAutreUtilisateur = new Serie("Bleach", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 74);
        serieMangaAutreUtilisateur.setNatureSerie(NatureSerie.MANGA);

        entityManager.persist(serieManga1);
        entityManager.persist(serieManga2);
        entityManager.persist(serieRoman);
        entityManager.persist(serieAbandonnee);
        entityManager.persist(serieMangaAutreUtilisateur);
        entityManager.flush();

        List<RepartitionCategorieDTO> resultat = serieRepository.compterSeriesParNature(utilisateur);

        assertThat(resultat)
                .extracting(RepartitionCategorieDTO::getNom, RepartitionCategorieDTO::getNombreSerie)
                .containsExactlyInAnyOrder(
                        tuple("MANGA", 2L),
                        tuple("ROMAN", 1L),
                        tuple("NON_DEFINI", 1L) // la série du setup, jamais assignée
                );
    }
}
