package controller;

import classes.Utilisateur;
import database.DBUtilisateur;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class StaffController {

    @FXML private TableView<Utilisateur> staffTable;
    @FXML private TableColumn<Utilisateur, String> colUsername;
    @FXML private TableColumn<Utilisateur, String> colRole;
    @FXML private TableColumn<Utilisateur, String> colCreatedDate;
    @FXML private TableColumn<Utilisateur, Void> colActions;
    @FXML private Label statusLabel;
    @FXML private Label totalUsersLabel;
    @FXML private Label totalAdminsLabel;
    @FXML private Label totalMembersLabel;
    @FXML private Label statsLabel;
    @FXML private ComboBox<String> filterCombo;
    @FXML private TextField searchField;

    private ObservableList<Utilisateur> allUsersList = FXCollections.observableArrayList();
    private FilteredList<Utilisateur> filteredUsers;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    private void initialize() {
        setupTableColumns();
        loadData();
        setupFilter();
        setupSearch();
        updateStatistics();
        animateEntrance();
    }

    private void setupTableColumns() {
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        
        // Role column with badges
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colRole.setCellFactory(column -> new TableCell<Utilisateur, String>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                if (empty || role == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(role.toUpperCase());
                    badge.setPadding(new javafx.geometry.Insets(4, 12, 4, 12));
                    badge.setStyle(
                        "-fx-background-radius: 12; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        (role.equalsIgnoreCase("admin") 
                            ? "-fx-background-color: #FFD700; -fx-text-fill: #000000;" 
                            : "-fx-background-color: #90CAF9; -fx-text-fill: #0D47A1;")
                    );
                    
                    if (role.equalsIgnoreCase("admin")) {
                        badge.setText("👑 " + badge.getText());
                    } else {
                        badge.setText("👤 " + badge.getText());
                    }
                    
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
        
        // Date column with formatting
        colCreatedDate.setCellValueFactory(c -> {
            String date = c.getValue().getCreatedDate();
            return new SimpleStringProperty(date != null ? date : "N/A");
        });
        
        // Actions column
        setActionColumn();
    }

    private void setActionColumn() {
        colActions.setCellFactory(param -> new TableCell<Utilisateur, Void>() {
            private final Button changePasswordBtn = new Button("🔑");
            private final Button deleteBtn = new Button("🗑️");
            private final HBox container = new HBox(8);

            {
                changePasswordBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #FF9800, #F57C00); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 6; -fx-padding: 6 12; " +
                    "-fx-cursor: hand; -fx-font-size: 13px;"
                );
                
                deleteBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #F44336, #D32F2F); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 6; -fx-padding: 6 12; " +
                    "-fx-cursor: hand; -fx-font-size: 13px;"
                );
                
                changePasswordBtn.setOnMouseEntered(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(100), changePasswordBtn);
                    scale.setToX(1.1);
                    scale.setToY(1.1);
                    scale.play();
                });
                
                changePasswordBtn.setOnMouseExited(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(100), changePasswordBtn);
                    scale.setToX(1.0);
                    scale.setToY(1.0);
                    scale.play();
                });
                
                deleteBtn.setOnMouseEntered(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(100), deleteBtn);
                    scale.setToX(1.1);
                    scale.setToY(1.1);
                    scale.play();
                });
                
                deleteBtn.setOnMouseExited(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(100), deleteBtn);
                    scale.setToX(1.0);
                    scale.setToY(1.0);
                    scale.play();
                });
                
                changePasswordBtn.setOnAction(e -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    showPasswordDialog(user);
                });
                
                deleteBtn.setOnAction(e -> {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    onDeleteUser(user);
                });
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().addAll(changePasswordBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    // Show buttons for regular users only
                    if (user != null && "user".equalsIgnoreCase(user.getRole())) {
                        setGraphic(container);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private void animateEntrance() {
        if (staffTable != null) {
            staffTable.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(600), staffTable);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    private void loadData() {
        allUsersList.setAll(DBUtilisateur.getAllUsers());
        filteredUsers = new FilteredList<>(allUsersList, p -> true);
        staffTable.setItems(filteredUsers);
    }

    private void setupFilter() {
        if (filterCombo != null) {
            filterCombo.getItems().setAll("Tous", "Admins", "Utilisateurs");
            filterCombo.setValue("Tous");
            filterCombo.setOnAction(e -> applyFilters());
        }
    }

    private void setupSearch() {
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                applyFilters();
            });
        }
    }

    private void applyFilters() {
        filteredUsers.setPredicate(user -> {
            // Filter by role
            String roleFilter = filterCombo.getValue();
            boolean roleMatch = true;
            
            if ("Admins".equals(roleFilter)) {
                roleMatch = "admin".equalsIgnoreCase(user.getRole());
            } else if ("Utilisateurs".equals(roleFilter)) {
                roleMatch = "user".equalsIgnoreCase(user.getRole());
            }
            
            // Filter by search
            String searchText = searchField != null ? searchField.getText() : "";
            boolean searchMatch = true;
            
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase().trim();
                searchMatch = user.getUsername().toLowerCase().contains(lowerCaseFilter);
            }
            
            return roleMatch && searchMatch;
        });
        
        updateStatistics();
    }

    private void updateStatistics() {
        int totalUsers = allUsersList.size();
        long admins = allUsersList.stream()
                .filter(u -> "admin".equalsIgnoreCase(u.getRole()))
                .count();
        long members = totalUsers - admins;
        
        if (totalUsersLabel != null) {
            totalUsersLabel.setText(String.valueOf(totalUsers));
        }
        
        if (totalAdminsLabel != null) {
            totalAdminsLabel.setText(String.valueOf(admins));
        }
        
        if (totalMembersLabel != null) {
            totalMembersLabel.setText(String.valueOf(members));
        }
        
        if (statsLabel != null) {
            statsLabel.setText(String.format(
                "👥 %d utilisateurs | 👑 %d admins | 👤 %d membres",
                totalUsers, admins, members
            ));
        }
        
        setStatus("Affichage: " + filteredUsers.size() + " utilisateur(s)", "#2196F3");
    }

    @FXML
    private void onAddAdmin() {
        Dialog<String[]> dialog = new Dialog<>();
        dialog.setTitle("➕ Ajouter un Administrateur");
        dialog.setHeaderText("Créer un nouveau compte administrateur");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        ButtonType createButton = new ButtonType("Créer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButton, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur");
        usernameField.setPrefWidth(300);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Mot de passe");
        passwordField.setPrefWidth(300);
        
        addFormRow(grid, 0, "👤 Nom d'utilisateur:", usernameField);
        addFormRow(grid, 1, "🔒 Mot de passe:", passwordField);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/staff.css").toExternalForm());
        
        javafx.scene.Node createBtn = dialog.getDialogPane().lookupButton(createButton);
        createBtn.setDisable(true);
        
        usernameField.textProperty().addListener((observable, oldValue, newValue) -> {
            createBtn.setDisable(newValue.trim().isEmpty() || passwordField.getText().trim().isEmpty());
        });
        
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            createBtn.setDisable(newValue.trim().isEmpty() || usernameField.getText().trim().isEmpty());
        });
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButton) {
                return new String[]{usernameField.getText().trim(), passwordField.getText().trim()};
            }
            return null;
        });
        
        javafx.application.Platform.runLater(() -> usernameField.requestFocus());
        
        Optional<String[]> result = dialog.showAndWait();
        result.ifPresent(data -> {
            String username = data[0];
            String password = data[1];
            
            boolean created = DBUtilisateur.createUser(username, password, "admin");
            if (created) {
                Utilisateur newAdmin = new Utilisateur(username, password, "admin");
                allUsersList.add(newAdmin);
                updateStatistics();
                setStatus("✅ Admin '" + username + "' créé avec succès!", "#4CAF50");
            } else {
                showErrorAlert("Erreur de création", 
                    "Impossible de créer l'administrateur", 
                    "Le nom d'utilisateur existe peut-être déjà.");
            }
        });
    }

    private void onDeleteUser(Utilisateur user) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("❓ Confirmer la suppression");
        confirm.setHeaderText("Supprimer l'utilisateur : " + user.getUsername());
        confirm.setContentText("Cette action est irréversible. Voulez-vous continuer?");
        
        styleAlert(confirm);
        
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            boolean deleted = DBUtilisateur.deleteUser(user.getUsername());
            if (deleted) {
                allUsersList.remove(user);
                updateStatistics();
                setStatus("✅ Utilisateur supprimé avec succès", "#4CAF50");
            } else {
                showErrorAlert("Erreur", "Impossible de supprimer l'utilisateur", "");
            }
        }
    }

    @FXML
    private void onRefresh() {
        loadData();
        applyFilters();
        setStatus("🔄 Données rafraîchies!", "#2196F3");
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), staffTable);
        scale.setToX(1.02);
        scale.setToY(1.02);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    private void showPasswordDialog(Utilisateur user) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("🔑 Changer le Mot de Passe");
        dialog.setHeaderText("Modifier le mot de passe pour : " + user.getUsername());
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        ButtonType changeButton = new ButtonType("Changer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(changeButton, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new javafx.geometry.Insets(20, 20, 20, 20));
        
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("Nouveau mot de passe");
        newPasswordField.setPrefWidth(300);
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirmer le mot de passe");
        confirmPasswordField.setPrefWidth(300);
        
        addFormRow(grid, 0, "🔒 Nouveau mot de passe:", newPasswordField);
        addFormRow(grid, 1, "🔒 Confirmer:", confirmPasswordField);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/staff.css").toExternalForm());
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == changeButton) {
                String newPass = newPasswordField.getText().trim();
                String confirmPass = confirmPasswordField.getText().trim();
                
                if (newPass.isEmpty()) {
                    showErrorAlert("Erreur", "Le mot de passe ne peut pas être vide", "");
                    return null;
                }
                
                if (!newPass.equals(confirmPass)) {
                    showErrorAlert("Erreur", "Les mots de passe ne correspondent pas", "");
                    return null;
                }
                
                return newPass;
            }
            return null;
        });
        
        javafx.application.Platform.runLater(() -> newPasswordField.requestFocus());
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(newPassword -> {
            boolean changed = DBUtilisateur.changePassword(user.getUsername(), newPassword);
            if (changed) {
                setStatus("✅ Mot de passe changé pour " + user.getUsername(), "#4CAF50");
            } else {
                showErrorAlert("Erreur", "Impossible de changer le mot de passe", "");
            }
        });
    }

    @FXML
    private void backToDashboard() {
        try {
            Stage stage = (Stage) staffTable.getScene().getWindow();
            DashboardController.returnToDashboard(stage);
        } catch (Exception e) {
            System.err.println("❌ Error returning to dashboard");
            e.printStackTrace();
        }
    }

    private void addFormRow(GridPane grid, int row, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #ECEFF1;");
        label.setMinWidth(180);
        
        GridPane.setConstraints(label, 0, row);
        GridPane.setConstraints(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
        
        grid.getChildren().addAll(label, field);
    }

    private void setStatus(String message, String color) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), statusLabel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ " + title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/staff.css").toExternalForm());
        alert.initModality(Modality.APPLICATION_MODAL);
    }
}