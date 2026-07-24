package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.ObjectifAnnuelDTO;
import fr.celine.suivideseries.entity.ObjectifAnnuel;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.service.ObjectifAnnuelService;
import fr.celine.suivideseries.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/objectifAnnuel")
public class ObjectifAnnuelController {

    private final ObjectifAnnuelService objectifAnnuelService;
    private final UtilisateurService utilisateurService;

    public ObjectifAnnuelController(ObjectifAnnuelService objectifAnnuelService,  UtilisateurService utilisateurService) {
        this.objectifAnnuelService = objectifAnnuelService;
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public ResponseEntity<ObjectifAnnuel> recupererObjectifAnnuel(@RequestParam int idUtilisateur) {
        Utilisateur utilisateur = utilisateurService.trouverUtilisateurParId(idUtilisateur);
        ObjectifAnnuel objectif = objectifAnnuelService.recupererObjectifAnnuel(utilisateur).orElse(null);
        return ResponseEntity.ok(objectif);
    }

    @PostMapping
    public ResponseEntity<ObjectifAnnuel> definirModifierObjectif(@RequestBody ObjectifAnnuelDTO dto) {
        Utilisateur utilisateur = utilisateurService.trouverUtilisateurParId(dto.getIdUtilisateur());
        return ResponseEntity.ok(objectifAnnuelService.definirObjectifAnnuel(utilisateur, dto.getValeurObjectif()));
    }
}
