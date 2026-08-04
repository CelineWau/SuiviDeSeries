package fr.celine.suivideseries.dto;

public class TailleSerieDTO {
    long petites;
    long moyennes;
    long sagas;

    public TailleSerieDTO(long petites, long moyennes, long sagas) {
        this.petites = petites;
        this.moyennes = moyennes;
        this.sagas = sagas;
    }

    public long getPetites() {
        return petites;
    }

    public long getMoyennes() {
        return moyennes;
    }

    public long getSagas() {
        return sagas;
    }
}
