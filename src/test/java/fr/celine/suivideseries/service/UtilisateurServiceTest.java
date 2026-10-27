package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.ModificationEmailDTO;
import fr.celine.suivideseries.dto.ProfilDTO;
import fr.celine.suivideseries.dto.RegisterRequestDTO;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.UtilisateurRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UtilisateurServiceTest {

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UtilisateurService utilisateurService;

    private Utilisateur utilisateur;

    @BeforeEach
    void setup() {
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        utilisateur.setIdUser(1);
        utilisateur.setMdp("Azerty123");
    }

    @Test
    @DisplayName("Doit modifier l'email quand il n'est pas utilisé par un autre compte")
    void modifierEmail_emailLibre_modifieEtRetourneProfil() {
        when(utilisateurRepository.findByEmailIgnoreCase("nouveau@email.fr")).thenReturn(Optional.empty());

        ProfilDTO resultat = utilisateurService.modifierEmail(utilisateur, new ModificationEmailDTO("nouveau@email.fr"));

        assertThat(resultat.getEmail()).isEqualTo("nouveau@email.fr");
        assertThat(utilisateur.getEmail()).isEqualTo("nouveau@email.fr");
        verify(utilisateurRepository, times(1)).save(utilisateur);
    }

    @Test
    @DisplayName("Doit lever une exception si l'email appartient à un autre compte")
    void modifierEmail_emailDejaPrisParUnAutre_leveBusinessException() {
        Utilisateur autreUtilisateur = new Utilisateur("Rowling", "Joanne", "JoJo", "jo@email.fr");
        autreUtilisateur.setIdUser(2);
        when(utilisateurRepository.findByEmailIgnoreCase("jo@email.fr")).thenReturn(Optional.of(autreUtilisateur));

        assertThatThrownBy(() -> utilisateurService.modifierEmail(utilisateur, new ModificationEmailDTO("jo@email.fr")))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cet email est déjà utilisé.");

        assertThat(utilisateur.getEmail()).isEqualTo("monemail@email.fr");
        verify(utilisateurRepository, never()).save(any());
    }

    @Test
    @DisplayName("Ne doit pas lever d'exception si l'utilisateur renvoie son propre email")
    void modifierEmail_propreEmail_pasDErreur() {
        when(utilisateurRepository.findByEmailIgnoreCase("monemail@email.fr")).thenReturn(Optional.of(utilisateur));

        ProfilDTO resultat = utilisateurService.modifierEmail(utilisateur, new ModificationEmailDTO("monemail@email.fr"));

        assertThat(resultat.getEmail()).isEqualTo("monemail@email.fr");
        verify(utilisateurRepository, times(1)).save(utilisateur);
    }

    @Test
    @DisplayName("Doit lever une exception à l'inscription si l'email est déjà utilisé")
    void inscrireUtilisateur_emailDejaUtilise_leveBusinessException() {
        RegisterRequestDTO requete = new RegisterRequestDTO("Rowling", "Joanne", "JoJo", "monemail@email.fr", "Azerty123");
        when(utilisateurRepository.findByEmailIgnoreCase("monemail@email.fr")).thenReturn(Optional.of(utilisateur));

        assertThatThrownBy(() -> utilisateurService.inscrireUtilisateur(requete))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Cet email est déjà utilisé.");

        verify(utilisateurRepository, never()).save(any());
    }
}
