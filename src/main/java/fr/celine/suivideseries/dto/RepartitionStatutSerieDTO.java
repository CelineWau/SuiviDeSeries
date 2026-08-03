package fr.celine.suivideseries.dto;

public class RepartitionStatutSerieDTO {
    long enCours;
    long terminees;
    long abandonnees;

    public RepartitionStatutSerieDTO(long enCours, long terminees, long abandonnees) {
        this.enCours = enCours;
        this.terminees = terminees;
        this.abandonnees = abandonnees;
    }

    public  long getEnCours() {
        return enCours;
    }

    public long getTerminees() {
        return terminees;
    }

    public long getAbandonnees() {
        return abandonnees;
    }
}
