package controller;

import database.DBUtilisateur;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.effect.*;
import javafx.scene.paint.Color;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;
import java.util.List;
import java.util.ArrayList;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField, confirmPasswordField;
    @FXML private TextField passwordVisibleField, confirmVisibleField;
    @FXML private CheckBox showPasswordCheck;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label messageLabel;
    @FXML private Label messageIcon;
    @FXML private VBox messageContainer;
    @FXML private Button registerButton;
    @FXML private Button cancelButton;
    @FXML private VBox registerCard;
    @FXML private ProgressIndicator strengthIndicator;
    @FXML private Label strengthLabel;
    @FXML private HBox strengthBars;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label loadingText;
    @FXML private Label usernameValidation;
    @FXML private Label passwordValidation;
    @FXML private Label confirmValidation;

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
    private static final int MIN_PASSWORD_LENGTH = 8;
    private static final int STRONG_PASSWORD_LENGTH = 12;

    private Timeline breathingAnimation;
    private Timeline glowPulseAnimation;
    private boolean isProcessing = false;

    @FXML
    private void initialize() {
        setupRoleComboBox();
        setupPasswordVisibility();
        setupAdvancedButtonEffects();
        setupEnhancedPasswordStrength();
        setupIntelligentValidation();
        initializePremiumStrengthBars();
        setupAccessibilityFeatures();
        playEntranceAnimation();
        startAmbientAnimations();
        
        initializeVisibility();
    }

    private void initializeVisibility() {
        if (messageContainer != null) messageContainer.setVisible(false);
        if (loadingIndicator != null) loadingIndicator.setVisible(false);
        if (loadingText != null) loadingText.setVisible(false);
        if (usernameValidation != null) usernameValidation.setVisible(false);
        if (passwordValidation != null) passwordValidation.setVisible(false);
        if (confirmValidation != null) confirmValidation.setVisible(false);
    }

    private void setupRoleComboBox() {
        if (roleCombo == null) return;
        
        roleCombo.getItems().clear();
        roleCombo.getItems().addAll(
            "👤 Standard Member",
            "👑 Administrator"
        );
        roleCombo.setValue("👤 Standard Member");
        
        // Add smooth transition effect
        roleCombo.setOnShowing(e -> animateComboBoxExpand());
        roleCombo.setOnHiding(e -> animateComboBoxCollapse());
    }

    private void animateComboBoxExpand() {
        if (roleCombo == null) return;
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), roleCombo);
        scale.setToX(1.02);
        scale.setToY(1.02);
        scale.setInterpolator(Interpolator.EASE_OUT);
        scale.play();
    }

    private void animateComboBoxCollapse() {
        if (roleCombo == null) return;
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), roleCombo);
        scale.setToX(1.0);
        scale.setToY(1.0);
        scale.setInterpolator(Interpolator.EASE_IN);
        scale.play();
    }

    private void setupPasswordVisibility() {
        if (showPasswordCheck == null || passwordVisibleField == null || confirmVisibleField == null) return;
        
        passwordVisibleField.managedProperty().bind(showPasswordCheck.selectedProperty());
        passwordVisibleField.visibleProperty().bind(showPasswordCheck.selectedProperty());
        
        confirmVisibleField.managedProperty().bind(showPasswordCheck.selectedProperty());
        confirmVisibleField.visibleProperty().bind(showPasswordCheck.selectedProperty());

        passwordField.managedProperty().bind(showPasswordCheck.selectedProperty().not());
        passwordField.visibleProperty().bind(showPasswordCheck.selectedProperty().not());
        
        confirmPasswordField.managedProperty().bind(showPasswordCheck.selectedProperty().not());
        confirmPasswordField.visibleProperty().bind(showPasswordCheck.selectedProperty().not());

        passwordVisibleField.textProperty().bindBidirectional(passwordField.textProperty());
        confirmVisibleField.textProperty().bindBidirectional(confirmPasswordField.textProperty());
        
        showPasswordCheck.selectedProperty().addListener((obs, oldVal, newVal) -> {
            playPasswordToggleAnimation(newVal);
        });
    }

    private void playPasswordToggleAnimation(boolean show) {
        javafx.scene.Node target = show ? passwordVisibleField : passwordField;
        javafx.scene.Node confirmTarget = show ? confirmVisibleField : confirmPasswordField;
        
        // Fade and scale animation
        FadeTransition fade1 = new FadeTransition(Duration.millis(250), target);
        fade1.setFromValue(0);
        fade1.setToValue(1);
        
        ScaleTransition scale1 = new ScaleTransition(Duration.millis(250), target);
        scale1.setFromX(0.95);
        scale1.setFromY(0.95);
        scale1.setToX(1.0);
        scale1.setToY(1.0);
        
        FadeTransition fade2 = new FadeTransition(Duration.millis(250), confirmTarget);
        fade2.setFromValue(0);
        fade2.setToValue(1);
        
        ScaleTransition scale2 = new ScaleTransition(Duration.millis(250), confirmTarget);
        scale2.setFromX(0.95);
        scale2.setFromY(0.95);
        scale2.setToX(1.0);
        scale2.setToY(1.0);
        
        ParallelTransition parallel = new ParallelTransition(fade1, scale1, fade2, scale2);
        parallel.setInterpolator(Interpolator.EASE_BOTH);
        parallel.play();
    }

    private void setupIntelligentValidation() {
        if (usernameField != null && usernameValidation != null) {
            usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
                validateUsernameAdvanced(newVal);
                updateFormValidationState();
            });
        }
        
        if (passwordField != null && passwordValidation != null) {
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                validatePasswordAdvanced(newVal);
                validatePasswordMatchAdvanced();
                updateFormValidationState();
            });
        }
        
        if (confirmPasswordField != null && confirmValidation != null) {
            confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
                validatePasswordMatchAdvanced();
                updateFormValidationState();
            });
        }
    }

    private void validateUsernameAdvanced(String username) {
        if (username == null || username.isEmpty()) {
            hideValidationWithAnimation(usernameValidation);
            return;
        }
        
        List<String> issues = new ArrayList<>();
        
        if (username.length() < 3) {
            showValidationAdvanced(usernameValidation, "⚠ Minimum 3 characters required", ValidationLevel.WARNING);
        } else if (username.length() > 20) {
            showValidationAdvanced(usernameValidation, "⚠ Maximum 20 characters allowed", ValidationLevel.WARNING);
        } else if (!USERNAME_PATTERN.matcher(username).matches()) {
            showValidationAdvanced(usernameValidation, "⚠ Only letters, numbers, and underscores", ValidationLevel.WARNING);
        } else {
            showValidationAdvanced(usernameValidation, "✓ Username valid", ValidationLevel.SUCCESS);
        }
    }

    private void validatePasswordAdvanced(String password) {
        if (password == null || password.isEmpty()) {
            hideValidationWithAnimation(passwordValidation);
            return;
        }
        
        if (password.length() < MIN_PASSWORD_LENGTH) {
            showValidationAdvanced(passwordValidation, "⚠ Minimum 8 characters required", ValidationLevel.WARNING);
        } else if (password.length() >= STRONG_PASSWORD_LENGTH) {
            showValidationAdvanced(passwordValidation, "✓ Strong length", ValidationLevel.SUCCESS);
        } else {
            showValidationAdvanced(passwordValidation, "✓ Valid length", ValidationLevel.INFO);
        }
    }

    private void validatePasswordMatchAdvanced() {
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        
        if (confirm == null || confirm.isEmpty()) {
            hideValidationWithAnimation(confirmValidation);
            return;
        }
        
        if (!password.equals(confirm)) {
            showValidationAdvanced(confirmValidation, "⚠ Passwords do not match", ValidationLevel.ERROR);
        } else {
            showValidationAdvanced(confirmValidation, "✓ Passwords match perfectly", ValidationLevel.SUCCESS);
        }
    }

    private enum ValidationLevel {
        SUCCESS("#10b981", "rgba(16, 185, 129, 0.15)", "rgba(16, 185, 129, 0.4)"),
        INFO("#3b82f6", "rgba(59, 130, 246, 0.15)", "rgba(59, 130, 246, 0.4)"),
        WARNING("#f59e0b", "rgba(245, 158, 11, 0.15)", "rgba(245, 158, 11, 0.4)"),
        ERROR("#ef4444", "rgba(239, 68, 68, 0.15)", "rgba(239, 68, 68, 0.4)");
        
        final String textColor, bgColor, borderColor;
        
        ValidationLevel(String textColor, String bgColor, String borderColor) {
            this.textColor = textColor;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
        }
    }

    private void showValidationAdvanced(Label label, String text, ValidationLevel level) {
        if (label == null) return;
        
        label.setText(text);
        label.setStyle(String.format(
            "-fx-text-fill: %s; " +
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 1.5; " +
            "-fx-border-radius: 8; " +
            "-fx-background-radius: 8; " +
            "-fx-padding: 6 12; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: 700; " +
            "-fx-letter-spacing: 0.3;",
            level.textColor, level.bgColor, level.borderColor
        ));
        
        if (!label.isVisible()) {
            label.setVisible(true);
            
            FadeTransition fade = new FadeTransition(Duration.millis(300), label);
            fade.setFromValue(0);
            fade.setToValue(1);
            
            TranslateTransition slide = new TranslateTransition(Duration.millis(300), label);
            slide.setFromY(-5);
            slide.setToY(0);
            
            ParallelTransition parallel = new ParallelTransition(fade, slide);
            parallel.setInterpolator(Interpolator.EASE_OUT);
            parallel.play();
        }
    }

    private void hideValidationWithAnimation(Label label) {
        if (label != null && label.isVisible()) {
            FadeTransition fade = new FadeTransition(Duration.millis(250), label);
            fade.setFromValue(1);
            fade.setToValue(0);
            
            TranslateTransition slide = new TranslateTransition(Duration.millis(250), label);
            slide.setToY(-5);
            
            ParallelTransition parallel = new ParallelTransition(fade, slide);
            parallel.setInterpolator(Interpolator.EASE_IN);
            parallel.setOnFinished(e -> label.setVisible(false));
            parallel.play();
        }
    }

    private void updateFormValidationState() {
        boolean isValid = isFormValid();
        
        if (registerButton != null) {
            registerButton.setDisable(!isValid && !isProcessing);
            
            if (isValid && !isProcessing) {
                addButtonReadyGlow(registerButton);
            }
        }
    }

    private boolean isFormValid() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();
        
        return !username.isEmpty() &&
               USERNAME_PATTERN.matcher(username).matches() &&
               password.length() >= MIN_PASSWORD_LENGTH &&
               password.equals(confirm);
    }

    private void addButtonReadyGlow(Button button) {
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#10b981", 0.6));
        glow.setRadius(20);
        glow.setSpread(0.5);
        button.setEffect(glow);
    }

    private void initializePremiumStrengthBars() {
        if (strengthBars == null) return;
        
        strengthBars.getChildren().clear();
        
        for (int i = 0; i < 6; i++) {
            Region bar = new Region();
            bar.setPrefWidth(32);
            bar.setPrefHeight(6);
            bar.setStyle(
                "-fx-background-color: rgba(71, 85, 105, 0.3); " +
                "-fx-background-radius: 3; " +
                "-fx-effect: innershadow(gaussian, rgba(0, 0, 0, 0.3), 2, 0.5, 0, 1);"
            );
            strengthBars.getChildren().add(bar);
        }
    }

    private void setupEnhancedPasswordStrength() {
        if (passwordField == null) return;
        
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateEnhancedPasswordStrength(newVal);
        });
    }

    private void updateEnhancedPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            if (strengthLabel != null) strengthLabel.setText("");
            if (strengthIndicator != null) strengthIndicator.setProgress(0);
            updatePremiumStrengthBars(0, "");
            return;
        }

        int strength = calculatePasswordStrength(password);
        double progress = Math.min(strength / 6.0, 1.0);
        
        if (strengthIndicator != null) {
            animateProgressIndicator(strengthIndicator, progress);
        }

        String strengthText;
        String color;
        
        if (strength <= 2) {
            strengthText = "🔴 Very Weak";
            color = "#ef4444";
        } else if (strength == 3) {
            strengthText = "🟠 Weak";
            color = "#f59e0b";
        } else if (strength == 4) {
            strengthText = "🟡 Moderate";
            color = "#eab308";
        } else if (strength == 5) {
            strengthText = "🟢 Strong";
            color = "#22c55e";
        } else {
            strengthText = "🟢 Very Strong";
            color = "#10b981";
        }

        if (strengthLabel != null) {
            animateLabelChange(strengthLabel, strengthText, color);
        }
        
        updatePremiumStrengthBars(strength, color);
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;
        
        if (password.length() >= 8) strength++;
        if (password.length() >= 12) strength++;
        if (UPPERCASE_PATTERN.matcher(password).matches()) strength++;
        if (LOWERCASE_PATTERN.matcher(password).matches()) strength++;
        if (DIGIT_PATTERN.matcher(password).matches()) strength++;
        if (SPECIAL_PATTERN.matcher(password).matches()) strength++;
        
        return strength;
    }

    private void animateProgressIndicator(ProgressIndicator indicator, double targetProgress) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(indicator.progressProperty(), indicator.getProgress())),
            new KeyFrame(Duration.millis(500), new KeyValue(indicator.progressProperty(), targetProgress, Interpolator.EASE_BOTH))
        );
        timeline.play();
    }

    private void animateLabelChange(Label label, String newText, String newColor) {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), label);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            label.setText(newText);
            label.setStyle(String.format(
                "-fx-text-fill: %s; " +
                "-fx-font-weight: 800; " +
                "-fx-font-size: 13px; " +
                "-fx-letter-spacing: 0.5; " +
                "-fx-effect: dropshadow(gaussian, %s, 6, 0.6, 0, 0);",
                newColor, newColor + "80"
            ));
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), label);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        fadeOut.play();
    }

    private void updatePremiumStrengthBars(int strength, String baseColor) {
        if (strengthBars == null) return;
        
        for (int i = 0; i < strengthBars.getChildren().size() && i < 6; i++) {
            javafx.scene.Node node = strengthBars.getChildren().get(i);
            if (node instanceof Region) {
                Region bar = (Region) node;
                final int index = i;
                
                PauseTransition delay = new PauseTransition(Duration.millis(i * 50));
                delay.setOnFinished(e -> {
                    if (index < strength) {
                        animateBarActivation(bar, getStrengthBarColor(strength));
                    } else {
                        animateBarDeactivation(bar);
                    }
                });
                delay.play();
            }
        }
    }

    private void animateBarActivation(Region bar, String color) {
        String style = String.format(
            "-fx-background-color: linear-gradient(to right, %s, %s); " +
            "-fx-background-radius: 3; " +
            "-fx-effect: dropshadow(gaussian, %s, 8, 0.7, 0, 0);",
            color, adjustBrightness(color, 1.2), color + "80"
        );
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(200), bar);
        scale.setToX(1.1);
        scale.setToY(1.3);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        scale.setOnFinished(e -> bar.setStyle(style));
        scale.play();
    }

    private void animateBarDeactivation(Region bar) {
        String style = 
            "-fx-background-color: rgba(71, 85, 105, 0.3); " +
            "-fx-background-radius: 3; " +
            "-fx-effect: innershadow(gaussian, rgba(0, 0, 0, 0.3), 2, 0.5, 0, 1);";
        
        FadeTransition fade = new FadeTransition(Duration.millis(200), bar);
        fade.setToValue(0.6);
        fade.setOnFinished(e -> {
            bar.setStyle(style);
            fade.setToValue(1.0);
            fade.play();
        });
        fade.play();
    }

    private String getStrengthBarColor(int strength) {
        if (strength <= 2) return "#ef4444";
        if (strength == 3) return "#f59e0b";
        if (strength == 4) return "#eab308";
        if (strength == 5) return "#22c55e";
        return "#10b981";
    }

    private String adjustBrightness(String color, double factor) {
        // Simple brightness adjustment
        return color;
    }

    private void setupAdvancedButtonEffects() {
        addPremiumButtonEffect(registerButton, 1.06);
        addPremiumButtonEffect(cancelButton, 1.04);
    }

    private void addPremiumButtonEffect(Button button, double scale) {
        if (button == null) return;
        
        button.setOnMouseEntered(e -> {
            if (button.isDisabled()) return;
            
            ScaleTransition st = new ScaleTransition(Duration.millis(250), button);
            st.setToX(scale);
            st.setToY(scale);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();
            
            // Add glow effect
            DropShadow glow = new DropShadow();
            glow.setColor(button == registerButton ? 
                Color.web("#10b981", 0.7) : Color.web("#10b981", 0.3));
            glow.setRadius(28);
            glow.setSpread(0.6);
            button.setEffect(glow);
        });

        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(250), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_IN);
            st.play();
            
            button.setEffect(null);
        });
    }

    private void setupAccessibilityFeatures() {
        // Add tooltips for better UX
        if (usernameField != null) {
            Tooltip userTip = new Tooltip("3-20 characters: letters, numbers, and underscores");
            usernameField.setTooltip(userTip);
        }
        
        if (passwordField != null) {
            Tooltip passTip = new Tooltip("Minimum 8 characters. Include uppercase, lowercase, numbers, and symbols for maximum security.");
            passwordField.setTooltip(passTip);
        }
    }

    private void playEntranceAnimation() {
        if (registerCard == null) return;
        
        registerCard.setOpacity(0);
        registerCard.setScaleX(0.92);
        registerCard.setScaleY(0.92);
        registerCard.setTranslateY(-40);

        FadeTransition fade = new FadeTransition(Duration.millis(1000), registerCard);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(Duration.millis(1000), registerCard);
        scale.setFromX(0.92);
        scale.setFromY(0.92);
        scale.setToX(1.0);
        scale.setToY(1.0);

        TranslateTransition translate = new TranslateTransition(Duration.millis(1000), registerCard);
        translate.setFromY(-40);
        translate.setToY(0);

        ParallelTransition parallel = new ParallelTransition(fade, scale, translate);
        parallel.setInterpolator(Interpolator.SPLINE(0.16, 1, 0.3, 1));
        parallel.play();
    }

    private void startAmbientAnimations() {
        startCardBreathing();
        startGlowPulse();
    }

    private void startCardBreathing() {
        if (registerCard == null) return;
        
        breathingAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, 
                new KeyValue(registerCard.scaleXProperty(), 1.0),
                new KeyValue(registerCard.scaleYProperty(), 1.0)
            ),
            new KeyFrame(Duration.seconds(4), 
                new KeyValue(registerCard.scaleXProperty(), 1.005, Interpolator.EASE_BOTH),
                new KeyValue(registerCard.scaleYProperty(), 1.005, Interpolator.EASE_BOTH)
            ),
            new KeyFrame(Duration.seconds(8), 
                new KeyValue(registerCard.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                new KeyValue(registerCard.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)
            )
        );
        breathingAnimation.setCycleCount(Timeline.INDEFINITE);
        breathingAnimation.play();
    }

    private void startGlowPulse() {
        if (registerButton == null) return;
        
        glowPulseAnimation = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {}),
            new KeyFrame(Duration.seconds(2), e -> {}),
            new KeyFrame(Duration.seconds(4), e -> {})
        );
        glowPulseAnimation.setCycleCount(Timeline.INDEFINITE);
        glowPulseAnimation.play();
    }

    @FXML
    private void onRegister() {
        if (isProcessing) return;
        
        String username = usernameField.getText().trim();
        String password = showPasswordCheck != null && showPasswordCheck.isSelected() 
            ? passwordVisibleField.getText() 
            : passwordField.getText();
        String confirmPassword = showPasswordCheck != null && showPasswordCheck.isSelected() 
            ? confirmVisibleField.getText() 
            : confirmPasswordField.getText();
        String roleSelection = roleCombo.getValue();

        // Validation
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAdvancedMessage("❌ All fields are required", MessageType.ERROR);
            playErrorShake();
            return;
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            showAdvancedMessage("❌ Invalid username format", MessageType.ERROR);
            playErrorShake();
            highlightField(usernameField);
            return;
        }

        if (password.length() < MIN_PASSWORD_LENGTH) {
            showAdvancedMessage("❌ Password must be at least 8 characters", MessageType.ERROR);
            playErrorShake();
            highlightField(passwordField);
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAdvancedMessage("❌ Passwords do not match", MessageType.ERROR);
            playErrorShake();
            highlightField(confirmPasswordField);
            return;
        }

        String role = roleSelection.contains("Administrator") ? "admin" : "user";

        setProcessingState(true);

        CompletableFuture.runAsync(() -> {
            try {
                Thread.sleep(1800);
                
                boolean success = DBUtilisateur.createUser(username, password, role);

                Platform.runLater(() -> {
                    setProcessingState(false);
                    
                    if (success) {
                        handleRegistrationSuccess();
                    } else {
                        handleRegistrationFailure();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    setProcessingState(false);
                    showAdvancedMessage("❌ System error. Please try again", MessageType.ERROR);
                    playErrorShake();
                });
            }
        });
    }

    private void setProcessingState(boolean processing) {
        isProcessing = processing;
        
        registerButton.setDisable(processing);
        cancelButton.setDisable(processing);
        usernameField.setDisable(processing);
        passwordField.setDisable(processing);
        confirmPasswordField.setDisable(processing);
        passwordVisibleField.setDisable(processing);
        confirmVisibleField.setDisable(processing);
        roleCombo.setDisable(processing);
        showPasswordCheck.setDisable(processing);
        
        if (processing) {
            registerButton.setText("⏳ Creating Account...");
            showLoadingAnimation();
        } else {
            registerButton.setText("🚀 Create Account");
            hideLoadingAnimation();
        }
    }

    private void showLoadingAnimation() {
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(true);
            loadingIndicator.setProgress(-1);
            
            FadeTransition fade = new FadeTransition(Duration.millis(400), loadingIndicator);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
        
        if (loadingText != null) {
            loadingText.setVisible(true);
            loadingText.setText("Securing your credentials...");
            
            FadeTransition fade = new FadeTransition(Duration.millis(400), loadingText);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

    private void hideLoadingAnimation() {
        if (loadingIndicator != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(300), loadingIndicator);
            fade.setToValue(0);
            fade.setOnFinished(e -> loadingIndicator.setVisible(false));
            fade.play();
        }
        
        if (loadingText != null) {
            FadeTransition fade = new FadeTransition(Duration.millis(300), loadingText);
            fade.setToValue(0);
            fade.setOnFinished(e -> loadingText.setVisible(false));
            fade.play();
        }
    }

    private void handleRegistrationSuccess() {
        showAdvancedMessage("✅ Account created successfully! Redirecting...", MessageType.SUCCESS);
        playCelebrationAnimation();
        
        PauseTransition pause = new PauseTransition(Duration.seconds(2.5));
        pause.setOnFinished(e -> closeWithAnimation());
        pause.play();
    }

    private void handleRegistrationFailure() {
        showAdvancedMessage("❌ Username already exists", MessageType.ERROR);
        playErrorShake();
        highlightField(usernameField);
    }

    private enum MessageType {
        SUCCESS("#10b981", "rgba(16, 185, 129, 0.12)", "rgba(16, 185, 129, 0.5)", "✅"),
        ERROR("#ef4444", "rgba(239, 68, 68, 0.12)", "rgba(239, 68, 68, 0.5)", "❌"),
        WARNING("#f59e0b", "rgba(245, 158, 11, 0.12)", "rgba(245, 158, 11, 0.5)", "⚠"),
        INFO("#3b82f6", "rgba(59, 130, 246, 0.12)", "rgba(59, 130, 246, 0.5)", "ℹ");
        
        final String textColor, bgColor, borderColor, icon;
        
        MessageType(String textColor, String bgColor, String borderColor, String icon) {
            this.textColor = textColor;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            this.icon = icon;
        }
    }

    private void showAdvancedMessage(String text, MessageType type) {
        if (messageLabel == null || messageContainer == null) return;
        
        messageContainer.setVisible(true);
        messageLabel.setText(text);
        
        if (messageIcon != null) {
            messageIcon.setText(type.icon);
        }
        
        messageContainer.setStyle(String.format(
            "-fx-background-color: %s; " +
            "-fx-border-color: %s; " +
            "-fx-border-width: 2; " +
            "-fx-border-radius: 14; " +
            "-fx-background-radius: 14; " +
            "-fx-padding: 14 20; " +
            "-fx-effect: dropshadow(gaussian, %s, 10, 0.5, 0, 4), " +
            "innershadow(gaussian, %s, 4, 0.5, 0, 1);",
            type.bgColor, type.borderColor, type.borderColor, type.borderColor
        ));
        
        messageLabel.setStyle(String.format(
            "-fx-text-fill: %s; " +
            "-fx-font-weight: 600; " +
            "-fx-font-size: 13px; " +
            "-fx-letter-spacing: 0.2;",
            type.textColor
        ));

        messageContainer.setTranslateY(-15);
        messageContainer.setOpacity(0);
        messageContainer.setScaleX(0.95);
        messageContainer.setScaleY(0.95);
        
        FadeTransition fade = new FadeTransition(Duration.millis(400), messageContainer);
        fade.setFromValue(0);
        fade.setToValue(1);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(400), messageContainer);
        translate.setFromY(-15);
        translate.setToY(0);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(400), messageContainer);
        scale.setFromX(0.95);
        scale.setFromY(0.95);
        scale.setToX(1.0);
        scale.setToY(1.0);
        
        ParallelTransition parallel = new ParallelTransition(fade, translate, scale);
        parallel.setInterpolator(Interpolator.EASE_OUT);
        parallel.play();
    }

    private void playErrorShake() {
        if (registerCard == null) return;
        
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), registerCard);
        shake.setByX(12);
        shake.setCycleCount(8);
        shake.setAutoReverse(true);
        shake.setInterpolator(Interpolator.LINEAR);
        shake.setOnFinished(e -> registerCard.setTranslateX(0));
        shake.play();
        
        // Add rotation for more dramatic effect
        RotateTransition rotate = new RotateTransition(Duration.millis(50), registerCard);
        rotate.setByAngle(1.5);
        rotate.setCycleCount(8);
        rotate.setAutoReverse(true);
        rotate.setOnFinished(e -> registerCard.setRotate(0));
        rotate.play();
    }

    private void playCelebrationAnimation() {
        if (registerCard == null) return;
        
        // Expand animation
        ScaleTransition expand = new ScaleTransition(Duration.millis(400), registerCard);
        expand.setToX(1.08);
        expand.setToY(1.08);
        expand.setAutoReverse(true);
        expand.setCycleCount(2);
        expand.setInterpolator(Interpolator.EASE_BOTH);
        
        // Glow effect
        DropShadow successGlow = new DropShadow();
        successGlow.setColor(Color.web("#10b981", 0.8));
        successGlow.setRadius(40);
        successGlow.setSpread(0.7);
        registerCard.setEffect(successGlow);
        
        expand.play();
        
        // Pulse the success message
        if (messageContainer != null && messageContainer.isVisible()) {
            Timeline pulse = new Timeline(
                new KeyFrame(Duration.ZERO, 
                    new KeyValue(messageContainer.scaleXProperty(), 1.0),
                    new KeyValue(messageContainer.scaleYProperty(), 1.0)
                ),
                new KeyFrame(Duration.millis(200), 
                    new KeyValue(messageContainer.scaleXProperty(), 1.05, Interpolator.EASE_BOTH),
                    new KeyValue(messageContainer.scaleYProperty(), 1.05, Interpolator.EASE_BOTH)
                ),
                new KeyFrame(Duration.millis(400), 
                    new KeyValue(messageContainer.scaleXProperty(), 1.0, Interpolator.EASE_BOTH),
                    new KeyValue(messageContainer.scaleYProperty(), 1.0, Interpolator.EASE_BOTH)
                )
            );
            pulse.setCycleCount(3);
            pulse.play();
        }
    }

    private void highlightField(javafx.scene.Node field) {
        if (field == null) return;
        
        // Pulse animation
        ScaleTransition pulse = new ScaleTransition(Duration.millis(150), field);
        pulse.setToX(1.06);
        pulse.setToY(1.06);
        pulse.setAutoReverse(true);
        pulse.setCycleCount(4);
        pulse.setInterpolator(Interpolator.EASE_BOTH);
        
        // Color flash
        Timeline colorFlash = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {
                if (field instanceof TextInputControl) {
                    field.setStyle(field.getStyle() + "-fx-border-color: #ef4444; -fx-border-width: 2;");
                }
            }),
            new KeyFrame(Duration.millis(600), e -> {
                if (field instanceof TextInputControl) {
                    field.setStyle(field.getStyle() + "-fx-border-color: rgba(71, 85, 105, 0.4); -fx-border-width: 1.5;");
                }
            })
        );
        
        ParallelTransition parallel = new ParallelTransition(pulse, colorFlash);
        parallel.play();
    }

    @FXML
    private void onCancel() {
        closeWithAnimation();
    }

    private void closeWithAnimation() {
        if (registerCard == null) return;
        
        // Stop ambient animations
        if (breathingAnimation != null) breathingAnimation.stop();
        if (glowPulseAnimation != null) glowPulseAnimation.stop();
        
        Stage stage = (Stage) registerButton.getScene().getWindow();
        
        FadeTransition fade = new FadeTransition(Duration.millis(500), registerCard);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        ScaleTransition scale = new ScaleTransition(Duration.millis(500), registerCard);
        scale.setToX(0.9);
        scale.setToY(0.9);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(500), registerCard);
        translate.setToY(40);
        
        ParallelTransition parallel = new ParallelTransition(fade, scale, translate);
        parallel.setInterpolator(Interpolator.EASE_IN);
        parallel.setOnFinished(e -> stage.close());
        parallel.play();
    }
}