package fr.celine.suivideseries.dto;

public class NombreParAnneeDTO {
    private long nombre;
    private int annee;

    public NombreParAnneeDTO(long nombre, int annee) {
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
