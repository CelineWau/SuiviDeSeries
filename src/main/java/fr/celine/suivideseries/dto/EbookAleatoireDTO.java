package fr.celine.suivideseries.dto;

public class EbookAleatoireDTO {
    String titre;
    String auteur;
    String nomSerie;

    public EbookAleatoireDTO(String titre, String auteur, String nomSerie) {
        this.titre = titre;
        this.auteur = auteur;
        this.nomSerie = nomSerie;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getNomSerie() {
        return nomSerie;
    }
}
