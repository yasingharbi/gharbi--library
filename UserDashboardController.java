package controller;

import classes.Livre;
import classes.Emprunt;
import database.DBEmprunt;
import javafx.animation.*;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.BibliothequeService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class UserDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private Label totalBooksLabel;
    @FXML private Label availableBooksLabel;
    @FXML private Label myLoansLabel;
    @FXML private Label overdueLoansLabel;
    
    @FXML private TableView<Livre> availableBooksTable;
    @FXML private TableColumn<Livre, String> colIsbn;
    @FXML private TableColumn<Livre, String> colTitre;
    @FXML private TableColumn<Livre, String> colAuteur;
    @FXML private TableColumn<Livre, String> colCategorie;
    @FXML private TableColumn<Livre, Integer> colQuantite;
    
    @FXML private TableView<Emprunt> myLoansTable;
    @FXML private TableColumn<Emprunt, Integer> colLoanId;
    @FXML private TableColumn<Emprunt, String> colLoanIsbn;
    @FXML private TableColumn<Emprunt, LocalDate> colLoanDate;
    @FXML private TableColumn<Emprunt, LocalDate> colReturnDate;
    @FXML private TableColumn<Emprunt, Integer> colDuration;
    @FXML private TableColumn<Emprunt, String> colStatus;
    
    @FXML private TextField searchBooksField;
    @FXML private ComboBox<String> categoryFilterCombo;
    
    private final BibliothequeService service = new BibliothequeService();
    private ObservableList<Livre> availableBooks;
    private FilteredList<Livre> filteredBooks;
    private ObservableList<Emprunt> myLoans;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    private String currentUsername;
    private String userPhone;

    @FXML
    private void initialize() {
        currentUsername = DashboardController.getSessionUsername();
        
        setupBooksTable();
        setupLoansTable();
        loadUserData();
        setupFilters();
        animateEntrance();
        startContinuousAnimations();
    }

    public void setUserInfo(String username, String phone) {
        this.currentUsername = username;
        this.userPhone = phone;
        
        if (welcomeLabel != null) {
            welcomeLabel.setText("Welcome, " + username + "!");
            animateLabel(welcomeLabel);
        }
        
        if (roleLabel != null) {
            roleLabel.setText("👤 Member Account");
            animateLabel(roleLabel);
        }
        
        loadUserData();
    }

    private void animateLabel(Label label) {
        FadeTransition fade = new FadeTransition(Duration.millis(1000), label);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(600), label);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.EASE_OUT);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(600), label);
        translate.setFromY(-20);
        translate.setToY(0);
        
        ParallelTransition parallel = new ParallelTransition(fade, scale, translate);
        parallel.play();
    }

    private void setupBooksTable() {
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        colTitre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitre()));
        colAuteur.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuteur()));
        colCategorie.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategorie()));
        
        colQuantite.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantite()).asObject());
        colQuantite.setCellFactory(column -> new TableCell<Livre, Integer>() {
            @Override
            protected void updateItem(Integer qty, boolean empty) {
                super.updateItem(qty, empty);
                if (empty || qty == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(qty));
                    if (qty == 0) {
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(244, 67, 54, 0.5), 5, 0.5, 0, 0);");
                    } else if (qty < 5) {
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(255, 152, 0, 0.5), 5, 0.5, 0, 0);");
                    } else {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(76, 175, 80, 0.5), 5, 0.5, 0, 0);");
                    }
                }
            }
        });
    }

    private void setupLoansTable() {
        colLoanId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colLoanIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbnLivre()));
        
        colLoanDate.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateEmprunt()));
        colLoanDate.setCellFactory(column -> new TableCell<Emprunt, LocalDate>() {
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
        
        colReturnDate.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateRetour()));
        colReturnDate.setCellFactory(column -> new TableCell<Emprunt, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else if (date == null) {
                    setText("En cours");
                    setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-font-style: italic; -fx-effect: dropshadow(gaussian, rgba(255, 152, 0, 0.6), 8, 0.5, 0, 0);");
                } else {
                    LocalDate today = LocalDate.now();
                    if (date.isAfter(today)) {
                        setText(date.format(dateFormatter) + " (prévu)");
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(255, 152, 0, 0.5), 5, 0.5, 0, 0);");
                    } else {
                        setText(date.format(dateFormatter));
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(76, 175, 80, 0.5), 5, 0.5, 0, 0);");
                    }
                }
            }
        });
        
        colDuration.setCellValueFactory(c -> {
            LocalDate start = c.getValue().getDateEmprunt();
            LocalDate end = c.getValue().getDateRetour();
            LocalDate today = LocalDate.now();
            
            if (start != null && end != null) {
                if (end.isBefore(today) || end.isEqual(today)) {
                    int days = Period.between(start, end).getDays();
                    return new SimpleObjectProperty<>(days);
                } else {
                    int days = Period.between(start, end).getDays();
                    return new SimpleObjectProperty<>(days);
                }
            } else if (start != null) {
                int days = Period.between(start, today).getDays();
                return new SimpleObjectProperty<>(days);
            }
            return new SimpleObjectProperty<>(0);
        });
        
        colDuration.setCellFactory(column -> new TableCell<Emprunt, Integer>() {
            @Override
            protected void updateItem(Integer days, boolean empty) {
                super.updateItem(days, empty);
                if (empty || days == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(days + " jour" + (days > 1 ? "s" : ""));
                    if (days > 30) {
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(244, 67, 54, 0.7), 8, 0.6, 0, 0);");
                    } else if (days > 14) {
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(255, 152, 0, 0.6), 6, 0.5, 0, 0);");
                    } else {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(76, 175, 80, 0.5), 5, 0.5, 0, 0);");
                    }
                }
            }
        });
        
        colStatus.setCellValueFactory(c -> {
            LocalDate retour = c.getValue().getDateRetour();
            LocalDate today = LocalDate.now();
            
            if (retour == null || retour.isAfter(today)) {
                return new SimpleStringProperty("En cours");
            } else {
                return new SimpleStringProperty("Retourné");
            }
        });
        
        colStatus.setCellFactory(column -> new TableCell<Emprunt, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(status);
                    badge.setPadding(new Insets(6, 14, 6, 14));
                    badge.setStyle(
                        "-fx-background-radius: 15; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0, 0, 0, 0.4), 8, 0.5, 0, 2); " +
                        (status.equals("En cours")
                            ? "-fx-background-color: linear-gradient(to right, #FFF3E0, #FFE0B2); -fx-text-fill: #E65100;"
                            : "-fx-background-color: linear-gradient(to right, #E8F5E9, #C8E6C9); -fx-text-fill: #2E7D32;")
                    );
                    
                    if (status.equals("En cours")) {
                        badge.setText("⏳ " + status);
                    } else {
                        badge.setText("✅ " + status);
                    }
                    
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
    }

    private void loadUserData() {
        List<Livre> allBooks = service.getAllLivres();
        List<Livre> available = allBooks.stream()
            .filter(Livre::isDisponible)
            .collect(Collectors.toList());
        
        availableBooks = FXCollections.observableArrayList(available);
        filteredBooks = new FilteredList<>(availableBooks, p -> true);
        availableBooksTable.setItems(filteredBooks);
        
        if (userPhone != null) {
            List<Emprunt> allLoans = DBEmprunt.getAllEmprunts();
            List<Emprunt> userLoans = allLoans.stream()
                .filter(e -> e.getTelephoneAdherent().equals(userPhone))
                .collect(Collectors.toList());
            
            myLoans = FXCollections.observableArrayList(userLoans);
            myLoansTable.setItems(myLoans);
        }
        
        List<String> categories = allBooks.stream()
            .map(Livre::getCategorie)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        
        if (categoryFilterCombo != null) {
            categoryFilterCombo.getItems().clear();
            categoryFilterCombo.getItems().add("Toutes les catégories");
            categoryFilterCombo.getItems().addAll(categories);
            categoryFilterCombo.setValue("Toutes les catégories");
        }
        
        updateStatistics();
        animateStatistics();
    }

    private void setupFilters() {
        if (searchBooksField != null) {
            searchBooksField.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        }
        
        if (categoryFilterCombo != null) {
            categoryFilterCombo.setOnAction(e -> applyFilters());
        }
    }

    private void applyFilters() {
        filteredBooks.setPredicate(livre -> {
            String searchText = searchBooksField != null ? searchBooksField.getText() : "";
            boolean searchMatch = true;
            
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerSearch = searchText.toLowerCase().trim();
                searchMatch = livre.getTitre().toLowerCase().contains(lowerSearch) ||
                             livre.getAuteur().toLowerCase().contains(lowerSearch) ||
                             livre.getIsbn().toLowerCase().contains(lowerSearch);
            }
            
            String selectedCategory = categoryFilterCombo != null ? categoryFilterCombo.getValue() : "Toutes les catégories";
            boolean categoryMatch = true;
            
            if (selectedCategory != null && !selectedCategory.equals("Toutes les catégories")) {
                categoryMatch = livre.getCategorie().equals(selectedCategory);
            }
            
            return searchMatch && categoryMatch;
        });
        
        updateStatistics();
    }

    private void updateStatistics() {
        int totalBooks = availableBooks.size();
        int availableCount = (int) availableBooks.stream()
            .filter(Livre::isDisponible)
            .count();
        
        if (totalBooksLabel != null) {
            totalBooksLabel.setText(String.valueOf(totalBooks));
        }
        
        if (availableBooksLabel != null) {
            availableBooksLabel.setText(String.valueOf(availableCount));
        }
        
        if (myLoans != null) {
            LocalDate today = LocalDate.now();
            long activeLoans = myLoans.stream()
                .filter(e -> e.getDateRetour() == null || e.getDateRetour().isAfter(today))
                .count();
            
            long overdue = myLoans.stream()
                .filter(e -> e.getDateRetour() == null)
                .filter(e -> Period.between(e.getDateEmprunt(), today).getDays() > 14)
                .count();
            
            if (myLoansLabel != null) {
                myLoansLabel.setText(String.valueOf(activeLoans));
            }
            
            if (overdueLoansLabel != null) {
                overdueLoansLabel.setText(String.valueOf(overdue));
            }
        }
    }

    private void animateStatistics() {
        Label[] statLabels = {totalBooksLabel, availableBooksLabel, myLoansLabel, overdueLoansLabel};
        
        for (int i = 0; i < statLabels.length; i++) {
            if (statLabels[i] != null && statLabels[i].getParent() != null) {
                VBox card = (VBox) statLabels[i].getParent();
                animateStatCard(card, i * 100);
            }
        }
    }

    private void animateStatCard(VBox card, int delay) {
        card.setOpacity(0);
        card.setScaleX(0.7);
        card.setScaleY(0.7);
        card.setTranslateY(30);
        
        FadeTransition fade = new FadeTransition(Duration.millis(600), card);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.setDelay(Duration.millis(delay));
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(600), card);
        scale.setFromX(0.7);
        scale.setFromY(0.7);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setDelay(Duration.millis(delay));
        scale.setInterpolator(Interpolator.EASE_OUT);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(600), card);
        translate.setFromY(30);
        translate.setToY(0);
        translate.setDelay(Duration.millis(delay));
        
        RotateTransition rotate = new RotateTransition(Duration.millis(600), card);
        rotate.setFromAngle(-10);
        rotate.setToAngle(0);
        rotate.setDelay(Duration.millis(delay));
        
        ParallelTransition parallel = new ParallelTransition(fade, scale, translate, rotate);
        parallel.play();
    }

    @FXML
    private void onRefresh() {
        loadUserData();
        showNotification("🔄 Données actualisées", "Les informations ont été mises à jour.");
        
        RotateTransition rotate = new RotateTransition(Duration.millis(500), availableBooksTable);
        rotate.setByAngle(360);
        rotate.play();
    }

    @FXML
    private void onViewBookDetails() {
        Livre selected = availableBooksTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showNotification("⚠️ Aucune sélection", "Veuillez sélectionner un livre.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("📚 Détails du Livre");
        alert.setHeaderText(selected.getTitre());
        alert.setContentText(
            "ISBN: " + selected.getIsbn() + "\n" +
            "Auteur: " + selected.getAuteur() + "\n" +
            "Catégorie: " + selected.getCategorie() + "\n" +
            "Quantité disponible: " + selected.getQuantite() + "\n" +
            "Statut: " + (selected.isDisponible() ? "✅ Disponible" : "❌ Non disponible")
        );
        alert.showAndWait();
    }

    @FXML
    private void onLogout() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("🚪 Déconnexion");
        confirm.setHeaderText("Voulez-vous vraiment vous déconnecter ?");
        confirm.setContentText("Utilisateur: " + currentUsername);
        
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                returnToLogin();
            }
        });
    }

    private void returnToLogin() {
        try {
            Scene currentScene = welcomeLabel.getScene();
            Parent root = currentScene.getRoot();
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            
            ScaleTransition scaleOut = new ScaleTransition(Duration.millis(500), root);
            scaleOut.setToX(0.8);
            scaleOut.setToY(0.8);
            
            RotateTransition rotateOut = new RotateTransition(Duration.millis(500), root);
            rotateOut.setByAngle(10);
            
            ParallelTransition parallelOut = new ParallelTransition(fadeOut, scaleOut, rotateOut);
            parallelOut.setOnFinished(e -> {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
                    Scene scene = new Scene(loader.load());
                    scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
                    
                    Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                    stage.setScene(scene);
                    stage.setTitle("GHARBI'S LIBRARY - Login");
                    stage.setResizable(false);
                    stage.centerOnScreen();
                    
                    Parent newRoot = scene.getRoot();
                    newRoot.setOpacity(0);
                    newRoot.setScaleX(0.8);
                    newRoot.setScaleY(0.8);
                    
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(600), newRoot);
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    
                    ScaleTransition scaleIn = new ScaleTransition(Duration.millis(600), newRoot);
                    scaleIn.setFromX(0.8);
                    scaleIn.setFromY(0.8);
                    scaleIn.setToX(1.0);
                    scaleIn.setToY(1.0);
                    scaleIn.setInterpolator(Interpolator.EASE_OUT);
                    
                    ParallelTransition parallelIn = new ParallelTransition(fadeIn, scaleIn);
                    parallelIn.play();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            parallelOut.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateEntrance() {
        if (availableBooksTable != null) {
            availableBooksTable.setOpacity(0);
            availableBooksTable.setTranslateX(-50);
            
            FadeTransition fade = new FadeTransition(Duration.millis(800), availableBooksTable);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(400));
            
            TranslateTransition translate = new TranslateTransition(Duration.millis(800), availableBooksTable);
            translate.setFromX(-50);
            translate.setToX(0);
            translate.setDelay(Duration.millis(400));
            
            ParallelTransition parallel = new ParallelTransition(fade, translate);
            parallel.play();
        }
        
        if (myLoansTable != null) {
            myLoansTable.setOpacity(0);
            myLoansTable.setTranslateX(50);
            
            FadeTransition fade = new FadeTransition(Duration.millis(800), myLoansTable);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(600));
            
            TranslateTransition translate = new TranslateTransition(Duration.millis(800), myLoansTable);
            translate.setFromX(50);
            translate.setToX(0);
            translate.setDelay(Duration.millis(600));
            
            ParallelTransition parallel = new ParallelTransition(fade, translate);
            parallel.play();
        }
    }

    private void startContinuousAnimations() {
        // Continuous stat card pulse
        Label[] statLabels = {totalBooksLabel, availableBooksLabel, myLoansLabel, overdueLoansLabel};
        
        for (Label label : statLabels) {
            if (label != null) {
                Timeline timeline = new Timeline(
                    new KeyFrame(Duration.ZERO, 
                        new KeyValue(label.scaleXProperty(), 1.0),
                        new KeyValue(label.scaleYProperty(), 1.0)
                    ),
                    new KeyFrame(Duration.seconds(1.5), 
                        new KeyValue(label.scaleXProperty(), 1.1, Interpolator.EASE_BOTH),
                        new KeyValue(label.scaleYProperty(), 1.1, Interpolator.EASE_BOTH)
                    ),
                    new KeyFrame(Duration.seconds(3), 
                        new KeyValue(label.scaleXProperty(), 1.0),
                        new KeyValue(label.scaleYProperty(), 1.0)
                    )
                );
                timeline.setCycleCount(Timeline.INDEFINITE);
                timeline.play();
            }
        }
    }

    private void showNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        alert.getDialogPane().setOpacity(0);
        alert.show();
        
        FadeTransition fade = new FadeTransition(Duration.millis(300), alert.getDialogPane());
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}