//package simple_banking_app.client.gui;
//
//import simple_banking_app.system.BankSystem;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class AdminLoginScreen extends JFrame {
//    private BankSystem bankSystem;
//
//    public AdminLoginScreen(BankSystem bankSystem) {
//        this.bankSystem = bankSystem;
//        initializeUI();
//    }
////
//    private void initializeUI() {
//        setTitle("Admin Login");
//        setSize(400, 250);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(10, 10, 10, 10);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        panel.add(titleLabel, gbc);
//
//        JLabel adminIdLabel = new JLabel("Admin ID:");
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.gridwidth = 1;
//        panel.add(adminIdLabel, gbc);
//
//        JTextField adminIdField = new JTextField(15);
//        gbc.gridx = 1;
//        panel.add(adminIdField, gbc);
//
//        JLabel passwordLabel = new JLabel("Password:");
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        panel.add(passwordLabel, gbc);
//
//        JPasswordField passwordField = new JPasswordField(15);
//        gbc.gridx = 1;
//        panel.add(passwordField, gbc);
//
//        JButton loginButton = new JButton("Login");
//        gbc.gridx = 0;
//        gbc.gridy = 3;
//        gbc.gridwidth = 2;
//        panel.add(loginButton, gbc);
//
//        JButton backButton = new JButton("Back to Welcome");
//        gbc.gridy = 4;
//        panel.add(backButton, gbc);
//
//        loginButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String adminId = adminIdField.getText();
//                String password = new String(passwordField.getPassword());
//
//                if (bankSystem.adminLogin(adminId, password)) {
//                    // Check if using default credentials
//                    if (adminId.equals("admin") && password.equals("admin123")) {
//                        JOptionPane.showMessageDialog(AdminLoginScreen.this,
//                                "You are using default credentials. Please change them for security.",
//                                "Security Warning", JOptionPane.WARNING_MESSAGE);
//                        new ChangeAdminCredentialsScreen(bankSystem).setVisible(true);
//                    } else {
//                        new AdminPanel(bankSystem).setVisible(true);
//                    }
//                    dispose();
//                } else {
//                    JOptionPane.showMessageDialog(AdminLoginScreen.this,
//                            "Invalid admin credentials",
//                            "Login Failed", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });
//
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                new WelcomeScreen(bankSystem).setVisible(true);
//                dispose();
//            }
//        });
//
//        add(panel);
//    }
//}