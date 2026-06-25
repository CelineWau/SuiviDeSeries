package fr.celine.suivideseries.dto;

import fr.celine.suivideseries.enums.StatutSerie;

public class SerieCreationDTO {

    int utilisateurId;
    String nom;
    StatutSerie statutSerie;
    int nombreLivreTotal;

    public int getUtilisateurId() {
        return utilisateurId;
    }

    public String getNom (){
        return nom;
    }

    public StatutSerie getStatutSerie() {
        return statutSerie;
    }

    public int getNombreLivreTotal() {
        return nombreLivreTotal;
    }
}
