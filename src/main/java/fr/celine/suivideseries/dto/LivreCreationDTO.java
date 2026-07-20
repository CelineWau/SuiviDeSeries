package fr.celine.suivideseries.dto;

import fr.celine.suivideseries.enums.FormatLivre;
import fr.celine.suivideseries.enums.StatutLivre;
import fr.celine.suivideseries.enums.StatutPublication;

import java.time.LocalDate;

public class LivreCreationDTO {

    String auteur;
    String titre;
    String isbn;
    int numeroDansLaSerie;
    StatutLivre statutLivre;
    StatutPublication statutPublication;
    FormatLivre formatLivre;
    LocalDate dateAcquisition;
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

    public FormatLivre getFormatLivre() {
        return formatLivre;
    }

    public LocalDate getDateAcquisition() {
        return dateAcquisition;
    }

    public int getSerieId() {
        return serieId;
    }
}
