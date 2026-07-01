package fr.celine.suivideseries.dto;

import fr.celine.suivideseries.entity.Serie;
import fr.celine.suivideseries.enums.StatutLivre;

public class LivreCreationDTO {

    String auteur;
    String titre;
    String isbn;
    StatutLivre statutLivre;
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

    public StatutLivre getStatutLivre() {
        return statutLivre;
    }

    public int getSerieId() {
        return serieId;
    }
}
