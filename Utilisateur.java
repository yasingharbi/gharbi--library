package classes;

import java.time.LocalDate;

public class Utilisateur {
    private String username;
    private String password;
    private String role;
    private LocalDate createdDate;

    public Utilisateur(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.createdDate = LocalDate.now();

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
    public String getCreatedDate() {
        return ""+createdDate;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
