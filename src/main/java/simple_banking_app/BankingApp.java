package simple_banking_app;

import simple_banking_app.client.gui.WelcomeScreen;
import simple_banking_app.system.BankSystem;

import javax.swing.*;

public class BankingApp {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Initialize bank system
            BankSystem bankSystem = new BankSystem();

            // Create and show welcome screen
            SwingUtilities.invokeLater(() -> {
                WelcomeScreen welcomeScreen = new WelcomeScreen(bankSystem);
                welcomeScreen.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to start application: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}