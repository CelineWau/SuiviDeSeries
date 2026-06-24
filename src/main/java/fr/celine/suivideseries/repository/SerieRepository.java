package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository  extends JpaRepository<Serie, Integer> {
    Optional<Serie> findByNom(String nom);


    @Query("SELECT s FROM Serie s LEFT JOIN s.livres l GROUP BY s.idSerie HAVING s.nombreLivreTotal - SUM(CASE WHEN l.statutLivre = fr.celine.suivideseries.enums.StatutLivre.LU " +
            "THEN 1 ELSE 0 END) = ?1")
    List<Serie> trouverSeriesParNombreLivresManquants(int livreManquant);

}
