package fr.celine.suivideseries.dto;

import java.time.LocalDate;

public class LivrePalVieillissantDTO {
    String titre;
    String auteur;
    String nomSerie;
    int numeroDansLaSerie;
    LocalDate dateAcquisition;

    public LivrePalVieillissantDTO (String titre, String auteur, String nomSerie, int numeroDansLaSerie, LocalDate dateAcquisition) {
        this.titre = titre;
        this.auteur = auteur;
        this.nomSerie = nomSerie;
        this.numeroDansLaSerie = numeroDansLaSerie;
        this.dateAcquisition = dateAcquisition;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getTitre() {
        return titre;
    }

    public String getNomSerie() {
        return nomSerie;
    }

    public int getNumeroDansLaSerie() {
        return numeroDansLaSerie;
    }

    public LocalDate getDateAcquisition() {
        return dateAcquisition;
    }
}
