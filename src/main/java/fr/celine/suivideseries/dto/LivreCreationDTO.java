package fr.celine.suivideseries.dto;

import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutPublication;

public class LivreCreationDTO {

    String auteur;
    String titre;
    String isbn;
    int numeroDansLaSerie;
    StatutLivre statutLivre;
    StatutPublication statutPublication;
    int serieId;

    public String getAuteur() {
        return auteur;
    }

    public String getTitre() {
        return titre;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getNumeroDansLaSerie() {
        return numeroDansLaSerie;
    }

    public StatutLivre getStatutLivre() {
        return statutLivre;
    }

    public StatutPublication getStatutPublication() {
        return statutPublication;
    }

    public int getSerieId() {
        return serieId;
    }
}
