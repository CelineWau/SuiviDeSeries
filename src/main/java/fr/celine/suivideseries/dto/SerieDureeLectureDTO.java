package fr.celine.suivideseries.dto;

public class SerieDureeLectureDTO {
    private String nom;
    private double dureeLecture;

    public SerieDureeLectureDTO(String nom,  double dureeLecture) {
        this.nom = nom;
        this.dureeLecture = dureeLecture;
    }

    public String getNom() {
        return nom;
    }

    public double getDureeLecture() {
        return dureeLecture;
    }
}