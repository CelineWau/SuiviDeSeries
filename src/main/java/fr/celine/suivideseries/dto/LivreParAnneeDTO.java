package fr.celine.suivideseries.dto;

public class LivreParAnneeDTO {
    private long nombre;
    private int annee;

    public LivreParAnneeDTO(long nombre, int annee) {
        this.nombre = nombre;
        this.annee = annee;
    }

    public long getNombre() {
        return nombre;
    }

    public int getAnnee() {
        return annee;
    }
}
