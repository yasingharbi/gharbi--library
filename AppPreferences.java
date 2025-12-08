package controller; // ou util; mais garde le même partout

import java.util.prefs.Preferences;

public class AppPreferences {

    private static final String KEY_THEME = "theme";
    private static final String KEY_ANIMATIONS = "animationsEnabled";
    private static final String KEY_NOTIFICATIONS = "notificationsEnabled";

    private static final AppPreferences INSTANCE = new AppPreferences();
    private final Preferences prefs = Preferences.userNodeForPackage(AppPreferences.class);

    private AppPreferences() {}

    public static AppPreferences getInstance() { return INSTANCE; }

    public String getTheme() {
        return prefs.get(KEY_THEME, "dark");
    }

    public void setTheme(String theme) {
        prefs.put(KEY_THEME, theme);
    }

    public boolean isAnimationsEnabled() {
        return prefs.getBoolean(KEY_ANIMATIONS, true);
    }

    public void setAnimationsEnabled(boolean enabled) {
        prefs.putBoolean(KEY_ANIMATIONS, enabled);
    }

    public boolean isNotificationsEnabled() {
        return prefs.getBoolean(KEY_NOTIFICATIONS, true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        prefs.putBoolean(KEY_NOTIFICATIONS, enabled);
    }
}
