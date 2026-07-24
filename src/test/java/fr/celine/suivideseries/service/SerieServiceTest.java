package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.SerieAvecLivresAAcheterDTO;
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
        assertThatThrownBy(() -> serieService.creerSerie(null, utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE,4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le nom de la série est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si l'utilisateur est nul")
    void creerSerie_utilisateurNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", null, StatutSerie.EN_COURS, StatutPublication.TERMINEE,4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un utilisateur doit être associé à une série.");
    }

    @Test
    @DisplayName("Doit lever une exception si le nombre total de livre est inférieur à zéro")
    void creerSerie_nombreLivreTotalInferieurAZero_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE,-8))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La série doit avoir un nombre de livre total supérieur à zéro.");
    }

    @Test
    @DisplayName("Doit lever une exception si le statut de la série est nul")
    void creerSerie_statutSerieNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, null, StatutPublication.TERMINEE,4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La série doit obligatoirement avoir un statut.");
    }

    @Test
    @DisplayName("Doit lever une exception si le statut de publication de la série est nul")
    void creerSerie_statutPublicationNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, null,4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La série doit obligatoirement avoir un statut de publication.");
    }

    @Test
    @DisplayName("Doit lever une exception si le série existe déjà en base de données")
    void creerSerie_dejaPresentEnBDD_leveBusinessException(){
        when(serieRepository.findByNom("Twilight")).thenReturn(Optional.of(new Serie()));
        assertThatThrownBy(()-> serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE,4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Une série existe déjà avec ce nom.");
    }

    @Test
    @DisplayName("Doit créer une nouvelle série")
    void creerSerie_donneesValides_returnsSerie() {

        when(serieRepository.save(any(Serie.class))).thenReturn(new Serie("Twilight", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE,4));

        Serie resultat = serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE,4);

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
    void trouverSerieAvecNombreLivresManquants_livresManquantsInferieurZero_leveBusinessException(){
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
    void trouverSeriesPresqueFiniesDansLaPal_livreManquantInferieurOuEgalZero_leveBusinessException(){
        assertThatThrownBy(() -> serieService.trouverSeriesPresqueFiniesDansLaPal(0))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le nombre de livre manquant ne peut pas être négatif ou égal à zéro.");
    }

    @Test
    @DisplayName("Doit retourner les séries presque finies dans la PAL")
    void trouverSeriesPresqueFiniesDansLaPal_donneesValides_returnsSeries(){
        when(serieRepository.trouverSeriesPresqueFinieDansLaPal(2)).thenReturn(List.of(serie));

        List<Serie> resultat = serieService.trouverSeriesPresqueFiniesDansLaPal(2);

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        verify(serieRepository, times(1)).trouverSeriesPresqueFinieDansLaPal(2);
    }

    @Test
    @DisplayName("Doit modifier le nombre de livres total et repasser la série en EN_COURS si elle n'est pas abandonnée")
    void modifierNombreLivreTotal_serieNonAbandonnee_repasseEnCours(){
        serie.setStatutSerie(StatutSerie.TERMINEE);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierNombreLivreTotal(1, 5);

        assertThat(resultat.getNombreLivreTotal()).isEqualTo(5);
        assertThat(resultat.getStatutSerie()).isEqualTo(StatutSerie.EN_COURS);
    }

    @Test
    @DisplayName("Ne doit pas repasser une série abandonnée en EN_COURS lors de la modification du nombre de livres")
    void modifierNombreLivreTotal_serieAbandonnee_resteAbandonnee(){
        serie.setStatutSerie(StatutSerie.ABANDONNEE);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierNombreLivreTotal(1, 5);

        assertThat(resultat.getNombreLivreTotal()).isEqualTo(5);
        assertThat(resultat.getStatutSerie()).isEqualTo(StatutSerie.ABANDONNEE);
    }

    @Test
    @DisplayName("Doit convertir les séries en DTO avec leur nombre de livres à acheter")
    void trouverSeriesAvecLivresAAcheter_returnListeDeDTO(){
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
    void modifierStatutSerie_transitionEnCoursVersTerminee_remplitDateFin(){
        serie.setStatutSerie(StatutSerie.EN_COURS);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierStatutSerie(1, StatutSerie.TERMINEE);

        assertThat(resultat.getDateFin()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Ne doit pas remplir la date de fin si la transition n'est pas EN_COURS vers TERMINEE")
    void modifierStatutSerie_transitionAutre_neRemplitPasDateFin(){
        serie.setStatutSerie(StatutSerie.EN_COURS);
        when(serieRepository.findById(1)).thenReturn(Optional.of(serie));
        when(serieRepository.save(any(Serie.class))).thenReturn(serie);

        Serie resultat = serieService.modifierStatutSerie(1, StatutSerie.ABANDONNEE);

        assertThat(resultat.getDateFin()).isNull();
    }
}
