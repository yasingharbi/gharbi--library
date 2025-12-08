package controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.BibliothequeService;
import javafx.application.Platform;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.chart.BarChart;

public class StatisticsController {

    @FXML private BarChart<String, Number> barChart;
    @FXML private PieChart pieChart;
    @FXML private LineChart<String, Number> lineChart;
    @FXML private Label totalLivres;
    @FXML private Label totalAdherents;
    @FXML private Label totalEmprunts;
    @FXML private Label totalAdmins;
    @FXML private Label totalRetards;
    @FXML private Label statsLabel;
    @FXML private VBox mainContainer;

    private BibliothequeService service = new BibliothequeService();
    private Timeline autoRefreshTimeline;

    @FXML
    private void initialize() {
        Platform.runLater(() -> {
            playEntranceAnimation();
            loadStatistics();
            setupBarChart();
            setupPieChart();
            setupLineChart();
            animateCharts();
            startAutoRefresh();
        });
    }

    /**
     * Ultra Premium Entrance Animation
     */
    private void playEntranceAnimation() {
        if (mainContainer != null) {
            mainContainer.setOpacity(0);
            mainContainer.setScaleX(0.95);
            mainContainer.setScaleY(0.95);
            
            FadeTransition fade = new FadeTransition(Duration.millis(600), mainContainer);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setInterpolator(Interpolator.EASE_OUT);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(600), mainContainer);
            scale.setFromX(0.95);
            scale.setFromY(0.95);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setInterpolator(Interpolator.EASE_OUT);
            
            ParallelTransition parallel = new ParallelTransition(fade, scale);
            parallel.play();
        }
    }

    /**
     * Load Statistics with Enhanced Animations
     */
    private void loadStatistics() {
        int livres = service.countLivres();
        int adherents = service.countAdherents();
        int emprunts = service.countEmprunts();
        int admins = service.countAdmins();
        int retards = service.countRetards();

        System.out.println("📊 Ultra Premium Statistics loaded:");
        System.out.println("  📚 Livres: " + livres);
        System.out.println("  👥 Adhérents: " + adherents);
        System.out.println("  📖 Emprunts: " + emprunts);
        System.out.println("  👑 Admins: " + admins);
        System.out.println("  ⚠️ Retards: " + retards);

        if (totalLivres != null) {
            animateCountUp(totalLivres, 0, livres, Duration.millis(1500));
        }
        
        if (totalAdherents != null) {
            animateCountUp(totalAdherents, 0, adherents, Duration.millis(1500));
        }
        
        if (totalEmprunts != null) {
            animateCountUp(totalEmprunts, 0, emprunts, Duration.millis(1500));
        }
        
        if (totalAdmins != null) {
            animateCountUp(totalAdmins, 0, admins, Duration.millis(1500));
        }
        
        if (totalRetards != null) {
            animateCountUp(totalRetards, 0, retards, Duration.millis(1500));
        }
        
        if (statsLabel != null) {
            String message = String.format(
                "📊 Vue d'ensemble: %d livres | %d adhérents | %d emprunts actifs | %d retards",
                livres, adherents, emprunts, retards
            );
            animateTextReveal(statsLabel, message);
        }
    }

    /**
     * Ultra Premium Count-Up Animation
     */
    private void animateCountUp(Label label, int start, int end, Duration duration) {
        Timeline timeline = new Timeline();
        final int steps = 60;
        final double increment = (end - start) / (double) steps;
        
        for (int i = 0; i <= steps; i++) {
            final int value = start + (int)(increment * i);
            KeyFrame keyFrame = new KeyFrame(
                duration.multiply(i / (double) steps),
                e -> {
                    label.setText(String.valueOf(value));
                    // Add pulse effect
                    ScaleTransition pulse = new ScaleTransition(Duration.millis(100), label);
                    pulse.setToX(1.1);
                    pulse.setToY(1.1);
                    pulse.setAutoReverse(true);
                    pulse.setCycleCount(2);
                    pulse.play();
                }
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        
        timeline.play();
        
        // Add glow effect on completion
        timeline.setOnFinished(e -> addGlowEffect(label));
    }

    /**
     * Text Reveal Animation
     */
    private void animateTextReveal(Label label, String text) {
        label.setText("");
        Timeline timeline = new Timeline();
        
        for (int i = 0; i <= text.length(); i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(
                Duration.millis(30 * i),
                e -> label.setText(text.substring(0, index))
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        
        timeline.play();
    }

    /**
     * Enhanced Bar Chart with Custom Styling
     */
    private void setupBarChart() {
        if (barChart == null) {
            System.err.println("⚠️ BarChart is NULL!");
            return;
        }
        
        try {
            barChart.getData().clear();
            
            int livres = service.countLivres();
            int adherents = service.countAdherents();
            int emprunts = service.countEmprunts();
            int admins = service.countAdmins();

            // Create multiple series for better visual effect
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Statistiques Globales");
            
            series.getData().add(new XYChart.Data<>("📚 Livres", livres));
            series.getData().add(new XYChart.Data<>("👥 Adhérents", adherents));
            series.getData().add(new XYChart.Data<>("📖 Emprunts", emprunts));
            series.getData().add(new XYChart.Data<>("👑 Admins", admins));
            
            barChart.getData().add(series);
            barChart.setTitle("📊 Statistiques en Barres");
            barChart.setLegendVisible(false);
            barChart.setAnimated(true);
            
            CategoryAxis xAxis = (CategoryAxis) barChart.getXAxis();
            NumberAxis yAxis = (NumberAxis) barChart.getYAxis();
            
            xAxis.setLabel("Catégories");
            yAxis.setLabel("Nombre");
            yAxis.setAutoRanging(true);
            yAxis.setForceZeroInRange(true);
            
            // Apply custom animation to bars
            Platform.runLater(() -> animateBars(series));
            
            barChart.layout();
            
            System.out.println("✅ Ultra Premium BarChart setup complete!");
            
        } catch (Exception e) {
            System.err.println("❌ Error setting up BarChart:");
            e.printStackTrace();
        }
    }

    /**
     * Animate Individual Bars
     */
    private void animateBars(XYChart.Series<String, Number> series) {
        int delay = 0;
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.setScaleY(0);
                node.setOpacity(0);
                
                ScaleTransition scale = new ScaleTransition(Duration.millis(800), node);
                scale.setFromY(0);
                scale.setToY(1);
                scale.setDelay(Duration.millis(delay));
                scale.setInterpolator(Interpolator.EASE_OUT);
                
                FadeTransition fade = new FadeTransition(Duration.millis(800), node);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.setDelay(Duration.millis(delay));
                
                ParallelTransition parallel = new ParallelTransition(scale, fade);
                parallel.play();
                
                // Add hover effect
                addBarHoverEffect(node);
                
                delay += 150;
            }
        }
    }

    /**
     * Bar Hover Effect
     */
    private void addBarHoverEffect(Node bar) {
        bar.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), bar);
            scale.setToY(1.1);
            scale.play();
            
            DropShadow glow = new DropShadow();
            glow.setColor(Color.rgb(33, 150, 243, 0.8));
            glow.setRadius(20);
            bar.setEffect(glow);
        });
        
        bar.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(200), bar);
            scale.setToY(1.0);
            scale.play();
            
            bar.setEffect(null);
        });
    }

    /**
     * Enhanced Pie Chart
     */
    private void setupPieChart() {
        if (pieChart == null) {
            System.err.println("⚠️ PieChart is NULL!");
            return;
        }
        
        try {
            pieChart.getData().clear();
            
            int livres = service.countLivres();
            int adherents = service.countAdherents();
            int emprunts = service.countEmprunts();

            PieChart.Data livresData = new PieChart.Data("📚 Livres (" + livres + ")", livres);
            PieChart.Data adherentsData = new PieChart.Data("👥 Adhérents (" + adherents + ")", adherents);
            PieChart.Data empruntsData = new PieChart.Data("📖 Emprunts (" + emprunts + ")", emprunts);

            pieChart.getData().addAll(livresData, adherentsData, empruntsData);
            
            pieChart.setTitle("📊 Distribution des Données");
            pieChart.setLegendVisible(true);
            pieChart.setStartAngle(90);
            pieChart.setClockwise(true);
            pieChart.setAnimated(true);
            pieChart.setLabelsVisible(true);
            
            // Apply custom animations and effects
            Platform.runLater(() -> {
                animatePieSlices();
                addPieSliceHoverEffects();
            });
            
            pieChart.layout();
            
            System.out.println("✅ Ultra Premium PieChart setup complete!");
            
        } catch (Exception e) {
            System.err.println("❌ Error setting up PieChart:");
            e.printStackTrace();
        }
    }

    /**
     * Animate Pie Slices
     */
    private void animatePieSlices() {
        int delay = 0;
        for (PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.setScaleX(0);
                node.setScaleY(0);
                node.setOpacity(0);
                
                ScaleTransition scale = new ScaleTransition(Duration.millis(800), node);
                scale.setFromX(0);
                scale.setFromY(0);
                scale.setToX(1);
                scale.setToY(1);
                scale.setDelay(Duration.millis(delay));
                scale.setInterpolator(Interpolator.EASE_OUT);
                
                FadeTransition fade = new FadeTransition(Duration.millis(800), node);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.setDelay(Duration.millis(delay));
                
                RotateTransition rotate = new RotateTransition(Duration.millis(800), node);
                rotate.setFromAngle(-180);
                rotate.setToAngle(0);
                rotate.setDelay(Duration.millis(delay));
                
                ParallelTransition parallel = new ParallelTransition(scale, fade, rotate);
                parallel.play();
                
                delay += 200;
            }
        }
    }

    /**
     * Pie Slice Hover Effects
     */
    private void addPieSliceHoverEffects() {
        for (PieChart.Data data : pieChart.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.setOnMouseEntered(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
                    scale.setToX(1.1);
                    scale.setToY(1.1);
                    scale.play();
                    
                    DropShadow glow = new DropShadow();
                    glow.setColor(Color.rgb(156, 39, 176, 0.8));
                    glow.setRadius(25);
                    node.setEffect(glow);
                });
                
                node.setOnMouseExited(e -> {
                    ScaleTransition scale = new ScaleTransition(Duration.millis(200), node);
                    scale.setToX(1.0);
                    scale.setToY(1.0);
                    scale.play();
                    
                    node.setEffect(null);
                });
            }
        }
    }

    /**
     * Setup Line Chart (New Feature)
     */
    private void setupLineChart() {
        if (lineChart == null) return;
        
        try {
            lineChart.getData().clear();
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Tendances Mensuelles");
            
            // Sample data - replace with real monthly data
            series.getData().add(new XYChart.Data<>("Jan", 45));
            series.getData().add(new XYChart.Data<>("Fév", 62));
            series.getData().add(new XYChart.Data<>("Mar", 78));
            series.getData().add(new XYChart.Data<>("Avr", 85));
            series.getData().add(new XYChart.Data<>("Mai", 92));
            series.getData().add(new XYChart.Data<>("Juin", 105));
            
            lineChart.getData().add(series);
            lineChart.setTitle("📈 Emprunts Mensuels");
            lineChart.setCreateSymbols(true);
            lineChart.setAnimated(true);
            
            Platform.runLater(() -> animateLineChart(series));
            
        } catch (Exception e) {
            System.err.println("❌ Error setting up LineChart:");
            e.printStackTrace();
        }
    }

    /**
     * Animate Line Chart
     */
    private void animateLineChart(XYChart.Series<String, Number> series) {
        int delay = 0;
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node symbol = data.getNode();
            if (symbol != null) {
                symbol.setScaleX(0);
                symbol.setScaleY(0);
                
                ScaleTransition scale = new ScaleTransition(Duration.millis(500), symbol);
                scale.setFromX(0);
                scale.setFromY(0);
                scale.setToX(1);
                scale.setToY(1);
                scale.setDelay(Duration.millis(delay));
                scale.setInterpolator(Interpolator.EASE_OUT);
                scale.play();
                
                delay += 100;
            }
        }
    }

    /**
     * Animate All Charts
     */
    private void animateCharts() {
        if (barChart != null) {
            barChart.setOpacity(0);
            barChart.setScaleX(0.9);
            barChart.setScaleY(0.9);
            
            FadeTransition fade = new FadeTransition(Duration.millis(1000), barChart);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(300));
            fade.setInterpolator(Interpolator.EASE_OUT);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(1000), barChart);
            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setDelay(Duration.millis(300));
            scale.setInterpolator(Interpolator.EASE_OUT);
            
            ParallelTransition parallel = new ParallelTransition(fade, scale);
            parallel.play();
        }
        
        if (pieChart != null) {
            pieChart.setOpacity(0);
            pieChart.setScaleX(0.9);
            pieChart.setScaleY(0.9);
            
            FadeTransition fade = new FadeTransition(Duration.millis(1000), pieChart);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.setDelay(Duration.millis(600));
            fade.setInterpolator(Interpolator.EASE_OUT);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(1000), pieChart);
            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.setDelay(Duration.millis(600));
            scale.setInterpolator(Interpolator.EASE_OUT);
            
            ParallelTransition parallel = new ParallelTransition(fade, scale);
            parallel.play();
        }
    }

    /**
     * Add Glow Effect
     */
    private void addGlowEffect(Label label) {
        Glow glow = new Glow(0.8);
        label.setEffect(glow);
        
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.seconds(1), e -> label.setEffect(null))
        );
        timeline.play();
    }

    /**
     * Start Auto-Refresh (Every 30 seconds)
     */
    private void startAutoRefresh() {
        autoRefreshTimeline = new Timeline(
            new KeyFrame(Duration.seconds(30), e -> {
                System.out.println("🔄 Auto-refreshing statistics...");
                onRefresh();
            })
        );
        autoRefreshTimeline.setCycleCount(Timeline.INDEFINITE);
        autoRefreshTimeline.play();
    }

    /**
     * Stop Auto-Refresh
     */
    public void stopAutoRefresh() {
        if (autoRefreshTimeline != null) {
            autoRefreshTimeline.stop();
        }
    }

    /**
     * Premium Refresh with Notification
     */
    @FXML
    private void onRefresh() {
        System.out.println("🔄 Refreshing statistics with premium effects...");
        
        // Flash effect on all stat cards
        if (totalLivres != null) flashCard(totalLivres);
        if (totalAdherents != null) flashCard(totalAdherents);
        if (totalEmprunts != null) flashCard(totalEmprunts);
        if (totalAdmins != null) flashCard(totalAdmins);
        if (totalRetards != null) flashCard(totalRetards);
        
        // Reload data
        loadStatistics();
        
        if (barChart != null) {
            barChart.getData().clear();
            setupBarChart();
        }
        
        if (pieChart != null) {
            pieChart.getData().clear();
            setupPieChart();
        }
        
        if (lineChart != null) {
            lineChart.getData().clear();
            setupLineChart();
        }
        
        animateCharts();
        
        // Show success notification
        if (statsLabel != null) {
            String originalText = statsLabel.getText();
            statsLabel.setText("✅ Données actualisées avec succès!");
            statsLabel.setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
            
            Timeline resetLabel = new Timeline(
                new KeyFrame(Duration.seconds(2), e -> {
                    statsLabel.setText(originalText);
                    statsLabel.setStyle("-fx-text-fill: #90CAF9; -fx-font-weight: bold;");
                })
            );
            resetLabel.play();
        }
        
        System.out.println("✅ Premium refresh complete!");
    }

    /**
     * Flash Card Effect
     */
    private void flashCard(Label label) {
        FadeTransition flash = new FadeTransition(Duration.millis(300), label);
        flash.setFromValue(1.0);
        flash.setToValue(0.3);
        flash.setAutoReverse(true);
        flash.setCycleCount(2);
        flash.play();
    }

    /**
     * Return to Dashboard with Smooth Transition
     */
    @FXML
    private void backToDashboard() {
        try {
            stopAutoRefresh();
            
            // Exit animation
            if (mainContainer != null) {
                FadeTransition fade = new FadeTransition(Duration.millis(300), mainContainer);
                fade.setFromValue(1);
                fade.setToValue(0);
                
                ScaleTransition scale = new ScaleTransition(Duration.millis(300), mainContainer);
                scale.setToX(0.95);
                scale.setToY(0.95);
                
                ParallelTransition parallel = new ParallelTransition(fade, scale);
                parallel.setOnFinished(e -> {
                    Stage stage = (Stage) totalLivres.getScene().getWindow();
                    DashboardController.returnToDashboard(stage);
                });
                parallel.play();
            } else {
                Stage stage = (Stage) totalLivres.getScene().getWindow();
                DashboardController.returnToDashboard(stage);
            }
        } catch (Exception e) {
            System.err.println("❌ Error returning to dashboard");
            e.printStackTrace();
        }
    }
}