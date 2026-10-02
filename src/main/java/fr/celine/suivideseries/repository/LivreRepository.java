package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.dto.AuteursSeriesEnCoursDTO;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
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

    @Query("SELECT DISTINCT l.auteur FROM Livre l WHERE l.serie.utilisateur = ?1 ORDER BY l.auteur ASC")
    List<String> trouverAuteurParOrdreAlphabetique(Utilisateur utilisateur);

    long countByStatutLivreAndFormatLivreAndSerieUtilisateur(StatutLivre statutLivre, FormatLivre formatLivre, Utilisateur utilisateur);

    @Query("SELECT new fr.celine.suivideseries.dto.AuteursSeriesEnCoursDTO(l.auteur, COUNT(DISTINCT l.serie)) FROM Livre l WHERE l.serie.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS " +
            "AND l.serie.utilisateur = ?1 GROUP BY l.auteur ORDER BY COUNT(DISTINCT l.serie) DESC, l.auteur ASC" )
    List<AuteursSeriesEnCoursDTO> trouverAuteursParNombreSerieEnCours(Utilisateur utilisateur, Pageable pageable);

    List<Livre> findByStatutLivreAndSerieUtilisateur(StatutLivre statutLivre, Utilisateur utilisateur);

    long countByNumeroDansLaSerieAndStatutLivreAndDateLectureBetweenAndSerieUtilisateur(int numeroDansLaSerie, StatutLivre statutLivre, LocalDate dateDebut, LocalDate dateFin, Utilisateur utilisateur);

    @Query("SELECT COUNT(l) FROM Livre l WHERE l.numeroDansLaSerie = 1 AND l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU AND l.dateLecture BETWEEN ?1 AND ?2 AND l.serie.dateFin " +
            "BETWEEN ?1 AND ?2 AND l.serie.utilisateur = ?3")
    long compterSeriesCommenceesEtFiniesMemeAnnee(LocalDate dateDebut, LocalDate dateFin, Utilisateur utilisateur);
}
