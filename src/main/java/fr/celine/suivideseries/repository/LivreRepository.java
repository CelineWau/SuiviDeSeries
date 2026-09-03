package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.dto.AuteursSeriesEnCoursDTO;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.enums.FormatLivre;
import fr.celine.suivideseries.enums.StatutLivre;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LivreRepository extends JpaRepository<Livre, Integer> {

    Optional<Livre> findByIsbn(String isbn);

    Optional<Livre> findByNumeroDansLaSerieAndSerie(int numeroDansLaSerie, Serie serie);

    @Query("SELECT DISTINCT l.auteur FROM Livre l ORDER BY l.auteur ASC")
    List<String> trouverAuteurParOrdreAlphabetique();

    long countByStatutLivreAndFormatLivre(StatutLivre statutLivre, FormatLivre formatLivre);

    @Query("SELECT new fr.celine.suivideseries.dto.AuteursSeriesEnCoursDTO(l.auteur, COUNT(DISTINCT l.serie)) FROM Livre l WHERE l.serie.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS " +
            "GROUP BY l.auteur ORDER BY COUNT(DISTINCT l.serie) DESC, l.auteur ASC" )
    List<AuteursSeriesEnCoursDTO> trouverAuteursParNombreSerieEnCours(Pageable pageable);

    List<Livre> findByStatutLivre(StatutLivre statutLivre);

    long countByNumeroDansLaSerieAndStatutLivreAndDateLectureBetween(int numeroDansLaSerie, StatutLivre statutLivre, LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT COUNT(l) FROM Livre l WHERE l.numeroDansLaSerie = 1 AND l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU AND l.dateLecture BETWEEN ?1 AND ?2 AND l.serie.dateFin " +
            "BETWEEN ?1 AND ?2")
    long compterSeriesCommenceesEtFiniesMemeAnnee(LocalDate dateDebut, LocalDate dateFin);
}
