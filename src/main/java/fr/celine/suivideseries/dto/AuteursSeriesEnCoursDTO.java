package fr.celine.suivideseries.dto;

public class AuteursSeriesEnCoursDTO {
    String auteur;
    long nombreSeries;

    public AuteursSeriesEnCoursDTO(String auteur, long nombreSeries) {
        this.auteur = auteur;
        this.nombreSeries = nombreSeries;
    }

    public String getAuteur() {
        return auteur;
    }

    public long getNombreSeries() {
        return nombreSeries;
    }
}
