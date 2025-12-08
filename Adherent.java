package classes;

import java.time.LocalDate;

public class Adherent {
    private String telephone;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String type;

    public Adherent(String telephone, String nom, String prenom, LocalDate dateNaissance, String type) {
        this.telephone = telephone;
        this.nom = nom;
        this.prenom = prenom;
        this.dateNaissance = dateNaissance;
        this.type = type;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
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

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return nom + " " + prenom + " (" + telephone + ") [" + type + "]{" + dateNaissance + "}";
    }
}
