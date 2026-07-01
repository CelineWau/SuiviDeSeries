package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.LivreCreationDTO;
import fr.celine.suivideseries.entity.Livre;
import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.service.LivreService;
import fr.celine.suivideseries.service.SerieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.ok(livreService.creerLivre(dto.getAuteur(), dto.getTitre(), dto.getIsbn(), dto.getStatutLivre(), serie));
    }
}
