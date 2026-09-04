package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.*;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.FormatLivre;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutPublication;
import fr.celine.suivideseries.enums.StatutSerie;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.LivreRepository;
import fr.celine.suivideseries.repository.SerieRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class SerieService {

    private final SerieRepository serieRepository;
    private final LivreRepository livreRepository;

    public SerieService(SerieRepository serieRepository, LivreRepository livreRepository) {
        this.serieRepository = serieRepository;
        this.livreRepository = livreRepository;
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
        return serieRepository.findById(id).orElseThrow(() -> new BusinessException("Série non trouvée."));
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
        LocalDate[] dates = calculerDatesAnnee();
        return serieRepository.countByDateFinBetween(dates[0], dates[1]);
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
    public List<SeriesDelaisseesDTO> trouverSerieDelaissees() {
        LocalDate date = LocalDate.now();
        LocalDate dateSeuil = date.minusYears(1);
        Pageable pageable = PageRequest.of(0, 15);
        List<Serie> series = serieRepository.trouverSeriesDelaissees(dateSeuil, pageable);

        return series.stream()
                .map(this::convertirEnDTODelaisses)
                .toList();
    }

    // Convertir une série en DTO avec la date de la dernière lecture
    private SeriesDelaisseesDTO convertirEnDTODelaisses(Serie serie) {
        LocalDate derniereLecture = serie.getLivres().stream()
                .map(Livre::getDateLecture)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        return new SeriesDelaisseesDTO(serie.getNom(), derniereLecture);
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

    // Calculer la répartition des séries par taille (petites/moyenne/sagas)
    public TailleSerieDTO calculerRepartitionTailleSeries(){
        List<Serie> series = serieRepository.findByStatutSerieNot(StatutSerie.ABANDONNEE);

        long petites = series.stream()
                .filter(s -> s.getNombreLivreTotal() >= 1 && s.getNombreLivreTotal() <= 3)
                .count();

        long moyennes = series.stream()
                .filter(s -> s.getNombreLivreTotal() >= 4 && s.getNombreLivreTotal() <= 7)
                .count();

        long sagas = series.stream()
                .filter(s -> s.getNombreLivreTotal() >= 8)
                .count();

        return new TailleSerieDTO(petites, moyennes, sagas);
    }

    // Calculer la différence entre la date de la première lecture et la date de la dernière lecture
    public double calculerDifferenceDatePremiereEtDerniereLecture(Serie serie){
        LocalDate derniereLecture = serie.getLivres().stream()
                .map(Livre::getDateLecture)
                .filter(Objects::nonNull)
                .max(LocalDate::compareTo)
                .orElse(null);

        LocalDate premiereLecture = serie.getLivres().stream()
                .map(Livre::getDateLecture)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(null);

        if(premiereLecture != null && derniereLecture != null){
            return ChronoUnit.DAYS.between(premiereLecture, derniereLecture);
        } else {
            return 0;
        }
    }

    // Calculer la durée moyenne des lectures TERMINEE
    public double calculerDureeMoyenneLecture() {
        List<Serie> series = serieRepository.findByStatutSerie(StatutSerie.TERMINEE);

        return series.stream()
                .mapToDouble(this::calculerDifferenceDatePremiereEtDerniereLecture)
                .average()
                .orElse(0);
    }

    // Trouver une série au hasard dans les séries ebook en cours
    public Serie trouverSerieAleatoireDansSerieEbook() {
        List<Serie> series = serieRepository.trouverSeriesAvecEbooksDansLaPal();
        if (series.isEmpty()) {
            throw new BusinessException("Il n'y a pas d'ebooks dans la pile à lire qui correspond à demande.");
        }
        int indexAleatoire = (int) (Math.random() * series.size());
        return series.get(indexAleatoire);
    }

    // Trouver le tome le plus petit dans une série
    public int trouverTomePlusPetitDansSerie(Serie serie){
        return serie.getLivres().stream()
                .filter(l -> l.getStatutLivre() == StatutLivre.DANS_PAL && l.getFormatLivre() == FormatLivre.EBOOK)
                .mapToInt(Livre::getNumeroDansLaSerie)
                .min()
                .orElse(0);
    }

    // Proposer un ebook à lire au hasard parmi les ebooks d'une série en cours dans la PAL
    public EbookAleatoireDTO proposerLivreAleatoire() {
        Serie serie = trouverSerieAleatoireDansSerieEbook();
        int numeroProchainTome = trouverTomePlusPetitDansSerie(serie);

        if(!tomesPrecedentsTousLus(serie, numeroProchainTome)){
            throw new BusinessException("Il manque un tome dans cette série avant de pouvoir en proposer un.");
        }

        Livre livre = trouverLivreParNumero(serie, numeroProchainTome);

        return new EbookAleatoireDTO(livre.getTitre(), livre.getAuteur(), serie.getNom(), livre.getNumeroDansLaSerie());
    }

    // Trouver un livre par rapport à son numéro de tome
    private Livre trouverLivreParNumero(Serie serie, int numero){
        return serie.getLivres().stream()
                .filter(l -> l.getNumeroDansLaSerie() == numero)
                .findFirst()
                .orElseThrow(() -> new BusinessException("Livre non trouvé."));
    }

    // Trouver les 5 livres les plus anciens en PAL (défi PAL vieillissante)
    public List<LivrePalVieillissantDTO> trouverLivresPalVieillissante() {
        List<Serie> series = serieRepository.trouverSeriesAvecEbooksDansLaPal();

        List<Livre> livres = series.stream()
                .map(serie -> trouverLivreParNumero(serie, trouverTomePlusPetitDansSerie(serie)))
                .filter(livre -> tomesPrecedentsTousLus(livre.getSerie(), livre.getNumeroDansLaSerie()))
                .sorted(Comparator.comparing(Livre::getDateAcquisition, Comparator.nullsFirst(Comparator.naturalOrder())))
                .limit(5)
                .toList();

        return livres.stream()
                .map(this::convertirEnDTOPalVieillissante)
                .toList();
    }

    // Convertir un livre en DTO pour le défi PAL vieillissante
    private LivrePalVieillissantDTO convertirEnDTOPalVieillissante(Livre livre){
        String nomSerie = livre.getSerie().getNom();
        return new LivrePalVieillissantDTO(livre.getTitre(), livre.getAuteur(), nomSerie, livre.getNumeroDansLaSerie(), livre.getDateAcquisition());
    }

    // Trouver les tomes précédents qui sont lus
    private boolean tomesPrecedentsTousLus(Serie serie, int numeroCandidat) {
        long nombreTomesLu = serie.getLivres().stream()
                .filter(l -> l.getNumeroDansLaSerie() < numeroCandidat && l.getStatutLivre() == StatutLivre.LU)
                .count();

        return nombreTomesLu == numeroCandidat - 1;
    }

    // Trouver les séries à surveiller
    public List<SerieASurveillerDTO> trouverSeriesASurveiller() {
        List<Serie> series = serieRepository.trouverSerieASurveiller();

        return series.stream()
                .map(this::convertirEnDTOASurveiller)
                .toList();
    }

    // Convertir une série en DTO pour la liste des séries à surveiller
    private SerieASurveillerDTO convertirEnDTOASurveiller(Serie serie) {
        Optional<Livre> tome1 = serie.getLivres().stream()
                .filter(l -> l.getNumeroDansLaSerie() == 1)
                .findFirst();
        String auteur =  tome1
                .map(Livre::getAuteur)
                .orElseGet(() -> serie.getLivres().stream()
                        .findFirst()
                        .map(Livre::getAuteur)
                        .orElse("Auteur inconnu."));
        return new SerieASurveillerDTO(serie.getIdSerie(), serie.getNom(), auteur);
    }

    // Trouver le premier tome à acheter dans une série
    private int trouverPremierTomeAAcheterDansSerie(Serie serie) {
        return serie.getLivres().stream()
                .filter(l -> l.getStatutLivre() == StatutLivre.A_ACHETER)
                .mapToInt(Livre::getNumeroDansLaSerie)
                .min()
                .orElse(0);
    }

    // Convertir un livre en DTO pour la liste des livres à acheter
    private LivreAAcheterDTO convertirEnDTOAcheter(Livre livre) {
        String nomSerie =  livre.getSerie().getNom();
        return new LivreAAcheterDTO(livre.getTitre(), livre.getAuteur(), nomSerie, livre.getNumeroDansLaSerie());
    }

    // Trouver les livres pour créer la liste de course
    private List<LivreAAcheterDTO> trouverListeCourses(FormatLivre format, int limite) {
        List<Serie> series = serieRepository.trouverSeriesAvecLivresAAcheterTrieesParDerniereLecture();

        return series.stream()
                .map(serie -> trouverLivreParNumero(serie, trouverPremierTomeAAcheterDansSerie(serie)))
                .filter(livre -> livre.getFormatLivre() == format)
                .limit(limite)
                .map(this::convertirEnDTOAcheter)
                .toList();
    }

    // Trouver la liste pour les livres papiers
    public List<LivreAAcheterDTO> trouverListeCoursesPapier() {
        return trouverListeCourses(FormatLivre.PAPIER, 20);
    }

    // Trouver la liste pour les ebooks
    public List<LivreAAcheterDTO> trouverListeCoursesEbook() {
        return trouverListeCourses(FormatLivre.EBOOK, 10);
    }

    // Modifier le nom d'une série
    public Serie modifierNomSerie(int id, String nouveauNom) {
        Serie serie = serieRepository.findById(id).orElseThrow(() -> new BusinessException("Série non trouvée."));

        boolean nomPrisParAutreSerie = serieRepository.findByNom(nouveauNom)
                .filter(s -> s.getIdSerie() != id)
                .isPresent();
        if(nomPrisParAutreSerie) {
            throw new BusinessException("Le nouveau titre de la série existe déjà.");
        }

        serie.setNom(nouveauNom);
        return serieRepository.save(serie);
    }

    // Calculer le temps de lecture d'une série
    public double calculerTempsLectureSerie(int id) {
        Serie serie = trouverSerieParId(id);
        return calculerDifferenceDatePremiereEtDerniereLecture(serie);
    }

    // Calculer le nombre de séries commencées dans l'année en cours
    public long compterSeriesCommenceesPourAnnee() {
        LocalDate[] dates = calculerDatesAnnee();
        return livreRepository.countByNumeroDansLaSerieAndStatutLivreAndDateLectureBetween(1, StatutLivre.LU, dates[0], dates[1]);
    }

    // Compter les séries commencées et finies dans l'année en cours
    public long compterSeriesCommenceesEtFinieMemeAnnee(LocalDate dateDebut, LocalDate dateFin) {
        return livreRepository.compterSeriesCommenceesEtFiniesMemeAnnee(dateDebut, dateFin);
    }

    // Calculer le ratio des séries commencées et finies dans l'année en cours
    public double calculerRatioSeriesCommenceesEtFinieMemeAnnee() {
        LocalDate[] dates = calculerDatesAnnee();

        if(compterSeriesCommenceesPourAnnee() == 0) {
            return 0;
        } else {
            return (double) compterSeriesCommenceesEtFinieMemeAnnee(dates[0], dates[1]) / compterSeriesCommenceesPourAnnee();
        }
    }

    // Calculer l'année en cours
    private LocalDate[] calculerDatesAnnee() {
        int annee = LocalDate.now().getYear();
        LocalDate dateDebut = LocalDate.of(annee, 1, 1);
        LocalDate dateFin = LocalDate.of(annee, 12, 31);
        return new LocalDate[]{dateDebut, dateFin};
    }

    // Isoler les séries avec uniquement le tome 1 de lu
    private boolean seulTomeUnLu(Serie serie) {
        long tomelu = serie.getLivres().stream()
                .filter(l -> l.getStatutLivre() == StatutLivre.LU)
                .count();
        return  tomelu == 1;
    }

    // Compter le nombre de séries avec que le tome 1 lu dans l'année
    public long compterSeriesAvecSeulTomeUnLuDansAnnee() {
        LocalDate[] dates = calculerDatesAnnee();
        List<Serie> series = serieRepository.trouverSeriesAvecTome1LuDansAnnee(dates[0], dates[1]);
        return series.stream()
                .filter(this::seulTomeUnLu)
                .count();
    }
}