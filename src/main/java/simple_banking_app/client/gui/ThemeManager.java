package simple_banking_app.client.gui;

import java.awt.*;

class ThemeManager {
    private static boolean isDarkMode = false;

    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
    }

    public static Color getBackgroundColor() {
        return isDarkMode ? new Color(40, 44, 52) : new Color(240, 242, 245);
    }

    public static Color getForegroundColor() {
        return isDarkMode ? new Color(220, 221, 225) : new Color(33, 37, 41);
    }

    public static Color getInputBackground() {
        return isDarkMode ? new Color(52, 58, 64) : new Color(255, 255, 255);
    }

    public static Color getButtonColor() {
        return isDarkMode ? new Color(0, 123, 255) : new Color(0, 123, 255);
    }

    public static Color getButtonTextColor() {
        return Color.WHITE;
    }

}