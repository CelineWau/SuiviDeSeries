package fr.celine.suivideseries.dto;

public class ErreurDTO {
    String message;

    public ErreurDTO(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
