package fr.celine.suivideseries.dto;

public class SerieASurveillerDTO {
    int idSerie;
    String nom;
    String auteur;

    public SerieASurveillerDTO(int idSerie, String nom, String auteur) {
        this.idSerie = idSerie;
        this.nom = nom;
        this.auteur = auteur;
    }

    public int getIdSerie() {
        return idSerie;
    }

    public String getNom() {
        return nom;
    }

    public String getAuteur() {
        return auteur;
    }
}
