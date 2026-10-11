package fr.celine.suivideseries.dto;

public class LoginRequestDTO {

    private String pseudo;
    private String mdp;
    private boolean resterConnecte;

    public LoginRequestDTO(String pseudo, String mdp, boolean resterConnecte) {
        this.pseudo = pseudo;
        this.mdp = mdp;
        this.resterConnecte = resterConnecte;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getMdp() {
        return mdp;
    }

    public boolean isResterConnecte() {
        return resterConnecte;
    }
}
