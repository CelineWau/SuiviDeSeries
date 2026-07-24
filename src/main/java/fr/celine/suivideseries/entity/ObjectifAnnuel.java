package fr.celine.suivideseries.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

@Entity
@Table
public class ObjectifAnnuel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_objectif")
    private int idObjectif;

    @NotNull(message = "L'année est obligatoire.")
    @Column(nullable = false)
    private int annee;

    @NotNull(message = "L'objectif est obligatoire.")
    @Min(value = 1, message = "L'objectif doit être d'au moins 1 série.")
    @Max(value = 50, message = "L'objectif ne peut pas dépasser 50 séries par an.")
    @Column(nullable = false)
    private int valeurObjectif;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_user")
    private Utilisateur utilisateur;

    public ObjectifAnnuel() {}

    public ObjectifAnnuel(int annee, int valeurObjectif, Utilisateur utilisateur) {
        this.annee = annee;
        this.valeurObjectif = valeurObjectif;
        this.utilisateur = utilisateur;
    }

    public int getIdObjectif() {
        return idObjectif;
    }

    public void setIdObjectif(int idObjectif) {
        this.idObjectif = idObjectif;
    }

    public int getAnnee() {
        return annee;
    }

    public void setAnnee(int annee) {
        this.annee = annee;
    }

    public int getValeurObjectif() {
        return valeurObjectif;
    }

    public void setValeurObjectif(int valeurObjectif) {
        this.valeurObjectif = valeurObjectif;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public String toString() {
        return "ObjectifAnnuel{" +
                "idObjectif=" + idObjectif +
                ", annee=" + annee +
                ", valeurObjectif=" + valeurObjectif +
                ", utilisateur=" + utilisateur +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ObjectifAnnuel that)) return false;
        return idObjectif == that.idObjectif && annee == that.annee && valeurObjectif == that.valeurObjectif && Objects.equals(utilisateur, that.utilisateur);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idObjectif, annee, valeurObjectif, utilisateur);
    }
}
