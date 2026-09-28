package fr.celine.suivideseries.dto;

public class RegisterRequestDTO {
    private String nom;
    private String prenom;
    private String pseudo;
    private String email;
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
