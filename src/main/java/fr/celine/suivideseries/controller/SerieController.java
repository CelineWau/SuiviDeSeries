package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.*;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.service.SerieService;
import fr.celine.suivideseries.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/series")
public class SerieController {

    private final SerieService serieService;
    private final UtilisateurService utilisateurService;

    public SerieController(SerieService serieService, UtilisateurService utilisateurService) {
        this.serieService = serieService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public ResponseEntity<List<Serie>> afficherSerie() {
        return ResponseEntity.ok(serieService.afficherSeries());
    }

    @GetMapping("/presqueFiniesPal")
    public ResponseEntity<List<Serie>> trouverSeriesPresqueFiniesDansLaPal(@RequestParam int seuil) {
        return ResponseEntity.ok(serieService.trouverSeriesPresqueFiniesDansLaPal(seuil));
    }

    @GetMapping("/seriesAvecLivresAAcheter")
    public ResponseEntity<List<SerieAvecLivresAAcheterDTO>> trouverSeriesAvecLivresAAcheter() {
        return ResponseEntity.ok(serieService.trouverSeriesAvecLivresAAcheter());
    }

    @GetMapping("/compteurSerieParAnnee")
    public ResponseEntity<Long> compterSeriesParAnnee() {
        return ResponseEntity.ok(serieService.compterSeriesPourAnnee());
    }

    @GetMapping("/trouverSerieAJour")
    public ResponseEntity<List<Serie>> trouverSeriesAJour() {
        return ResponseEntity.ok(serieService.trouverSerieAJour());
    }

    @GetMapping("/seriesDelaissees")
    public ResponseEntity<List<SeriesDelaisseesDTO>> trouverSeriesDelaissees() {
        return ResponseEntity.ok(serieService.trouverSerieDelaissees());
    }

    @GetMapping("/ratioSeries")
    public ResponseEntity<Double> afficherRatioSeries() {
        return ResponseEntity.ok(serieService.calculerRatioSeries());
    }

    @GetMapping("/repartitionStatutSerie")
    public ResponseEntity<RepartitionStatutSerieDTO> afficherRepartitionStatutSerie() {
        return ResponseEntity.ok(serieService.calculerRepartitionStatutSeries());
    }

    @GetMapping("/seriesLesPlusLongues")
    public ResponseEntity<SeriesLesPlusLonguesDTO> afficherLesSeriesPlusLongues() {
        return ResponseEntity.ok(serieService.trouverSeriePlusLongueEnCoursEtTerminee());
    }

    @GetMapping("/repartitionTailleSeries")
    public ResponseEntity<TailleSerieDTO> afficherRepartitionTailleSeries() {
        return ResponseEntity.ok(serieService.calculerRepartitionTailleSeries());
    }

    @GetMapping("/dureeMoyenneLectureSerie")
    public ResponseEntity<Double> afficherDureeMoyenneLectureSerie() {
        return ResponseEntity.ok(serieService.calculerDureeMoyenneLecture());
    }

    @PostMapping
    public ResponseEntity<Serie> creerSerie(@RequestBody SerieCreationDTO dto) {
        Utilisateur utilisateur = utilisateurService.trouverUtilisateurParId(dto.getUtilisateurId());
        return ResponseEntity.ok(serieService.creerSerie(dto.getNom(), utilisateur, dto.getStatutSerie(), dto.getStatutPublication(), dto.getNombreLivreTotal()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerSerie(@PathVariable int id) {
        serieService.supprimerSerie(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/nombreLivreTotal")
    public ResponseEntity<Serie> modifierNombreLivreTotal(@PathVariable int id, @RequestBody NombreLivreTotalDTO dto){
        return ResponseEntity.ok(serieService.modifierNombreLivreTotal(id, dto.getNombreLivreTotal()));
    }

    @PatchMapping("/{id}/statutPublication")
    public ResponseEntity<Serie> modifierStatutPublication(@PathVariable int id, @RequestBody StatutPublicationDTO dto){
        return ResponseEntity.ok(serieService.modifierStatutPublication(id, dto.getStatutPublication()));
    }

    @PatchMapping("/{id}/statutSerie")
    public ResponseEntity<Serie> modifierStatutSerie(@PathVariable int id, @RequestBody StatutSerieDTO dto) {
        return ResponseEntity.ok(serieService.modifierStatutSerie(id, dto.getStatutSerie()));
    }
}
