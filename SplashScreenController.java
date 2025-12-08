package controller;

import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class SplashScreenController {

    @FXML private StackPane splashRoot;
    @FXML private VBox splashContainer;
    @FXML private ImageView logoImage;
    @FXML private StackPane logoContainer;
    @FXML private Circle logoCircle;
    @FXML private Circle glowCircle1;
    @FXML private Circle glowCircle2;
    @FXML private Circle glowCircle3;

    private Stage stage;

    @FXML
    private void initialize() {
        System.out.println("🎬 Splash Screen Initialized");
        
        // Cache tout au début
        hideAllElements();
        
        // Lance l'animation style Riot Games
        startRiotStyleIntro();
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    private void hideAllElements() {
        // Tout invisible au départ
        if (logoImage != null) {
            logoImage.setOpacity(0);
            logoImage.setScaleX(0.1);
            logoImage.setScaleY(0.1);
        }
        if (logoCircle != null) logoCircle.setOpacity(0);
        if (glowCircle1 != null) glowCircle1.setOpacity(0);
        if (glowCircle2 != null) glowCircle2.setOpacity(0);
        if (glowCircle3 != null) glowCircle3.setOpacity(0);
    }

    private void startRiotStyleIntro() {
        SequentialTransition mainSequence = new SequentialTransition();
        
        // 1. Petit délai initial (flash noir)
        mainSequence.getChildren().add(new PauseTransition(Duration.millis(300)));
        
        // 2. EXPLOSION DU LOGO (comme Riot)
        mainSequence.getChildren().add(createRiotLogoExplosion());
        
        // 3. Pause pour admirer le logo (5 secondes)
        mainSequence.getChildren().add(new PauseTransition(Duration.millis(3000)));
        
        // 4. Transition vers l'app
        mainSequence.setOnFinished(e -> transitionToApp());
        
        mainSequence.play();
    }

    private Animation createRiotLogoExplosion() {
        ParallelTransition explosion = new ParallelTransition();
        
        if (logoImage != null) {
            // 1. SCALE EXPLOSION - de très petit à grand avec bounce
            ScaleTransition scale = new ScaleTransition(Duration.millis(800), logoImage);
            scale.setFromX(0.1);
            scale.setFromY(0.1);
            scale.setToX(1.2);  // Overshoot
            scale.setToY(1.2);
            scale.setInterpolator(Interpolator.EASE_OUT);
            
            // 2. FADE IN rapide
            FadeTransition fade = new FadeTransition(Duration.millis(600), logoImage);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            // 3. ROTATION légère (comme un impact)
            RotateTransition rotate = new RotateTransition(Duration.millis(800), logoImage);
            rotate.setFromAngle(-15);
            rotate.setToAngle(5);
            rotate.setInterpolator(Interpolator.EASE_OUT);
            
            explosion.getChildren().addAll(scale, fade, rotate);
            
            // Après l'explosion, stabiliser le logo
            explosion.setOnFinished(e -> stabilizeLogo());
        }
        
        // CERCLES DE CHOC (comme les ondes de Riot)
        if (logoCircle != null) {
            createShockwave(logoCircle, 0, explosion);
        }
        if (glowCircle1 != null) {
            createShockwave(glowCircle1, 100, explosion);
        }
        if (glowCircle2 != null) {
            createShockwave(glowCircle2, 200, explosion);
        }
        if (glowCircle3 != null) {
            createShockwave(glowCircle3, 300, explosion);
        }
        
        return explosion;
    }

    private void createShockwave(Circle circle, double delayMs, ParallelTransition parent) {
        // Shockwave qui part du centre et s'étend
        circle.setScaleX(0.3);
        circle.setScaleY(0.3);
        circle.setOpacity(0);
        
        ParallelTransition shockwave = new ParallelTransition();
        
        // Expansion
        ScaleTransition expand = new ScaleTransition(Duration.millis(1000), circle);
        expand.setFromX(0.3);
        expand.setFromY(0.3);
        expand.setToX(2.0);
        expand.setToY(2.0);
        expand.setDelay(Duration.millis(delayMs));
        
        // Fade qui apparaît puis disparaît
        FadeTransition fade = new FadeTransition(Duration.millis(1000), circle);
        fade.setFromValue(0);
        fade.setToValue(0.8);
        fade.setDelay(Duration.millis(delayMs));
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), circle);
        fadeOut.setFromValue(0.8);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.millis(delayMs + 500));
        
        shockwave.getChildren().addAll(expand, fade, fadeOut);
        parent.getChildren().add(shockwave);
    }

    private void stabilizeLogo() {
        // Petit bounce back pour stabiliser le logo à sa taille normale
        if (logoImage != null) {
            ScaleTransition stabilize = new ScaleTransition(Duration.millis(300), logoImage);
            stabilize.setFromX(1.2);
            stabilize.setFromY(1.2);
            stabilize.setToX(1.0);
            stabilize.setToY(1.0);
            stabilize.setInterpolator(Interpolator.EASE_BOTH);
            
            RotateTransition straighten = new RotateTransition(Duration.millis(300), logoImage);
            straighten.setFromAngle(5);
            straighten.setToAngle(0);
            straighten.setInterpolator(Interpolator.EASE_BOTH);
            
            ParallelTransition stabilization = new ParallelTransition(stabilize, straighten);
            stabilization.play();
        }
    }

    private void transitionToApp() {
        if (stage == null) {
            System.err.println("❌ Stage is null!");
            return;
        }
        
        // Fade out rapide du splash screen
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), splashRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> loadLoginScreen());
        fadeOut.play();
    }

    private void loadLoginScreen() {
        try {
            System.out.println("🔄 Loading main application...");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/login.css").toExternalForm());
            
            stage.setScene(scene);
            stage.setTitle("GHARBI'S LIBRARY - Login");
            stage.setResizable(false);
            stage.centerOnScreen();
            
            // Fade in de l'app principale
            scene.getRoot().setOpacity(0);
            FadeTransition fadeIn = new FadeTransition(Duration.millis(400), scene.getRoot());
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
            
            System.out.println("✅ Application loaded successfully");
            
        } catch (IOException ex) {
            System.err.println("❌ ERROR: Unable to load application");
            ex.printStackTrace();
        }
    }
}