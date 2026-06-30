package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.SerieCreationDTO;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.service.SerieService;
import fr.celine.suivideseries.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping
    public ResponseEntity<Serie> creerSerie(@RequestBody SerieCreationDTO dto) {
        Utilisateur utilisateur = utilisateurService.trouverUtilisateurParId(dto.getUtilisateurId());
        return ResponseEntity.ok(serieService.creerSerie(dto.getNom(), utilisateur, dto.getStatutSerie(), dto.getNombreLivreTotal()));
    }
}
