package fr.celine.suivideseries.repository;

import fr.celine.suivideseries.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Integer> {

    Optional<Genre> findByNom(String nom);
}
