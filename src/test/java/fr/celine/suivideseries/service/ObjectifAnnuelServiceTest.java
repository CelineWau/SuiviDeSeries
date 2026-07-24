package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.ObjectifAnnuel;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.ObjectifAnnuelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ObjectifAnnuelServiceTest {

    @Mock
    private ObjectifAnnuelRepository objectifAnnuelRepository;

    @InjectMocks
    private ObjectifAnnuelService objectifAnnuelService;

    private Utilisateur utilisateur;
    private int anneeEnCours;

    @BeforeEach
    void setup(){
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        anneeEnCours = LocalDate.now().getYear();
    }

    @Test
    @DisplayName("Doit lever une exception si la valeur de l'objectif est inférieure à 1")
    void definirObjectifAnnuel_valeurInferieureAUn_leveBusinessException(){
        assertThatThrownBy(() -> objectifAnnuelService.definirObjectifAnnuel(utilisateur, 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La valeur de l'objectif doit être comprise entre 1 et 50 séries par an.");
    }

    @Test
    @DisplayName("Doit lever une exception si la valeur de l'objectif dépasse 50")
    void definirObjectifAnnuel_valeurSuperieureACinquante_leveBusinessException(){
        assertThatThrownBy(() -> objectifAnnuelService.definirObjectifAnnuel(utilisateur, 51))
                .isInstanceOf(BusinessException.class)
                .hasMessage("La valeur de l'objectif doit être comprise entre 1 et 50 séries par an.");
    }

    @Test
    @DisplayName("Doit lever une exception si l'utilisateur est nul")
    void definirObjectifAnnuel_utilisateurNull_leveBusinessException(){
        assertThatThrownBy(() -> objectifAnnuelService.definirObjectifAnnuel(null, 15))
                .isInstanceOf(BusinessException.class)
                .hasMessage("L'objectif annuel doit être associé à un utilisateur.");
    }

    @Test
    @DisplayName("Doit créer un nouvel objectif si aucun n'existe encore pour cette année")
    void definirObjectifAnnuel_aucunObjectifExistant_creeNouvelObjectif(){
        when(objectifAnnuelRepository.findByUtilisateurAndAnnee(utilisateur, anneeEnCours)).thenReturn(Optional.empty());
        when(objectifAnnuelRepository.save(any(ObjectifAnnuel.class))).thenReturn(new ObjectifAnnuel(anneeEnCours, 15, utilisateur));

        ObjectifAnnuel resultat = objectifAnnuelService.definirObjectifAnnuel(utilisateur, 15);

        assertThat(resultat.getValeurObjectif()).isEqualTo(15);
        assertThat(resultat.getAnnee()).isEqualTo(anneeEnCours);
        verify(objectifAnnuelRepository, times(1)).save(any(ObjectifAnnuel.class));
    }

    @Test
    @DisplayName("Doit modifier l'objectif existant plutôt que d'en créer un nouveau")
    void definirObjectifAnnuel_objectifExistant_modifieObjectifExistant(){
        ObjectifAnnuel objectifExistant = new ObjectifAnnuel(anneeEnCours, 10, utilisateur);
        when(objectifAnnuelRepository.findByUtilisateurAndAnnee(utilisateur, anneeEnCours)).thenReturn(Optional.of(objectifExistant));
        when(objectifAnnuelRepository.save(any(ObjectifAnnuel.class))).thenReturn(objectifExistant);

        ObjectifAnnuel resultat = objectifAnnuelService.definirObjectifAnnuel(utilisateur, 20);

        assertThat(resultat.getValeurObjectif()).isEqualTo(20);
        verify(objectifAnnuelRepository, times(1)).save(objectifExistant);
    }

    @Test
    @DisplayName("Doit retourner l'objectif de l'année en cours si présent")
    void recupererObjectifAnnuel_objectifPresent_returnObjectif(){
        ObjectifAnnuel objectif = new ObjectifAnnuel(anneeEnCours, 15, utilisateur);
        when(objectifAnnuelRepository.findByUtilisateurAndAnnee(utilisateur, anneeEnCours)).thenReturn(Optional.of(objectif));

        Optional<ObjectifAnnuel> resultat = objectifAnnuelService.recupererObjectifAnnuel(utilisateur);

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getValeurObjectif()).isEqualTo(15);
    }

    @Test
    @DisplayName("Doit retourner un Optional vide si aucun objectif n'existe pour l'année en cours")
    void recupererObjectifAnnuel_aucunObjectif_returnEmpty(){
        when(objectifAnnuelRepository.findByUtilisateurAndAnnee(utilisateur, anneeEnCours)).thenReturn(Optional.empty());

        Optional<ObjectifAnnuel> resultat = objectifAnnuelService.recupererObjectifAnnuel(utilisateur);

        assertThat(resultat).isEmpty();
    }
}
