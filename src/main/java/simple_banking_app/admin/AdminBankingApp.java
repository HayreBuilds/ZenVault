package simple_banking_app.admin;

import simple_banking_app.admin.gui.AdminDashboardScreen;
import simple_banking_app.system.BankSystem;
import simple_banking_app.system.AdminSystem;
import simple_banking_app.admin.gui.AdminLoginScreen;
import javax.swing.*;

public class AdminBankingApp {
    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Initialize bank and admin systems
            BankSystem bankSystem = new BankSystem();
            AdminSystem adminSystem = new AdminSystem(bankSystem);

            // Create and show admin login screen
            SwingUtilities.invokeLater(() -> {
                AdminLoginScreen loginScreen = new AdminLoginScreen(bankSystem);
                loginScreen.setVisible(true);
            });
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Failed to start admin application: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}