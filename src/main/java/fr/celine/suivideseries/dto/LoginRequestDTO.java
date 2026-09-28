package fr.celine.suivideseries.dto;

public class LoginRequestDTO {

    private String pseudo;
    private String mdp;

    public LoginRequestDTO(String pseudo, String mdp) {
        this.pseudo = pseudo;
        this.mdp = mdp;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getMdp() {
        return mdp;
    }
}
