package fr.celine.suivideseries.dto;

public class RepartitionFormatDTO {
    long luEbook;
    long luPapier;
    long palEbook;
    long palPapier;

    public RepartitionFormatDTO(long luEbook, long luPapier, long palEbook, long palPapier) {
        this.luEbook = luEbook;
        this.luPapier = luPapier;
        this.palEbook = palEbook;
        this.palPapier = palPapier;
    }

    public long getLuEbook() {
        return luEbook;
    }

    public long getLuPapier() {
        return luPapier;
    }

    public long getPalEbook() {
        return palEbook;
    }

    public long getPalPapier() {
        return palPapier;
    }
}