package fr.celine.suivideseries.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private int idUser;

    @NotNull(message = "Le nom est obligatoire")
    @Size(message = "Le nom ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String nom;

    @NotNull(message = "Le prénom est obligatoire")
    @Size(message = "Le prénom ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String prenom;

    @NotNull(message = "Le pseudo est obligatoire")
    @Size(message = "Le pseudo ne peut pas dépasser 100 caractères")
    @Column(nullable = false, length = 100)
    private String pseudo;

    @NotNull(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String mdp;

    @NotNull(message = "L'email est obligatoire")
    @Email(message = "L'email n'est pas valide")
    @Column(nullable = false)
    private String email;

    @Column
    @ManyToMany
    private List<Serie> serie = new ArrayList<>();

    public Utilisateur (){
    }

    public Utilisateur(String nom, String prenom, String pseudo, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.email = email;
    }

    public Utilisateur(String nom, String prenom, String pseudo, String email, List<Serie> serie) {
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.email = email;
        this.serie = serie;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public void setMdp(String mdp) {
        this.mdp = mdp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Serie> getSerie() {
        return serie;
    }

    public void setSerie(List<Serie> serie) {
        this.serie = serie;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Utilisateur that)) return false;
        return idUser == that.idUser && Objects.equals(nom, that.nom) && Objects.equals(prenom, that.prenom) && Objects.equals(pseudo, that.pseudo) && Objects.equals(mdp, that.mdp) && Objects.equals(email, that.email) && Objects.equals(serie, that.serie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser, nom, prenom, pseudo, mdp, email, serie);
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "idUser=" + idUser +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", pseudo='" + pseudo + '\'' +
                ", mdp='" + mdp + '\'' +
                ", email='" + email + '\'' +
                ", serie=" + serie +
                '}';
    }
}