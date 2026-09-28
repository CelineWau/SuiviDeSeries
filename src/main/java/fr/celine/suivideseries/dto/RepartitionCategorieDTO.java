package fr.celine.suivideseries.dto;

public class RepartitionCategorieDTO {
    String nom;
    long nombreSerie;

    public RepartitionCategorieDTO(String nom, long nombreSerie) {
        this.nom = nom;
        this.nombreSerie = nombreSerie;
    }

    public String getNom() {
        return nom;
    }

    public long getNombreSerie() {
        return nombreSerie;
    }
}
