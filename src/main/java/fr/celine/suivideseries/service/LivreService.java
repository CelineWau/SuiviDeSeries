package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.LivreRepository;
import org.springframework.stereotype.Service;

@Service
public class LivreService {

    private final LivreRepository livreRepository;

    public LivreService(LivreRepository livreRepository) {
        this.livreRepository = livreRepository;
    }

    // Ajouter un livre
    public Livre creerLivre (String auteur, String titre, String isbn, int numeroDansLaSerie, StatutLivre statutLivre, Serie serie) {

        // Validation métier
        if(auteur == null || auteur.isBlank()) {
            throw new BusinessException("L'auteur est obligatoire.");
        }

        if(titre == null || titre.isBlank()) {
            throw new BusinessException("Le titre est obligatoire.");
        }

        if(isbn == null ||isbn.isBlank()) {
            throw new BusinessException("L'ISBN est obligatoire.");
        }

        if(numeroDansLaSerie <= 0) {
            throw new BusinessException("Le numero dans la série n'est pas valide.");
        }

        if(statutLivre == null) {
            throw new BusinessException("Le livre doit obligatoirement avoir un statut.");
        }

        if(serie == null) {
            throw new BusinessException("Le livre doit être associé à une série.");
        }

        if(livreRepository.findByIsbn(isbn).isPresent()){
            throw new BusinessException("Un livre existe déjà avec cet ISBN.");
        }

        if(livreRepository.findByNumeroDansLaSerieAndSerie(numeroDansLaSerie, serie).isPresent()){
            throw new BusinessException("Un livre avec ce numéro existe déjà dans cette série.");
        }

        Livre livre = new Livre(auteur, titre, isbn, numeroDansLaSerie, statutLivre, serie);
        return livreRepository.save(livre);
    }

    // Modifier le statut d'un livre
    public Livre modifierStatutLivre(int id, StatutLivre nouveauStatut) {
        Livre livre = livreRepository.findById(id).orElseThrow(() -> new BusinessException("Livre non trouvé."));
        livre.setStatutLivre(nouveauStatut);
        return livreRepository.save(livre);
    }
}
