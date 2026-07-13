package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.enums.StatutSerie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository  extends JpaRepository<Serie, Integer> {
    Optional<Serie> findByNom(String nom);


    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l GROUP BY s.idSerie HAVING s.nombreLivreTotal - SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU " +
            "THEN 1 ELSE 0 END) = ?1")
    List<Serie> trouverSeriesParNombreLivresManquants(int livreManquant);

    @Query(value = "SELECT * FROM serie ORDER BY CASE statut_serie WHEN 1 THEN 1 WHEN 0 THEN 2 WHEN 2 THEN 3 END, nom ASC", nativeQuery = true)
    List<Serie> trierParStatut();
}
