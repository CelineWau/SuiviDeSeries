package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.*;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.FormatLivre;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutPublication;
import fr.celine.suivideseries.enums.StatutSerie;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.SerieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SerieServiceTest {

    @Mock
    private SerieRepository serieRepository;

    @InjectMocks
    private SerieService serieService;

    private Utilisateur utilisateur;
    private Serie serie;

    @BeforeEach
    void setup() {
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        serie = new Serie("Le Prieuré de l'oranger", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 2);
    }

    @Test
    @DisplayName("Doit lever une exception si le nom est nul")
    void creerSerie_nomNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie(null, utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le nom de la série est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si l'utilisateur est nul")
    void creerSerie_utilisateurNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", null, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un utilisateur doit être associé à une série.");
    }

    @Test
    @DisplayName("Doit lever une exception si le nombre total de livre est inférieur à zéro")
    void creerSerie_nombreLivreTotalInferieurAZero_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, -8))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La série doit avoir un nombre de livre total supérieur à zéro.");
    }

    @Test
    @DisplayName("Doit lever une exception si le statut de la série est nul")
    void creerSerie_statutSerieNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, null, StatutPublication.TERMINEE, 4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La série doit obligatoirement avoir un statut.");
    }

    @Test
    @DisplayName("Doit lever une exception si le statut de publication de la série est nul")
    void creerSerie_statutPublicationNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, null, 4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La série doit obligatoirement avoir un statut de publication.");
    }

    @Test
    @DisplayName("Doit lever une exception si le série existe déjà en base de données")
    void creerSerie_dejaPresentEnBDD_leveBusinessException() {
        when(serieRepository.findByNom("Twilight")).thenReturn(Optional.of(new Serie()));
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Une série existe déjà avec ce nom.");
    }

    @Test
    @DisplayName("Doit créer une nouvelle série")
    void creerSerie_donneesValides_returnsSerie() {

        when(serieRepository.save(any(Serie.class))).thenReturn(new Serie("Twilight", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 4));

        Serie resultat = serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 4);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Twilight");
        assertThat(resultat.getStatutSerie()).isEqualTo(StatutSerie.EN_COURS);
        assertThat(resultat.getStatutPublication()).isEqualTo(StatutPublication.TERMINEE);
        assertThat(resultat.getNombreLivreTotal()).isEqualTo(4);
        assertThat(resultat.getUtilisateur().getFirst()).isEqualTo(utilisateur);
        verify(serieRepository, times(1)).save(any(Serie.class));
    }

    @Test
    @DisplayName("Doit lever une exception si le nombre de livres manquants est inférieur ou égal à 0")
    void trouverSerieAvecNombreLivresManquants_livresManquantsInferieurZero_leveBusinessException() {
        assertThatThrownBy(() -> serieService.trouverSerieAvecNombreLivresManquants(-9))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le nombre de livre manquant ne peut pas être négatif ou égal à zéro.");
    }

    @Test
    @DisplayName("Doit trouver une série avec des livres manquants")
    void trouverSerieAvecNombreLivresManquants_donneesValide_returnsSeries() {
        when(serieRepository.trouverSeriesParNombreLivresManquants(1)).thenReturn(List.of(serie));

        List<Serie> resultat = serieService.trouverSerieAvecNombreLivresManquants(1);

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        verify(serieRepository, times(1)).trouverSeriesParNombreLivresManquants(1);
    }

    @Test
    @DisplayName("Doit lever une exception si le nombre de livres manquants pour la PAL est inférieur ou égal à 0")
    void trouverSeriesPresqueFiniesDansLaPal_livreManquantInferieurOuEgalZero_leveBusinessException() {
        assertThatThrownBy(() -> serieService.trouverSeriesPresqueFiniesDansLaPal(0))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le nombre de livre manquant ne peut pas être négatif ou égal à zéro.");
    }

    @Test
    @DisplayName("Doit retourner les séries presque finies dans la PAL")
    void trouverSeriesPresqueFiniesDansLaPal_donneesValides_returnsSeries() {
        when(serieRepository.trouverSeriesPresqueFinieDansLaPal(2)).thenReturn(List.of(serie));

        List<Serie> resultat = serieService.trouverSeriesPresqueFiniesDansLaPal(2);

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        verify(serieRepository, times(1)).trouverSeriesPresqueFinieDansLaPal(2);
    }

    @Test
    @DisplayName("Doit modifier le nombre de livres total et repasser la série en EN_COURS si elle n'est pas abandonnée")
    void modifierNombreLivreTotal_serieNonAbandonnee_repasseEnCours() {
        serie.setStatutSerie(StatutSerie.TERMINEE);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierNombreLivreTotal(1, 5);

        assertThat(resultat.getNombreLivreTotal()).isEqualTo(5);
        assertThat(resultat.getStatutSerie()).isEqualTo(StatutSerie.EN_COURS);
    }

    @Test
    @DisplayName("Ne doit pas repasser une série abandonnée en EN_COURS lors de la modification du nombre de livres")
    void modifierNombreLivreTotal_serieAbandonnee_resteAbandonnee() {
        serie.setStatutSerie(StatutSerie.ABANDONNEE);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierNombreLivreTotal(1, 5);

        assertThat(resultat.getNombreLivreTotal()).isEqualTo(5);
        assertThat(resultat.getStatutSerie()).isEqualTo(StatutSerie.ABANDONNEE);
    }

    @Test
    @DisplayName("Doit convertir les séries en DTO avec leur nombre de livres à acheter")
    void trouverSeriesAvecLivresAAcheter_returnListeDeDTO() {
        Livre livreLu = new Livre("Tolkien", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null, serie);
        Livre livreAAcheter1 = new Livre("Tolkien", "Tome 2", "2222222222222", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null, serie);
        Livre livreAAcheter2 = new Livre("Tolkien", "Tome 3", "3333333333333", 3, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null, serie);
        serie.getLivres().add(livreLu);
        serie.getLivres().add(livreAAcheter1);
        serie.getLivres().add(livreAAcheter2);

        when(serieRepository.trouverSeriesAvecLivresAAcheter(any(Pageable.class))).thenReturn(List.of(serie));

        List<SerieAvecLivresAAcheterDTO> resultat = serieService.trouverSeriesAvecLivresAAcheter();

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst().getNom()).isEqualTo(serie.getNom());
        assertThat(resultat.getFirst().getNombreLivreAAcheter()).isEqualTo(2);
        verify(serieRepository, times(1)).trouverSeriesAvecLivresAAcheter(any(Pageable.class));
    }

    @Test
    @DisplayName("Doit remplir la date de fin automatiquement lors du passage de EN_COURS à TERMINEE")
    void modifierStatutSerie_transitionEnCoursVersTerminee_remplitDateFin() {
        serie.setStatutSerie(StatutSerie.EN_COURS);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierStatutSerie(1, StatutSerie.TERMINEE);

        assertThat(resultat.getDateFin()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Ne doit pas remplir la date de fin si la transition n'est pas EN_COURS vers TERMINEE")
    void modifierStatutSerie_transitionAutre_neRemplitPasDateFin() {
        serie.setStatutSerie(StatutSerie.EN_COURS);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierStatutSerie(1, StatutSerie.ABANDONNEE);

        assertThat(resultat.getDateFin()).isNull();
    }

    @Test
    @DisplayName("Doit répartir les séries en petites, moyennes et sagas selon leur nombre de livres")
    void calculerRepartitionTailleSeries_donneesValides_returnsRepartitionCorrecte() {
        Serie petite = new Serie("Petite série", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 2);
        Serie moyenne = new Serie("Série moyenne", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 5);
        Serie saga = new Serie("Grande saga", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 12);
        when(serieRepository.findByStatutSerieNot(StatutSerie.ABANDONNEE)).thenReturn(List.of(petite, moyenne, saga));

        TailleSerieDTO resultat = serieService.calculerRepartitionTailleSeries();

        assertThat(resultat.getPetites()).isEqualTo(1);
        assertThat(resultat.getMoyennes()).isEqualTo(1);
        assertThat(resultat.getSagas()).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit retourner une répartition à zéro si aucune série n'est présente")
    void calculerRepartitionTailleSeries_aucuneSerie_returnsRepartitionAZero() {
        when(serieRepository.findByStatutSerieNot(StatutSerie.ABANDONNEE)).thenReturn(List.of());

        TailleSerieDTO resultat = serieService.calculerRepartitionTailleSeries();

        assertThat(resultat.getPetites()).isEqualTo(0);
        assertThat(resultat.getMoyennes()).isEqualTo(0);
        assertThat(resultat.getSagas()).isEqualTo(0);
    }

    @Test
    @DisplayName("Doit calculer la différence en jours entre la première et la dernière lecture")
    void calculerDifferenceDatePremiereEtDerniereLecture_datesPresentes_returnsDifferenceEnJours() {
        Livre livre1 = new Livre("Tolkien", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026, 1, 1), serie);
        Livre livre2 = new Livre("Tolkien", "Tome 2", "2222222222222", 2, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026, 1, 11), serie);
        serie.getLivres().add(livre1);
        serie.getLivres().add(livre2);

        double resultat = serieService.calculerDifferenceDatePremiereEtDerniereLecture(serie);

        assertThat(resultat).isEqualTo(10.0);
    }

    @Test
    @DisplayName("Doit retourner zéro si aucun livre n'a de date de lecture")
    void calculerDifferenceDatePremiereEtDerniereLecture_aucuneDateLecture_returnsZero() {
        Livre livre1 = new Livre("Tolkien", "Tome 1", "1111111111111", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null, serie);
        serie.getLivres().add(livre1);

        double resultat = serieService.calculerDifferenceDatePremiereEtDerniereLecture(serie);

        assertThat(resultat).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Doit calculer la durée moyenne de lecture des séries terminées")
    void calculerDureeMoyenneLecture_seriesTerminees_returnsMoyenneCorrecte() {
        Serie serieA = new Serie("Série A", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 2);
        serieA.getLivres().add(new Livre("Auteur A", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026, 1, 1), serieA));
        serieA.getLivres().add(new Livre("Auteur A", "Tome 2", "2222222222222", 2, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026, 1, 11), serieA));

        Serie serieB = new Serie("Série B", utilisateur, StatutSerie.TERMINEE, StatutPublication.TERMINEE, 2);
        serieB.getLivres().add(new Livre("Auteur B", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026, 2, 1), serieB));
        serieB.getLivres().add(new Livre("Auteur B", "Tome 2", "4444444444444", 2, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026, 2, 21), serieB));

        when(serieRepository.findByStatutSerie(StatutSerie.TERMINEE)).thenReturn(List.of(serieA, serieB));

        double resultat = serieService.calculerDureeMoyenneLecture();

        assertThat(resultat).isEqualTo(15.0);
    }

    @Test
    @DisplayName("Doit retourner zéro si aucune série terminée n'est présente")
    void calculerDureeMoyenneLecture_aucuneSerieTerminee_returnsZero() {
        when(serieRepository.findByStatutSerie(StatutSerie.TERMINEE)).thenReturn(List.of());

        double resultat = serieService.calculerDureeMoyenneLecture();

        assertThat(resultat).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Doit lever une exception si aucune série avec ebook dans la PAL n'est trouvée")
    void trouverSerieAleatoireDansSerieEbook_aucuneSerie_leveBusinessException() {
        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of());

        assertThatThrownBy(() -> serieService.trouverSerieAleatoireDansSerieEbook())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Il n'y a pas d'ebooks dans la pile à lire qui correspond à demande.");
    }

    @Test
    @DisplayName("Doit retourner l'unique série disponible avec ebook dans la PAL")
    void trouverSerieAleatoireDansSerieEbook_uneSerieDisponible_returnsCetteSerie() {
        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of(serie));

        Serie resultat = serieService.trouverSerieAleatoireDansSerieEbook();

        assertThat(resultat).isEqualTo(serie);
    }

    @Test
    @DisplayName("Doit trouver le numéro de tome le plus petit parmi les ebooks en PAL")
    void trouverTomePlusPetitDansSerie_ebooksDansPal_returnsNumeroLePlusPetit() {
        serie.getLivres().add(new Livre("Tolkien", "Tome 3", "1111111111111", 3, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null, serie));
        serie.getLivres().add(new Livre("Tolkien", "Tome 1", "2222222222222", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null, serie));
        serie.getLivres().add(new Livre("Tolkien", "Tome 2", "3333333333333", 2, StatutLivre.LU, FormatLivre.EBOOK, null, null, serie));

        int resultat = serieService.trouverTomePlusPetitDansSerie(serie);

        assertThat(resultat).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit retourner zéro si aucun ebook n'est en PAL dans la série")
    void trouverTomePlusPetitDansSerie_aucunEbookEnPal_returnsZero() {
        serie.getLivres().add(new Livre("Tolkien", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.PAPIER, null, null, serie));

        int resultat = serieService.trouverTomePlusPetitDansSerie(serie);

        assertThat(resultat).isEqualTo(0);
    }

    @Test
    @DisplayName("Doit proposer un ebook aléatoire à lire parmi les séries en cours dans la PAL")
    void proposerLivreAleatoire_livreDisponible_returnsEbookAleatoireDTO() {
        Livre livre = new Livre("Tolkien", "Le retour du roi", "1111111111111", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null, serie);
        serie.getLivres().add(livre);
        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of(serie));

        EbookAleatoireDTO resultat = serieService.proposerLivreAleatoire();

        assertThat(resultat.getTitre()).isEqualTo("Le retour du roi");
        assertThat(resultat.getAuteur()).isEqualTo("Tolkien");
        assertThat(resultat.getNomSerie()).isEqualTo(serie.getNom());
        assertThat(resultat.getNumeroDansLaSerie()).isEqualTo(1);
    }

    @Test
    @DisplayName("Doit lever une exception si aucun tome de la série n'est en PAL/ebook")
    void proposerLivreAleatoire_aucunLivreCorrespondant_leveBusinessException() {
        serie.getLivres().add(new Livre("Tolkien", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.PAPIER, null, null, serie));
        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of(serie));

        assertThatThrownBy(() -> serieService.proposerLivreAleatoire())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Il manque un tome dans cette série avant de pouvoir en proposer un.");
    }

    @Test
    @DisplayName("Ne doit pas proposer un livre s'il manque un tome non lu avant lui dans la série")
    void proposerLivreAleatoire_tomeIntermediaireManquant_leveBusinessException() {
        Livre tome1Lu = new Livre("Raymond E. Feist", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null, serie);
        Livre tome10EnPal = new Livre("Raymond E. Feist", "Tome 10", "2222222222222", 10, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null, serie);
        serie.getLivres().add(tome1Lu);
        serie.getLivres().add(tome10EnPal);

        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of(serie));

        assertThatThrownBy(() -> serieService.proposerLivreAleatoire())
                .isInstanceOf(BusinessException.class)
                .hasMessage("Il manque un tome dans cette série avant de pouvoir en proposer un.");
    }

    @Test
    @DisplayName("Doit retourner les livres triés par date d'acquisition croissante")
    void trouverLivresPalVieillissante_livresAvecDate_returnTriesParDateCroissante() {
        Utilisateur autreUtilisateur = new Utilisateur("Rowling", "Joanne", "JoJo", "jo@email.fr");
        autreUtilisateur.setMdp("Azerty123");

        Serie serieRecente = new Serie("Harry Potter", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livreRecent = new Livre("J. K. Rowling", "Tome 1", "1111111111111", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK,
                LocalDate.of(2026, 5, 10), null, serieRecente);

        Serie serieAncienne = new Serie("Alpha & Omega", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livreAncien = new Livre("Patricia Briggs", "Tome 1", "2222222222222", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK,
                LocalDate.of(2023, 5, 2), null, serieAncienne);

        serieRecente.getLivres().add(livreRecent);
        serieAncienne.getLivres().add(livreAncien);

        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of(serieRecente, serieAncienne));

        List<LivrePalVieillissantDTO> resultat = serieService.trouverLivresPalVieillissante();

        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getTitre()).isEqualTo(livreAncien.getTitre());
        assertThat(resultat.get(1).getTitre()).isEqualTo(livreRecent.getTitre());
    }

    @Test
    @DisplayName("Doit placer les livres sans date d'acquisition en premier")
    void trouverLivresPalVieillissante_livreSansDate_returnEnPremier() {
        Utilisateur autreUtilisateur = new Utilisateur("Rowling", "Joanne", "JoJo", "jo@email.fr");
        autreUtilisateur.setMdp("Azerty123");

        Serie serieAvecDate = new Serie("Alpha & Omega", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livreAvecDate = new Livre("Patricia Briggs", "Tome 1", "1111111111111", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK,
                LocalDate.of(2023, 5, 2), null, serieAvecDate);

        Serie serieSansDate = new Serie("Les Rougon-Macquart", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
        Livre livreSansDate = new Livre("Emile Zola", "Nana", "2222222222222", 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null,
                serieSansDate);

        serieAvecDate.getLivres().add(livreAvecDate);
        serieSansDate.getLivres().add(livreSansDate);

        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of(serieAvecDate, serieSansDate));

        List<LivrePalVieillissantDTO> resultat = serieService.trouverLivresPalVieillissante();

        assertThat(resultat).hasSize(2);
        assertThat(resultat.get(0).getTitre()).isEqualTo("Nana");
        assertThat(resultat.get(0).getDateAcquisition()).isNull();
        assertThat(resultat.get(1).getTitre()).isEqualTo(livreAvecDate.getTitre());
    }

    @Test
    @DisplayName("Doit limiter le résultat à 5 livres")
    void trouverLivresPalVieillissante_plusDeCinqSeries_returnLimiteACinq() {
        List<Serie> series = new java.util.ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            Utilisateur utilisateurBoucle = new Utilisateur("Nom" + i, "Prenom" + i, "Pseudo" + i, "email" + i + "@email.fr");
            utilisateurBoucle.setMdp("Azerty123");
            Serie serieBoucle = new Serie("Serie " + i, utilisateurBoucle, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 1);
            Livre livreBoucle = new Livre("Auteur " + i, "Titre " + i, "000000000000" + i, 1, StatutLivre.DANS_PAL, FormatLivre.EBOOK,
                    LocalDate.of(2024, 1, i), null, serieBoucle);
            serieBoucle.getLivres().add(livreBoucle);
            series.add(serieBoucle);
        }
        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(series);

        List<LivrePalVieillissantDTO> resultat = serieService.trouverLivresPalVieillissante();

        assertThat(resultat).hasSize(5);
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucune série candidate n'est trouvée")
    void trouverLivresPalVieillissante_aucuneSerie_returnListeVide() {
        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of());

        List<LivrePalVieillissantDTO> resultat = serieService.trouverLivresPalVieillissante();

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Ne doit pas proposer un livre s'il manque un tome non lu avant lui dans la série")
    void trouverLivresPalVieillissante_tomeIntermediaireManquant_excludesSerie() {
        Utilisateur autreUtilisateur = new Utilisateur("Feist", "Raymond", "RayF", "ray@email.fr");
        autreUtilisateur.setMdp("Azerty123");

        Serie serieAvecTrou = new Serie("Les Chroniques du Kondor", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 10);
        Livre tome1Lu = new Livre("Raymond E. Feist", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK,
                LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1), serieAvecTrou);
        Livre tome10EnPal = new Livre("Raymond E. Feist", "L'ombre d'une reine noire", "2222222222222", 10, StatutLivre.DANS_PAL, FormatLivre.EBOOK,
                LocalDate.of(2022, 12, 1), null, serieAvecTrou);
        // Les tomes 2 à 9 ne sont pas enregistrés du tout : trou dans la série.
        serieAvecTrou.getLivres().add(tome1Lu);
        serieAvecTrou.getLivres().add(tome10EnPal);

        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of(serieAvecTrou));

        List<LivrePalVieillissantDTO> resultat = serieService.trouverLivresPalVieillissante();

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Doit proposer un livre si tous les tomes précédents sont bien lus")
    void trouverLivresPalVieillissante_tomesPrecedentsTousLus_includesSerie() {
        Utilisateur autreUtilisateur = new Utilisateur("Briggs", "Patricia", "PatB", "pat@email.fr");
        autreUtilisateur.setMdp("Azerty123");

        Serie serieSansTrou = new Serie("Alpha & Omega", autreUtilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        Livre tome1Lu = new Livre("Patricia Briggs", "Tome 1", "3333333333333", 1, StatutLivre.LU, FormatLivre.EBOOK,
                LocalDate.of(2022, 1, 1), LocalDate.of(2022, 2, 1), serieSansTrou);
        Livre tome2Lu = new Livre("Patricia Briggs", "Tome 2", "4444444444444", 2, StatutLivre.LU, FormatLivre.EBOOK,
                LocalDate.of(2022, 3, 1), LocalDate.of(2022, 4, 1), serieSansTrou);
        Livre tome3EnPal = new Livre("Patricia Briggs", "Tome 3", "5555555555555", 3, StatutLivre.DANS_PAL, FormatLivre.EBOOK,
                LocalDate.of(2023, 5, 2), null, serieSansTrou);
        serieSansTrou.getLivres().add(tome1Lu);
        serieSansTrou.getLivres().add(tome2Lu);
        serieSansTrou.getLivres().add(tome3EnPal);

        when(serieRepository.trouverSeriesAvecEbooksDansLaPal()).thenReturn(List.of(serieSansTrou));

        List<LivrePalVieillissantDTO> resultat = serieService.trouverLivresPalVieillissante();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst().getTitre()).isEqualTo("Tome 3");
        assertThat(resultat.getFirst().getNumeroDansLaSerie()).isEqualTo(3);
    }

    @Test
    @DisplayName("Doit retourner les séries à surveiller avec l'auteur du tome 1")
    void trouverSeriesASurveiller_tome1Present_returnAuteurDuTome1() {
        Serie serieCosmere = new Serie("Cosmere", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 20);
        Livre tome2 = new Livre("Brandon Sanderson", "Tome 2", "1111111111111", 2, StatutLivre.LU, FormatLivre.EBOOK, null, null, serieCosmere);
        Livre tome1 = new Livre("Brandon Sanderson", "Tome 1", "2222222222222", 1, StatutLivre.LU, FormatLivre.EBOOK, null, null, serieCosmere);
        serieCosmere.getLivres().add(tome2);
        serieCosmere.getLivres().add(tome1);

        when(serieRepository.trouverSerieASurveiller()).thenReturn(List.of(serieCosmere));

        List<SerieASurveillerDTO> resultat = serieService.trouverSeriesASurveiller();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst().getNom()).isEqualTo("Cosmere");
        assertThat(resultat.getFirst().getAuteur()).isEqualTo("Brandon Sanderson");
    }

    @Test
    @DisplayName("Doit utiliser l'auteur d'un autre tome si le tome 1 n'est pas enregistré")
    void trouverSeriesASurveiller_tome1Absent_returnAuteurAutreTome() {
        Serie serieSansTome1 = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 10);
        Livre tome3 = new Livre("Ilona Andrews", "Tome 3", "3333333333333", 3, StatutLivre.LU, FormatLivre.EBOOK, null, null, serieSansTome1);
        serieSansTome1.getLivres().add(tome3);

        when(serieRepository.trouverSerieASurveiller()).thenReturn(List.of(serieSansTome1));

        List<SerieASurveillerDTO> resultat = serieService.trouverSeriesASurveiller();

        assertThat(resultat.getFirst().getAuteur()).isEqualTo("Ilona Andrews");
    }

    @Test
    @DisplayName("Doit retourner 'Auteur inconnu' si la série n'a aucun livre enregistré")
    void trouverSeriesASurveiller_aucunLivre_returnAuteurInconnu() {
        Serie serieVide = new Serie("Nouvelle série", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 5);

        when(serieRepository.trouverSerieASurveiller()).thenReturn(List.of(serieVide));

        List<SerieASurveillerDTO> resultat = serieService.trouverSeriesASurveiller();

        assertThat(resultat.getFirst().getAuteur()).isEqualTo("Auteur inconnu.");
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucune série n'est à surveiller")
    void trouverSeriesASurveiller_aucuneSerie_returnListeVide() {
        when(serieRepository.trouverSerieASurveiller()).thenReturn(List.of());

        List<SerieASurveillerDTO> resultat = serieService.trouverSeriesASurveiller();

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner uniquement les livres papier dans la liste de courses papier")
    void trouverListeCoursesPapier_seriesCandidates_returnUniquementPapier() {
        Serie seriePapier = new Serie("Havrefer", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 6);
        Livre livrePapier = new Livre("Richard Ford", "Tome 2", "1111111111111", 2, StatutLivre.A_ACHETER, FormatLivre.PAPIER, null, null, seriePapier);
        seriePapier.getLivres().add(livrePapier);

        Serie serieEbook = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 12);
        Livre livreEbook = new Livre("Ilona Andrews", "Tome 2", "2222222222222", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null, serieEbook);
        serieEbook.getLivres().add(livreEbook);

        when(serieRepository.trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture()).thenReturn(List.of(seriePapier, serieEbook));

        List<LivreAAcheterDTO> resultat = serieService.trouverListeCoursesPapier();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst().getNomSerie()).isEqualTo("Havrefer");
    }

    @Test
    @DisplayName("Doit retourner uniquement les livres ebook dans la liste de courses ebook")
    void trouverListeCoursesEbook_seriesCandidates_returnUniquementEbook() {
        Serie seriePapier = new Serie("Havrefer", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 6);
        Livre livrePapier = new Livre("Richard Ford", "Tome 2", "1111111111111", 2, StatutLivre.A_ACHETER, FormatLivre.PAPIER, null, null, seriePapier);
        seriePapier.getLivres().add(livrePapier);

        Serie serieEbook = new Serie("Kate Daniels", utilisateur, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 12);
        Livre livreEbook = new Livre("Ilona Andrews", "Tome 2", "2222222222222", 2, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null, serieEbook);
        serieEbook.getLivres().add(livreEbook);

        when(serieRepository.trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture()).thenReturn(List.of(seriePapier, serieEbook));

        List<LivreAAcheterDTO> resultat = serieService.trouverListeCoursesEbook();

        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst().getNomSerie()).isEqualTo("Kate Daniels");
    }

    @Test
    @DisplayName("Doit limiter la liste de courses papier à 20 résultats")
    void trouverListeCoursesPapier_plusDeVingtSeries_returnLimiteAVingt() {
        List<Serie> series = new java.util.ArrayList<>();
        for (int i = 1; i <= 21; i++) {
            Utilisateur utilisateurBoucle = new Utilisateur("Nom" + i, "Prenom" + i, "Pseudo" + i, "email" + i + "@email.fr");
            utilisateurBoucle.setMdp("Azerty123");
            Serie serieBoucle = new Serie("Serie " + i, utilisateurBoucle, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 5);
            Livre livreBoucle = new Livre("Auteur " + i, "Tome 1", "000000000000" + i, 1, StatutLivre.A_ACHETER, FormatLivre.PAPIER, null, null,
                    serieBoucle);
            serieBoucle.getLivres().add(livreBoucle);
            series.add(serieBoucle);
        }
        when(serieRepository.trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture()).thenReturn(series);

        List<LivreAAcheterDTO> resultat = serieService.trouverListeCoursesPapier();

        assertThat(resultat).hasSize(20);
    }

    @Test
    @DisplayName("Doit limiter la liste de courses ebook à 10 résultats")
    void trouverListeCoursesEbook_plusDeDixSeries_returnLimiteADix() {
        List<Serie> series = new java.util.ArrayList<>();
        for (int i = 1; i <= 11; i++) {
            Utilisateur utilisateurBoucle = new Utilisateur("Nom" + i, "Prenom" + i, "Pseudo" + i, "email" + i + "@email.fr");
            utilisateurBoucle.setMdp("Azerty123");
            Serie serieBoucle = new Serie("Serie " + i, utilisateurBoucle, StatutSerie.EN_COURS, StatutPublication.EN_COURS, 5);
            Livre livreBoucle = new Livre("Auteur " + i, "Tome 1", "111111111111" + i, 1, StatutLivre.A_ACHETER, FormatLivre.EBOOK, null, null,
                    serieBoucle);
            serieBoucle.getLivres().add(livreBoucle);
            series.add(serieBoucle);
        }
        when(serieRepository.trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture()).thenReturn(series);

        List<LivreAAcheterDTO> resultat = serieService.trouverListeCoursesEbook();

        assertThat(resultat).hasSize(10);
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucune série n'a de livre à acheter")
    void trouverListeCoursesPapier_aucuneSerie_returnListeVide() {
        when(serieRepository.trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture()).thenReturn(List.of());

        List<LivreAAcheterDTO> resultat = serieService.trouverListeCoursesPapier();

        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Doit retourner la série correspondant à l'id")
    void trouverSerieParId_serieExistante_returnsSerie(){
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));

        Serie resultat = serieService.trouverSerieParId(1);

        assertThat(resultat).isEqualTo(serie);
    }

    @Test
    @DisplayName("Doit lever une exception si la série n'existe pas")
    void trouverSerieParId_serieInexistante_leveBusinessException(){
        when(serieRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> serieService.trouverSerieParId(99))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Série non trouvée.");
    }

    @Test
    @DisplayName("Doit modifier le nom de la série quand rien ne change")
    void modifierNomSerie_memeNom_returnsSerieModifiee(){
        serie.setIdSerie(1);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.findByNom(serie.getNom())).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierNomSerie(1, serie.getNom());

        assertThat(resultat.getNom()).isEqualTo(serie.getNom());
        verify(serieRepository, times(1)).save(serie);
    }

    @Test
    @DisplayName("Doit modifier le nom de la série avec un nouveau nom disponible")
    void modifierNomSerie_nouveauNomDisponible_returnsSerieModifiee(){
        serie.setIdSerie(1);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.findByNom("Nouveau nom")).thenReturn(Optional.empty());
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierNomSerie(1, "Nouveau nom");

        assertThat(resultat.getNom()).isEqualTo("Nouveau nom");
    }

    @Test
    @DisplayName("Doit lever une exception si le nom appartient déjà à une autre série")
    void modifierNomSerie_nomPrisParAutreSerie_leveBusinessException(){
        serie.setIdSerie(1);
        Serie autreSerie = new Serie("Chasseuse de la nuit", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        autreSerie.setIdSerie(2);

        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.findByNom("Chasseuse de la nuit")).thenReturn(Optional.of(autreSerie));

        assertThatThrownBy(() -> serieService.modifierNomSerie(1, "Chasseuse de la nuit"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le nouveau titre de la série existe déjà.");
    }

    @Test
    @DisplayName("Doit calculer le temps de lecture d'une série via son id")
    void calculerTempsLectureSerie_serieExistante_returnsDifferenceEnJours(){
        Livre livre1 = new Livre("Tolkien", "Tome 1", "1111111111111", 1, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026, 1, 1), serie);
        Livre livre2 = new Livre("Tolkien", "Tome 2", "2222222222222", 2, StatutLivre.LU, FormatLivre.EBOOK, null, LocalDate.of(2026, 1, 11), serie);
        serie.getLivres().add(livre1);
        serie.getLivres().add(livre2);

        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));

        double resultat = serieService.calculerTempsLectureSerie(1);

        assertThat(resultat).isEqualTo(10.0);
    }
}