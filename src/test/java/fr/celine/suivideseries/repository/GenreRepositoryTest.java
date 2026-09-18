package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Genre;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@DataJpaTest
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Genre genre;

    @BeforeEach
    void setup() {
        genre= new Genre("Fantasy");

        entityManager.persist(genre);
        entityManager.flush();
    }

    @Test
    @DisplayName("Doit retrouve le genre par le nom donné")
    void findByNom_returnGenre() {
        Optional<Genre> resultat = genreRepository.findByNom("Fantasy");

        assertThat(resultat).isPresent();
        assertThat(resultat.get().getNom()).isEqualTo("Fantasy");
    }
}
