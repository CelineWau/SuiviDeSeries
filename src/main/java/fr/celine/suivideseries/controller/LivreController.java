package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.FormatLivreDTO;
import fr.celine.suivideseries.dto.LivreCreationDTO;
import fr.celine.suivideseries.dto.RepartitionFormatDTO;
import fr.celine.suivideseries.dto.StatutLivreDTO;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.service.LivreService;
import fr.celine.suivideseries.service.SerieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
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
    public ResponseEntity<Livre> creerLivre(@RequestBody LivreCreationDTO dto){
        Serie serie =  serieService.trouverSerieParId(dto.getSerieId());
        return ResponseEntity.ok(livreService.creerLivre(dto.getAuteur(), dto.getTitre(), dto.getIsbn(), dto.getNumeroDansLaSerie(), dto.getStatutLivre(), dto.getFormatLivre(), dto.getDateAcquisition(), serie));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Livre> modifierStatutLivre(@PathVariable int id, @RequestBody StatutLivreDTO dto) {
        return ResponseEntity.ok(livreService.modifierStatutLivre(id, dto.getStatut()));
    }

    @PatchMapping("/{id}/formatLivre")
    public ResponseEntity<Livre> modifierFormatLivre(@PathVariable int id, @RequestBody FormatLivreDTO dto) {
        return ResponseEntity.ok(livreService.modifierFormatLivre(id, dto.getFormatLivre()));
    }

    @GetMapping("/auteurs")
    public ResponseEntity<List<String>> trouverAuteurs() {
        return ResponseEntity.ok(livreService.trouverAuteurs());
    }

    @GetMapping("/repartitionFormat")
    public ResponseEntity<RepartitionFormatDTO> calculerRepartitionFormatDansPalEtLu() {
        return ResponseEntity.ok(livreService.calculerRepartionFormatDansPalEtLu());
    }
}
