package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.enums.FormatLivre;
import fr.celine.suivideseries.enums.StatutLivre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LivreRepository extends JpaRepository<Livre, Integer> {

    Optional<Livre> findByIsbn(String isbn);

    Optional<Livre> findByNumeroDansLaSerieAndSerie(int numeroDansLaSerie, Serie serie);

    @Query("SELECT DISTINCT l.auteur FROM Livre l ORDER BY l.auteur ASC")
    List<String> trouverAuteurParOrdreAlphabetique();

    long countByStatutLivreAndFormatLivre(StatutLivre statutLivre, FormatLivre formatLivre);

    @Query("SELECT l.auteur FROM Livre l WHERE l.serie.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS GROUP BY l.auteur ORDER BY COUNT(DISTINCT l.serie) DESC, " +
            "l.auteur ASC" )
    List<String> trouverAuteursParNombreSerieEnCours(Pageable pageable);
}
