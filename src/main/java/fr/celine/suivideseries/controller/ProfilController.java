package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.ModificationEmailDTO;
import fr.celine.suivideseries.dto.ProfilDTO;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.service.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profil")
public class ProfilController {
    private final UtilisateurService utilisateurService;

    public ProfilController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @GetMapping
    public ResponseEntity<ProfilDTO> consulterProfil(@AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(utilisateurService.convertirUtilisateurEnDTO(utilisateurConnecte));
    }

    @PatchMapping("/email")
    public ResponseEntity<ProfilDTO> modifierEmail(@Valid @RequestBody ModificationEmailDTO dto, @AuthenticationPrincipal Utilisateur utilisateurConnecte) {
        return ResponseEntity.ok(utilisateurService.modifierEmail(utilisateurConnecte, dto));
    }
}