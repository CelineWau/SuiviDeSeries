package fr.celine.suivideseries.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.celine.suivideseries.enums.StatutLivre;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_livre")
    private int idLivre;

    @NotNull(message = "L'auteur du livre est obligatoire")
    @Column(nullable = false)
    private String auteur;

    @NotNull(message = "Le titre du livre est obligatoire")
    @Column(nullable = false)
    private String titre;

    @NotNull(message = "L'ISBN du livre est obligatoire")
    @Column(nullable = false, unique = true)
    private String isbn;

    @NotNull(message = "Le statut du livre est obligatoire")
    @Column(nullable = false)
    private StatutLivre statutLivre;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_serie")
    private Serie serie;

    public Livre(){}

    public Livre(String auteur, String titre, String isbn, StatutLivre statutLivre, Serie serie) {
        this.auteur = auteur;
        this.titre = titre;
        this.isbn = isbn;
        this.statutLivre = statutLivre;
        this.serie = serie;
    }

    public int getIdLivre() {
        return idLivre;
    }

    public void setIdLivre(int idLivre) {
        this.idLivre = idLivre;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public StatutLivre getStatutLivre () {
        return statutLivre;
    }

    public void setStatutLivre (StatutLivre statutLivre) {
        this.statutLivre = statutLivre;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie (Serie serie) {
        this.serie = serie;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Livre livre)) return false;
        return idLivre == livre.idLivre && Objects.equals(auteur, livre.auteur) && Objects.equals(titre, livre.titre) && Objects.equals(isbn, livre.isbn) && statutLivre == livre.statutLivre && Objects.equals(serie, livre.serie);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLivre, auteur, titre, isbn, statutLivre, serie);
    }

    @Override
    public String toString() {
        return "Livre{" +
                "idLivre=" + idLivre +
                ", auteur='" + auteur + '\'' +
                ", titre='" + titre + '\'' +
                ", isbn='" + isbn + '\'' +
                ", statutLivre=" + statutLivre +
                ", serie=" + serie +
                '}';
    }
}
