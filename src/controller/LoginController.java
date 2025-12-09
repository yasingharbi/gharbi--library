package controller;

import classes.Utilisateur;
import database.DBUtilisateur;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.ParallelTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField passwordVisibleField;
    @FXML private CheckBox showPassword, staySignedIn;
    @FXML private Label messageLabel;
    @FXML private Label titleLabel;
    @FXML private Button loginButton;
    @FXML private Hyperlink forgotLink, registerLink;
    @FXML private ImageView logoImage;
    @FXML private ImageView backgroundImage;
    @FXML private VBox loginCard;

    @FXML
    private void initialize() {
        try {
            // Load custom font
            Font.loadFont(getClass().getResourceAsStream("/fonts/Poppins-Regular.ttf"), 16);
            
            // Load logo
            if (logoImage != null) {
                try {
                    Image logo = new Image(getClass().getResourceAsStream("/images/logo.png"));
                    logoImage.setImage(logo);
                } catch (Exception e) {
                    System.err.println("⚠ Logo not found");
                }
            }
            
            // Load background
            if (backgroundImage != null) {
                try {
                    Image bg = new Image(getClass().getResourceAsStream("/images/login.jpg"));
                    backgroundImage.setImage(bg);
                } catch (Exception e) {
                    System.err.println("⚠ Background not found");
                }
            }
        } catch (Exception e) {
            System.err.println("⚠ Error loading images: " + e.getMessage());
        }

        // Set library title
        if (titleLabel != null) {
            titleLabel.setText("GHARBI'S LIBRARY");
        }

        // Password visibility toggle
        if (passwordVisibleField != null && showPassword != null && passwordField != null) {
            passwordVisibleField.managedProperty().bind(showPassword.selectedProperty());
            passwordVisibleField.visibleProperty().bind(showPassword.selectedProperty());
            passwordField.managedProperty().bind(showPassword.selectedProperty().not());
            passwordField.visibleProperty().bind(showPassword.selectedProperty().not());
            passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        }

        // Button hover effects
        setupButtonEffects();
        setupLinkEffects();

        if (messageLabel != null) {
            messageLabel.setText("");
        }

        // Entrance animation for login card
        animateLoginCardEntrance();
    }

    private void setupButtonEffects() {
        if (loginButton != null) {
            loginButton.setStyle("-fx-background-color: linear-gradient(to right, #32be8f, #38d39f); " +
                               "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; " +
                               "-fx-font-size: 16px; -fx-cursor: hand;");
            
            loginButton.setOnMouseEntered(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), loginButton);
                scale.setToX(1.05);
                scale.setToY(1.05);
                scale.play();
                loginButton.setStyle("-fx-background-color: linear-gradient(to right, #38d39f, #32be8f); " +
                                   "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; " +
                                   "-fx-font-size: 16px; -fx-cursor: hand;");
            });
            
            loginButton.setOnMouseExited(e -> {
                ScaleTransition scale = new ScaleTransition(Duration.millis(200), loginButton);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
                loginButton.setStyle("-fx-background-color: linear-gradient(to right, #32be8f, #38d39f); " +
                                   "-fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 25; " +
                                   "-fx-font-size: 16px; -fx-cursor: hand;");
            });
        }
    }

    private void setupLinkEffects() {
        if (forgotLink != null) {
            forgotLink.setOnMouseEntered(e -> {
                forgotLink.setStyle("-fx-text-fill: #32be8f; -fx-underline: true; -fx-font-weight: bold;");
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), forgotLink);
                scale.setToX(1.1);
                scale.setToY(1.1);
                scale.play();
            });
            forgotLink.setOnMouseExited(e -> {
                forgotLink.setStyle("-fx-text-fill: #38d39f; -fx-underline: false;");
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), forgotLink);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            });
        }

        if (registerLink != null) {
            registerLink.setOnMouseEntered(e -> {
                registerLink.setStyle("-fx-text-fill: #32be8f; -fx-underline: true; -fx-font-weight: bold;");
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), registerLink);
                scale.setToX(1.1);
                scale.setToY(1.1);
                scale.play();
            });
            registerLink.setOnMouseExited(e -> {
                registerLink.setStyle("-fx-text-fill: #38d39f; -fx-underline: false;");
                ScaleTransition scale = new ScaleTransition(Duration.millis(100), registerLink);
                scale.setToX(1.0);
                scale.setToY(1.0);
                scale.play();
            });
        }
    }

    private void animateLoginCardEntrance() {
        if (loginCard != null) {
            loginCard.setOpacity(0);
            loginCard.setScaleX(0.7);
            loginCard.setScaleY(0.7);
            loginCard.setTranslateY(50);
            
            FadeTransition fade = new FadeTransition(Duration.millis(800), loginCard);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(800), loginCard);
            scale.setFromX(0.7);
            scale.setFromY(0.7);
            scale.setToX(1.0);
            scale.setToY(1.0);
            
            TranslateTransition translate = new TranslateTransition(Duration.millis(800), loginCard);
            translate.setFromY(50);
            translate.setToY(0);
            
            ParallelTransition parallel = new ParallelTransition(fade, scale, translate);
            parallel.play();
        }
    }

    @FXML
    private void onLogin(javafx.event.ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = showPassword != null && showPassword.isSelected() 
            ? passwordVisibleField.getText() 
            : passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            setMessage("⚠ Please fill in all fields", "#ff4444");
            shakeLoginCard();
            return;
        }

        // Show loading state
        loginButton.setDisable(true);
        loginButton.setText("Logging in...");

        Utilisateur user = DBUtilisateur.login(username, password);
        
        if (user != null) {
            String roleDisplay = user.getRole().equalsIgnoreCase("admin") ? "Administrator" : "Member";
            setMessage("✅ " + user.getUsername() + " connected successfully as " + roleDisplay + "!", "#22dd22");
            
            // Delay to show success message
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> openDashboard(event, user.getUsername(), user.getRole()));
            pause.play();
        } else {
            setMessage("❌ Invalid credentials. Please try again.", "#ff4444");
            shakeLoginCard();
            loginButton.setDisable(false);
            loginButton.setText("Log in");
        }
    }

	    @FXML
	    private void onRegister(javafx.event.ActionEvent event) {
	        try {
	            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/register.fxml"));
	            Scene scene = new Scene(loader.load());
	            scene.getStylesheets().add(getClass().getResource("/css/register.css").toExternalForm());
	            
	            Stage stage = new Stage();
	            stage.setScene(scene);
	            stage.setTitle("GHARBI'S LIBRARY - Register");
	            stage.setResizable(false);
	            stage.centerOnScreen();
	            stage.show();
	        } catch (IOException ex) {
	            System.err.println("❌ ERROR: Unable to load register.fxml");
	            ex.printStackTrace();
	            setMessage("❌ Unable to open registration.", "#ff4444");
	        }
	    }

    @FXML
    private void onForgotPassword(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/forgot_password.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("/css/forgot_password.css").toExternalForm());
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("GHARBI'S LIBRARY - Password Recovery");
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            System.err.println("❌ ERROR: Unable to load forgot_password.fxml");
            ex.printStackTrace();
            setMessage("❌ Unable to open password recovery.", "#ff4444");
        }
    }

    private void openDashboard(javafx.event.ActionEvent event, String username, String role) {
        try {
            Node currentRoot = ((Node) event.getSource()).getScene().getRoot();
            
            FadeTransition fadeOut = new FadeTransition(Duration.millis(500), currentRoot);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(e -> {
                try {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                    Scene scene;
                    
                    if ("admin".equalsIgnoreCase(role)) {
                        // Admin Dashboard
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
                        scene = new Scene(loader.load());
                        scene.getStylesheets().add(getClass().getResource("/css/dashboard.css").toExternalForm());
                        
                        DashboardController controller = loader.getController();
                        controller.setUserInfo(username, role);
                        
                        stage.setTitle("GHARBI'S LIBRARY - Admin Dashboard");
                    } else {
                        // User Dashboard
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/user_dashboard.fxml"));
                        scene = new Scene(loader.load());
                        scene.getStylesheets().add(getClass().getResource("/css/user_dashboard.css").toExternalForm());
                        
                        UserDashboardController controller = loader.getController();
                        controller.setUserInfo(username, username); // Use username as phone for now
                        
                        stage.setTitle("GHARBI'S LIBRARY - Member Portal");
                    }
                    
                    stage.setScene(scene);
                    stage.setResizable(true);
                    stage.setMaximized(true);
                    stage.centerOnScreen();
                    
                    scene.getRoot().setOpacity(0);
                    FadeTransition fadeIn = new FadeTransition(Duration.millis(500), scene.getRoot());
                    fadeIn.setFromValue(0.0);
                    fadeIn.setToValue(1.0);
                    fadeIn.play();
                    
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            fadeOut.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMessage(String text, String color) {
        if (messageLabel != null) {
            messageLabel.setText(text);
            messageLabel.setStyle("-fx-text-fill: " + color + "; -fx-font-size: 14px; -fx-font-weight: bold;");
            
            // Fade in message
            FadeTransition fade = new FadeTransition(Duration.millis(300), messageLabel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    private void shakeLoginCard() {
        if (loginCard != null) {
            TranslateTransition tt = new TranslateTransition(Duration.millis(70), loginCard);
            tt.setByX(15);
            tt.setCycleCount(6);
            tt.setAutoReverse(true);
            tt.setOnFinished(e -> loginCard.setTranslateX(0));
            tt.play();
        }
    }
}