package controller;

import database.DBUtilisateur;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.security.SecureRandom;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private Label messageLabel;
    @FXML private Label messageIcon;
    @FXML private Label newPasswordLabel;
    @FXML private Button resetButton;
    @FXML private Button closeButton;
    @FXML private Button backButton;
    @FXML private Button copyButton;
    @FXML private VBox forgotCard;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private VBox passwordContainer;
    @FXML private VBox messageContainer;
    @FXML private Label strengthLabel;
    @FXML private Label progressText;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    @FXML
    private void initialize() {
        setupButtonEffects();
        setupInputValidation();
        animateEntrance();
        
        // Initial visibility
        if (newPasswordLabel != null) newPasswordLabel.setVisible(false);
        if (closeButton != null) closeButton.setVisible(false);
        if (copyButton != null) copyButton.setVisible(false);
        if (progressIndicator != null) progressIndicator.setVisible(false);
        if (passwordContainer != null) passwordContainer.setVisible(false);
        if (messageContainer != null) messageContainer.setVisible(false);
        if (strengthLabel != null) strengthLabel.setVisible(false);
        if (progressText != null) progressText.setVisible(false);
    }

    private void setupButtonEffects() {
        addButtonHoverEffect(resetButton, 1.05);
        addButtonHoverEffect(closeButton, 1.03);
        addButtonHoverEffect(backButton, 1.03);
        addButtonHoverEffect(copyButton, 1.08);
    }

    private void addButtonHoverEffect(Button button, double scale) {
        if (button == null) return;
        
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(scale);
            st.setToY(scale);
            st.play();
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }

    private void setupInputValidation() {
        if (emailField == null) return;
        
        emailField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.isEmpty()) {
                validateEmailFormat(newVal);
            } else {
                emailField.setStyle("");
            }
        });
    }

    private void validateEmailFormat(String email) {
        if (EMAIL_PATTERN.matcher(email).matches()) {
            emailField.setStyle("-fx-border-color: #4CAF50;");
        } else {
            emailField.setStyle("-fx-border-color: #FF5252;");
        }
    }

    private void animateEntrance() {
        if (forgotCard == null) return;
        
        forgotCard.setOpacity(0);
        forgotCard.setTranslateY(-30);

        FadeTransition fade = new FadeTransition(Duration.millis(800), forgotCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition translate = new TranslateTransition(Duration.millis(800), forgotCard);
        translate.setFromY(-30);
        translate.setToY(0);

        ParallelTransition parallel = new ParallelTransition(fade, translate);
        parallel.setInterpolator(Interpolator.EASE_OUT);
        parallel.play();
    }

    @FXML
    private void onReset() {
        String email = emailField.getText().trim();

        // Comprehensive validation
        if (email.isEmpty()) {
            showMessage("❌ Veuillez saisir une adresse email", "#FF5252", false);
            shakeCard();
            return;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            showMessage("❌ Format d'email invalide. Exemple: utilisateur@domaine.com", "#FF5252", false);
            shakeCard();
            return;
        }

        String username = email.substring(0, email.indexOf("@"));

        if (username.length() < 3) {
            showMessage("❌ Le nom d'utilisateur doit contenir au moins 3 caractères", "#FF5252", false);
            shakeCard();
            return;
        }

        // Show loading state
        setLoadingState(true);

        // Asynchronous password reset
        CompletableFuture.runAsync(() -> {
            try {
                // Simulate processing time for better UX
                Thread.sleep(1500);
                
                String newPassword = generateSecurePassword(14);
                boolean success = DBUtilisateur.resetPassword(username, newPassword);

                Platform.runLater(() -> {
                    setLoadingState(false);
                    
                    if (success) {
                        handleSuccessfulReset(newPassword);
                    } else {
                        handleFailedReset();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setLoadingState(false);
                    showMessage("❌ Erreur système. Veuillez réessayer", "#FF5252", false);
                    shakeCard();
                });
            }
        });
    }

    private void setLoadingState(boolean loading) {
        resetButton.setDisable(loading);
        emailField.setDisable(loading);
        
        if (loading) {
            resetButton.setText("⏳ Traitement en cours...");
            if (progressIndicator != null) {
                progressIndicator.setVisible(true);
                FadeTransition fade = new FadeTransition(Duration.millis(300), progressIndicator);
                fade.setFromValue(0);
                fade.setToValue(1);
                fade.play();
            }
            if (progressText != null) {
                progressText.setVisible(true);
            }
        } else {
            resetButton.setText("🚀 Réinitialiser le Mot de Passe");
            if (progressIndicator != null) {
                progressIndicator.setVisible(false);
            }
            if (progressText != null) {
                progressText.setVisible(false);
            }
        }
    }

    private void handleSuccessfulReset(String newPassword) {
        showMessage("✅ Mot de passe réinitialisé avec succès!", "#4CAF50", true);
        
        // Display new password with animation
        if (passwordContainer != null) {
            passwordContainer.setVisible(true);
            
            if (newPasswordLabel != null) {
                newPasswordLabel.setText(newPassword);
                newPasswordLabel.setVisible(true);
            }
            
            if (copyButton != null) {
                copyButton.setVisible(true);
            }
            
            // Animate password reveal
            FadeTransition fade = new FadeTransition(Duration.millis(500), passwordContainer);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(500), passwordContainer);
            scale.setFromX(0.9);
            scale.setFromY(0.9);
            scale.setToX(1.0);
            scale.setToY(1.0);
            
            ParallelTransition parallel = new ParallelTransition(fade, scale);
            parallel.setInterpolator(Interpolator.EASE_OUT);
            parallel.play();
        }

        // Show password strength
        if (strengthLabel != null) {
            strengthLabel.setText("🛡️ Force: Très Forte");
            strengthLabel.setVisible(true);
            
            FadeTransition fade = new FadeTransition(Duration.millis(400), strengthLabel);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }

        // Switch buttons
        resetButton.setVisible(false);
        if (closeButton != null) {
            closeButton.setVisible(true);
            
            FadeTransition fade = new FadeTransition(Duration.millis(400), closeButton);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    private void handleFailedReset() {
        showMessage("❌ Utilisateur introuvable. Vérifiez votre email", "#FF5252", false);
        resetButton.setDisable(false);
        shakeCard();
        
        // Pulse effect on email field
        pulseElement(emailField);
    }

    private String generateSecurePassword(int length) {
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String digits = "0123456789";
        String special = "@#$%&*!?";
        String allChars = uppercase + lowercase + digits + special;
        
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);
        
        // Ensure at least one of each type
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));
        
        // Fill remaining length
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }
        
        // Shuffle the password
        return shuffleString(password.toString(), random);
    }

    private String shuffleString(String str, SecureRandom random) {
        char[] chars = str.toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }
        return new String(chars);
    }

    @FXML
    private void onCopyPassword() {
        if (newPasswordLabel == null) return;
        
        String password = newPasswordLabel.getText();
        
        // Copy to clipboard
        javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
        content.putString(password);
        javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
        
        // Visual feedback
        String originalText = copyButton.getText();
        copyButton.setText("✓ Copié!");
        copyButton.setStyle("-fx-background-color: linear-gradient(to right, #4CAF50, #45A049);");
        
        // Pulse animation
        ScaleTransition scale = new ScaleTransition(Duration.millis(150), copyButton);
        scale.setToX(1.15);
        scale.setToY(1.15);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.play();
        
        // Reset after delay
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(e -> {
            copyButton.setText(originalText);
            copyButton.setStyle("");
        });
        pause.play();
    }

    @FXML
    private void onClose() {
        animateExit();
    }

    @FXML
    private void onBack() {
        animateExit();
    }

    private void animateExit() {
        if (forgotCard == null) return;
        
        Stage stage = (Stage) emailField.getScene().getWindow();
        
        FadeTransition fade = new FadeTransition(Duration.millis(400), forgotCard);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(400), forgotCard);
        translate.setToY(30);
        
        ParallelTransition parallel = new ParallelTransition(fade, translate);
        parallel.setInterpolator(Interpolator.EASE_IN);
        parallel.setOnFinished(e -> stage.close());
        parallel.play();
    }

    private void showMessage(String text, String color, boolean success) {
        if (messageLabel == null || messageContainer == null) return;
        
        messageContainer.setVisible(true);
        messageLabel.setText(text);
        
        // Set icon based on success/failure
        if (messageIcon != null) {
            messageIcon.setText(success ? "✅" : "❌");
        }
        
        String bgColor = success ? "rgba(76, 175, 80, 0.12)" : "rgba(255, 82, 82, 0.12)";
        String borderColor = success ? "rgba(76, 175, 80, 0.35)" : "rgba(255, 82, 82, 0.35)";
        
        messageContainer.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s;",
            bgColor, borderColor
        ));
        
        messageLabel.setStyle("-fx-text-fill: " + color + ";");

        // Slide in animation
        messageContainer.setTranslateY(-10);
        messageContainer.setOpacity(0);
        
        FadeTransition fade = new FadeTransition(Duration.millis(400), messageContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(400), messageContainer);
        translate.setFromY(-10);
        translate.setToY(0);
        
        ParallelTransition parallel = new ParallelTransition(fade, translate);
        parallel.setInterpolator(Interpolator.EASE_OUT);
        parallel.play();
    }

    private void shakeCard() {
        if (forgotCard == null) return;
        
        TranslateTransition shake = new TranslateTransition(Duration.millis(60), forgotCard);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.setOnFinished(e -> forgotCard.setTranslateX(0));
        shake.play();
    }

    private void pulseElement(javafx.scene.Node node) {
        if (node == null) return;
        
        ScaleTransition pulse = new ScaleTransition(Duration.millis(200), node);
        pulse.setToX(1.05);
        pulse.setToY(1.05);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(2);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        pulse.play();
    }
}