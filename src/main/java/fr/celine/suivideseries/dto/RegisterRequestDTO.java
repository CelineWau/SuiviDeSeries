package fr.celine.suivideseries.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequestDTO {
    @NotBlank(message = "Le nom est obligatoire.")
    @Size(max = 100, message = "Le nom doit avoir moins de 100 caractères.")
    private String nom;
    @NotBlank(message = "Le prénom est obligatoire.")
    @Size(max = 100, message = "Le prénom doit avoir moins 100 caractères.")
    private String prenom;
    @NotBlank(message = "Le pseudo est obligatoire.")
    @Size(min = 3, max = 50, message = "Le pseudo doit avoir entre 3 et 50 caractères.")
    private String pseudo;
    @NotBlank(message = "L'email est obligatoire.")
    @Email(message = "L'email doit avoir une forme valide.")
    private String email;
    @NotBlank(message = "Le mot de passe est obligatoire.")
    @Size(min = 8, max = 72, message = "Le mot de passe doit avoir entre 8 et 72 caractères.")
    private String mdp;

    public RegisterRequestDTO(String nom, String prenom, String pseudo, String email, String mdp) {
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.email = email;
        this.mdp = mdp;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getEmail() {
        return email;
    }

    public String getMdp() {
        return mdp;
    }
}
