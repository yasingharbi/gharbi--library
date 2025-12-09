package controller;

import classes.Emprunt;
import database.DBLivre;
import javafx.beans.property.SimpleIntegerProperty;
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

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class EmpruntDialogController {

    @FXML private TableView<Emprunt> loansTable;
    @FXML private TableColumn<Emprunt, Integer> colId;
    @FXML private TableColumn<Emprunt, String> colIsbn, colTelephone, colStatut;
    @FXML private TableColumn<Emprunt, LocalDate> colDateEmprunt, colDateRetour;
    @FXML private TableColumn<Emprunt, Integer> colDuree;
    @FXML private TableColumn<Emprunt, Void> colActions;
    @FXML private TextField searchField;
    @FXML private Label statsLabel;

    private final BibliothequeService service = new BibliothequeService();
    private ObservableList<Emprunt> loans;
    private FilteredList<Emprunt> filteredLoans;
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
        loans = FXCollections.observableArrayList(service.getAllEmprunts());
        filteredLoans = new FilteredList<>(loans, p -> true);
        loansTable.setItems(filteredLoans);
    }

    private void configureTableColumns() {
        // ID column
        colId.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getId()).asObject());
        colId.setCellFactory(column -> new TableCell<Emprunt, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                if (empty || id == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("#" + id);
                    setStyle("-fx-font-weight: bold; -fx-text-fill: #616161;");
                }
            }
        });

        // ISBN column
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbnLivre()));
        
        // Telephone column
        colTelephone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTelephoneAdherent()));
        
        // Date Emprunt column with formatting
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
        
        // FIX #6: Date Retour column with corrected formatting and color coding
        colDateRetour.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getDateRetour()));
        colDateRetour.setCellFactory(column -> new TableCell<Emprunt, LocalDate>() {
            @Override
            protected void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (empty) {
                    setText(null);
                    setStyle("");
                } else if (date == null) {
                    setText("En cours");
                    setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold; -fx-font-style: italic;");
                } else {
                    LocalDate today = LocalDate.now();
                    if (date.isAfter(today)) {
                        // Future date - still in progress
                        setText(date.format(dateFormatter) + " (prévu)");
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                    } else {
                        // Past date - returned
                        setText(date.format(dateFormatter));
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // Duration column (calculated)
        colDuree.setCellValueFactory(c -> {
            LocalDate start = c.getValue().getDateEmprunt();
            LocalDate end = c.getValue().getDateRetour();
            LocalDate today = LocalDate.now();
            
            if (start != null && end != null) {
                // If returned, calculate actual duration
                if (end.isBefore(today) || end.isEqual(today)) {
                    int days = Period.between(start, end).getDays();
                    return new SimpleObjectProperty<>(days);
                } else {
                    // If future return date, calculate days until return
                    int days = Period.between(start, end).getDays();
                    return new SimpleObjectProperty<>(days);
                }
            } else if (start != null) {
                // If not returned, calculate days since loan
                int days = Period.between(start, today).getDays();
                return new SimpleObjectProperty<>(days);
            }
            return new SimpleObjectProperty<>(0);
        });
        
        colDuree.setCellFactory(column -> new TableCell<Emprunt, Integer>() {
            @Override
            protected void updateItem(Integer days, boolean empty) {
                super.updateItem(days, empty);
                if (empty || days == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(days + " jour" + (days > 1 ? "s" : ""));
                    
                    // Color code based on duration
                    if (days > 30) {
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                    } else if (days > 14) {
                        setStyle("-fx-text-fill: #FF9800; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        // FIX #6: Status column with corrected logic
        colStatut.setCellValueFactory(c -> {
            LocalDate retour = c.getValue().getDateRetour();
            LocalDate today = LocalDate.now();
            
            // Check if dateRetour is null OR in the future
            if (retour == null || retour.isAfter(today)) {
                return new SimpleStringProperty("En cours");
            } else {
                return new SimpleStringProperty("Retourné");
            }
        });
        
        colStatut.setCellFactory(column -> new TableCell<Emprunt, String>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label badge = new Label(statut);
                    badge.setPadding(new Insets(4, 12, 4, 12));
                    badge.setStyle(
                        "-fx-background-radius: 12; " +
                        "-fx-font-weight: bold; " +
                        "-fx-font-size: 11px; " +
                        (statut.equals("En cours")
                            ? "-fx-background-color: #FFF3E0; -fx-text-fill: #E65100;"
                            : "-fx-background-color: #E8F5E9; -fx-text-fill: #2E7D32;")
                    );
                    
                    if (statut.equals("En cours")) {
                        badge.setText("⏳ " + statut);
                    } else {
                        badge.setText("✅ " + statut);
                    }
                    
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
        
        // Actions column
        setActionsColumn();
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredLoans.setPredicate(emprunt -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase().trim();
                
                return emprunt.getIsbnLivre().toLowerCase().contains(lowerCaseFilter)
                        || emprunt.getTelephoneAdherent().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(emprunt.getId()).contains(lowerCaseFilter);
            });
            updateStatistics();
        });
    }

    // FIX #6: Corrected table behavior with proper status checking
    private void configureTableBehavior() {
        loansTable.setPlaceholder(new Label("Aucun emprunt enregistré"));
        loansTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Row styling based on status
        loansTable.setRowFactory(tv -> {
            TableRow<Emprunt> row = new TableRow<Emprunt>() {
                @Override
                protected void updateItem(Emprunt emprunt, boolean empty) {
                    super.updateItem(emprunt, empty);
                    if (empty || emprunt == null) {
                        setStyle("");
                    } else {
                        LocalDate today = LocalDate.now();
                        // FIX: Check if loan is active (dateRetour is null OR in the future)
                        if (emprunt.getDateRetour() == null || emprunt.getDateRetour().isAfter(today)) {
                            // En cours - subtle orange background
                            setStyle("-fx-background-color: #FFF9F0;");
                        } else {
                            setStyle("");
                        }
                    }
                }
            };
            
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Emprunt emprunt = row.getItem();
                    LocalDate today = LocalDate.now();
                    // Only allow return if loan is active
                    if (emprunt.getDateRetour() == null || emprunt.getDateRetour().isAfter(today)) {
                        onReturn();
                    }
                }
            });
            
            return row;
        });
    }

    private void updateStatistics() {
        if (statsLabel != null) {
            int total = filteredLoans.size();
            LocalDate today = LocalDate.now();
            long enCours = filteredLoans.stream()
                    .filter(e -> e.getDateRetour() == null || e.getDateRetour().isAfter(today))
                    .count();
            long retournes = total - enCours;
            
            statsLabel.setText(String.format("Total: %d | ⏳ En cours: %d | ✅ Retournés: %d", 
                    total, enCours, retournes));
        }
    }

    @FXML
    private void onAdd() {
        Optional<Emprunt> result = createLoanDialog(null);
        result.ifPresent(loan -> {
            try {
                service.ajouterEmprunt(loan);
                loans.add(loan);
                loansTable.refresh();
                updateStatistics();
                showSuccessNotification("Emprunt ajouté", 
                    "L'emprunt a été enregistré avec succès.");
            } catch (Exception e) {
                showErrorAlert("Erreur d'ajout", "Impossible d'ajouter l'emprunt.", e.getMessage());
            }
        });
    }

    @FXML
    private void onReturn() {
        Emprunt selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        
        if (selectedLoan == null) {
            showWarningAlert("Aucune sélection", "Veuillez sélectionner un emprunt.");
            return;
        }
        
        LocalDate today = LocalDate.now();
        
        // Check if already returned (dateRetour exists and is in the past)
        if (selectedLoan.getDateRetour() != null && 
            (selectedLoan.getDateRetour().isBefore(today) || selectedLoan.getDateRetour().isEqual(today))) {
            showInfoNotification("Déjà retourné", "Ce livre a déjà été retourné le " + 
                selectedLoan.getDateRetour().format(dateFormatter));
            return;
        }
        
        Optional<ButtonType> result = showConfirmationDialog(
            "Confirmer le retour",
            "Enregistrer le retour du livre",
            "ISBN: " + selectedLoan.getIsbnLivre() + "\nDate: " + today.format(dateFormatter)
        );
        
        result.ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.enregistrerRetour(selectedLoan.getId(), today);
                    selectedLoan.setDateRetour(today);
                    loansTable.refresh();
                    updateStatistics();
                    showSuccessNotification("Retour enregistré", "Le retour a été enregistré avec succès.");
                } catch (Exception e) {
                    showErrorAlert("Erreur", "Impossible d'enregistrer le retour.", e.getMessage());
                }
            }
        });
    }

    // FIX #6: Corrected delete method with active loan check
    @FXML
    private void onDelete() {
        Emprunt selectedLoan = loansTable.getSelectionModel().getSelectedItem();
        
        if (selectedLoan == null) {
            showWarningAlert("Aucune sélection", "Veuillez sélectionner un emprunt à supprimer.");
            return;
        }
        
        // FIX: Check if loan is still active (not returned)
        LocalDate today = LocalDate.now();
        if (selectedLoan.getDateRetour() == null || selectedLoan.getDateRetour().isAfter(today)) {
            showErrorAlert("Suppression impossible", 
                "Cet emprunt ne peut pas être supprimé",
                "Le livre n'a pas encore été retourné. Veuillez d'abord enregistrer le retour.");
            return;
        }
        
        Optional<ButtonType> result = showConfirmationDialog(
            "Confirmer la suppression",
            "Supprimer l'emprunt #" + selectedLoan.getId(),
            "Cette action est irréversible. Voulez-vous vraiment supprimer cet emprunt ?"
        );
        
        result.ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    service.supprimerEmprunt(selectedLoan.getId());
                    loans.remove(selectedLoan);
                    loansTable.refresh();
                    updateStatistics();
                    showSuccessNotification("Suppression réussie", "L'emprunt a été supprimé avec succès.");
                } catch (Exception e) {
                    showErrorAlert("Erreur de suppression", "Impossible de supprimer l'emprunt.", e.getMessage());
                }
            }
        });
    }

    @FXML
    private void onSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            filteredLoans.setPredicate(p -> true);
            updateStatistics();
            showInfoNotification("Recherche réinitialisée", "Tous les emprunts sont affichés.");
        } else {
            long count = filteredLoans.size();
            showInfoNotification("Résultats de recherche", count + " emprunt(s) trouvé(s).");
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            Stage stage = (Stage) loansTable.getScene().getWindow();
            DashboardController.returnToDashboard(stage);
        } catch (Exception e) {
            System.err.println("❌ Error returning to dashboard");
            e.printStackTrace();
        }
    }
    
    private Optional<Emprunt> createLoanDialog(Emprunt existingLoan) {
        Dialog<Emprunt> dialog = new Dialog<>();
        dialog.setTitle(existingLoan == null ? "➕ Nouvel emprunt" : "✏️ Modifier l'emprunt");
        dialog.setHeaderText(existingLoan == null ? 
            "Enregistrer un nouvel emprunt de livre" : 
            "Modifier les informations de l'emprunt");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        ButtonType saveButton = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        // Create form fields
        ComboBox<String> isbnCombo = new ComboBox<>();
        isbnCombo.setEditable(true);
        isbnCombo.setPromptText("Sélectionner ou saisir ISBN");
        isbnCombo.setPrefWidth(300);
        List<String> isbns = service.getAllIsbnLivres();
        isbnCombo.getItems().addAll(isbns);
        
        ComboBox<String> telCombo = new ComboBox<>();
        telCombo.setEditable(true);
        telCombo.setPromptText("Sélectionner ou saisir téléphone");
        telCombo.setPrefWidth(300);
        List<String> telephones = service.getAllTelephonesAdherents();
        telCombo.getItems().addAll(telephones);
        
        DatePicker dateEmpruntPicker = new DatePicker(LocalDate.now());
        dateEmpruntPicker.setPromptText("JJ/MM/AAAA");
        dateEmpruntPicker.setPrefWidth(300);
        
        DatePicker dateRetourPicker = new DatePicker();
        dateRetourPicker.setPromptText("JJ/MM/AAAA (optionnel)");
        dateRetourPicker.setPrefWidth(300);
        
        // Auto-complete for ISBN
        isbnCombo.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) {
                isbnCombo.getItems().setAll(isbns);
                return;
            }
            isbnCombo.show();
            isbnCombo.getItems().setAll(isbns.stream()
                    .filter(s -> s.toLowerCase().contains(newV.toLowerCase()))
                    .toList());
        });
        
        // Auto-complete for Telephone
        telCombo.getEditor().textProperty().addListener((obs, oldV, newV) -> {
            if (newV == null || newV.isEmpty()) {
                telCombo.getItems().setAll(telephones);
                return;
            }
            telCombo.show();
            telCombo.getItems().setAll(telephones.stream()
                    .filter(t -> t.startsWith(newV))
                    .toList());
        });
        
        // Populate fields if editing
        if (existingLoan != null) {
            isbnCombo.setValue(existingLoan.getIsbnLivre());
            isbnCombo.setDisable(true);
            telCombo.setValue(existingLoan.getTelephoneAdherent());
            telCombo.setDisable(true);
            dateEmpruntPicker.setValue(existingLoan.getDateEmprunt());
            dateRetourPicker.setValue(existingLoan.getDateRetour());
        }
        
        // Create grid layout
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        
        // Add fields with labels
        addFormRow(grid, 0, "📖 ISBN du livre:", isbnCombo);
        addFormRow(grid, 1, "👤 Téléphone adhérent:", telCombo);
        addFormRow(grid, 2, "📅 Date d'emprunt:", dateEmpruntPicker);
        addFormRow(grid, 3, "📅 Date de retour:", dateRetourPicker);
        
        // Info label for availability
        Label availabilityLabel = new Label();
        availabilityLabel.setStyle("-fx-text-fill: #616161; -fx-font-size: 12px; -fx-font-style: italic;");
        GridPane.setColumnSpan(availabilityLabel, 2);
        grid.add(availabilityLabel, 0, 4);
        
        isbnCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                if (DBLivre.isDisponible(newVal)) {
                    availabilityLabel.setText("✅ Livre disponible pour l'emprunt");
                    availabilityLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-size: 12px; -fx-font-weight: bold;");
                } else {
                    availabilityLabel.setText("❌ Livre non disponible (déjà emprunté ou inexistant)");
                    availabilityLabel.setStyle("-fx-text-fill: #F44336; -fx-font-size: 12px; -fx-font-weight: bold;");
                }
            } else {
                availabilityLabel.setText("");
            }
        });
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(550);
        
        // Style the dialog
        dialog.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/emprunt.css").toExternalForm());
        
        // Enable/disable save button based on validation
        javafx.scene.Node saveBtn = dialog.getDialogPane().lookupButton(saveButton);
        saveBtn.setDisable(true);
        
        // Real-time validation
        javafx.beans.binding.BooleanBinding isInvalid = 
                javafx.beans.binding.Bindings.createBooleanBinding(() -> {
                    return isbnCombo.getValue() == null || isbnCombo.getValue().isEmpty()
                            || telCombo.getValue() == null || telCombo.getValue().isEmpty()
                            || dateEmpruntPicker.getValue() == null;
                }, isbnCombo.valueProperty(), telCombo.valueProperty(), dateEmpruntPicker.valueProperty());
        
        saveBtn.disableProperty().bind(isInvalid);
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                String chosenIsbn = isbnCombo.getValue();
                String chosenTel = telCombo.getValue();
                
                if (chosenIsbn == null || chosenIsbn.isBlank()) {
                    showErrorAlert("Erreur", "ISBN invalide", "Veuillez choisir un ISBN valide.");
                    return null;
                }
                
                if (!DBLivre.isDisponible(chosenIsbn) && existingLoan == null) {
                    showErrorAlert("Livre indisponible", 
                        "Ce livre n'est pas disponible pour l'emprunt.", 
                        "Il est peut-être déjà emprunté ou n'existe pas dans la base de données.");
                    return null;
                }
                
                return new Emprunt(
                    existingLoan != null ? existingLoan.getId() : 0,
                    chosenIsbn,
                    chosenTel,
                    dateEmpruntPicker.getValue(),
                    dateRetourPicker.getValue()
                );
            }
            return null;
        });
        
        // Request focus
        javafx.application.Platform.runLater(() -> isbnCombo.requestFocus());
        
        return dialog.showAndWait();
    }

    private void addFormRow(GridPane grid, int row, String labelText, javafx.scene.Node field) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        label.setMinWidth(180);
        
        GridPane.setConstraints(label, 0, row);
        GridPane.setConstraints(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
        
        grid.getChildren().addAll(label, field);
    }

    // FIX #6: Updated actions column to only show return button for active loans
    private void setActionsColumn() {
        colActions.setCellFactory(col -> new TableCell<Emprunt, Void>() {
            private final HBox container = new HBox(5);
            private final Button returnBtn = new Button("📥 Retour");
            
            {
                returnBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #4CAF50, #388E3C); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 6; -fx-padding: 6 12; " +
                    "-fx-cursor: hand; -fx-font-size: 12px;"
                );
                
                returnBtn.setOnAction(e -> {
                    Emprunt emprunt = getTableView().getItems().get(getIndex());
                    LocalDate today = LocalDate.now();
                    // Only process return for active loans
                    if (emprunt.getDateRetour() == null || emprunt.getDateRetour().isAfter(today)) {
                        getTableView().getSelectionModel().select(emprunt);
                        onReturn();
                    }
                });
                
                container.setAlignment(Pos.CENTER);
                container.getChildren().add(returnBtn);
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    Emprunt emprunt = getTableView().getItems().get(getIndex());
                    LocalDate today = LocalDate.now();
                    // FIX: Only show button if loan is active
                    if (emprunt != null && (emprunt.getDateRetour() == null || emprunt.getDateRetour().isAfter(today))) {
                        returnBtn.setVisible(true);
                        setGraphic(container);
                    } else {
                        setGraphic(null);
                    }
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
        alert.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/emprunt.css").toExternalForm());
        alert.initModality(Modality.APPLICATION_MODAL);
    }
}