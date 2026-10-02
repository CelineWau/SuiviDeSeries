package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.*;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.service.LivreService;
import fr.celine.suivideseries.service.SerieService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/livres")
public class LivreController {

    private final LivreService livreService;
    private final SerieService serieService;

    public LivreController(LivreService livreService,  SerieService serieService) {
        this.livreService = livreService;
        this.serieService = serieService;
    }

    @PostMapping
    public ResponseEntity<Livre> creerLivre(@RequestBody LivreCreationDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        Serie serie =  serieService.trouverSerieParId(dto.getSerieId(), utilisateurConnecte);
        return ResponseEntity.status(HttpStatus.CREATED).body(livreService.creerLivre(dto.getAuteur(), dto.getTitre(), dto.getIsbn(), dto.getNumeroDansLaSerie(), dto.getStatutLivre(), dto.getFormatLivre(), dto.getDateAcquisition(),
                dto.getDateLecture(), serie));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Livre> modifierStatutLivre(@PathVariable int id, @RequestBody StatutLivreDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(livreService.modifierStatutLivre(id, dto.getStatut(), utilisateurConnecte));
    }

    @PatchMapping("/{id}/formatLivre")
    public ResponseEntity<Livre> modifierFormatLivre(@PathVariable int id, @RequestBody FormatLivreDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(livreService.modifierFormatLivre(id, dto.getFormatLivre(), utilisateurConnecte));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Livre> modifierLivre(@PathVariable int id, @RequestBody ModifierLivreDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(livreService.modifierLivre(id, dto.getTitre(), dto.getAuteur(), dto.getIsbn(), dto.getNumeroDansLaSerie(), utilisateurConnecte));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerLivre(@PathVariable int id, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        livreService.supprimerLivre(id, utilisateurConnecte);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/auteurs")
    public ResponseEntity<List<String>> trouverAuteurs(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(livreService.trouverAuteurs(utilisateurConnecte));
    }

    @GetMapping("/repartitionFormat")
    public ResponseEntity<RepartitionFormatDTO> calculerRepartitionFormatDansPalEtLu(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(livreService.calculerRepartitionFormatDansPalEtLu(utilisateurConnecte));
    }

    @GetMapping("/auteursSeriesEnCours")
    public ResponseEntity<List<AuteursSeriesEnCoursDTO>> afficherAuteursSeriesEnCours(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(livreService.trouverAuteursAvecSerieEnCours(utilisateurConnecte));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Livre> trouverLivre(@PathVariable int id, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(livreService.trouverLivreParId(id, utilisateurConnecte));
    }

    @GetMapping("/dureeMoyenneDansPal")
    public ResponseEntity<Double> afficherDureeMoyenneDansPal(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(livreService.calculerTempsMoyenPal(utilisateurConnecte));
    }
}
