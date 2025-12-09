package controller;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

public class SettingsController {

    @FXML private RadioButton darkThemeRadio;
    @FXML private RadioButton lightThemeRadio;
    @FXML private CheckBox animationsCheck;
    @FXML private CheckBox notificationsCheck;

    private final AppPreferences prefs = AppPreferences.getInstance();

    @FXML
    private void initialize() {
        // Charger l'état
        if ("dark".equalsIgnoreCase(prefs.getTheme())) {
            darkThemeRadio.setSelected(true);
        } else {
            lightThemeRadio.setSelected(true);
        }

        animationsCheck.setSelected(prefs.isAnimationsEnabled());
        notificationsCheck.setSelected(prefs.isNotificationsEnabled());
    }

    @FXML
    private void onSave() {
        prefs.setTheme(darkThemeRadio.isSelected() ? "dark" : "light");
        prefs.setAnimationsEnabled(animationsCheck.isSelected());
        prefs.setNotificationsEnabled(notificationsCheck.isSelected());
        close();
    }

    @FXML
    private void onCancel() {
        close();
    }

    private void close() {
        Stage stage = (Stage) animationsCheck.getScene().getWindow();
        stage.close();
    }
}
