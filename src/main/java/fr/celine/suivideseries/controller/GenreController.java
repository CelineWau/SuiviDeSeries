package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.GenreDTO;
import fr.celine.suivideseries.entity.Genre;
import fr.celine.suivideseries.service.GenreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @PostMapping
    public ResponseEntity<Genre> creerGenre(@RequestBody GenreDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(genreService.creerGenre(dto.getNom()));
    }

    @GetMapping
    public ResponseEntity<List<Genre>> listerGenres() {
        return ResponseEntity.ok(genreService.trouverGenres());
    }
}
