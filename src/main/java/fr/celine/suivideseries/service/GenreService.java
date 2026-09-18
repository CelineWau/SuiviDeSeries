package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.Genre;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.GenreRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreService {

    final private GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    // Créer un genre
    public Genre creerGenre(String nom) {
        // Validation métier
        if(nom == null || nom.isBlank()) {
            throw new BusinessException("Le nom du genre est obligatoire.");
        }

        if(genreRepository.findByNom(nom).isPresent()){
            throw new BusinessException("Un genre existe déjà avec ce nom.");
        }

        Genre genre = new Genre(nom);
        return genreRepository.save(genre);
    }

    // Afficher les genres
    public List<Genre> trouverGenres() {
        return genreRepository.findAll();
    }

    // Trouver un genre par son ID
    public Genre trouverGenreParId(int id) {
        return genreRepository.findById(id).orElseThrow(() -> new BusinessException("Genre non trouvé."));
    }
}
