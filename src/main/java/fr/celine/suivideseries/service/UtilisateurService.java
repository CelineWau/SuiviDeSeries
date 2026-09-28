package fr.celine.suivideseries.service;

import fr.celine.suivideseries.dto.AuthResponseDTO;
import fr.celine.suivideseries.dto.LoginRequestDTO;
import fr.celine.suivideseries.dto.RegisterRequestDTO;
import fr.celine.suivideseries.dto.SerieCreationDTO;
import fr.celine.suivideseries.entity.Utilisateur;
import fr.celine.suivideseries.exception.BusinessException;
import fr.celine.suivideseries.repository.UtilisateurRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    // Trouver un utilisateur par id
    public Utilisateur trouverUtilisateurParId(int id){
        return utilisateurRepository.findById(id).orElseThrow();
    }

    // Inscrire un nouvel utilisateur
    public AuthResponseDTO inscrireUtilisateur(RegisterRequestDTO requete) {
        if(utilisateurRepository.findByPseudo(requete.getPseudo()).isPresent()){
            throw new BusinessException("Ce pseudo existe déjà");
        }

        Utilisateur utilisateur = new Utilisateur(requete.getNom(), requete.getPrenom(), requete.getPseudo(), requete.getEmail());
        utilisateur.setMdp(passwordEncoder.encode(requete.getMdp()));
        utilisateurRepository.save(utilisateur);

        return new AuthResponseDTO(jwtService.genererToken(utilisateur));
    }

    // Connecter un utilisateur existant
    public AuthResponseDTO connecterUtilisateur(LoginRequestDTO requete){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requete.getPseudo(), requete.getMdp())
        );

        UserDetails utilisateur = utilisateurRepository.findByPseudo(requete.getPseudo()).orElseThrow();

        return new AuthResponseDTO(jwtService.genererToken(utilisateur));
    }
}
