package fr.celine.suivideseries.dto;

public class ModifierLivreDTO {
    String titre;
    String auteur;
    String isbn;
    int numeroDansLaSerie;

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getNumeroDansLaSerie() {
        return numeroDansLaSerie;
    }
}
