package main;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import database.*;
import classes.*;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class BibliothequeService {
    private DBLivre livre;
    private DBAdherent adherent;
    private DBEmprunt emprunt;
    public BibliothequeService() {
    	livre = new DBLivre();
    	adherent = new DBAdherent();
    	emprunt = new DBEmprunt();
    }
    public void ajouterLivre(Livre l) {
    	livre.ajouterLivre(l);
    }
    public void modifierLivre(Livre l) {
    	livre.modifierLivre(l);
    }
    public void supprimerLivre(String isbn) {
    	livre.supprimerLivre(isbn); 
    }
    public Livre rechercherLivre(String isbn) {
    	return livre.rechercherLivreParISBN(isbn); 
    }
    public List<Livre> getAllLivres() {
    	return livre.getAllLivres(); 
    }
    public void ajouterAdherent(Adherent a) {
    	adherent.ajouterAdherent(a); }
    public void modifierAdherent(Adherent a) {
    	adherent.modifierAdherent(a); 
    }
    public void supprimerAdherent(String tel) { 
    	adherent.supprimerAdherent(tel); 
    }
    public Adherent rechercherAdherent(String tel) {
    	return adherent.rechercherParTelephone(tel); 
    }
    public List<Adherent> getAllAdherents() {
    	return adherent.getAllAdherents(); 
    }
    public void ajouterEmprunt(Emprunt e) {
        emprunt.ajouterEmprunt(e);
        Livre l = livre.rechercherLivreParISBN(e.getIsbnLivre());
        if (l != null) {
            l.setDisponible(false);
            livre.modifierLivre(l);
        }
    }


    public void enregistrerRetour(int idEmprunt, LocalDate dateRetour) {
    	emprunt.enregistrerRetour(idEmprunt, dateRetour);
        @SuppressWarnings("static-access")
		Emprunt e = emprunt.getAllEmprunts().stream().filter(em -> em.getId() == idEmprunt).findFirst().orElse(null);
        if (e != null) {
            Livre l = livre.rechercherLivreParISBN(e.getIsbnLivre());
            if (l != null) {
                l.setDisponible(true);
                livre.modifierLivre(l);
            }
        }
    }
    public List<Emprunt> getAllEmprunts() { 
    	@SuppressWarnings("static-access")
		List<Emprunt> allEmprunts = emprunt.getAllEmprunts();
		return allEmprunts; 
    }
    public Livre getLivreByISBN(String isbn) {
        return DBLivre.getLivreByISBN(isbn);
    }

    public List<Livre> getLivresByTitre(String titre) {
        return DBLivre.getLivresByTitre(titre);
    }

    public List<Livre> getLivresByAuteur(String auteur) {
        return DBLivre.getLivresByAuteur(auteur);
    }
    public Adherent getAdherentByTelephone(String tel) {
        return DBAdherent.getAdherentByTelephone(tel);
    }

    public List<Adherent> getAdherentsByNom(String nom) {
        return DBAdherent.getAdherentsByNom(nom);
    }

    public List<Adherent> getAdherentsByPrenom(String prenom) {
        return DBAdherent.getAdherentsByPrenom(prenom);
    }

    public static boolean updateAdminPassword(String newPassword) {
        String sql = "UPDATE utilisateur SET password=? WHERE username='admin'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Livre> searchLivres(String keyword) {
        return DBLivre.getLivresByTitre(keyword);
    }

    public List<Adherent> searchAdherents(String keyword) {
        return DBAdherent.getAdherentsByNom(keyword);
    }

    public List<Emprunt> searchEmprunts(String keyword) {
        return DBEmprunt.getAllEmprunts().stream()
            .filter(e -> e.getIsbnLivre().contains(keyword) || e.getTelephoneAdherent().contains(keyword))
            .collect(Collectors.toList());
    }
    public static String generateRandomPasswordStatic(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
        java.security.SecureRandom random = new java.security.SecureRandom();
        StringBuilder sb = new StringBuilder(length);
        for (int i=0;i<length;i++) sb.append(chars.charAt(random.nextInt(chars.length())));
        return sb.toString();
    }
    public List<String> getAllTelephonesAdherents() {
        return adherent.getAllTelephones();
    }

    public List<String> getAllIsbnLivres() {
        return livre.getAllIsbn();
    }
    public boolean changerTypeAdherent(String telephone, String nouveauType) {
        return DBAdherent.changerTypeAdherent(telephone, nouveauType);
    }
    public int countLivres()    { return DBLivre.countLivres(); }
    public int countAdherents() { return DBAdherent.countAdherents(); }
    public int countAdmins()    { return DBUtilisateur.countAdmins(); }

    public int countEmprunts() {
        int count = 0;

        String sql = "SELECT COUNT(*) FROM emprunt";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur countEmprunts() : " + e.getMessage());
        }

        return count;
    }
    
	
    public int countRetards() {
        int count = 0;

        String sql =
            "SELECT COUNT(*) FROM emprunt " +
            "WHERE date_retour < CURRENT_DATE AND date_retour_effective IS NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                count = rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Erreur countRetards() : " + e.getMessage());
        }

        return count;
    }

    public void supprimerEmprunt(int idEmprunt) {
        emprunt.supprimerEmprunt(idEmprunt);
    }
    public void supprimerLivre1(String isbn) {
        livre.supprimerLivre(isbn);
    }

    public void supprimerAdherent1(String tel) {
        adherent.supprimerAdherent(tel);
    }

}
