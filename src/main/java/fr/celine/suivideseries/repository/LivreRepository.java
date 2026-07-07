package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LivreRepository extends JpaRepository<Livre, Integer> {

    Optional<Livre> findByIsbn(String isbn);

    Optional<Livre> findByNumeroDansLaSerieAndSerie(int numeroDansLaSerie, Serie serie);
}
