package fr.celine.suivideseries.dto;

public class LivreAAcheterDTO {
    String nom;
    String auteur;
    String nomSerie;
    int tome;

    public LivreAAcheterDTO(String nom, String auteur, String nomSerie, int  tome) {
        this.nom = nom;
        this.auteur = auteur;
        this.nomSerie = nomSerie;
        this.tome = tome;
    }

    public String getNom() {
        return nom;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getNomSerie() {
        return nomSerie;
    }

    public int getTome() {
        return tome;
    }
}