package fr.celine.suivideseries.dto;

import java.time.LocalDate;

public class SeriesDelaisseesDTO {
    String nom;
    LocalDate derniereLecture;

    public SeriesDelaisseesDTO(String nom, LocalDate derniereLecture) {
        this.nom = nom;
        this.derniereLecture = derniereLecture;
    }

    public String getNom() {
        return nom;
    }

    public LocalDate getDerniereLecture() {
        return derniereLecture;
    }
}
