//package simple_banking_app.client.gui;
//
//import simple_banking_app.system.BankSystem;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//
//public class ChangeAdminCredentialsScreen extends JFrame {
//    private BankSystem bankSystem;
//
//    public ChangeAdminCredentialsScreen(BankSystem bankSystem) {
//        this.bankSystem = bankSystem;
//        initializeUI();
//    }
//
//    private void initializeUI() {
//        setTitle("Change Admin Credentials");
//        setSize(400, 350);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(10, 10, 10, 10);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel titleLabel = new JLabel("Change Admin Credentials", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        panel.add(titleLabel, gbc);
//
//        JLabel currentIdLabel = new JLabel("Current Admin ID:");
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.gridwidth = 1;
//        panel.add(currentIdLabel, gbc);
//
//        JTextField currentIdField = new JTextField(15);
//        gbc.gridx = 1;
//        panel.add(currentIdField, gbc);
//
//        JLabel currentPassLabel = new JLabel("Current Password:");
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        panel.add(currentPassLabel, gbc);
//
//        JPasswordField currentPassField = new JPasswordField(15);
//        gbc.gridx = 1;
//        panel.add(currentPassField, gbc);
//
//        JLabel newIdLabel = new JLabel("New Admin ID:");
//        gbc.gridx = 0;
//        gbc.gridy = 3;
//        panel.add(newIdLabel, gbc);
//
//        JTextField newIdField = new JTextField(15);
//        gbc.gridx = 1;
//        panel.add(newIdField, gbc);
//
//        JLabel newPassLabel = new JLabel("New Password:");
//        gbc.gridx = 0;
//        gbc.gridy = 4;
//        panel.add(newPassLabel, gbc);
//
//        JPasswordField newPassField = new JPasswordField(15);
//        gbc.gridx = 1;
//        panel.add(newPassField, gbc);
//
//        JLabel confirmPassLabel = new JLabel("Confirm New Password:");
//        gbc.gridx = 0;
//        gbc.gridy = 5;
//        panel.add(confirmPassLabel, gbc);
//
//        JPasswordField confirmPassField = new JPasswordField(15);
//        gbc.gridx = 1;
//        panel.add(confirmPassField, gbc);
//
//        JButton submitButton = new JButton("Update Credentials");
//        gbc.gridx = 0;
//        gbc.gridy = 6;
//        gbc.gridwidth = 2;
//        panel.add(submitButton, gbc);
//
//        JButton backButton = new JButton("Back to Admin Panel");
//        gbc.gridy = 7;
//        panel.add(backButton, gbc);
//
//        submitButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                String currentId = currentIdField.getText();
//                String currentPass = new String(currentPassField.getPassword());
//                String newId = newIdField.getText();
//                String newPass = new String(newPassField.getPassword());
//                String confirmPass = new String(confirmPassField.getPassword());
//
//                if (currentId.isEmpty() || currentPass.isEmpty() || newId.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
//                    JOptionPane.showMessageDialog(ChangeAdminCredentialsScreen.this,
//                            "Please fill in all fields",
//                            "Error", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }
//
//                if (!newPass.equals(confirmPass)) {
//                    JOptionPane.showMessageDialog(ChangeAdminCredentialsScreen.this,
//                            "New passwords do not match",
//                            "Error", JOptionPane.ERROR_MESSAGE);
//                    return;
//                }
//
//                if (bankSystem.changeAdminCredentials(currentId, currentPass, newId, newPass)) {
//                    JOptionPane.showMessageDialog(ChangeAdminCredentialsScreen.this,
//                            "Admin credentials updated successfully",
//                            "Success", JOptionPane.INFORMATION_MESSAGE);
//                    new AdminPanel(bankSystem).setVisible(true);
//                    dispose();
//                } else {
//                    JOptionPane.showMessageDialog(ChangeAdminCredentialsScreen.this,
//                            "Failed to update credentials. Check current ID and password.",
//                            "Error", JOptionPane.ERROR_MESSAGE);
//                }
//            }
//        });
//
//        backButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                new AdminPanel(bankSystem).setVisible(true);
//                dispose();
//            }
//        });
//
//        add(panel);
//    }
//}