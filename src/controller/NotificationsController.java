package controller;

import classes.Emprunt;
import classes.Livre;
import classes.Adherent;
import javafx.animation.FadeTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.BibliothequeService;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsController {

    @FXML private TableView<NotificationRecord> notificationsTable;
    @FXML private TableColumn<NotificationRecord, String> colType;
    @FXML private TableColumn<NotificationRecord, String> colMessage;
    @FXML private TableColumn<NotificationRecord, String> colDate;
    @FXML private TableColumn<NotificationRecord, String> colPriority;
    @FXML private Label totalNotificationsLabel;
    @FXML private Label urgentLabel;
    @FXML private Label warningLabel;
    @FXML private Label infoLabel;
    @FXML private Label statusLabel;
    @FXML private ComboBox<String> filterCombo;
    @FXML private TextField searchField;

    private final BibliothequeService service = new BibliothequeService();
    private ObservableList<NotificationRecord> allNotifications;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        setupTable();
        loadNotifications();
        setupFilter();
        updateStatistics();
        animateEntrance();
    }

    private void setupTable() {
        colType.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        colMessage.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMessage()));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDate()));
        colPriority.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPriority()));

        // Style priority column
        colPriority.setCellFactory(column -> new TableCell<NotificationRecord, String>() {
            @Override
            protected void updateItem(String priority, boolean empty) {
                super.updateItem(priority, empty);
                if (empty || priority == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(priority);
                    badge.setPadding(new javafx.geometry.Insets(4, 12, 4, 12));
                    badge.setStyle(
                        "-fx-background-radius: 10; -fx-font-weight: bold; -fx-font-size: 11px; " +
                        (priority.equals("URGENT") 
                            ? "-fx-background-color: #F44336; -fx-text-fill: white;" 
                            : priority.equals("WARNING")
                            ? "-fx-background-color: #FF9800; -fx-text-fill: white;"
                            : "-fx-background-color: #2196F3; -fx-text-fill: white;")
                    );
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        // Style type column
        colType.setCellFactory(column -> new TableCell<NotificationRecord, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                } else {
                    String icon = getIconForType(type);
                    setText(icon + " " + type);
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });
    }

    private String getIconForType(String type) {
        switch (type) {
            case "Retard": return "⚠️";
            case "Retour Attendu": return "📅";
            case "Livre Indisponible": return "❌";
            case "Nouveau Livre": return "📚";
            case "Membre Premium": return "👑";
            default: return "ℹ️";
        }
    }

    private void loadNotifications() {
        allNotifications = FXCollections.observableArrayList();
        
        // Check for overdue books
        List<Emprunt> overdueLoans = service.getAllEmprunts().stream()
            .filter(e -> e.getDateRetour() == null)
            .filter(e -> Period.between(e.getDateEmprunt(), LocalDate.now()).getDays() > 14)
            .collect(Collectors.toList());

        for (Emprunt loan : overdueLoans) {
            Adherent member = service.getAdherentByTelephone(loan.getTelephoneAdherent());
            Livre book = service.getLivreByISBN(loan.getIsbnLivre());
            if (member != null && book != null) {
                int daysLate = Period.between(loan.getDateEmprunt(), LocalDate.now()).getDays() - 14;
                allNotifications.add(new NotificationRecord(
                    "Retard",
                    member.getPrenom() + " " + member.getNom() + " - \"" + book.getTitre() + "\" (" + daysLate + " jours de retard)",
                    LocalDate.now().format(dateFormatter),
                    "URGENT"
                ));
            }
        }

        // Check for books due soon (next 3 days)
        List<Emprunt> dueSoon = service.getAllEmprunts().stream()
            .filter(e -> e.getDateRetour() == null)
            .filter(e -> {
                int days = Period.between(e.getDateEmprunt(), LocalDate.now()).getDays();
                return days >= 11 && days <= 14;
            })
            .collect(Collectors.toList());

        for (Emprunt loan : dueSoon) {
            Adherent member = service.getAdherentByTelephone(loan.getTelephoneAdherent());
            Livre book = service.getLivreByISBN(loan.getIsbnLivre());
            if (member != null && book != null) {
                int daysUntilDue = 14 - Period.between(loan.getDateEmprunt(), LocalDate.now()).getDays();
                allNotifications.add(new NotificationRecord(
                    "Retour Attendu",
                    member.getPrenom() + " " + member.getNom() + " - \"" + book.getTitre() + "\" (dans " + daysUntilDue + " jours)",
                    LocalDate.now().format(dateFormatter),
                    "WARNING"
                ));
            }
        }

        // Check for unavailable books
        List<Livre> unavailableBooks = service.getAllLivres().stream()
            .filter(l -> !l.isDisponible())
            .collect(Collectors.toList());

        for (Livre book : unavailableBooks) {
            allNotifications.add(new NotificationRecord(
                "Livre Indisponible",
                "\"" + book.getTitre() + "\" est actuellement emprunté",
                LocalDate.now().format(dateFormatter),
                "INFO"
            ));
        }

        // Check for new premium members (last 30 days)
        List<Adherent> premiumMembers = service.getAllAdherents().stream()
            .filter(a -> a.getType().equalsIgnoreCase("premium"))
            .collect(Collectors.toList());

        for (Adherent member : premiumMembers) {
            allNotifications.add(new NotificationRecord(
                "Membre Premium",
                member.getPrenom() + " " + member.getNom() + " est un membre Premium",
                LocalDate.now().format(dateFormatter),
                "INFO"
            ));
        }

        notificationsTable.setItems(allNotifications);
    }

    private void setupFilter() {
        if (filterCombo != null) {
            filterCombo.setItems(FXCollections.observableArrayList(
                "Tous", "URGENT", "WARNING", "INFO"
            ));
            filterCombo.setValue("Tous");
            filterCombo.setOnAction(e -> applyFilters());
        }

        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
    }

    private void applyFilters() {
        ObservableList<NotificationRecord> filtered = FXCollections.observableArrayList(allNotifications);

        // Filter by priority
        String priorityFilter = filterCombo.getValue();
        if (!priorityFilter.equals("Tous")) {
            filtered = filtered.stream()
                .filter(n -> n.getPriority().equals(priorityFilter))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        // Filter by search
        String search = searchField.getText();
        if (search != null && !search.trim().isEmpty()) {
            String lowerSearch = search.toLowerCase().trim();
            filtered = filtered.stream()
                .filter(n -> n.getMessage().toLowerCase().contains(lowerSearch) ||
                           n.getType().toLowerCase().contains(lowerSearch))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        }

        notificationsTable.setItems(filtered);
        updateStatistics();
    }

    private void updateStatistics() {
        int total = allNotifications.size();
        long urgent = allNotifications.stream().filter(n -> n.getPriority().equals("URGENT")).count();
        long warning = allNotifications.stream().filter(n -> n.getPriority().equals("WARNING")).count();
        long info = allNotifications.stream().filter(n -> n.getPriority().equals("INFO")).count();

        if (totalNotificationsLabel != null) totalNotificationsLabel.setText(String.valueOf(total));
        if (urgentLabel != null) urgentLabel.setText(String.valueOf(urgent));
        if (warningLabel != null) warningLabel.setText(String.valueOf(warning));
        if (infoLabel != null) infoLabel.setText(String.valueOf(info));
    }

    @FXML
    private void onRefresh() {
        loadNotifications();
        updateStatistics();
        setStatus("🔄 Notifications actualisées!", "#4CAF50");
    }

    @FXML
    private void onClearAll() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmer");
        confirm.setHeaderText("Effacer toutes les notifications?");
        confirm.setContentText("Cette action ne peut pas être annulée.");
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                allNotifications.clear();
                notificationsTable.setItems(allNotifications);
                updateStatistics();
                setStatus("✅ Notifications effacées", "#4CAF50");
            }
        });
    }

    @FXML
    private void backToDashboard() {
        try {
            Stage stage = (Stage) notificationsTable.getScene().getWindow();
            DashboardController.returnToDashboard(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateEntrance() {
        if (notificationsTable != null) {
            notificationsTable.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(600), notificationsTable);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    private void setStatus(String message, String color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-weight: bold;");
        }
    }

    public static class NotificationRecord {
        private final String type, message, date, priority;

        public NotificationRecord(String type, String message, String date, String priority) {
            this.type = type;
            this.message = message;
            this.date = date;
            this.priority = priority;
        }

        public String getType() { return type; }
        public String getMessage() { return message; }
        public String getDate() { return date; }
        public String getPriority() { return priority; }
    }
}