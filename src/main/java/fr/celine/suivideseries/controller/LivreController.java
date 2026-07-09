package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.LivreCreationDTO;
import fr.celine.suivideseries.dto.StatutLivreDTO;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.service.LivreService;
import fr.celine.suivideseries.service.SerieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(livreService.creerLivre(dto.getAuteur(), dto.getTitre(), dto.getIsbn(), dto.getNumeroDansLaSerie(), dto.getStatutLivre(), serie));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<Livre> modifierStatutLivre(@PathVariable int id, @RequestBody StatutLivreDTO dto) {
        return ResponseEntity.ok(livreService.modifierStatutLivre(id, dto.getStatut()));
    }
}
