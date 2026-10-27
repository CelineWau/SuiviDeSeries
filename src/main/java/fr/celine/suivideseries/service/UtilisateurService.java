package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.*;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.UtilisateurRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public UtilisateurService(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    // Inscrire un nouvel utilisateur
    public AuthResponseDTO inscrireUtilisateur(RegisterRequestDTO requete) {
        if(utilisateurRepository.findByPseudo(requete.getPseudo()).isPresent()){
            throw new BusinessException("Ce pseudo existe déjà.");
        }

        if (utilisateurRepository.findByEmailIgnoreCase(requete.getEmail()).isPresent()) {
            throw new BusinessException("Cet email est déjà utilisé.");
        }

        Utilisateur utilisateur = new Utilisateur(requete.getNom(), requete.getPrenom(), requete.getPseudo(), requete.getEmail());
        utilisateur.setMdp(passwordEncoder.encode(requete.getMdp()));
        utilisateurRepository.save(utilisateur);

        return new AuthResponseDTO(jwtService.genererToken(utilisateur, false));
    }

    // Connecter un utilisateur existant
    public AuthResponseDTO connecterUtilisateur(LoginRequestDTO requete){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requete.getPseudo(), requete.getMdp())
        );

        UserDetails utilisateur = utilisateurRepository.findByPseudo(requete.getPseudo()).orElseThrow();
        boolean resterConnecte = requete.isResterConnecte();

        return new AuthResponseDTO(jwtService.genererToken(utilisateur, resterConnecte));
    }

    // Convertir un utilisateur en DTO
    public ProfilDTO convertirUtilisateurEnDTO(Utilisateur utilisateur){
        return new ProfilDTO(utilisateur.getNom(), utilisateur.getPrenom(), utilisateur.getPseudo(), utilisateur.getEmail());
    }

    // Modification d'un email
    public ProfilDTO modifierEmail(Utilisateur utilisateur, ModificationEmailDTO requete){
        Optional<Utilisateur> existant = utilisateurRepository.findByEmailIgnoreCase(requete.getEmail());

        if(existant.isPresent() && existant.get().getIdUser() !=  utilisateur.getIdUser()){
            throw new BusinessException("Cet email est déjà utilisé.");
        }

        utilisateur.setEmail(requete.getEmail());
        utilisateurRepository.save(utilisateur);

        return convertirUtilisateurEnDTO(utilisateur);
    }
}
