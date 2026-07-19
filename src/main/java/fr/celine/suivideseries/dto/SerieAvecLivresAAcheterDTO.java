package fr.celine.suivideseries.dto;

public class SerieAvecLivresAAcheterDTO {
    private int idSerie;
    private String nom;
    private int nombreLivreAAcheter;

    public SerieAvecLivresAAcheterDTO(int  idSerie, String nom, int nombreLivreAAcheter) {
        this.idSerie = idSerie;
        this.nom = nom;
        this.nombreLivreAAcheter = nombreLivreAAcheter;
    }

    public int getIdSerie() {
        return idSerie;
    }

    public String getNom() {
        return nom;
    }
    public int getNombreLivreAAcheter() {
        return nombreLivreAAcheter;
    }
}
