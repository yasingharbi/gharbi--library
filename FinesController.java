package controller;

import classes.Emprunt;
import database.DBEmprunt;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class FinesController {

    @FXML private TableView<Emprunt> blocklistTable;
    @FXML private TableColumn<Emprunt, Integer> colId;
    @FXML private TableColumn<Emprunt, String> colTelAdherent;
    @FXML private TableColumn<Emprunt, String> colIsbnLivre;
    @FXML private TableColumn<Emprunt, LocalDate> colDateEmprunt;
    @FXML private TableColumn<Emprunt, LocalDate> colDateRetour;
    @FXML private TableColumn<Emprunt, Integer> colDaysLate;
    @FXML private TableColumn<Emprunt, Double> colFineAmount;
    
    @FXML private Label statusLabel;
    @FXML private Label totalOverdueLabel;
    @FXML private Label totalFinesLabel;
    @FXML private Label worstOffenderLabel;
    @FXML private Label statsLabel;

    private ObservableList<Emprunt> blocklist;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final double FINE_PER_DAY = 2.0; // 2 DT per day

    @FXML
    private void initialize() {
        setupTableColumns();
        updateBlocklist();
        animateEntrance();
    }

    private void setupTableColumns() {
        // ID column
        if (colId != null) {
            colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
            colId.setCellFactory(column -> new TableCell<Emprunt, Integer>() {
                @Override
                protected void updateItem(Integer id, boolean empty) {
                    super.updateItem(id, empty);
                    if (empty || id == null) {
                        setText(null);
                    } else {
                        setText("#" + id);
                        setStyle("-fx-font-weight: bold; -fx-text-fill: #90A4AE;");
                    }
                }
            });
        }

        colTelAdherent.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelephoneAdherent()));
        colIsbnLivre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbnLivre()));
        
        // Date columns with formatting
        colDateEmprunt.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateEmprunt()));
        colDateEmprunt.setCellFactory(column -> new TableCell<Emprunt, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(date.format(dateFormatter));
                }
            }
        });
        
        colDateRetour.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateRetour()));
        colDateRetour.setCellFactory(column -> new TableCell<Emprunt, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                } else if (date == null) {
                    setText("En cours");
                    setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                } else {
                    setText(date.format(dateFormatter));
                    setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                }
            }
        });
        
        // Days late column
        if (colDaysLate != null) {
            colDaysLate.setCellValueFactory(c -> {
                LocalDate returnDate = c.getValue().getDateRetour();
                if (returnDate != null) {
                    int days = Period.between(returnDate, LocalDate.now()).getDays();
                    return new SimpleObjectProperty<>(days);
                }
                return new SimpleObjectProperty<>(0);
            });
            
            colDaysLate.setCellFactory(column -> new TableCell<Emprunt, Integer>() {
                @Override
                protected void updateItem(Integer days, boolean empty) {
                    super.updateItem(days, empty);
                    if (empty || days == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(days + " jour" + (days > 1 ? "s" : ""));
                        if (days > 30) {
                            setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-font-size: 14px;");
                        } else if (days > 14) {
                            setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                        }
                    }
                }
            });
        }
        
        // Fine amount column
        if (colFineAmount != null) {
            colFineAmount.setCellValueFactory(c -> {
                LocalDate returnDate = c.getValue().getDateRetour();
                if (returnDate != null) {
                    int days = Period.between(returnDate, LocalDate.now()).getDays();
                    double fine = days * FINE_PER_DAY;
                    return new SimpleObjectProperty<>(fine);
                }
                return new SimpleObjectProperty<>(0.0);
            });
            
            colFineAmount.setCellFactory(column -> new TableCell<Emprunt, Double>() {
                @Override
                protected void updateItem(Double fine, boolean empty) {
                    super.updateItem(fine, empty);
                    if (empty || fine == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%.2f DT", fine));
                        if (fine > 100) {
                            setStyle("-fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-font-size: 14px;");
                        } else if (fine > 50) {
                            setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                        } else {
                            setStyle("-fx-text-fill: #FF5722; -fx-font-weight: bold;");
                        }
                    }
                }
            });
        }
    }

    private void animateEntrance() {
        if (blocklistTable != null) {
            blocklistTable.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(600), blocklistTable);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    private void updateBlocklist() {
        LocalDate now = LocalDate.now();
        List<Emprunt> enRetard = DBEmprunt.getAllEmprunts().stream()
                .filter(e -> e.getDateRetour() == null || 
                           (e.getDateRetour() != null && e.getDateRetour().isBefore(now)))
                .collect(Collectors.toList());
        
        blocklist = FXCollections.observableArrayList(enRetard);
        blocklistTable.setItems(blocklist);
        
        updateStatistics();
    }

    private void updateStatistics() {
        int totalOverdue = blocklist.size();
        double totalFines = blocklist.stream()
            .mapToDouble(e -> {
                if (e.getDateRetour() != null) {
                    int days = Period.between(e.getDateRetour(), LocalDate.now()).getDays();
                    return days * FINE_PER_DAY;
                }
                return 0.0;
            })
            .sum();
        
        if (totalOverdueLabel != null) {
            totalOverdueLabel.setText(String.valueOf(totalOverdue));
        }
        
        if (totalFinesLabel != null) {
            totalFinesLabel.setText(String.format("%.2f DT", totalFines));
        }
        
        if (statsLabel != null) {
            statsLabel.setText(String.format(
                "⚠️ %d emprunts en retard | 💰 %.2f DT d'amendes totales",
                totalOverdue, totalFines
            ));
        }
        
        statusLabel.setText("En retard: " + totalOverdue + " | Amendes: " + String.format("%.2f DT", totalFines));
        statusLabel.setStyle("-fx-text-fill: #FF5722; -fx-font-size: 15px; -fx-font-weight: bold;");
    }

    @FXML
    private void onRefresh(javafx.event.ActionEvent event) {
        updateBlocklist();
        blocklistTable.refresh();
        
        setStatus("🔄 Liste rafraîchie: " + blocklist.size() + " emprunt(s) en retard", "#2196F3");
        
        // Animate refresh
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), blocklistTable);
        scale.setToX(1.02);
        scale.setToY(1.02);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    @FXML
    private void backToDashboard() {
        try {
            Stage stage = (Stage) blocklistTable.getScene().getWindow();
            DashboardController.returnToDashboard(stage);
        } catch (Exception e) {
            System.err.println("❌ Error returning to dashboard");
            e.printStackTrace();
        }
    }

    private void setStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 15px; -fx-font-weight: bold;");
        
        // Fade in animation
        FadeTransition fade = new FadeTransition(Duration.millis(300), statusLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}