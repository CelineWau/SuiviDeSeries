package fr.celine.suivideseries.dto;

import fr.celine.suivideseries.enums.StatutPublication;
import fr.celine.suivideseries.enums.StatutSerie;

public class SerieCreationDTO {

    int utilisateurId;
    String nom;
    StatutSerie statutSerie;
    StatutPublication statutPublication;
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

    public StatutPublication getStatutPublication() {
        return statutPublication;
    }

    public int getNombreLivreTotal() {
        return nombreLivreTotal;
    }
}
