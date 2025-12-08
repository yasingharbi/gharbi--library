package database;

import classes.Livre;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBLivre {

    public boolean ajouterLivre(Livre livre) {
        String sql = "INSERT INTO livre (isbn, titre, auteur, categorie, disponible, quantite) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, livre.getIsbn());
            ps.setString(2, livre.getTitre());
            ps.setString(3, livre.getAuteur());
            ps.setString(4, livre.getCategorie());
            ps.setBoolean(5, livre.isDisponible());
            ps.setInt(6, livre.getQuantite()); 
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            if (e.getMessage().contains("Duplicate entry")) {
                System.out.println("⚠ ISBN déjà existant !");
                return false;
            }
            e.printStackTrace();
            return false;
        }
    }

    public void modifierLivre(Livre livre) {
        String sql = "UPDATE livre SET titre=?, auteur=?, categorie=?, disponible=?, quantite=? WHERE isbn=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, livre.getTitre());
            ps.setString(2, livre.getAuteur());
            ps.setString(3, livre.getCategorie());
            ps.setBoolean(4, livre.isDisponible());
            ps.setInt(5, livre.getQuantite());
            ps.setString(6, livre.getIsbn());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void supprimerLivre(String isbn) {
        String sql = "DELETE FROM livre WHERE isbn=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, isbn);
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ DBLivre.supprimerLivre: " + rowsAffected + " row(s) deleted for ISBN: " + isbn);
        } catch (SQLException e) {
            System.err.println("❌ Error in DBLivre.supprimerLivre for ISBN: " + isbn);
            e.printStackTrace();
        }
    }

    public Livre rechercherLivreParISBN(String isbn) {
        String sql = "SELECT * FROM livre WHERE isbn=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getBoolean("disponible"),
                        rs.getInt("quantite")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Livre> getAllLivres() {
        List<Livre> livres = new ArrayList<>();
        String sql = "SELECT * FROM livre";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                livres.add(new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getBoolean("disponible"),
                        rs.getInt("quantite") 
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return livres;
    }

    public static Livre getLivreByISBN(String isbn) {
        String sql = "SELECT * FROM livre WHERE isbn = ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getBoolean("disponible"),
                        rs.getInt("quantite") 
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<Livre> getLivresByTitre(String titre) {
        List<Livre> list = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE titre LIKE ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + titre + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getBoolean("disponible"),
                        rs.getInt("quantite")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<Livre> getLivresByAuteur(String auteur) {
        List<Livre> list = new ArrayList<>();
        String sql = "SELECT * FROM livre WHERE auteur LIKE ?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, "%" + auteur + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new Livre(
                        rs.getString("isbn"),
                        rs.getString("titre"),
                        rs.getString("auteur"),
                        rs.getString("categorie"),
                        rs.getBoolean("disponible"),
                        rs.getInt("quantite") 
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<String> getAllIsbn() {
        List<String> list = new ArrayList<>();
        String sql = "SELECT isbn FROM livre";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(rs.getString("isbn"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean isDisponible(String isbn) {
        String sql = "SELECT disponible FROM livre WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getBoolean("disponible");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static int countLivres() {
        try (var st = DBConnection.getConnection().createStatement();
             var rs = st.executeQuery("SELECT COUNT(*) FROM livre")) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) { e.printStackTrace(); return 0; }
    }

}
