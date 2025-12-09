package controller;
import database.DBEmprunt;
import classes.Adherent;
import classes.Emprunt;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.BibliothequeService;
import java.util.stream.Collectors;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class AdherentDialogController {

    @FXML private TableView<Adherent> membersTable;
    @FXML private TableColumn<Adherent, String> colTelephone, colNom, colPrenom, colType;
    @FXML private TableColumn<Adherent, LocalDate> colDateNaissance;
    @FXML private TableColumn<Adherent, Integer> colAge;
    @FXML private TableColumn<Adherent, Void> colTypeActions;
    @FXML private TextField searchField;
    @FXML private Label statsLabel;

    private final BibliothequeService service = new BibliothequeService();
    private ObservableList<Adherent> adherents;
    private FilteredList<Adherent> filteredAdherents;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private void initialize() {
        initializeTableData();
        configureTableColumns();
        setupSearchFilter();
        configureTableBehavior();
        updateStatistics();
    }

    private void initializeTableData() {
        adherents = FXCollections.observableArrayList(service.getAllAdherents());
        filteredAdherents = new FilteredList<>(adherents, p -> true);
        membersTable.setItems(filteredAdherents);
    }

    private void configureTableColumns() {
        // Basic columns
        colTelephone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelephone()));
        colNom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNom()));
        colPrenom.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPrenom()));
        
        // Date column with formatting
        colDateNaissance.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateNaissance()));
        colDateNaissance.setCellFactory(column -> new TableCell<Adherent, LocalDate>() {
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
        
        // Age column (calculated)
        colAge.setCellValueFactory(c -> {
            LocalDate birthDate = c.getValue().getDateNaissance();
            if (birthDate != null) {
                int age = Period.between(birthDate, LocalDate.now()).getYears();
                return new SimpleObjectProperty<>(age);
            }
            return new SimpleObjectProperty<>(0);
        });
        
        colAge.setCellFactory(column -> new TableCell<Adherent, Integer>() {
            @Override
            protected void updateItem(Integer age, boolean empty) {
                super.updateItem(age, empty);
                if (empty || age == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(age + " ans");
                    if (age < 18) {
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #424242;");
                    }
                }
            }
        });
        
        // Type column with styled badges
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType()));
        colType.setCellFactory(column -> new TableCell<Adherent, String>() {
            @Override
            protected void updateItem(String type, boolean empty) {
                super.updateItem(type, empty);
                if (empty || type == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(type.toUpperCase());
                    badge.setPadding(new Insets(4, 12, 4, 12));
                    badge.setStyle(
                        "-fx-background-radius: 12; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        (type.equalsIgnoreCase("premium") 
                            ? "-fx-background-color: #FFD700; -fx-text-fill: #000000;" 
                            : "-fx-background-color: #90CAF9; -fx-text-fill: #0D47A1;")
                    );
                    
                    // Add crown emoji for premium
                    if (type.equalsIgnoreCase("premium")) {
                        badge.setText("👑 " + badge.getText());
                    }
                    
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
        
        // Action column
        setTypeActionColumn();
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredAdherents.setPredicate(adherent -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase().trim();
                
                return adherent.getNom().toLowerCase().contains(lowerCaseFilter)
                        || adherent.getPrenom().toLowerCase().contains(lowerCaseFilter)
                        || adherent.getTelephone().toLowerCase().contains(lowerCaseFilter)
                        || adherent.getType().toLowerCase().contains(lowerCaseFilter);
            });
            updateStatistics();
        });
    }

    private void configureTableBehavior() {
        membersTable.setPlaceholder(new Label("Aucun adhérent trouvé"));
        membersTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Double-click to edit
        membersTable.setRowFactory(tv -> {
            TableRow<Adherent> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    onEdit();
                }
            });
            return row;
        });
    }

    private void updateStatistics() {
        if (statsLabel != null) {
            int total = filteredAdherents.size();
            long premium = filteredAdherents.stream()
                    .filter(a -> a.getType().equalsIgnoreCase("premium"))
                    .count();
            long standard = total - premium;
            
            statsLabel.setText(String.format("Total: %d | 👑 Premium: %d | Standard: %d", 
                    total, premium, standard));
        }
    }

    @FXML
    private void onAdd() {
        Optional<Adherent> result = createAdherentDialog(null);
        result.ifPresent(adherent -> {
            try {
                service.ajouterAdherent(adherent);
                adherents.add(adherent);
                membersTable.refresh();
                membersTable.getSelectionModel().select(adherent);
                updateStatistics();
                showSuccessNotification("Adhérent ajouté", 
                    "L'adhérent " + adherent.getPrenom() + " " + adherent.getNom() + " a été ajouté avec succès.");
            } catch (Exception e) {
                showErrorAlert("Erreur d'ajout", "Impossible d'ajouter l'adhérent.", e.getMessage());
            }
        });
    }

    @FXML
    private void onEdit() {
        Adherent selectedAdherent = membersTable.getSelectionModel().getSelectedItem();
        
        if (selectedAdherent == null) {
            showWarningAlert("Aucune sélection", "Veuillez sélectionner un adhérent à modifier.");
            return;
        }
        
        Optional<Adherent> result = createAdherentDialog(selectedAdherent);
        result.ifPresent(adherent -> {
            try {
                service.modifierAdherent(adherent);
                int index = adherents.indexOf(selectedAdherent);
                adherents.set(index, adherent);
                membersTable.refresh();
                membersTable.getSelectionModel().select(adherent);
                updateStatistics();
                showSuccessNotification("Modification réussie", "L'adhérent a été modifié avec succès.");
            } catch (Exception e) {
                showErrorAlert("Erreur de modification", "Impossible de modifier l'adhérent.", e.getMessage());
            }
        });
    }

    @FXML
    private void onDelete() {
        Adherent selectedAdherent = membersTable.getSelectionModel().getSelectedItem();
        
        if (selectedAdherent == null) {
            showWarningAlert("Aucune sélection", "Veuillez sélectionner un adhérent à supprimer.");
            return;
        }
        
        // FIX: Check if member has active loans
        List<Emprunt> activeLoans = DBEmprunt.getAllEmprunts().stream()
            .filter(e -> e.getTelephoneAdherent().equals(selectedAdherent.getTelephone()))
            .filter(e -> e.getDateRetour() == null || e.getDateRetour().isAfter(LocalDate.now()))
            .collect(Collectors.toList());
        
        if (!activeLoans.isEmpty()) {
            showErrorAlert("Suppression impossible", 
                "Cet adhérent ne peut pas être supprimé",
                "L'adhérent a " + activeLoans.size() + " emprunt(s) actif(s) en cours.");
            return;
        }
        
        Optional<ButtonType> result = showConfirmationDialog(
            "Confirmer la suppression",
            "Supprimer l'adhérent : " + selectedAdherent.getPrenom() + " " + selectedAdherent.getNom(),
            "Cette action est irréversible. Voulez-vous vraiment supprimer cet adhérent ?"
        );
        
        result.ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // THIS WAS THE PROBLEM - service.supprimerAdherent was being called but not working
                    // Let's call the database method directly to ensure it executes
                    service.supprimerAdherent(selectedAdherent.getTelephone());
                    
                    // Remove from observable list
                    adherents.remove(selectedAdherent);
                    
                    // Force refresh of the table
                    membersTable.refresh();
                    
                    // Update statistics
                    updateStatistics();
                    
                    showSuccessNotification("Suppression réussie", 
                        "L'adhérent " + selectedAdherent.getPrenom() + " " + selectedAdherent.getNom() + 
                        " a été supprimé avec succès.");
                } catch (Exception e) {
                    e.printStackTrace();
                    showErrorAlert("Erreur de suppression", 
                        "Impossible de supprimer l'adhérent.", 
                        "Erreur: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            filteredAdherents.setPredicate(p -> true);
            updateStatistics();
            showInfoNotification("Recherche réinitialisée", "Tous les adhérents sont affichés.");
        } else {
            long count = filteredAdherents.size();
            showInfoNotification("Résultats de recherche", count + " adhérent(s) trouvé(s).");
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            Stage stage = (Stage) membersTable.getScene().getWindow();
            DashboardController.returnToDashboard(stage);
        } catch (Exception e) {
            System.err.println("❌ Error returning to dashboard");
            e.printStackTrace();
        }
    }
    private Optional<Adherent> createAdherentDialog(Adherent existingAdherent) {
        Dialog<Adherent> dialog = new Dialog<>();
        dialog.setTitle(existingAdherent == null ? "➕ Ajouter un nouvel adhérent" : "✏️ Modifier l'adhérent");
        dialog.setHeaderText(existingAdherent == null ? 
            "Veuillez remplir les informations de l'adhérent" : 
            "Modifiez les informations de l'adhérent");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        ButtonType saveButton = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        // Create form fields
        TextField telephoneField = createStyledTextField("Ex: +216 XX XXX XXX");
        TextField nomField = createStyledTextField("Nom de famille");
        TextField prenomField = createStyledTextField("Prénom");
        DatePicker dateNaissancePicker = new DatePicker();
        dateNaissancePicker.setPromptText("JJ/MM/AAAA");
        dateNaissancePicker.setPrefWidth(300);
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("Standard", "Premium");
        typeComboBox.setValue("Standard");
        typeComboBox.setPrefWidth(300);
        typeComboBox.setStyle("-fx-font-size: 14px;");
        
        // Populate fields if editing
        if (existingAdherent != null) {
            telephoneField.setText(existingAdherent.getTelephone());
            telephoneField.setDisable(true);
            telephoneField.setStyle("-fx-opacity: 0.7; -fx-background-color: #f0f0f0;");
            nomField.setText(existingAdherent.getNom());
            prenomField.setText(existingAdherent.getPrenom());
            dateNaissancePicker.setValue(existingAdherent.getDateNaissance());
            typeComboBox.setValue(existingAdherent.getType().substring(0, 1).toUpperCase() + 
                                 existingAdherent.getType().substring(1).toLowerCase());
        }
        
        // Create grid layout
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        
        // Add fields with labels
        addFormRow(grid, 0, "📞 Téléphone:", telephoneField);
        addFormRow(grid, 1, "👤 Nom:", nomField);
        addFormRow(grid, 2, "👤 Prénom:", prenomField);
        addFormRow(grid, 3, "📅 Date de naissance:", dateNaissancePicker);
        addFormRow(grid, 4, "🏷️ Type d'adhésion:", typeComboBox);
        
        // Add age info label
        Label ageInfoLabel = new Label();
        ageInfoLabel.setStyle("-fx-text-fill: #616161; -fx-font-size: 12px; -fx-font-style: italic;");
        GridPane.setColumnSpan(ageInfoLabel, 2);
        grid.add(ageInfoLabel, 0, 5);
        
        dateNaissancePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int age = Period.between(newVal, LocalDate.now()).getYears();
                ageInfoLabel.setText("ℹ️ Âge: " + age + " ans" + 
                    (age < 18 ? " (Mineur)" : " (Majeur)"));
            } else {
                ageInfoLabel.setText("");
            }
        });
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(550);
        
        // Style the dialog
        try {
            dialog.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/adherent.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("⚠ CSS not found");
        }
        
        // Enable/disable save button based on validation
        javafx.scene.Node saveBtn = dialog.getDialogPane().lookupButton(saveButton);
        saveBtn.setDisable(true);
        
        // Real-time validation
        javafx.beans.binding.BooleanBinding isInvalid = telephoneField.textProperty().isEmpty()
                .or(nomField.textProperty().isEmpty())
                .or(prenomField.textProperty().isEmpty())
                .or(dateNaissancePicker.valueProperty().isNull());
        
        saveBtn.disableProperty().bind(isInvalid);
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                if (!validateAdherentFields(telephoneField, nomField, prenomField, dateNaissancePicker)) {
                    return null;
                }
                
                return new Adherent(
                    telephoneField.getText().trim(),
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    dateNaissancePicker.getValue(),
                    typeComboBox.getValue().toLowerCase()
                );
            }
            return null;
        });
        
        // Request focus on first field
        javafx.application.Platform.runLater(() -> {
            if (existingAdherent == null) {
                telephoneField.requestFocus();
            } else {
                nomField.requestFocus();
            }
        });
        
        return dialog.showAndWait();
    }

    private TextField createStyledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setPrefWidth(300);
        return field;
    }

    private void addFormRow(GridPane grid, int row, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        label.setMinWidth(150);
        
        GridPane.setConstraints(label, 0, row);
        GridPane.setConstraints(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
        
        grid.getChildren().addAll(label, field);
    }

    private boolean validateAdherentFields(TextField telephone, TextField nom, 
                                          TextField prenom, DatePicker dateNaissance) {
        StringBuilder errors = new StringBuilder();
        
        if (telephone.getText().trim().isEmpty()) {
            errors.append("• Le téléphone est obligatoire\n");
        }
        
        if (nom.getText().trim().isEmpty()) {
            errors.append("• Le nom est obligatoire\n");
        }
        
        if (prenom.getText().trim().isEmpty()) {
            errors.append("• Le prénom est obligatoire\n");
        }
        
        if (dateNaissance.getValue() == null) {
            errors.append("• La date de naissance est obligatoire\n");
        } else if (dateNaissance.getValue().isAfter(LocalDate.now())) {
            errors.append("• La date de naissance ne peut pas être dans le futur\n");
        }
        
        if (errors.length() > 0) {
            showErrorAlert("Erreurs de validation", 
                "Veuillez corriger les erreurs suivantes:", errors.toString());
            return false;
        }
        
        return true;
    }

    private void setTypeActionColumn() {
        colTypeActions.setCellFactory(col -> new TableCell<Adherent, Void>() {
            private final HBox container = new HBox(5);
            private final Button changeTypeBtn = new Button();
            
            {
                changeTypeBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 6; -fx-padding: 6 12; " +
                    "-fx-cursor: hand; -fx-font-size: 12px;"
                );
                
                changeTypeBtn.setOnAction(e -> {
                    Adherent adherent = getTableView().getItems().get(getIndex());
                    String currentType = adherent.getType();
                    String newType = currentType.equalsIgnoreCase("standard") ? "premium" : "standard";
                    
                    Optional<ButtonType> confirm = showConfirmationDialog(
                        "Changer le type d'adhésion",
                        "Modifier l'adhésion de: " + adherent.getPrenom() + " " + adherent.getNom(),
                        String.format("Passer de '%s' à '%s' ?", 
                            currentType.toUpperCase(), newType.toUpperCase())
                    );
                    
                    confirm.ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            boolean success = service.changerTypeAdherent(adherent.getTelephone(), newType);
                            if (success) {
                                adherent.setType(newType);
                                getTableView().refresh();
                                updateStatistics();
                                showSuccessNotification("Type modifié", 
                                    "L'adhésion a été changée en " + newType.toUpperCase());
                            } else {
                                showErrorAlert("Erreur", "Impossible de changer le type d'adhésion.", "");
                            }
                        }
                    });
                });
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().add(changeTypeBtn);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Adherent adherent = getTableView().getItems().get(getIndex());
                    if (adherent != null) {
                        boolean isPremium = adherent.getType().equalsIgnoreCase("premium");
                        changeTypeBtn.setText(isPremium ? "⬇️ Standard" : "⬆️ Premium");
                        changeTypeBtn.setStyle(
                            isPremium 
                                ? "-fx-background-color: linear-gradient(to bottom, #90CAF9, #42A5F5); " +
                                  "-fx-text-fill: white; -fx-font-weight: bold; " +
                                  "-fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;"
                                : "-fx-background-color: linear-gradient(to bottom, #FFD700, #FFA500); " +
                                  "-fx-text-fill: white; -fx-font-weight: bold; " +
                                  "-fx-background-radius: 6; -fx-padding: 6 12; -fx-cursor: hand; -fx-font-size: 12px;"
                        );
                    }
                    setGraphic(container);
                }
            }
        });
    }

    // ==================== Alert & Notification Methods ====================
    
    private void showSuccessNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("✅ " + title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void showInfoNotification(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ℹ️ " + title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void showWarningAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("⚠️ " + title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ " + title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        styleAlert(alert);
        alert.showAndWait();
    }

    private Optional<ButtonType> showConfirmationDialog(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("❓ " + title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        ButtonType yesButton = new ButtonType("Oui, continuer", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("Non, annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(yesButton, noButton);
        styleAlert(alert);
        
        return alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        try {
            alert.getDialogPane().getStylesheets().add(
                getClass().getResource("/css/adherent.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("⚠ CSS not found");
        }
        alert.initModality(Modality.APPLICATION_MODAL);
    }
}