package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.*;
import fr.celine.suivideseries.entity.Genre;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.enums.*;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.LivreRepository;
import fr.celine.suivideseries.repository.SerieRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SerieService {

    private final SerieRepository serieRepository;
    private final LivreRepository livreRepository;
    private final GenreService genreService;

    public SerieService(SerieRepository serieRepository, LivreRepository livreRepository, GenreService genreService) {
        this.serieRepository = serieRepository;
        this.livreRepository = livreRepository;
        this.genreService = genreService;
    }

    // Ajouter une série en BDD
    public Serie creerSerie(String nom, Utilisateur utilisateur, StatutSerie statutSerie, StatutPublication statutPublication, int nombreLivreTotal, NatureSerie natureSerie, int idGenre) {

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

        if(natureSerie == null) {
            throw new BusinessException("La série doit obligatoirement avoir un type de série (Roman, Manga, Comics...).");
        }

        if(serieRepository.findByNom(nom).isPresent()) {
            throw new BusinessException("Une série existe déjà avec ce nom.");
        }

        Serie serie = new Serie(nom, utilisateur, statutSerie, statutPublication, nombreLivreTotal);
        Serie serieSauvegardee = serieRepository.save(serie);
        if(natureSerie != NatureSerie.NON_DEFINI) {
            serieSauvegardee = this.modifierNatureSerie(serieSauvegardee.getIdSerie(), natureSerie, utilisateur);
        }
        if(idGenre != 0) {
            serieSauvegardee = this.modifierGenreSerie(serieSauvegardee.getIdSerie(), idGenre, utilisateur);
        }
        return serieSauvegardee;
    }

    // Trouver les séries avec un nombre de livres manquants dans la PAL
    public List<Serie> trouverSeriesPresqueFiniesDansLaPal(int livreManquant, Utilisateur utilisateur) {

        // Validation métier
        if(livreManquant <= 0) {
            throw new BusinessException("Le nombre de livre manquant ne peut pas être négatif ou égal à zéro.");
        }

        List<Integer> ids = serieRepository.trouverIdsSeriesPresqueFinieDansLaPal(livreManquant, utilisateur);
        return serieRepository.trouverSeriesAvecDetailsParIds(ids);
    }

    // Afficher les séries
    public List<Serie> afficherSeries(Utilisateur utilisateur) {
        return serieRepository.trierParStatut(utilisateur);
    }

    // Trouver une série par Id
    public Serie trouverSerieParId(int id, Utilisateur utilisateur) {
        Serie serie = serieRepository.findById(id).orElseThrow(() -> new BusinessException("Série non trouvée."));
        if (serie.getUtilisateur().getIdUser() != utilisateur.getIdUser()) {
            throw new BusinessException("Série non trouvée.");
        }
        return serie;
    }

    // Supprimer une série
    public void supprimerSerie(int id, Utilisateur utilisateur) {
        trouverSerieParId(id, utilisateur);
        serieRepository.deleteById(id);
    }

    // Modifier le nombre de livres dans une série
    public Serie modifierNombreLivreTotal(int id, int nouveauTotal, Utilisateur utilisateur) {
        Serie serie = trouverSerieParId(id, utilisateur);
        serie.setNombreLivreTotal(nouveauTotal);
        if (serie.getStatutSerie() != StatutSerie.ABANDONNEE) {
            serie.setStatutSerie(StatutSerie.EN_COURS);
        }
        return serieRepository.save(serie);
    }

    // Modifier le statut de publication d'une série
    public Serie modifierStatutPublication(int id, StatutPublication nouveauStatutPublication, Utilisateur utilisateur) {
        Serie serie = trouverSerieParId(id, utilisateur);
        serie.setStatutPublication(nouveauStatutPublication);
        return serieRepository.save(serie);
    }

    // Modifier le statut de la série
    public Serie modifierStatutSerie(int id, StatutSerie nouveauStatutSerie, Utilisateur utilisateur) {
        Serie serie = trouverSerieParId(id, utilisateur);
        StatutSerie ancienStatut = serie.getStatutSerie();
        serie.setStatutSerie(nouveauStatutSerie);
        if (ancienStatut == StatutSerie.EN_COURS && nouveauStatutSerie == StatutSerie.TERMINEE) {
            serie.setDateFin(LocalDate.now());
        }
        return serieRepository.save(serie);
    }

    // Remet une liste de Serie dans l'ordre exact d'une liste d'ids
    public List<Serie> trierSelonOrdreIds(List<Serie> series, List<Integer> ids) {
        Map<Integer, Serie> seriesParIds = series.stream()
                .collect(Collectors.toMap(Serie::getIdSerie, Function.identity()));
        return ids.stream()
                .map(seriesParIds::get)
                .filter(Objects::nonNull)
                .toList();
    }

    // Trouver 10 séries avec des livres à acheter
    public List<SerieAvecLivresAAcheterDTO> trouverSeriesAvecLivresAAcheter(Utilisateur utilisateur) {
        Pageable pageable = PageRequest.of(0, 10);
        List<Integer> ids = serieRepository.trouverIdsSeriesAvecLivresAAcheter(pageable, utilisateur);
        List<Serie> series =  serieRepository.trouverSeriesAvecDetailsParIds(ids);
        List<Serie> seriesTriees = trierSelonOrdreIds(series, ids);

        return seriesTriees.stream()
                .map(this::convertirEnDTO)
                .toList();
    }

    // Compter les séries entre le 1er janvier et le 31 décembre
    public long compterSeriesPourAnnee(Utilisateur utilisateur){
        LocalDate[] dates = calculerDatesAnnee();
        return serieRepository.countByDateFinBetweenAndUtilisateur(dates[0], dates[1], utilisateur);
    }

    // Convertir une série en DTO avec son nombre de livres à acheter
    private SerieAvecLivresAAcheterDTO convertirEnDTO(Serie serie) {
        int nombreLivreAAcheter = (int) serie.getLivres().stream()
                .filter(l -> l.getStatutLivre() == StatutLivre.A_ACHETER)
                .count();

        return new SerieAvecLivresAAcheterDTO(serie.getIdSerie(), serie.getNom(), nombreLivreAAcheter);
    }

    // Trouver les séries à jour
    public List<Serie> trouverSerieAJour(Utilisateur utilisateur) {
        List<Integer> ids = serieRepository.trouverIdsSeriesAJour(utilisateur);
        return serieRepository.trouverSeriesAvecDetailsParIds(ids);
    }

    // Trouver les séries délaissées depuis plus d'un an
    public List<SeriesDelaisseesDTO> trouverSerieDelaissees(Utilisateur utilisateur) {
        LocalDate date = LocalDate.now();
        LocalDate dateSeuil = date.minusYears(1);
        Pageable pageable = PageRequest.of(0, 15);
        List<Integer> ids = serieRepository.trouverIdsSeriesDelaissees(dateSeuil, utilisateur, pageable);
        List<Serie> series =  serieRepository.trouverSeriesAvecDetailsParIds(ids);
        List<Serie> seriesTriees = trierSelonOrdreIds(series, ids);

        return seriesTriees.stream()
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
    public double calculerRatioSeries(Utilisateur utilisateur) {
        long seriesTerminees = serieRepository.countByStatutSerieAndUtilisateur(StatutSerie.TERMINEE, utilisateur);
        long seriesEnCours = serieRepository.countByStatutSerieAndUtilisateur(StatutSerie.EN_COURS, utilisateur);
        long seriesCommencees = seriesTerminees + seriesEnCours;
        if (seriesCommencees == 0){
            return 0;
        } else {
            return (double) seriesTerminees / seriesCommencees;
        }
    }

    // Calculer la répartition entre les séries en cours, terminées et abandonnées
    public RepartitionStatutSerieDTO calculerRepartitionStatutSeries(Utilisateur utilisateur) {
        long seriesTerminees = serieRepository.countByStatutSerieAndUtilisateur(StatutSerie.TERMINEE, utilisateur);
        long seriesEnCours = serieRepository.countByStatutSerieAndUtilisateur(StatutSerie.EN_COURS, utilisateur);
        long seriesAbandonnees = serieRepository.countByStatutSerieAndUtilisateur(StatutSerie.ABANDONNEE, utilisateur);
        return new RepartitionStatutSerieDTO(seriesEnCours, seriesTerminees, seriesAbandonnees);
    }

    // Trouver les séries les plus longues dans En cours et Terminées
    public SeriesLesPlusLonguesDTO trouverSeriePlusLongueEnCoursEtTerminee(Utilisateur utilisateur) {
        Serie seriePlusLongueEnCours = serieRepository.findFirstByStatutSerieAndUtilisateurOrderByNombreLivreTotalDesc(StatutSerie.EN_COURS, utilisateur).orElse(null);
        Serie seriePlusLongueTerminee = serieRepository.findFirstByStatutSerieAndUtilisateurOrderByNombreLivreTotalDesc(StatutSerie.TERMINEE, utilisateur).orElse(null);
        return new SeriesLesPlusLonguesDTO(seriePlusLongueEnCours, seriePlusLongueTerminee);
    }

    // Calculer la répartition des séries par taille (petites/moyenne/sagas)
    public TailleSerieDTO calculerRepartitionTailleSeries(Utilisateur utilisateur){
        List<Serie> series = serieRepository.findByStatutSerieNotAndUtilisateur(StatutSerie.ABANDONNEE, utilisateur);

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
    public double calculerDureeMoyenneLecture(Utilisateur utilisateur) {
        List<Serie> series = serieRepository.findByStatutSerieAndUtilisateur(StatutSerie.TERMINEE, utilisateur);

        return series.stream()
                .mapToDouble(this::calculerDifferenceDatePremiereEtDerniereLecture)
                .average()
                .orElse(0);
    }

    // Trouver une série au hasard dans les séries ebook en cours
    public Serie trouverSerieAleatoireDansSerieEbook(Utilisateur utilisateur) {
        List<Serie> series = serieRepository.trouverSeriesAvecEbooksDansLaPal(utilisateur);
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
    public EbookAleatoireDTO proposerLivreAleatoire(Utilisateur utilisateur) {
        Serie serie = trouverSerieAleatoireDansSerieEbook(utilisateur);
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
    public List<LivrePalVieillissantDTO> trouverLivresPalVieillissante(Utilisateur utilisateur) {
        List<Serie> series = serieRepository.trouverSeriesAvecEbooksDansLaPal(utilisateur);

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
    public List<SerieASurveillerDTO> trouverSeriesASurveiller(Utilisateur utilisateur) {
        List<Integer> ids = serieRepository.trouverIdsSerieASurveiller(utilisateur);
        List<Serie> series = serieRepository.trouverSeriesAvecDetailsParIds(ids);

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
    private List<LivreAAcheterDTO> trouverListeCourses(FormatLivre format, int limite, Utilisateur utilisateur) {
        List<Integer> ids = serieRepository.trouverIdsSeriesAvecLivresAAcheterTrieesParDerniereLecture(utilisateur);
        List<Serie> series = serieRepository.trouverSeriesAvecDetailsParIds(ids);
        List<Serie> seriesTriees = trierSelonOrdreIds(series, ids);

        return seriesTriees.stream()
                .map(serie -> trouverLivreParNumero(serie, trouverPremierTomeAAcheterDansSerie(serie)))
                .filter(livre -> livre.getFormatLivre() == format)
                .limit(limite)
                .map(this::convertirEnDTOAcheter)
                .toList();
    }

    // Trouver la liste pour les livres papiers
    public List<LivreAAcheterDTO> trouverListeCoursesPapier(Utilisateur utilisateur) {
        return trouverListeCourses(FormatLivre.PAPIER, 20, utilisateur);
    }

    // Trouver la liste pour les ebooks
    public List<LivreAAcheterDTO> trouverListeCoursesEbook(Utilisateur utilisateur) {
        return trouverListeCourses(FormatLivre.EBOOK, 10, utilisateur);
    }

    // Modifier le nom d'une série
    public Serie modifierNomSerie(int id, String nouveauNom, Utilisateur utilisateur) {
        Serie serie = trouverSerieParId(id, utilisateur);

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
    public double calculerTempsLectureSerie(int id, Utilisateur utilisateur) {
        Serie serie = trouverSerieParId(id, utilisateur);
        return calculerDifferenceDatePremiereEtDerniereLecture(serie);
    }

    // Calculer le nombre de séries commencées dans l'année en cours
    public long compterSeriesCommenceesPourAnnee(Utilisateur utilisateur) {
        LocalDate[] dates = calculerDatesAnnee();
        return livreRepository.countByNumeroDansLaSerieAndStatutLivreAndDateLectureBetweenAndSerieUtilisateur(1, StatutLivre.LU, dates[0], dates[1], utilisateur);
    }

    // Compter les séries commencées et finies dans l'année en cours
    public long compterSeriesCommenceesEtFinieMemeAnnee(LocalDate dateDebut, LocalDate dateFin, Utilisateur utilisateur) {
        return livreRepository.compterSeriesCommenceesEtFiniesMemeAnnee(dateDebut, dateFin, utilisateur);
    }

    // Calculer le ratio des séries commencées et finies dans l'année en cours
    public double calculerRatioSeriesCommenceesEtFinieMemeAnnee(Utilisateur utilisateur) {
        LocalDate[] dates = calculerDatesAnnee();

        if(compterSeriesCommenceesPourAnnee(utilisateur) == 0) {
            return 0;
        } else {
            return (double) compterSeriesCommenceesEtFinieMemeAnnee(dates[0], dates[1], utilisateur) / compterSeriesCommenceesPourAnnee(utilisateur);
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
    public long compterSeriesAvecSeulTomeUnLuDansAnnee(Utilisateur utilisateur) {
        LocalDate[] dates = calculerDatesAnnee();
        List<Serie> series = serieRepository.trouverSeriesAvecTome1LuDansAnnee(dates[0], dates[1], utilisateur);
        return series.stream()
                .filter(this::seulTomeUnLu)
                .count();
    }

    // Calculer la série en cours avec le temps de lecture le plus long et le plus court
    public SeriesTermineesPlusLonguePlusCourteDTO trouverSeriesTermineesPlusLonguePlusCourte(Utilisateur utilisateur) {
        List<Serie> series = serieRepository.findByStatutSerieAndUtilisateur(StatutSerie.TERMINEE, utilisateur);

        List<Serie> seriesAvecLecture = series.stream()
                .filter(s -> calculerDifferenceDatePremiereEtDerniereLecture(s) > 0)
                .toList();

        Serie plusLongue = seriesAvecLecture.stream()
                .max(Comparator.comparing(this::calculerDifferenceDatePremiereEtDerniereLecture))
                .orElse(null);

        Serie plusCourte = seriesAvecLecture.stream()
                .min(Comparator.comparing(this::calculerDifferenceDatePremiereEtDerniereLecture))
                .orElse(null);

        SerieDureeLectureDTO dtoPlusLongue = null;
        if(plusLongue != null) {
            dtoPlusLongue = new SerieDureeLectureDTO(plusLongue.getNom(), calculerDifferenceDatePremiereEtDerniereLecture(plusLongue));
        }

        SerieDureeLectureDTO dtoPlusCourte = null;
        if(plusCourte != null) {
            dtoPlusCourte = new SerieDureeLectureDTO(plusCourte.getNom(), calculerDifferenceDatePremiereEtDerniereLecture(plusCourte));
        }

        return new SeriesTermineesPlusLonguePlusCourteDTO(dtoPlusLongue, dtoPlusCourte);
    }

    // Trouver les séries qui ne sont pas commencées
    public List<Serie> trouverSeriesJamaisCommencees(Utilisateur utilisateur) {
        return serieRepository.trouverSeriesJamaisCommencees(utilisateur);
    }

    // Modifier la série si elle est lu en anglais
    public Serie modifierLireEnAnglais(int id, boolean lireEnAnglais, Utilisateur utilisateur) {
        Serie serie = trouverSerieParId(id, utilisateur);
        serie.setLireEnAnglais(lireEnAnglais);
        return serieRepository.save(serie);
    }

    // Trouver les séries qui sont à lire en anglais
    public List<Serie> trouverSeriesALireEnAnglais(Utilisateur utilisateur) {
        return serieRepository.findByLireEnAnglaisAndStatutSerie(true, StatutSerie.EN_COURS, utilisateur);
    }

    // Modifier la nature (roman, BD, manga, comics...) de la série
    public Serie modifierNatureSerie(int id, NatureSerie natureSerie, Utilisateur utilisateur) {
        Serie serie = trouverSerieParId(id, utilisateur);
        serie.setNatureSerie(natureSerie);
        return serieRepository.save(serie);
    }

    // Modifier le genre d'une série
    public Serie modifierGenreSerie(int idSerie, int idGenre, Utilisateur utilisateur) {
        Serie serie = trouverSerieParId(idSerie, utilisateur);
        Genre genre = genreService.trouverGenreParId(idGenre);
        serie.setGenre(genre);
        return serieRepository.save(serie);
    }

    // Compter les séries par genre
    public List<RepartitionCategorieDTO> compterSeriesParGenre(Utilisateur utilisateur) {
        return serieRepository.compterSeriesParGenre(utilisateur);
    }

    // Compter les séries par nature
    public List<RepartitionCategorieDTO> compterSeriesParNature(Utilisateur utilisateur) {
        return serieRepository.compterSeriesParNature(utilisateur);
    }
}