package fr.celine.suivideseries.service;

import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.StatutSerie;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.SerieRepository;

import java.util.List;

public class SerieService {

    private final SerieRepository serieRepository;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    // Ajouter une série en BDD
    public Serie creerSerie(String nom, Utilisateur utilisateur, StatutSerie statutSerie, int nombreLivreTotal) {

        // Validation métier
        if(nom == null || nom.isBlank()) {
            throw new BusinessException("Le nom de la série est obligatoire.");
        }

        if(utilisateur == null) {
            throw new BusinessException("Un utilisateur doit être associé à une série.");
        }

        if(nombreLivreTotal <= 0) {
            throw new BusinessException("La série doit avoir un nombre de livre total supérieur à zéro.");
        }

        if(statutSerie == null) {
            throw new BusinessException("La série doit obligatoirement avoir un statut.");
        }

        if(serieRepository.findByNom(nom).isPresent()) {
            throw new BusinessException("Une série existe déjà avec ce nom.");
        }

        Serie serie = new Serie(nom, utilisateur, statutSerie, nombreLivreTotal);
        return serieRepository.save(serie);
    }

    // Trouver les séries avec un nombre de livres manquants
    public List<Serie> trouverSerieAvecNombreLivresManquants(int livreManquant) {

        // Validation métier
        if(livreManquant <= 0) {
            throw new BusinessException("Le nombre de livre manquant ne peut pas être négatif ou égal à zéro.");
        }

         return serieRepository.trouverSeriesParNombreLivresManquants(livreManquant);
    }

}
