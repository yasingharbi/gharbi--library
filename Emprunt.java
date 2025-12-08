package classes;

import java.time.LocalDate;

public class Emprunt {
    private int id;
    private String isbnLivre;
    private String telephoneAdherent;
    private LocalDate dateEmprunt;
    private LocalDate dateRetour;

    public Emprunt(int id, String isbnLivre, String telephoneAdherent, LocalDate dateEmprunt, LocalDate dateRetour) {
        this.id = id;
        this.isbnLivre = isbnLivre;
        this.telephoneAdherent = telephoneAdherent;
        this.dateEmprunt = dateEmprunt;
        this.dateRetour = dateRetour;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsbnLivre() {
        return isbnLivre;
    }

    public void setIsbnLivre(String isbnLivre) {
        this.isbnLivre = isbnLivre;
    }

    public String getTelephoneAdherent() {
        return telephoneAdherent;
    }

    public void setTelephoneAdherent(String telephoneAdherent) {
        this.telephoneAdherent = telephoneAdherent;
    }

    public LocalDate getDateEmprunt() {
        return dateEmprunt;
    }

    public void setDateEmprunt(LocalDate dateEmprunt) {
        this.dateEmprunt = dateEmprunt;
    }

    public LocalDate getDateRetour() {
        return dateRetour;
    }

    public void setDateRetour(LocalDate dateRetour) {
        this.dateRetour = dateRetour;
    }

    @Override
    public String toString() {
        return "Emprunt: Livre ISBN=" + isbnLivre + ", Adhérent=" + telephoneAdherent +
                ", Date Emprunt=" + dateEmprunt + ", Date Retour=" + dateRetour;
    }
}
