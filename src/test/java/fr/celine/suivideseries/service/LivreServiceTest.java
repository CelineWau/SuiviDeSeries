package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.StatutLivre;
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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LivreServiceTest {

    @Mock
    private LivreRepository livreRepository;

    @InjectMocks
    private LivreService livreService;

    private Utilisateur utilisateur;
    private Serie serie;
    private Livre livre;

    @BeforeEach
    void setup(){
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        utilisateur.setMdp("Azerty123");
        serie = new Serie("Le puits des mémoires", utilisateur, StatutSerie.EN_COURS, 3);
        livre = new Livre("Gabriel Katz", "La traque", "1234567891234", StatutLivre.LU, serie);
    }

    @Test
    @DisplayName("Doit lever une exception si l'auteur est nul")
    void creerLivre_auteurNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre(null, "Le fils de la lune", "9876543219876", StatutLivre.LU, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("L'auteur est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si le titre est nul")
    void creerLivre_titreNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", null, "9876543219876", StatutLivre.LU, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le titre est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si l'isbn est nul")
    void creerLivre_isbnNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", null, StatutLivre.LU, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("L'ISBN est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si le statut du livre est nul")
    void creerLivre_statutLivreNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", null, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le livre doit obligatoirement avoir un statut.");
    }

    @Test
    @DisplayName("Doit lever une exception si la série est nulle")
    void creerLivre_serieNull_leveBusinessException(){
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", StatutLivre.LU, null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le livre doit être associé à une série.");
    }

    @Test
    @DisplayName("Doit lever une exception si le livre ets déjà present un base de données")
    void creerLivre_dejapresentenBDD_leveBusinessException(){
        when(livreRepository.findByIsbn("9876543219876")).thenReturn(Optional.of(new Livre()));
        assertThatThrownBy(() -> livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", StatutLivre.LU, serie))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un livre existe déjà avec cet ISBN.");
    }

    @Test
    @DisplayName("Doit créer un nouveau livre")
    void creerLivre_donneesValid_returnsLivre() {
        when(livreRepository.save(any(Livre.class))).thenReturn(new Livre("Gabriel Katz", "Le fils de la lune", "9876543219876", StatutLivre.DANS_PAL, serie));

        Livre resultat = livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "9876543219876", StatutLivre.DANS_PAL, serie);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getAuteur()).isEqualTo("Gabriel Katz");
        assertThat(resultat.getTitre()).isEqualTo("Le fils de la lune");
        assertThat(resultat.getIsbn()).isEqualTo("9876543219876");
        assertThat(resultat.getStatutLivre()).isEqualTo(StatutLivre.DANS_PAL);
        assertThat(resultat.getSerie()).isEqualTo(serie);
        verify(livreRepository, times(1)).save(any(Livre.class));

    }
}
