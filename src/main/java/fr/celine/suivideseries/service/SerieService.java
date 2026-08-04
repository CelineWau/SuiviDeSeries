package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.RepartitionStatutSerieDTO;
import fr.celine.suivideseries.dto.SerieAvecLivresAAcheterDTO;
import fr.celine.suivideseries.dto.SeriesLesPlusLonguesDTO;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutPublication;
import fr.celine.suivideseries.enums.StatutSerie;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.SerieRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SerieService {

    private final SerieRepository serieRepository;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    // Ajouter une série en BDD
    public Serie creerSerie(String nom, Utilisateur utilisateur, StatutSerie statutSerie, StatutPublication statutPublication, int nombreLivreTotal) {

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

        if(statutPublication == null) {
            throw new BusinessException("La série doit obligatoirement avoir un statut de publication.");
        }

        if(serieRepository.findByNom(nom).isPresent()) {
            throw new BusinessException("Une série existe déjà avec ce nom.");
        }

        Serie serie = new Serie(nom, utilisateur, statutSerie, statutPublication, nombreLivreTotal);
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

    // Trouver les séries avec un nombre de livres manquants dans la PAL
    public List<Serie> trouverSeriesPresqueFiniesDansLaPal(int livreManquant) {

        // Validation métier
        if(livreManquant <= 0) {
            throw new BusinessException("Le nombre de livre manquant ne peut pas être négatif ou égal à zéro.");
        }

        return serieRepository.trouverSeriesPresqueFinieDansLaPal(livreManquant);
    }

    // Afficher les séries
    public List<Serie> afficherSeries() {
        return serieRepository.trierParStatut();
    }

    // Trouver une série par Id
    public Serie trouverSerieParId(int id) {
        return serieRepository.findById(id).orElseThrow();
    }

    // Supprimer une série
    public void supprimerSerie(int id) {
        serieRepository.deleteById(id);
    }

    // Modifier le nombre de livres dans une série
    public Serie modifierNombreLivreTotal(int id, int nouveauTotal) {
        Serie serie = trouverSerieParId(id);
        serie.setNombreLivreTotal(nouveauTotal);
        if (serie.getStatutSerie() != StatutSerie.ABANDONNEE) {
            serie.setStatutSerie(StatutSerie.EN_COURS);
        }
        return serieRepository.save(serie);
    }

    // Modifier le statut de publication d'une série
    public Serie modifierStatutPublication(int id, StatutPublication nouveauStatutPublication) {
        Serie serie = trouverSerieParId(id);
        serie.setStatutPublication(nouveauStatutPublication);
        return serieRepository.save(serie);
    }

    // Modifier le statut de la série
    public Serie modifierStatutSerie(int id, StatutSerie nouveauStatutSerie) {
        Serie serie = trouverSerieParId(id);
        StatutSerie ancienStatut = serie.getStatutSerie();
        serie.setStatutSerie(nouveauStatutSerie);
        if (ancienStatut == StatutSerie.EN_COURS && nouveauStatutSerie == StatutSerie.TERMINEE) {
            serie.setDateFin(LocalDate.now());
        }
        return serieRepository.save(serie);
    }

    // Trouver 10 séries avec des livres à acheter
    public List<SerieAvecLivresAAcheterDTO> trouverSeriesAvecLivresAAcheter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Serie> series = serieRepository.trouverSeriesAvecLivresAAcheter(pageable);

        return series.stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    // Compter les séries entre le 1er janvier et le 31 décembre
    public long compterSeriesPourAnnee(){
        int annee = LocalDate.now().getYear();
        LocalDate dateDebut = LocalDate.of(annee, 1, 1);
        LocalDate dateFin = LocalDate.of(annee, 12, 31);

        return serieRepository.countByDateFinBetween(dateDebut, dateFin);
    }

    // Convertir une série en DTO avec son nombre de livres à acheter
    private SerieAvecLivresAAcheterDTO convertirEnDTO(Serie serie) {
        int nombreLivreAAcheter = (int) serie.getLivres().stream()
                .filter(l -> l.getStatutLivre() == StatutLivre.A_ACHETER)
                .count();

        return new SerieAvecLivresAAcheterDTO(serie.getIdSerie(), serie.getNom(), nombreLivreAAcheter);
    }

    // Trouver les séries à jour
    public List<Serie> trouverSerieAJour() {
        return serieRepository.trouverSeriesAJour();
    }

    // Trouver les séries délaissées depuis plus d'un an
    public List<Serie> trouverSerieDelaissees() {
        LocalDate date = LocalDate.now();
        LocalDate dateSeuil = date.minusYears(1);
        return serieRepository.trouverSeriesDelaissees(dateSeuil);
    }

    // Calculer le ratio de séries finies vs commencées
    public double calculerRatioSeries() {
        long seriesTerminees = serieRepository.countByStatutSerie(StatutSerie.TERMINEE);
        long seriesEnCours = serieRepository.countByStatutSerie(StatutSerie.EN_COURS);
        long seriesCommencees = seriesTerminees + seriesEnCours;
        if (seriesCommencees == 0){
            return 0;
        } else {
            return (double) seriesTerminees / seriesCommencees;
        }
    }

    // Calculer la répartition entre les séries en cours, terminées et abandonnées
    public RepartitionStatutSerieDTO calculerRepartitionStatutSeries() {
        long seriesTerminees = serieRepository.countByStatutSerie(StatutSerie.TERMINEE);
        long seriesEnCours = serieRepository.countByStatutSerie(StatutSerie.EN_COURS);
        long seriesAbandonnees = serieRepository.countByStatutSerie(StatutSerie.ABANDONNEE);
        return new RepartitionStatutSerieDTO(seriesEnCours, seriesTerminees, seriesAbandonnees);
    }

    // Trouver les séries les plus longues dans En cours et Terminées
    public SeriesLesPlusLonguesDTO trouverSeriePlusLongueEnCoursEtTerminee() {
        Serie seriePlusLongueEnCours = serieRepository.findFirstByStatutSerieOrderByNombreLivreTotalDesc(StatutSerie.EN_COURS).orElse(null);
        Serie seriePlusLongueTerminee = serieRepository.findFirstByStatutSerieOrderByNombreLivreTotalDesc(StatutSerie.TERMINEE).orElse(null);
        return new SeriesLesPlusLonguesDTO(seriePlusLongueEnCours, seriePlusLongueTerminee);
    }
}
