package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
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
        serie = new Serie("Le Prieuré de l'oranger", utilisateur, StatutSerie.EN_COURS, 2);
    }

    @Test
    @DisplayName("Doit lever une exception si le nom est nul")
    void creerSerie_nomNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie(null, utilisateur, StatutSerie.EN_COURS,4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le nom de la série est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si l'utilisateur est nul")
    void creerSerie_utilisateurNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", null, StatutSerie.EN_COURS,4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un utilisateur doit être associé à une série.");
    }

    @Test
    @DisplayName("Doit lever une exception si le nombre total de livre est inférieur à zéro")
    void creerSerie_nombreLivreTotalInferieurAZero_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS,-8))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La série doit avoir un nombre de livre total supérieur à zéro.");
    }

    @Test
    @DisplayName("Doit lever une exception si le statut de la série est nul")
    void creerSerie_statutSerieNull_leveBusinessException() {
        assertThatThrownBy(() -> serieService.creerSerie("Twilight", utilisateur, null,4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La série doit obligatoirement avoir un statut.");
    }

    @Test
    @DisplayName("Doit lever une exception si le série existe déjà en base de données")
    void creerSerie_dejaPresentEnBDD_leveBusinessException(){
        when(serieRepository.findByNom("Twilight")).thenReturn(Optional.of(new Serie()));
        assertThatThrownBy(()-> serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, 4))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Une série existe déjà avec ce nom.");
    }

    @Test
    @DisplayName("Doit créer une nouvelle série")
    void creerSerie_donneesValides_returnsSerie() {

        when(serieRepository.save(any(Serie.class))).thenReturn(new Serie("Twilight", utilisateur, StatutSerie.EN_COURS, 4));

        Serie resultat = serieService.creerSerie("Twilight", utilisateur, StatutSerie.EN_COURS, 4);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Twilight");
        assertThat(resultat.getStatutSerie()).isEqualTo(StatutSerie.EN_COURS);
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
}
