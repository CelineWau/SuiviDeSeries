package fr.celine.suivideseries.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Table
@Entity
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_genre")
    private int id;

    @NotNull(message = "Le nom du genre est obligatoire.")
    @Column(nullable = false, unique = true)
    private String nom;

    @JsonIgnore
    @OneToMany(mappedBy = "genre")
    private List<Serie> series = new ArrayList<>();

    public Genre( String nom) {
        this.nom = nom;
    }

    public Genre() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Serie> getSeries() {
        return series;
    }

    public void setSeries(List<Serie> series) {
        this.series = series;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Genre genre)) return false;
        return id == genre.id && Objects.equals(nom, genre.nom) && Objects.equals(series, genre.series);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nom, series);
    }

    @Override
    public String toString() {
        return "Genre{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", series=" + series +
                '}';
    }
}
