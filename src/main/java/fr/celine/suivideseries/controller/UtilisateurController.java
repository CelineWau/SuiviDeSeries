package fr.celine.suivideseries.controller;

import fr.celine.suivideseries.dto.AuthResponseDTO;
import fr.celine.suivideseries.dto.LoginRequestDTO;
import fr.celine.suivideseries.dto.RegisterRequestDTO;
import fr.celine.suivideseries.service.UtilisateurService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> inscrireUtilisateur(@RequestBody RegisterRequestDTO requete) {
        return ResponseEntity.ok(utilisateurService.inscrireUtilisateur(requete));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> connecterUtilisateur(@RequestBody LoginRequestDTO requete) {
        return ResponseEntity.ok(utilisateurService.connecterUtilisateur(requete));
    }
}
