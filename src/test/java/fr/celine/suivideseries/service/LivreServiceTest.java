package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutSerie;
import fr.celine.suivideseries.repository.LivreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
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
    @DisplayName("Doit créer un nouveau livre")
    void creerLivre_donneesValid_returnsLivre() {
        when(livreRepository.save(any(Livre.class))).thenReturn(new Livre("Gabriel Katz", "Le fils de la lune", "1234567891324", StatutLivre.DANS_PAL, serie));

        Livre resultat = livreService.creerLivre("Gabriel Katz", "Le fils de la lune", "1234567891324", StatutLivre.DANS_PAL, serie);

        assertThat(resultat).isNotNull();
        assertThat(resultat.getAuteur()).isEqualTo("Gabriel Katz");
        assertThat(resultat.getTitre()).isEqualTo("Le fils de la lune");
        assertThat(resultat.getIsbn()).isEqualTo("1234567891324");
        assertThat(resultat.getStatutLivre()).isEqualTo(StatutLivre.DANS_PAL);
        assertThat(resultat.getSerie()).isEqualTo(serie);
        verify(livreRepository, times(1)).save(any(Livre.class));

    }
}
