package controller;

import classes.Livre;
import database.DBLivre;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;
import java.util.stream.Collectors;

public class CategoryController {

    @FXML private TableView<CategoryRecord> categoryTable;
    @FXML private TableColumn<CategoryRecord, String> colCategory;
    @FXML private TableColumn<CategoryRecord, Integer> colBookCount;
    @FXML private TableColumn<CategoryRecord, Double> colPercentage;
    @FXML private TableColumn<CategoryRecord, Void> colActions;

    @FXML private TextField categoryField;
    @FXML private PieChart categoryChart;

    @FXML private Label totalCategoriesLabel;
    @FXML private Label totalBooksLabel;
    @FXML private Label mostPopularLabel;
    @FXML private Label emptyLabel;
    @FXML private Label statusLabel;
    @FXML private Label statsLabel;

    private ObservableList<CategoryRecord> categoryList = FXCollections.observableArrayList();
    private List<Livre> allBooks = new ArrayList<>();

    @FXML
    private void initialize() {
        setupTableColumns();
        setupActionsColumn();
        loadData();
        animateEntrance();
    }

    private void setupTableColumns() {
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colBookCount.setCellValueFactory(new PropertyValueFactory<>("bookCount"));
        
        if (colPercentage != null) {
            colPercentage.setCellValueFactory(new PropertyValueFactory<>("percentage"));
            colPercentage.setCellFactory(column -> new TableCell<CategoryRecord, Double>() {
                @Override
                protected void updateItem(Double percentage, boolean empty) {
                    super.updateItem(percentage, empty);
                    if (empty || percentage == null) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(String.format("%.1f%%", percentage));
                        setStyle("-fx-text-fill: #90CAF9; -fx-font-weight: bold;");
                    }
                }
            });
        }

        // Color-coded book count
        colBookCount.setCellFactory(column -> new TableCell<CategoryRecord, Integer>() {
            @Override
            protected void updateItem(Integer count, boolean empty) {
                super.updateItem(count, empty);
                if (empty || count == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(count) + " 📚");
                    if (count == 0) {
                        setStyle("-fx-text-fill: #F44336; -fx-font-weight: bold;");
                    } else if (count > 20) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold; -fx-font-size: 14px;");
                    } else if (count > 10) {
                        setStyle("-fx-text-fill: #32be8f; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #2196F3; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void setupActionsColumn() {
        colActions.setCellFactory(column -> new TableCell<CategoryRecord, Void>() {
            private final Button deleteBtn = new Button("🗑️");
            {
                deleteBtn.setStyle(
                    "-fx-background-color: linear-gradient(to bottom, #F44336, #D32F2F); " +
                    "-fx-text-fill: white; -fx-font-weight: bold; " +
                    "-fx-background-radius: 6; -fx-padding: 6 12; " +
                    "-fx-cursor: hand; -fx-font-size: 13px;"
                );
                
                deleteBtn.setOnMouseEntered(e -> {
                    deleteBtn.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #E53935, #C62828); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 6; -fx-padding: 6 12; " +
                        "-fx-cursor: hand; -fx-font-size: 13px;"
                    );
                    ScaleTransition scale = new ScaleTransition(Duration.millis(100), deleteBtn);
                    scale.setToX(1.1);
                    scale.setToY(1.1);
                    scale.play();
                });
                
                deleteBtn.setOnMouseExited(e -> {
                    deleteBtn.setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #F44336, #D32F2F); " +
                        "-fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 6; -fx-padding: 6 12; " +
                        "-fx-cursor: hand; -fx-font-size: 13px;"
                    );
                    ScaleTransition scale = new ScaleTransition(Duration.millis(100), deleteBtn);
                    scale.setToX(1.0);
                    scale.setToY(1.0);
                    scale.play();
                });
                
                deleteBtn.setOnAction(event -> {
                    CategoryRecord record = getTableView().getItems().get(getIndex());
                    onDeleteCategory(record);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox container = new HBox(deleteBtn);
                    container.setAlignment(Pos.CENTER);
                    setGraphic(container);
                }
            }
        });
    }

    private void animateEntrance() {
        if (categoryTable != null) {
            categoryTable.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(600), categoryTable);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    private void loadData() {
        allBooks = new DBLivre().getAllLivres();
        updateCategoryList();
        updateStatistics();
        updateChart();
    }

    private void updateCategoryList() {
        categoryList.clear();
        Map<String, Long> categoryCounts = allBooks.stream()
            .collect(Collectors.groupingBy(Livre::getCategorie, Collectors.counting()));
        
        int totalBooks = allBooks.size();
        
        categoryCounts.forEach((cat, count) -> {
            double percentage = totalBooks > 0 ? (count * 100.0 / totalBooks) : 0.0;
            categoryList.add(new CategoryRecord(cat, count.intValue(), percentage));
        });
        
        // Sort by book count (descending)
        categoryList.sort((a, b) -> Integer.compare(b.getBookCount(), a.getBookCount()));
        categoryTable.setItems(categoryList);
    }

    private void updateStatistics() {
        int totalCategories = categoryList.size();
        int totalBooks = allBooks.size();
        String mostPopular = categoryList.isEmpty() ? "N/A" :
            categoryList.stream()
                .max(Comparator.comparingInt(CategoryRecord::getBookCount))
                .map(CategoryRecord::getCategory)
                .orElse("N/A");
        long emptyCategories = categoryList.stream()
            .filter(c -> c.getBookCount() == 0)
            .count();
        
        totalCategoriesLabel.setText(String.valueOf(totalCategories));
        totalBooksLabel.setText(String.valueOf(totalBooks));
        mostPopularLabel.setText(mostPopular);
        emptyLabel.setText(String.valueOf(emptyCategories));
        
        if (statsLabel != null) {
            statsLabel.setText(String.format(
                "📊 %d categories | 📚 %d books | ⭐ Most popular: %s",
                totalCategories, totalBooks, mostPopular
            ));
        }
    }

    private void updateChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        for (CategoryRecord rec : categoryList) {
            if (rec.getBookCount() > 0) {
                pieChartData.add(new PieChart.Data(
                    rec.getCategory() + " (" + rec.getBookCount() + ")",
                    rec.getBookCount()
                ));
            }
        }
        
        categoryChart.setData(pieChartData);
        categoryChart.setTitle("📊 Distribution des livres par catégorie");
        categoryChart.setLegendVisible(true);
        categoryChart.setStartAngle(90);
        categoryChart.setClockwise(true);
    }

    @FXML
    private void onAddCategory() {
        String category = categoryField.getText().trim();
        
        if (category.isEmpty()) {
            showErrorAlert("Champ vide", "Veuillez entrer un nom de catégorie!");
            return;
        }
        
        boolean exists = categoryList.stream()
            .anyMatch(c -> c.getCategory().equalsIgnoreCase(category));
        
        if (exists) {
            showErrorAlert("Doublon", "La catégorie '" + category + "' existe déjà!");
            return;
        }
        
        // Add new category
        categoryList.add(new CategoryRecord(category, 0, 0.0));
        categoryTable.setItems(categoryList);
        categoryField.clear();
        updateStatistics();
        updateChart();
        
        setStatus("✅ Catégorie '" + category + "' ajoutée avec succès!", "#4CAF50");
        
        // Animate success
        animateSuccess();
    }

    private void onDeleteCategory(CategoryRecord record) {
        if (record.getBookCount() > 0) {
            showErrorAlert("Suppression impossible", 
                "Impossible de supprimer: la catégorie contient " + record.getBookCount() + " livre(s)!");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("❓ Confirmer la suppression");
        alert.setHeaderText("Supprimer la catégorie '" + record.getCategory() + "'");
        alert.setContentText("Cette action est irréversible. Voulez-vous continuer?");
        
        styleAlert(alert);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            categoryList.remove(record);
            updateStatistics();
            updateChart();
            setStatus("✅ Catégorie supprimée avec succès", "#4CAF50");
        }
    }

    @FXML
    private void onRefresh() {
        loadData();
        setStatus("🔄 Données rafraîchies!", "#2196F3");
        
        // Animate refresh
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), categoryTable);
        scale.setToX(1.02);
        scale.setToY(1.02);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    @FXML
    private void backToDashboard() {
        try {
            Stage stage = (Stage) categoryTable.getScene().getWindow();
            DashboardController.returnToDashboard(stage);
        } catch (Exception e) {
            System.err.println("❌ Error returning to dashboard");
            e.printStackTrace();
        }
    }

    private void setStatus(String message, String color) {
        statusLabel.setText(message);
        statusLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");
        
        // Fade in animation
        FadeTransition fade = new FadeTransition(Duration.millis(300), statusLabel);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("❌ " + title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        styleAlert(alert);
        alert.showAndWait();
    }

    private void styleAlert(Alert alert) {
        alert.getDialogPane().getStylesheets().add(
            getClass().getResource("/css/category.css").toExternalForm());
        alert.initModality(Modality.APPLICATION_MODAL);
    }

    private void animateSuccess() {
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), categoryField);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
    }

    // CategoryRecord class
    public static class CategoryRecord {
        private final StringProperty category;
        private final IntegerProperty bookCount;
        private final DoubleProperty percentage;

        public CategoryRecord(String cat, int bookCount, double percentage) {
            this.category = new SimpleStringProperty(cat);
            this.bookCount = new SimpleIntegerProperty(bookCount);
            this.percentage = new SimpleDoubleProperty(percentage);
        }

        public String getCategory() { return category.get(); }
        public int getBookCount() { return bookCount.get(); }
        public double getPercentage() { return percentage.get(); }
        
        public StringProperty categoryProperty() { return category; }
        public IntegerProperty bookCountProperty() { return bookCount; }
        public DoubleProperty percentageProperty() { return percentage; }
    }
}