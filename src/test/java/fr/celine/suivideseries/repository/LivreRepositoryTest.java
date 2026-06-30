package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutSerie;
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
public class LivreRepositoryTest {

    @Autowired
    private LivreRepository livreRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Utilisateur utilisateur;
    private Serie serie;
    private Livre livre;

    @BeforeEach
    void setup(){
        utilisateur = new Utilisateur("Waucheul", "Céline", "Kitsune", "monemail@email.fr");
        utilisateur.setMdp("Azerty123");
        serie = new Serie("Le puits des mémoires", utilisateur, StatutSerie.EN_COURS, 3);
        livre = new Livre("Gabriel Katz", "La traque", "1234567891234", StatutLivre.LU, serie);

        entityManager.persist(utilisateur);
        entityManager.persist(serie);
        entityManager.persist(livre);
        entityManager.flush();
    }

    @Test
    @DisplayName("Doit retourner le livre par un isbn donné")
    void findByIsbn_ReturnLivre(){
        Optional<Livre> resultat = livreRepository.findByIsbn("1234567891234");


        assertThat(resultat).isPresent();
        assertThat(resultat.get().getIsbn()).isEqualTo("1234567891234");
    }


}
