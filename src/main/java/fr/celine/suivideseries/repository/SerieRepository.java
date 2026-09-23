package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.enums.NatureSerie;
import fr.celine.suivideseries.enums.StatutPublication;
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

    @Query("SELECT DISTINCT s FROM Serie s LEFT JOIN FETCH s.livres LEFT JOIN FETCH s.genre ORDER BY CASE s.statutSerie " +
            "WHEN fr.celine.suivideseries.enums.StatutSerie.EN_COURS THEN 1 " +
            "WHEN fr.celine.suivideseries.enums.StatutSerie.ABANDONNEE THEN 2 " +
            "WHEN fr.celine.suivideseries.enums.StatutSerie.TERMINEE THEN 3 END, s.nom ASC")
    List<Serie> trierParStatut();

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie != fr.celine.suivideseries.enums.StatutSerie.ABANDONNEE " +
            "GROUP BY s.idSerie HAVING SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.A_ACHETER THEN 1 ELSE 0 END) > 0 " +
            "ORDER BY SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.A_ACHETER THEN 1 ELSE 0 END) ASC")
    List<Serie> trouverSeriesAvecLivresAAcheter(Pageable pageable);

    long countByDateFinBetween(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT s.idSerie FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS " +
            "AND s.statutPublication != fr.celine.suivideseries.enums.StatutPublication.TERMINEE GROUP BY s.idSerie " +
            "HAVING MAX(s.nombreLivreTotal) - SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU THEN 1 ELSE 0 END) = 0")
    List<Integer> trouverIdsSeriesAJour();

    @Query("SELECT DISTINCT s FROM Serie s LEFT JOIN FETCH s.livres LEFT JOIN FETCH s.genre WHERE s.idSerie IN :ids")
    List<Serie> trouverSeriesAvecDetailsParIds(List<Integer> ids);

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS GROUP BY s.idSerie HAVING MAX(l.dateLecture) < ?1 " +
            "AND SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU THEN 1 ELSE 0 END) != COUNT(l) ORDER BY MAX(l.dateLecture) ASC")
    List<Serie> trouverSeriesDelaissees (LocalDate dateSeuil, Pageable pageable);

    long countByStatutSerie(StatutSerie statutSerie);

    Optional<Serie> findFirstByStatutSerieOrderByNombreLivreTotalDesc(StatutSerie statutSerie);

    List<Serie> findByStatutSerieNot(StatutSerie statutSerie);

    List<Serie> findByStatutSerie(StatutSerie statutSerie);

    @Query("SELECT s FROM Serie s WHERE s.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS AND EXISTS (SELECT l FROM Livre l WHERE l.serie = s " +
            "AND l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.DANS_PAL AND l.formatLivre = fr.celine.suivideseries.enums.FormatLivre.EBOOK)")
    List<Serie> trouverSeriesAvecEbooksDansLaPal();

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS " +
            "AND s.statutPublication = fr.celine.suivideseries.enums.StatutPublication.EN_COURS GROUP BY s.idSerie " +
            "HAVING SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU THEN 1 ELSE 0 END) != COUNT(l)")
    List<Serie> trouverSerieASurveiller();

    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l WHERE s.statutSerie = fr.celine.suivideseries.enums.StatutSerie.EN_COURS AND EXISTS (SELECT l2 FROM Livre l2 WHERE l2.serie = s AND " +
            "l2.statutLivre = fr.celine.suivideseries.enums.StatutLivre.A_ACHETER) GROUP BY s.idSerie HAVING MAX(l.dateLecture) IS NOT NULL ORDER BY MAX(l.dateLecture) ASC, " +
            "SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.A_ACHETER THEN 1 ELSE 0 END) ASC")
    List<Serie> trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture();

    @Query("SELECT s FROM Serie s WHERE EXISTS (SELECT l FROM Livre l WHERE l.serie = s AND l.numeroDansLaSerie = 1 AND l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU AND l.dateLecture " +
            "BETWEEN ?1 AND ?2)")
    List<Serie> trouverSeriesAvecTome1LuDansAnnee(LocalDate dateDebut, LocalDate dateFin);

    @Query("SELECT DISTINCT s FROM Serie s LEFT JOIN FETCH s.livres LEFT JOIN FETCH s.genre " +
            "WHERE NOT EXISTS (SELECT l FROM Livre l WHERE l.serie = s AND l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU) AND s.statutSerie = " +
            "fr.celine.suivideseries.enums.StatutSerie.EN_COURS")
    List<Serie> trouverSeriesJamaisCommencees();

    List<Serie> findByLireEnAnglaisAndStatutSerie(boolean lireEnAnglais, StatutSerie statutSerie);
}
