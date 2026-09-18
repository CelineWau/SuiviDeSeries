package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.Genre;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.GenreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    @Test
    @DisplayName("Doit lever une exception si le nom du genre est nul")
    void creerGenre_nomNull_leveBusinessException(){
        assertThatThrownBy(() -> genreService.creerGenre(null))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Le nom du genre est obligatoire.");
    }

    @Test
    @DisplayName("Doit lever une exception si le nom du genre est déjà présent dans la base de donnnées")
    void creerGenre_nomDejaPresentDansBDD_leveBusinessException(){
        when(genreRepository.findByNom("Fantasy")).thenReturn(Optional.of(new Genre()));
        assertThatThrownBy(() -> genreService.creerGenre("Fantasy"))
                .isInstanceOf(BusinessException.class)
                .hasMessage("Un genre existe déjà avec ce nom.");
    }

    @Test
    @DisplayName("Doit retourner un nouveau genre")
    void creerGenre_donneesValides_returnsGenre() {
        when(genreRepository.save(any(Genre.class))).thenReturn(new Genre("Fantasy"));

        Genre resultat = genreService.creerGenre("Fantasy");

        assertThat(resultat).isNotNull();
        assertThat(resultat.getNom()).isEqualTo("Fantasy");
        verify(genreRepository, times(1)).save(any(Genre.class));
    }
}