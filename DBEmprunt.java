package database;

import classes.Emprunt;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DBEmprunt {
    
    public void ajouterEmprunt(Emprunt e) {
        String sql = "INSERT INTO emprunt (isbnLivre, telephoneAdherent, dateEmprunt, dateRetour) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, e.getIsbnLivre());
            ps.setString(2, e.getTelephoneAdherent());
            ps.setDate(3, Date.valueOf(e.getDateEmprunt()));
            if (e.getDateRetour() != null) {
                ps.setDate(4, Date.valueOf(e.getDateRetour()));
            } else {
                ps.setNull(4, Types.DATE);
            }
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void enregistrerRetour(int idEmprunt, LocalDate dateRetour) {
        String sql = "UPDATE emprunt SET dateRetour=? WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(dateRetour));
            ps.setInt(2, idEmprunt);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void supprimerEmprunt(int idEmprunt) {
        String sql = "DELETE FROM emprunt WHERE id=?";
        try (PreparedStatement ps = DBConnection.getConnection().prepareStatement(sql)) {
            ps.setInt(1, idEmprunt);
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public static List<Emprunt> getAllEmprunts() {
        List<Emprunt> list = new ArrayList<>();
        String sql = "SELECT * FROM emprunt";
        try (Statement st = DBConnection.getConnection().createStatement()) {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Date dRetour = rs.getDate("dateRetour");
                list.add(new Emprunt(
                        rs.getInt("id"),
                        rs.getString("isbnLivre"),
                        rs.getString("telephoneAdherent"),
                        rs.getDate("dateEmprunt").toLocalDate(),
                        dRetour != null ? dRetour.toLocalDate() : null
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }
}