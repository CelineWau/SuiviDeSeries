package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.ObjectifAnnuelDTO;
import fr.celine.suivideseries.entity.ObjectifAnnuel;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.service.ObjectifAnnuelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/objectifAnnuel")
public class ObjectifAnnuelController {

    private final ObjectifAnnuelService objectifAnnuelService;

    public ObjectifAnnuelController(ObjectifAnnuelService objectifAnnuelService) {
        this.objectifAnnuelService = objectifAnnuelService;
    }

    @GetMapping
    public ResponseEntity<ObjectifAnnuel> recupererObjectifAnnuel(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        ObjectifAnnuel objectif = objectifAnnuelService.recupererObjectifAnnuel(utilisateurConnecte).orElse(null);
        return ResponseEntity.ok(objectif);
    }

    @PostMapping
    public ResponseEntity<ObjectifAnnuel> definirModifierObjectif(@RequestBody ObjectifAnnuelDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(objectifAnnuelService.definirObjectifAnnuel(utilisateurConnecte, dto.getValeurObjectif()));
    }
}