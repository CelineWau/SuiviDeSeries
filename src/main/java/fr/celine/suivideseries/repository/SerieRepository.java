package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.enums.StatutSerie;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SerieRepository  extends JpaRepository<Serie, Integer> {

    Optional<Serie> findByNom(String nom);

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l GROUP BY s.idSerie HAVING s.nombreLivreTotal - SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU " +
            "THEN 1 ELSE 0 END) = ?1")
    List<Serie> trouverSeriesParNombreLivresManquants(int livreManquant);

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie != fr.celine.suivideseries.enums.StatutSerie.ABANDONNEE " +
            "AND s.statutSerie != fr.celine.suivideseries.enums.StatutSerie.TERMINEE " +
            "GROUP BY s.idSerie HAVING s.nombreLivreTotal - SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU THEN 1 ELSE 0 END) <= ?1 " +
            "AND s.nombreLivreTotal - SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU THEN 1 ELSE 0 END) > 0 " +
            "AND SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.A_ACHETER THEN 1 ELSE 0 END) = 0")
    List<Serie> trouverSeriesPresqueFinieDansLaPal(int livreManquant);

    @Query(value = "SELECT * FROM serie ORDER BY CASE statut_serie WHEN 1 THEN 1 WHEN 0 THEN 2 WHEN 2 THEN 3 END, nom ASC", nativeQuery = true)
    List<Serie> trierParStatut();

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie != fr.celine.suivideseries.enums.StatutSerie.ABANDONNEE " +
            "GROUP BY s.idSerie HAVING SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.A_ACHETER THEN 1 ELSE 0 END) > 0 " +
            "ORDER BY SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.A_ACHETER THEN 1 ELSE 0 END) ASC")
    List<Serie> trouverSeriesAvecLivresAAcheter(Pageable pageable);

    long countByDateFinBetween(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS " +
            "AND s.statutPublication != fr.celine.suivideseries.enums.StatutPublication.TERMINEE GROUP BY s.idSerie " +
            "HAVING SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU THEN 1 ELSE 0 END) = COUNT(l)")
    List<Serie> trouverSeriesAJour();

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS GROUP BY s.idSerie HAVING MAX(l.dateLecture) < ?1")
    List<Serie> trouverSeriesDelaissees (LocalDate dateSeuil);

    long countByStatutSerie(StatutSerie statutSerie);
}
