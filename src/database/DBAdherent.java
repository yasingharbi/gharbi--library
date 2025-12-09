package database;

import classes.Adherent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBAdherent {
    public void ajouterAdherent(Adherent a) {
        String sql = "INSERT INTO adherent (telephone, nom, prenom, dateNaissance, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, a.getTelephone());
            ps.setString(2, a.getNom());
            ps.setString(3, a.getPrenom());
            ps.setDate(4, Date.valueOf(a.getDateNaissance()));
            ps.setString(5, a.getType());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void modifierAdherent(Adherent a) {
        String sql = "UPDATE adherent SET nom=?, prenom=?, dateNaissance=?, type=? WHERE telephone=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setDate(3, Date.valueOf(a.getDateNaissance()));
            ps.setString(4, a.getType());
            ps.setString(5, a.getTelephone());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void supprimerAdherent(String telephone) {
        String sql = "DELETE FROM adherent WHERE telephone=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, telephone);
            int rowsAffected = ps.executeUpdate();
            System.out.println("✅ DBAdherent.supprimerAdherent: " + rowsAffected + " row(s) deleted for telephone: " + telephone);
        } catch (SQLException e) {
            System.err.println("❌ Error in DBAdherent.supprimerAdherent for telephone: " + telephone);
            e.printStackTrace();
        }
    }
    public Adherent rechercherParTelephone(String telephone) {
        String sql = "SELECT * FROM adherent WHERE telephone=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, telephone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Adherent(
                        rs.getString("telephone"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("dateNaissance").toLocalDate(),
                        rs.getString("type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public List<Adherent> getAllAdherents() {
        List<Adherent> list = new ArrayList<>();
        String sql = "SELECT * FROM adherent";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                list.add(new Adherent(
                        rs.getString("telephone"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("dateNaissance").toLocalDate(),
                        rs.getString("type")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static Adherent getAdherentByTelephone(String telephone) {
        String sql = "SELECT * FROM adherent WHERE telephone = ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, telephone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Adherent(
                        rs.getString("telephone"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("dateNaissance").toLocalDate(),
                        rs.getString("typeAdherent")
                );
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    public static List<Adherent> getAdherentsByNom(String nom) {
        List<Adherent> list = new ArrayList<>();
        String sql = "SELECT * FROM adherent WHERE nom LIKE ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, "%" + nom + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Adherent(
                        rs.getString("telephone"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("dateNaissance").toLocalDate(),
                        rs.getString("typeAdherent")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
    public static List<Adherent> getAdherentsByPrenom(String prenom) {
        List<Adherent> list = new ArrayList<>();
        String sql = "SELECT * FROM adherent WHERE prenom LIKE ?";

        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {

            ps.setString(1, "%" + prenom + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(new Adherent(
                        rs.getString("telephone"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getDate("dateNaissance").toLocalDate(),
                        rs.getString("typeAdherent")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }
   
	public List<String> getAllTelephones() {
		List<String> list = new ArrayList<>();
        String sql = "SELECT telephone FROM Adherent";
        try (Connection c = DBConnection.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(rs.getString("telephone"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
	}
	public static boolean changerTypeAdherent(String telephone, String nouveauType) {
	    String sql = "UPDATE adherent SET type = ? WHERE telephone = ?";
	    try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
	        ps.setString(1, nouveauType);
	        ps.setString(2, telephone);
	        return ps.executeUpdate() > 0;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	public static int countAdherents() {
	    try (var st = DBConnection.getConnection().createStatement();
	         var rs = st.executeQuery("SELECT COUNT(*) FROM adherent")) {
	        return rs.next() ? rs.getInt(1) : 0;
	    } catch (Exception e) { e.printStackTrace(); return 0; }
	}




}
