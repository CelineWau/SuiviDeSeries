package fr.celine.suivideseries.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.celine.suivideseries.enums.FormatLivre;
import fr.celine.suivideseries.enums.StatutLivre;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;
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

    @NotNull(message = "Le numéro du livre dans la série est obligatoire.")
    @Column(nullable = false)
    private int numeroDansLaSerie;

    @Column
    @ColumnDefault("0")
    private int nombreDePages;

    @Min(value = 0, message = "La note minimale ne peut pas être en dessous de 0.")
    @Max(value = 5, message = "La note maximale ne peut pas dépasser 5.")
    @Column
    @ColumnDefault("0")
    private int note;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le statut du livre est obligatoire.")
    @Column(nullable = false)
    private StatutLivre statutLivre;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Le format du livre est obligatoire.")
    @Column(nullable = false)
    private FormatLivre formatLivre;

    @Column
    private LocalDate dateAcquisition;

    @Column
    private LocalDate dateLecture;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_serie")
    private Serie serie;

    public Livre(){}

    public Livre(String auteur, String titre, String isbn, int numeroDansLaSerie, StatutLivre statutLivre, FormatLivre formatLivre, LocalDate dateAcquisition, LocalDate dateLecture,
                 Serie serie) {
        this.auteur = auteur;
        this.titre = titre;
        this.isbn = isbn;
        this.numeroDansLaSerie = numeroDansLaSerie;
        this.statutLivre = statutLivre;
        this.formatLivre = formatLivre;
        this.dateAcquisition = dateAcquisition;
        this.dateLecture = dateLecture;
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

    public int getNumeroDansLaSerie() {
        return numeroDansLaSerie;
    }

    public void setNumeroDansLaSerie(int numeroDansLaSerie) {
        this.numeroDansLaSerie = numeroDansLaSerie;
    }

    public StatutLivre getStatutLivre () {
        return statutLivre;
    }

    public void setStatutLivre (StatutLivre statutLivre) {
        this.statutLivre = statutLivre;
    }

    public FormatLivre getFormatLivre () {
        return formatLivre;
    }

    public void setFormatLivre (FormatLivre formatLivre) {
        this.formatLivre = formatLivre;
    }

    public LocalDate getDateAcquisition() {
        return dateAcquisition;
    }

    public void setDateAcquisition(LocalDate dateAcquisition) {
        this.dateAcquisition = dateAcquisition;
    }

    public LocalDate getDateLecture() {
        return dateLecture;
    }

    public void setDateLecture(LocalDate dateLecture) {
        this.dateLecture = dateLecture;
    }

    public Serie getSerie() {
        return serie;
    }

    public void setSerie (Serie serie) {
        this.serie = serie;
    }

    public int getNombreDePages() {
        return nombreDePages;
    }

    public void setNombreDePages(int nombreDePages) {
        this.nombreDePages = nombreDePages;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Livre livre = (Livre) o;
        return idLivre == livre.idLivre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLivre);
    }

    @Override
    public String toString() {
        return "Livre{" +
                "idLivre=" + idLivre +
                ", auteur='" + auteur + '\'' +
                ", titre='" + titre + '\'' +
                ", isbn='" + isbn + '\'' +
                ", numeroDansLaSerie=" + numeroDansLaSerie + '\'' +
                ", statutLivre=" + statutLivre +
                ", formatLivre=" + formatLivre +
                ", dateAcquisition=" + dateAcquisition +
                ", dateLecture=" + dateLecture +
                ", serie=" + (serie != null ? serie.getIdSerie() : null) +
                ", nombreDePages=" + nombreDePages +
                ", note=" + note +
                '}';
    }
}