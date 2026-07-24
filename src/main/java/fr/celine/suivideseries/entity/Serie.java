package fr.celine.suivideseries.entity;

import fr.celine.suivideseries.enums.StatutPublication;
import fr.celine.suivideseries.enums.StatutSerie;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table
public class Serie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_serie")
    private int idSerie;

    @NotNull(message = "Le nom de la série est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotNull(message = "Le nombre de livre total d'une série est obligatoire")
    @Column(nullable = false)
    private int nombreLivreTotal;

    @Column
    private LocalDate dateFin;

    @NotNull(message = "La série doit être attribuée à un utilisateur")
    @Column(nullable = false)
    @ManyToMany
    @JoinTable(
            name = "serie_utilisateur",
            joinColumns = @JoinColumn(name = "id_serie"),
            inverseJoinColumns = @JoinColumn(name = "id_user")
    )
    private List<Utilisateur> utilisateur = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @NotNull(message = "La série doit avoir un statut.")
    @Column(name = "statut_serie", nullable = false)
    private StatutSerie statutSerie;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "La série doit avoir un statut de publication")
    @Column(name = "statut_publication",  nullable = false)
    private StatutPublication  statutPublication;

    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL)
    private List<Livre> livres = new ArrayList<>();

    public Serie(String nom, List<Utilisateur> utilisateur, StatutSerie statutSerie, StatutPublication statutPublication, int nombreLivreTotal) {
        this.nom = nom;
        this.nombreLivreTotal = nombreLivreTotal;
        this.utilisateur = utilisateur;
        this.statutSerie = statutSerie;
        this.statutPublication = statutPublication;
    }

    public Serie(String nom, Utilisateur utilisateur, StatutSerie statutSerie, StatutPublication statutPublication, int nombreLivreTotal) {
        this.nom = nom;
        this.nombreLivreTotal = nombreLivreTotal;
        this.utilisateur = new ArrayList<>();
        this.utilisateur.add(utilisateur);
        this.statutSerie = statutSerie;
        this.statutPublication = statutPublication;
    }

    public Serie() {
    }

    public int getIdSerie() {
        return idSerie;
    }

    public void setIdSerie(int idSerie) {
        this.idSerie = idSerie;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public int getNombreLivreTotal() {
        return nombreLivreTotal;
    }

    public void setNombreLivreTotal(int nombreLivreTotal) {
        this.nombreLivreTotal = nombreLivreTotal;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public List<Utilisateur> getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(List<Utilisateur> utilisateur) {
        this.utilisateur = utilisateur;
    }

    public StatutSerie getStatutSerie() {
        return statutSerie;
    }

    public void setStatutSerie(StatutSerie statutSerie) {
        this.statutSerie = statutSerie;
    }

    public StatutPublication getStatutPublication() {
        return statutPublication;
    }

    public void setStatutPublication(StatutPublication statutPublication) {
        this.statutPublication = statutPublication;
    }

    public List<Livre> getLivres(){
        return livres;
    }

    public void setLivres(List<Livre> livres) {
        this.livres = livres;
    }

    @Override
    public String toString() {
        return "Serie{" +
                "idSerie=" + idSerie +
                ", nom='" + nom + '\'' +
                ", nombreLivreTotal=" + nombreLivreTotal +
                ", dateFin=" + dateFin +
                ", utilisateur=" + utilisateur +
                ", statutSerie=" + statutSerie +
                ", statutPublication=" + statutPublication +
                ", livres" + livres +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Serie serie)) return false;
        return idSerie == serie.idSerie && nombreLivreTotal == serie.nombreLivreTotal && Objects.equals(nom, serie.nom) && Objects.equals(dateFin, serie.dateFin) && Objects.equals(utilisateur, serie.utilisateur) && statutSerie == serie.statutSerie && statutPublication == serie.statutPublication && Objects.equals(livres, serie.livres);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idSerie, nom, nombreLivreTotal, dateFin, utilisateur, statutSerie, statutPublication, livres);
    }
}
