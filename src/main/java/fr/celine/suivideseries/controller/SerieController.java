package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.*;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.service.SerieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/series")
public class SerieController {

    private final SerieService serieService;

    public SerieController(SerieService serieService) {
        this.serieService = serieService;
    }

    @GetMapping
    public ResponseEntity<List<Serie>> afficherSerie(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.afficherSeries(utilisateurConnecte));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Serie> trouverSerie(@PathVariable int id, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSerieParId(id, utilisateurConnecte));
    }

    @GetMapping("/presqueFiniesPal")
    public ResponseEntity<List<Serie>> trouverSeriesPresqueFiniesDansLaPal(@RequestParam int seuil, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSeriesPresqueFiniesDansLaPal(seuil, utilisateurConnecte));
    }

    @GetMapping("/seriesAvecLivresAAcheter")
    public ResponseEntity<List<SerieAvecLivresAAcheterDTO>> trouverSeriesAvecLivresAAcheter(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSeriesAvecLivresAAcheter(utilisateurConnecte));
    }

    @GetMapping("/compteurSerieParAnnee")
    public ResponseEntity<Long> compterSeriesParAnnee(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.compterSeriesPourAnnee(utilisateurConnecte));
    }

    @GetMapping("/trouverSerieAJour")
    public ResponseEntity<List<Serie>> trouverSeriesAJour(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSerieAJour(utilisateurConnecte));
    }

    @GetMapping("/seriesDelaissees")
    public ResponseEntity<List<SeriesDelaisseesDTO>> trouverSeriesDelaissees(@AuthenticationPrincipal  Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSerieDelaissees(utilisateurConnecte));
    }

    @GetMapping("/ratioSeries")
    public ResponseEntity<Double> afficherRatioSeries(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.calculerRatioSeries(utilisateurConnecte));
    }

    @GetMapping("/repartitionStatutSerie")
    public ResponseEntity<RepartitionStatutSerieDTO> afficherRepartitionStatutSerie(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.calculerRepartitionStatutSeries(utilisateurConnecte));
    }

    @GetMapping("/seriesLesPlusLongues")
    public ResponseEntity<SeriesLesPlusLonguesDTO> afficherLesSeriesPlusLongues(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSeriePlusLongueEnCoursEtTerminee(utilisateurConnecte));
    }

    @GetMapping("/repartitionTailleSeries")
    public ResponseEntity<TailleSerieDTO> afficherRepartitionTailleSeries(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.calculerRepartitionTailleSeries(utilisateurConnecte));
    }

    @GetMapping("/dureeMoyenneLectureSerie")
    public ResponseEntity<Double> afficherDureeMoyenneLectureSerie(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.calculerDureeMoyenneLecture(utilisateurConnecte));
    }

    @GetMapping("/ebookAleatoire")
    public ResponseEntity<EbookAleatoireDTO> afficherEbookAleatoire(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.proposerLivreAleatoire(utilisateurConnecte));
    }

    @GetMapping("/palVieillissante")
    public ResponseEntity<List<LivrePalVieillissantDTO>> afficherPalVieillissante(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverLivresPalVieillissante(utilisateurConnecte));
    }

    @GetMapping("/aSurveiller")
    public ResponseEntity<List<SerieASurveillerDTO>> afficherSerieASurveiller(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSeriesASurveiller(utilisateurConnecte));
    }

    @GetMapping("/listeCoursePapier")
    public ResponseEntity<List<LivreAAcheterDTO>> afficherListeCoursePapier(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverListeCoursesPapier(utilisateurConnecte));
    }

    @GetMapping("/listeCourseEbook")
    public ResponseEntity<List<LivreAAcheterDTO>> afficherListeCourseEbook(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverListeCoursesEbook(utilisateurConnecte));
    }

    @GetMapping("/{id}/tempsLecture")
    public ResponseEntity<Double> afficherTempsLecture(@PathVariable int id, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.calculerTempsLectureSerie(id, utilisateurConnecte));
    }

    @GetMapping("/compteurSeriesCommenceesParAnnee")
    public ResponseEntity<Long> afficherCompteurSeriesCommenceesParAnnee(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.compterSeriesCommenceesPourAnnee(utilisateurConnecte));
    }

    @GetMapping("/ratioSeriesCommenceesEtFiniesParAnnee")
    public ResponseEntity<Double> afficherRatioSeriesCommenceesParAnnee(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.calculerRatioSeriesCommenceesEtFinieMemeAnnee(utilisateurConnecte));
    }

    @GetMapping("/nombreSeriesAvecQueTomeUnLuDansAnnee")
    public ResponseEntity<Long> afficherNombreSeriesAvecQueTomeUnLuDansAnnee(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.compterSeriesAvecSeulTomeUnLuDansAnnee(utilisateurConnecte));
    }

    @GetMapping("/seriesTermineesPlusLonguePlusCourte")
    public ResponseEntity<SeriesTermineesPlusLonguePlusCourteDTO> afficherSeriesTermineesPlusLonguePlusCourte(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSeriesTermineesPlusLonguePlusCourte(utilisateurConnecte));
    }

    @GetMapping("/seriesJamaisCommencees")
    public ResponseEntity<List<Serie>> afficherSeriesJamaisCommencees(@AuthenticationPrincipal  Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSeriesJamaisCommencees(utilisateurConnecte));
    }

    @GetMapping("/lireEnAnglais")
    public ResponseEntity<List<Serie>> afficherLireEnAnglais(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.trouverSeriesALireEnAnglais(utilisateurConnecte));
    }

    @GetMapping("/seriesParGenre")
    public ResponseEntity<List<RepartitionCategorieDTO>> afficherSeriesParGenre(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.compterSeriesParGenre(utilisateurConnecte));
    }

    @GetMapping("/seriesParNature")
    public ResponseEntity<List<RepartitionCategorieDTO>> afficherSeriesParNature(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.compterSeriesParNature(utilisateurConnecte));
    }

    @PostMapping
    public ResponseEntity<Serie> creerSerie(@RequestBody SerieCreationDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serieService.creerSerie(dto.getNom(), utilisateurConnecte, dto.getStatutSerie(), dto.getStatutPublication(), dto.getNombreLivreTotal(),
                dto.getNatureSerie(), dto.getIdGenre()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSerie(@PathVariable int id, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        serieService.supprimerSerie(id, utilisateurConnecte);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/nombreLivreTotal")
    public ResponseEntity<Serie> modifierNombreLivreTotal(@PathVariable int id, @RequestBody NombreLivreTotalDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.modifierNombreLivreTotal(id, dto.getNombreLivreTotal(), utilisateurConnecte));
    }

    @PatchMapping("/{id}/statutPublication")
    public ResponseEntity<Serie> modifierStatutPublication(@PathVariable int id, @RequestBody StatutPublicationDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.modifierStatutPublication(id, dto.getStatutPublication(), utilisateurConnecte));
    }

    @PatchMapping("/{id}/statutSerie")
    public ResponseEntity<Serie> modifierStatutSerie(@PathVariable int id, @RequestBody StatutSerieDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.modifierStatutSerie(id, dto.getStatutSerie(), utilisateurConnecte));
    }

    @PatchMapping("/{id}/nom")
    public ResponseEntity<Serie> modifierNomSerie(@PathVariable int id, @RequestBody NomSerieDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.modifierNomSerie(id, dto.getNom(), utilisateurConnecte));
    }

    @PatchMapping("/{id}/lireEnAnglais")
    public ResponseEntity<Serie> modifierLireEnAnglais(@PathVariable int id, @RequestBody LireEnAnglaisDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.modifierLireEnAnglais(id, dto.isLireEnAnglais(), utilisateurConnecte));
    }

    @PatchMapping("/{id}/natureSerie")
    public ResponseEntity<Serie> modifierNatureSerie(@PathVariable int id, @RequestBody NatureSerieDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.modifierNatureSerie(id, dto.getNatureSerie(), utilisateurConnecte));
    }

    @PatchMapping("/{id}/genre")
    public ResponseEntity<Serie> modifierGenreSerie(@PathVariable int id, @RequestBody GenreSerieDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(serieService.modifierGenreSerie(id, dto.getIdGenre(), utilisateurConnecte));
    }
}
