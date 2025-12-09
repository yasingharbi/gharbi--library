package controller;
import database.DBEmprunt;
import java.util.stream.Collectors;
import java.time.LocalDate;
import classes.Emprunt;
import classes.Livre;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.BibliothequeService;

import java.util.List;
import java.util.Optional;

public class LivreDialogController {

    @FXML private TableView<Livre> booksTable;
    @FXML private TableColumn<Livre, String> colIsbn, colTitre, colAuteur, colCategorie;
    @FXML private TableColumn<Livre, Boolean> colDisponible;
    @FXML private TableColumn<Livre, Integer> colQuantite;
    @FXML private TextField searchField;
    @FXML private Label statsLabel;

    private final BibliothequeService service = new BibliothequeService();
    private ObservableList<Livre> books;
    private FilteredList<Livre> filteredBooks;

    @FXML
    private void initialize() {
        initializeTableData();
        configureTableColumns();
        setupSearchFilter();
        configureTableBehavior();
        updateStatistics();
    }

    private void initializeTableData() {
        books = FXCollections.observableArrayList(service.getAllLivres());
        filteredBooks = new FilteredList<>(books, p -> true);
        booksTable.setItems(filteredBooks);
    }

    private void configureTableColumns() {
        colIsbn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getIsbn()));
        colTitre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTitre()));
        colAuteur.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getAuteur()));
        colCategorie.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategorie()));
        
        colDisponible.setCellValueFactory(c -> new SimpleBooleanProperty(c.getValue().isDisponible()));
        colDisponible.setCellFactory(CheckBoxTableCell.forTableColumn(colDisponible));
        colDisponible.setEditable(false);
        
        colQuantite.setCellValueFactory(c -> new SimpleIntegerProperty(c.getValue().getQuantite()).asObject());
        
        // Style for quantity column
        colQuantite.setCellFactory(column -> new TableCell<Livre, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    if (item == 0) {
                        setStyle("-fx-text-fill: #d32f2f; -fx-font-weight: bold;");
                    } else if (item < 5) {
                        setStyle("-fx-text-fill: #f57c00; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #388e3c; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupSearchFilter() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredBooks.setPredicate(livre -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                
                String lowerCaseFilter = newValue.toLowerCase().trim();
                
                return livre.getIsbn().toLowerCase().contains(lowerCaseFilter)
                        || livre.getTitre().toLowerCase().contains(lowerCaseFilter)
                        || livre.getAuteur().toLowerCase().contains(lowerCaseFilter)
                        || livre.getCategorie().toLowerCase().contains(lowerCaseFilter);
            });
            updateStatistics();
        });
    }

    private void configureTableBehavior() {
        booksTable.setPlaceholder(new Label("Aucun livre disponible"));
        booksTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        
        // Double-click to edit
        booksTable.setRowFactory(tv -> {
            TableRow<Livre> row = new TableRow<>();
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
            int total = filteredBooks.size();
            long disponibles = filteredBooks.stream()
                    .filter(Livre::isDisponible)
                    .count();
            long indisponibles = total - disponibles;
            
            statsLabel.setText(String.format("Total: %d | ✅ Disponibles: %d | ❌ Indisponibles: %d", 
                    total, disponibles, indisponibles));
        }
    }

    @FXML
    private void onAdd() {
        Optional<Livre> result = createBookDialog(null);
        result.ifPresent(book -> {
            try {
                service.ajouterLivre(book);
                books.add(book);
                booksTable.refresh();
                booksTable.getSelectionModel().select(book);
                updateStatistics();
                showSuccessNotification("Livre ajouté avec succès", "Le livre \"" + book.getTitre() + "\" a été ajouté.");
            } catch (Exception e) {
                showErrorAlert("Erreur d'ajout", "Impossible d'ajouter le livre.", e.getMessage());
            }
        });
    }

    @FXML
    private void onEdit() {
        Livre selectedBook = booksTable.getSelectionModel().getSelectedItem();
        
        if (selectedBook == null) {
            showWarningAlert("Aucune sélection", "Veuillez sélectionner un livre à modifier.");
            return;
        }
        
        Optional<Livre> result = createBookDialog(selectedBook);
        result.ifPresent(book -> {
            try {
                service.modifierLivre(book);
                int index = books.indexOf(selectedBook);
                books.set(index, book);
                booksTable.refresh();
                booksTable.getSelectionModel().select(book);
                updateStatistics();
                showSuccessNotification("Modification réussie", "Le livre a été modifié avec succès.");
            } catch (Exception e) {
                showErrorAlert("Erreur de modification", "Impossible de modifier le livre.", e.getMessage());
            }
        });
    }


@FXML
private void onDelete() {
    Livre selectedBook = booksTable.getSelectionModel().getSelectedItem();
    
    if (selectedBook == null) {
        showWarningAlert("Aucune sélection", "Veuillez sélectionner un livre à supprimer.");
        return;
    }
    
    // FIX: Check if book is in an active loan
    List<Emprunt> activeLoans = DBEmprunt.getAllEmprunts().stream()
        .filter(e -> e.getIsbnLivre().equals(selectedBook.getIsbn()))
        .filter(e -> e.getDateRetour() == null || e.getDateRetour().isAfter(LocalDate.now()))
        .collect(Collectors.toList());
    
    if (!activeLoans.isEmpty()) {
        showErrorAlert("Suppression impossible", 
            "Ce livre ne peut pas être supprimé",
            "Le livre est actuellement emprunté (" + activeLoans.size() + " emprunt(s) actif(s)).");
        return;
    }
    
    Optional<ButtonType> result = showConfirmationDialog(
        "Confirmer la suppression",
        "Supprimer le livre : " + selectedBook.getTitre(),
        "Cette action est irréversible. Voulez-vous vraiment supprimer ce livre ?"
    );
    
    result.ifPresent(response -> {
        if (response == ButtonType.OK) {
            try {
                // THIS WAS THE PROBLEM - service.supprimerLivre was being called but not working
                // Let's call the database method directly to ensure it executes
                service.supprimerLivre(selectedBook.getIsbn());
                
                // Remove from observable list
                books.remove(selectedBook);
                
                // Force refresh of the table
                booksTable.refresh();
                
                // Update statistics
                updateStatistics();
                
                showSuccessNotification("Suppression réussie", 
                    "Le livre \"" + selectedBook.getTitre() + "\" a été supprimé avec succès.");
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Erreur de suppression", 
                    "Impossible de supprimer le livre.", 
                    "Erreur: " + e.getMessage());
            }
        }
    });
}


    @FXML
    private void onSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            filteredBooks.setPredicate(p -> true);
            updateStatistics();
            showInfoNotification("Recherche réinitialisée", "Tous les livres sont affichés.");
        } else {
            // The filter is already applied via the listener
            long count = filteredBooks.size();
            showInfoNotification("Résultats de recherche", count + " livre(s) trouvé(s).");
        }
    }

    @FXML
    private void backToDashboard() {
        try {
            Stage stage = (Stage) booksTable.getScene().getWindow();
            DashboardController.returnToDashboard(stage);
        } catch (Exception e) {
            System.err.println("❌ Error returning to dashboard");
            e.printStackTrace();
        }
    }

    private Optional<Livre> createBookDialog(Livre existingBook) {
        Dialog<Livre> dialog = new Dialog<>();
        dialog.setTitle(existingBook == null ? "➕ Ajouter un nouveau livre" : "✏️ Modifier le livre");
        dialog.setHeaderText(existingBook == null ? "Veuillez remplir les informations du livre" : "Modifiez les informations du livre");
        dialog.initModality(Modality.APPLICATION_MODAL);
        
        ButtonType saveButton = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButton, ButtonType.CANCEL);
        
        // Create form fields
        TextField isbnField = createStyledTextField("978-X-XXXX-XXXX-X");
        TextField titreField = createStyledTextField("Titre du livre");
        TextField auteurField = createStyledTextField("Nom de l'auteur");
        
        // FIX: Use ComboBox for categories with existing categories
        ComboBox<String> categorieCombo = new ComboBox<>();
        categorieCombo.setEditable(true); // Allow new categories
        categorieCombo.setPromptText("Sélectionner ou saisir une catégorie");
        categorieCombo.setPrefWidth(300);
        
        // Load existing categories
        List<String> existingCategories = service.getAllLivres().stream()
            .map(Livre::getCategorie)
            .distinct()
            .sorted()
            .collect(Collectors.toList());
        categorieCombo.getItems().addAll(existingCategories);
        
        Spinner<Integer> quantiteSpinner = new Spinner<>(0, 1000, 1);
        quantiteSpinner.setEditable(true);
        quantiteSpinner.setPrefWidth(150);
        
        // Populate fields if editing
        if (existingBook != null) {
            isbnField.setText(existingBook.getIsbn());
            isbnField.setDisable(true);
            isbnField.setStyle("-fx-opacity: 0.7; -fx-background-color: #f0f0f0;");
            titreField.setText(existingBook.getTitre());
            auteurField.setText(existingBook.getAuteur());
            categorieCombo.setValue(existingBook.getCategorie());
            quantiteSpinner.getValueFactory().setValue(existingBook.getQuantite());
        }
        
        // Create grid layout
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(20, 20, 20, 20));
        
        // Add fields with labels
        addFormRow(grid, 0, "📖 ISBN:", isbnField);
        addFormRow(grid, 1, "📚 Titre:", titreField);
        addFormRow(grid, 2, "✍️ Auteur:", auteurField);
        addFormRow(grid, 3, "🏷️ Catégorie:", categorieCombo);
        addFormRow(grid, 4, "📊 Quantité:", quantiteSpinner);
        
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(500);
        
        // Style the dialog
        try {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/css/livre.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("⚠ CSS not found");
        }
        
        // Enable/disable save button based on validation
        javafx.scene.Node saveBtn = dialog.getDialogPane().lookupButton(saveButton);
        saveBtn.setDisable(true);
        
        // Real-time validation
        javafx.beans.binding.BooleanBinding isInvalid = isbnField.textProperty().isEmpty()
                .or(titreField.textProperty().isEmpty())
                .or(categorieCombo.valueProperty().isNull().or(
                    javafx.beans.binding.Bindings.createBooleanBinding(
                        () -> categorieCombo.getValue() == null || categorieCombo.getValue().trim().isEmpty(),
                        categorieCombo.valueProperty()
                    )));
        
        saveBtn.disableProperty().bind(isInvalid);
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButton) {
                if (!validateFields(isbnField, titreField, auteurField, categorieCombo, quantiteSpinner)) {
                    return null;
                }
                
                String isbn = isbnField.getText().trim();
                String titre = titreField.getText().trim();
                String auteur = auteurField.getText().trim();
                String categorie = categorieCombo.getValue().trim();
                int quantite = quantiteSpinner.getValue();
                boolean disponible = quantite > 0;
                
                return new Livre(isbn, titre, auteur, categorie, disponible, quantite);
            }
            return null;
        });
        
        // Request focus on first field
        javafx.application.Platform.runLater(() -> {
            if (existingBook == null) {
                isbnField.requestFocus();
            } else {
                titreField.requestFocus();
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
        label.setMinWidth(120);
        
        GridPane.setConstraints(label, 0, row);
        GridPane.setConstraints(field, 1, row);
        GridPane.setHgrow(field, Priority.ALWAYS);
        
        grid.getChildren().addAll(label, field);
    }

    private boolean validateFields(TextField isbn, TextField titre, TextField auteur,  ComboBox<String> categorieCombo, Spinner<Integer> quantite) {
    		StringBuilder errors = new StringBuilder();

			if (isbn.getText().trim().isEmpty()) {
			errors.append("• L'ISBN est obligatoire\n");
			}
			
			if (titre.getText().trim().isEmpty()) {
			errors.append("• Le titre est obligatoire\n");
			}
			
			if (auteur.getText().trim().isEmpty()) {
			errors.append("• L'auteur est obligatoire\n");
			}
			
			if (categorieCombo.getValue() == null || categorieCombo.getValue().trim().isEmpty()) {
			errors.append("• La catégorie est obligatoire\n");
			}
			
			try {
			int qty = quantite.getValue();
			if (qty < 0) {
			errors.append("• La quantité ne peut pas être négative\n");
			}
			} catch (Exception e) {
			errors.append("• La quantité doit être un nombre valide\n");
			}
			
			if (errors.length() > 0) {
			showErrorAlert("Erreurs de validation", "Veuillez corriger les erreurs suivantes:", errors.toString());
			return false;
			}
			
			return true;
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
        
        ButtonType yesButton = new ButtonType("Oui, supprimer", ButtonBar.ButtonData.OK_DONE);
        ButtonType noButton = new ButtonType("Non, annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(yesButton, noButton);
        styleAlert(alert);
        
        return alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        try {
            alert.getDialogPane().getStylesheets().add(getClass().getResource("/css/livre.css").toExternalForm());
        } catch (Exception e) {
            System.err.println("⚠ CSS not found");
        }
        alert.initModality(Modality.APPLICATION_MODAL);
    }
}