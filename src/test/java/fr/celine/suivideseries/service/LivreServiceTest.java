package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.AuteursSeriesEnCoursDTO;
import fr.celine.suivideseries.dto.RepartitionFormatDTO;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.FormatLivre;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutPublication;
import fr.celine.suivideseries.enums.StatutSerie;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.LivreRepository;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LivreServiceTest {

    @Mock
    private LivreRepository livreRepository;

    @Mock
    private SerieService serieService;

    @InjectMocks
    private LivreService livreService;

    private Utilisateur utilisateur;
    private Serie serie;
    private Livre livre;

    @BeforeEach
    void setup(){
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        utilisateur.setMdp("Azerty123");
        serie = new Serie("Le puits des mémoires", utilisateur, StatutSerie.EN_COURS, StatutPublication.TERMINEE, 3);
        livre = new Livre("Gabriel Katz", "La traque", "1234567891234", 1, StatutLivre.LU, FormatLivre.PAPIER, null, null, serie);
    }

    @Test
    @DisplayName("Doit lever une exception si l'auteur est nul")
    void creerLivre_auteurNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre(null, "Le fils de la lune", "9876543219876", 2, StatutLivre.LU,  FormatLivre.PAPIER, null,
                null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("L'auteur est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si le titre est nul")
    void creerLivre_titreNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", null, "9876543219876", 2, StatutLivre.LU,  FormatLivre.PAPIER, null,
                null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le titre est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si l'isbn est nul")
    void creerLivre_isbnNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", null, 2, StatutLivre.LU, FormatLivre.PAPIER, null,
                null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("L'ISBN est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si le numéro dans la série est égal à 0")
    void creerLivre_numeroDansLaSerieEgalZero_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", 0, StatutLivre.LU, FormatLivre.PAPIER,
                null, null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le numero dans la série n'est pas valide.");
    }

    @Test
    @DisplayName("Doit lever une exception si le statut du livre est nul")
    void creerLivre_statutLivreNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, null, FormatLivre.PAPIER,
                null, null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le livre doit obligatoirement avoir un statut.");
    }

    @Test
    @DisplayName("Doit lever une exception si le format du livre est nul")
    void creerLivre_formatLivreNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.LU, null,
                null, null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le format du livre est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si la série est nulle")
    void creerLivre_serieNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.LU, FormatLivre.PAPIER,
                null, null,  null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le livre doit être associé à une série.");
    }

    @Test
    @DisplayName("Doit lever une exception si le livre est déjà present un base de données")
    void creerLivre_dejapresentenBDD_leveBusinessException(){
        when(livreRepository.findByIsbn("9876543219876")).thenReturn(Optional.of(new Livre()));
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.LU, FormatLivre.PAPIER,
                null, null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un livre existe déjà avec cet ISBN.");
    }

    @Test
    @DisplayName("Doit lever une exception si un livre avec ce numéro existe déjà dans cette série.")
    void creerLivre_aDejaCeNumeroDansLaSerie_leveBusinessException(){
        when(livreRepository.findByNumeroDansLaSerieAndSerie(2, serie)).thenReturn(Optional.of(new Livre()));
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.LU, FormatLivre.PAPIER,
                null, null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un livre avec ce numéro existe déjà dans cette série.");
    }

    @Test
    @DisplayName("Doit créer un nouveau livre")
    void creerLivre_donneesValid_returnsLivre() {
        when(livreRepository.save(any(Livre.class))).thenReturn(new Livre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.DANS_PAL,
                FormatLivre.PAPIER, null, null, serie));

        Livre resultat = livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.DANS_PAL, FormatLivre.PAPIER,
                null, null, serie);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getAuteur()).isEqualTo("Gabriel Katz");
        assertThat(resultat.getTitre()).isEqualTo("Le fils de la lune");
        assertThat(resultat.getIsbn()).isEqualTo("9876543219876");
        assertThat(resultat.getNumeroDansLaSerie()).isEqualTo(2);
        assertThat(resultat.getStatutLivre()).isEqualTo(StatutLivre.DANS_PAL);
        assertThat(resultat.getFormatLivre()).isEqualTo(FormatLivre.PAPIER);
        assertThat(resultat.getSerie()).isEqualTo(serie);
        verify(livreRepository, times(1)).save(any(Livre.class));
    }
    @Test
    @DisplayName("Doit retourner la liste des auteurs")
    void trouverAuteurs_returnListeAuteurs(){
        when(livreRepository.trouverAuteurParOrdreAlphabetique()).thenReturn(List.of("Alison Germain", "Gabriel Katz"));

        List<String> resultat = livreService.trouverAuteurs();

        assertThat(resultat).isNotNull();
        assertThat(resultat).containsExactly("Alison Germain", "Gabriel Katz");
        verify(livreRepository, times(1)).trouverAuteurParOrdreAlphabetique();
    }

    @Test
    @DisplayName("Doit lever une exception si le livre à modifier n'existe pas")
    void modifierStatutLivre_livreNonTrouve_leveBusinessException(){
        when(livreRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livreService.modifierStatutLivre(99, StatutLivre.LU))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Livre non trouvé.");
    }

    @Test
    @DisplayName("Doit modifier le statut du livre sans basculer la série en terminée si des livres restent non lus")
    void modifierStatutLivre_livresRestants_neBasculePasSerieEnTerminee(){
        Livre livre2 = new Livre("Gabriel Katz", "Le fils de la lune", "9876543219876", 2, StatutLivre.DANS_PAL, FormatLivre.PAPIER, null,
                null,serie);
        livre.setStatutLivre(StatutLivre.DANS_PAL);
        serie.getLivres().add(livre);
        serie.getLivres().add(livre2);

        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);

        Livre resultat = livreService.modifierStatutLivre(1, StatutLivre.LU);

        assertThat(resultat.getStatutLivre()).isEqualTo(StatutLivre.LU);
        verify(serieService, never()).modifierStatutSerie(anyInt(), any(StatutSerie.class));
    }

    @Test
    @DisplayName("Doit basculer la série en TERMINEE quand tous les livres sont LU et le compte est complet")
    void modifierStatutLivre_tousLesLivresLus_basculeSerieEnTerminee(){
        Livre livre2 = new Livre("Gabriel Katz", "La traque 2", "9876543219877", 2, StatutLivre.LU, FormatLivre.EBOOK, null, null, serie);
        Livre livre3 = new Livre("Gabriel Katz", "La traque 3", "9876543219878", 3, StatutLivre.DANS_PAL, FormatLivre.EBOOK, null, null, serie);
        serie.getLivres().add(livre);
        serie.getLivres().add(livre2);
        serie.getLivres().add(livre3);

        when(livreRepository.findById(3)).thenReturn(Optional.of(livre3));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre3);

        livreService.modifierStatutLivre(3, StatutLivre.LU);

        verify(serieService, times(1)).modifierStatutSerie(serie.getIdSerie(), StatutSerie.TERMINEE);
    }

    @Test
    @DisplayName("Doit lever une exception si le livre à modifier le format n'existe pas")
    void modifierFormatLivre_livreNonTrouve_leveBusinessException(){
        when(livreRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livreService.modifierFormatLivre(99, FormatLivre.EBOOK))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Livre non trouvé.");
    }

    @Test
    @DisplayName("Doit modifier le format du livre")
    void modifierFormatLivre_donneesValides_returnsLivreAvecNouveauFormat(){
        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);

        Livre resultat = livreService.modifierFormatLivre(1, FormatLivre.EBOOK);

        assertThat(resultat.getFormatLivre()).isEqualTo(FormatLivre.EBOOK);
        verify(livreRepository, times(1)).save(livre);
    }

    @Test
    @DisplayName("Doit calculer la répartition ebook/papier pour les livres LU et DANS_PAL")
    void calculerRepartionFormatDansPalEtLu_returnsRepartitionCorrecte(){
        when(livreRepository.countByStatutLivreAndFormatLivre(StatutLivre.LU, FormatLivre.EBOOK)).thenReturn(10L);
        when(livreRepository.countByStatutLivreAndFormatLivre(StatutLivre.LU, FormatLivre.PAPIER)).thenReturn(5L);
        when(livreRepository.countByStatutLivreAndFormatLivre(StatutLivre.DANS_PAL, FormatLivre.EBOOK)).thenReturn(3L);
        when(livreRepository.countByStatutLivreAndFormatLivre(StatutLivre.DANS_PAL, FormatLivre.PAPIER)).thenReturn(2L);

        RepartitionFormatDTO resultat = livreService.calculerRepartitionFormatDansPalEtLu();

        assertThat(resultat.getLuEbook()).isEqualTo(10L);
        assertThat(resultat.getLuPapier()).isEqualTo(5L);
        assertThat(resultat.getPalEbook()).isEqualTo(3L);
        assertThat(resultat.getPalPapier()).isEqualTo(2L);
    }

    @Test
    @DisplayName("Doit remplir la date d'acquisition automatiquement lors du passage de A_ACHETER à DANS_PAL")
    void modifierStatutLivre_transitionAAcheterVersDansPal_remplitDateAcquisition(){
        livre.setStatutLivre(StatutLivre.A_ACHETER);
        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);

        Livre resultat = livreService.modifierStatutLivre(1, StatutLivre.DANS_PAL);

        assertThat(resultat.getDateAcquisition()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Ne doit pas remplir la date d'acquisition si la transition n'est pas A_ACHETER vers DANS_PAL")
    void modifierStatutLivre_transitionAutre_neRemplitPasDateAcquisition(){
        livre.setStatutLivre(StatutLivre.DANS_PAL);
        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);

        Livre resultat = livreService.modifierStatutLivre(1, StatutLivre.LU);

        assertThat(resultat.getDateAcquisition()).isNull();
    }

    @Test
    @DisplayName("Doit remplir la date de lecture automatiquement lors du passage de DANS_PAL à LU")
    void modifierStatutLivre_transitionDansPalVersLu_remplitDateLecture(){
        livre.setStatutLivre(StatutLivre.DANS_PAL);
        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);

        Livre resultat = livreService.modifierStatutLivre(1, StatutLivre.LU);

        assertThat(resultat.getDateLecture()).isEqualTo(LocalDate.now());
    }

    @Test
    @DisplayName("Ne doit pas remplir la date de lecture si la transition n'est pas DANS_PAL vers LU")
    void modifierStatutLivre_transitionAutre_neRemplitPasDateLecture(){
        livre.setStatutLivre(StatutLivre.A_ACHETER);
        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);

        Livre resultat = livreService.modifierStatutLivre(1, StatutLivre.DANS_PAL);

        assertThat(resultat.getDateLecture()).isNull();
    }

    @Test
    @DisplayName("Doit retourner les auteurs avec le plus de séries en cours")
    void trouverAuteursAvecSerieEnCours_donneesValides_returnsListeDeDTO(){
        AuteursSeriesEnCoursDTO dto = new AuteursSeriesEnCoursDTO("Tolkien", 3);
        when(livreRepository.trouverAuteursParNombreSerieEnCours(any(Pageable.class))).thenReturn(List.of(dto));

        List<AuteursSeriesEnCoursDTO> resultat = livreService.trouverAuteursAvecSerieEnCours();

        assertThat(resultat).isNotNull();
        assertThat(resultat).hasSize(1);
        assertThat(resultat.getFirst().getAuteur()).isEqualTo("Tolkien");
        assertThat(resultat.getFirst().getNombreSeries()).isEqualTo(3);
        verify(livreRepository, times(1)).trouverAuteursParNombreSerieEnCours(any(Pageable.class));
    }

    @Test
    @DisplayName("Doit retourner une liste vide si aucun auteur n'a de série en cours")
    void trouverAuteursAvecSerieEnCours_aucunAuteur_returnsListeVide(){
        when(livreRepository.trouverAuteursParNombreSerieEnCours(any(Pageable.class))).thenReturn(List.of());

        List<AuteursSeriesEnCoursDTO> resultat = livreService.trouverAuteursAvecSerieEnCours();

        assertThat(resultat).isNotNull();
        assertThat(resultat).isEmpty();
    }

    @Test
    @DisplayName("Doit lever une exception si le livre à modifier n'existe pas")
    void modifierLivre_livreNonTrouve_leveBusinessException(){
        when(livreRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> livreService.modifierLivre(99, "Nouveau titre", "Nouvel auteur", "1234567891234", 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Livre non trouvé.");
    }

    @Test
    @DisplayName("Doit modifier le livre quand rien ne change (même ISBN, même numéro)")
    void modifierLivre_memesValeurs_returnsLivreModifie(){
        livre.setIdLivre(1);
        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.findByIsbn("1234567891234")).thenReturn(Optional.of(livre));
        when(livreRepository.findByNumeroDansLaSerieAndSerie(1, serie)).thenReturn(Optional.of(livre));
        when(livreRepository.save(any(Livre.class))).thenReturn(livre);

        Livre resultat = livreService.modifierLivre(1, "Nouveau titre", "Nouvel auteur", "1234567891234", 1);

        assertThat(resultat.getTitre()).isEqualTo("Nouveau titre");
        assertThat(resultat.getAuteur()).isEqualTo("Nouvel auteur");
        verify(livreRepository, times(1)).save(livre);
    }

    @Test
    @DisplayName("Doit lever une exception si l'ISBN appartient déjà à un autre livre")
    void modifierLivre_isbnPrisParAutreLivre_leveBusinessException(){
        Livre autreLivre = new Livre("Autre auteur", "Autre titre", "9999999999999", 2, StatutLivre.LU, FormatLivre.PAPIER, null, null, serie);

        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.findByIsbn("9999999999999")).thenReturn(Optional.of(autreLivre));

        assertThatThrownBy(() -> livreService.modifierLivre(1, "Nouveau titre", "Nouvel auteur", "9999999999999", 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un livre existe déjà avec cet ISBN.");
    }

    @Test
    @DisplayName("Doit lever une exception si le numéro appartient déjà à un autre livre de la série")
    void modifierLivre_numeroPrisParAutreLivre_leveBusinessException(){
        livre.setIdLivre(1);
        Livre autreLivre = new Livre("Autre auteur", "Autre titre", "9999999999999", 2, StatutLivre.LU, FormatLivre.PAPIER, null, null, serie);

        when(livreRepository.findById(1)).thenReturn(Optional.of(livre));
        when(livreRepository.findByIsbn("1234567891234")).thenReturn(Optional.of(livre));
        when(livreRepository.findByNumeroDansLaSerieAndSerie(2, serie)).thenReturn(Optional.of(autreLivre));

        assertThatThrownBy(() -> livreService.modifierLivre(1, "Nouveau titre", "Nouvel auteur", "1234567891234", 2))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un livre avec ce numéro existe déjà dans cette série.");
    }

    @Test
    @DisplayName("Doit supprimer un livre par son id")
    void supprimerLivre_appelleDeleteById(){
        livreService.supprimerLivre(1);

        verify(livreRepository, times(1)).deleteById(1);
    }
}
