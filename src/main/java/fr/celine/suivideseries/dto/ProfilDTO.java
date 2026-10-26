package fr.celine.suivideseries.dto;

public class ProfilDTO {
    private final String nom;
    private final String prenom;
    private final String pseudo;
    private final String email;

    public ProfilDTO(String nom, String prenom, String pseudo, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.email = email;
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
}