//package simple_banking_app.client.gui;
//
//import simple_banking_app.model.User;
//import simple_banking_app.system.BankSystem;
//
//import javax.swing.*;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.text.SimpleDateFormat;
//import java.util.List;
//
//public class AdminPanel extends JFrame {
//    private BankSystem bankSystem;
//    private JTable userTable;
//    private DefaultTableModel tableModel;
//
//    public AdminPanel(BankSystem bankSystem) {
//        this.bankSystem = bankSystem;
//        initializeUI();
//    }
//
//    private void initializeUI() {
//        setTitle("Admin Panel");
//        setSize(900, 600);
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(null);
//
//        JPanel mainPanel = new JPanel(new BorderLayout());
//        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        // Initialize table model
//        String[] columnNames = {"Account Number", "Name", "Account Type", "Balance", "Loan", "Status", "Last Login"};
//        tableModel = new DefaultTableModel(columnNames, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        // Create the table
//        userTable = new JTable(tableModel);
//        refreshUserTable();
//
//        // Create menu bar with only Actions menu
//        JMenuBar menuBar = createMenuBar();
//        setJMenuBar(menuBar);
//
//        // Create top panel with search and action buttons
//        JPanel topPanel = createTopPanel();
//        mainPanel.add(topPanel, BorderLayout.NORTH);
//
//        // Add table with scroll pane
//        JScrollPane scrollPane = new JScrollPane(userTable);
//        mainPanel.add(scrollPane, BorderLayout.CENTER);
//
//        // Create bottom panel with Back button
//        JPanel bottomPanel = createBottomPanel();
//        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
//
//        add(mainPanel);
//    }
//
//    private JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//
//        // Actions menu
//        JMenu actionsMenu = new JMenu("Actions");
//
//        JMenuItem deleteInactiveItem = new JMenuItem("Delete Inactive Accounts");
//        deleteInactiveItem.addActionListener(e -> {
//            if (bankSystem.deleteInactiveAccounts()) {
//                refreshUserTable();
//                JOptionPane.showMessageDialog(this, "Inactive accounts deleted successfully.");
//            } else {
//                JOptionPane.showMessageDialog(this, "Failed to delete inactive accounts.");
//            }
//        });
//        actionsMenu.add(deleteInactiveItem);
//
//        JMenuItem changeCredentialsItem = new JMenuItem("Change Admin Credentials");
//        changeCredentialsItem.addActionListener(e -> {
//            new ChangeAdminCredentialsScreen(bankSystem).setVisible(true);
//            dispose();
//        });
//        actionsMenu.add(changeCredentialsItem);
//
//        menuBar.add(actionsMenu);
//
//        return menuBar;
//    }
//
//    private JPanel createTopPanel() {
//        JPanel topPanel = new JPanel(new BorderLayout());
//
//        // Search panel (left side)
//        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
//        JTextField searchField = new JTextField(20);
//        JButton searchButton = new JButton("Search");
//
//        searchButton.addActionListener(e -> {
//            String query = searchField.getText();
//            List<User> results = bankSystem.searchUsers(query);
//            updateTableModel(results);
//        });
//
//        searchPanel.add(new JLabel("Search:"));
//        searchPanel.add(searchField);
//        searchPanel.add(searchButton);
//
//        // Action buttons panel (right side)
//        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
//        JButton toggleStatusButton = new JButton("Toggle Active Status");
//        toggleStatusButton.addActionListener(e -> toggleUserStatus());
//
//        JButton viewDetailsButton = new JButton("View Account Details");
//        viewDetailsButton.addActionListener(e -> viewUserDetails());
//
//        JButton resetPasswordButton = new JButton("Reset User Password");
//        resetPasswordButton.addActionListener(e -> showPasswordResetDialog());
//
//        actionButtonsPanel.add(toggleStatusButton);
//        actionButtonsPanel.add(viewDetailsButton);
//        actionButtonsPanel.add(resetPasswordButton);
//
//        // Add panels to top panel
//        topPanel.add(searchPanel, BorderLayout.WEST);
//        topPanel.add(actionButtonsPanel, BorderLayout.EAST);
//
//        return topPanel;
//    }
//
//    private JPanel createBottomPanel() {
//        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
//        JButton backButton = new JButton("Back to Welcome");
//        backButton.addActionListener(e -> {
//            new WelcomeScreen(bankSystem).setVisible(true);
//            dispose();
//        });
//        bottomPanel.add(backButton);
//        return bottomPanel;
//    }
//
//    private void showPasswordResetDialog() {
//        JDialog dialog = new JDialog(this, "Reset User Password", true);
//        dialog.setSize(400, 250);
//        dialog.setLocationRelativeTo(this);
//
//        JPanel resetPanel = createPasswordResetPanel(dialog);
//        dialog.add(resetPanel);
//        dialog.setVisible(true);
//    }
//
//    private JPanel createPasswordResetPanel(JDialog dialog) {
//        JPanel panel = new JPanel(new GridBagLayout());
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(5, 5, 5, 5);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//
//        JLabel accountLabel = new JLabel("Account Number:");
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        panel.add(accountLabel, gbc);
//
//        JTextField accountField = new JTextField(15);
//        gbc.gridx = 1;
//        panel.add(accountField, gbc);
//
//        JLabel newPinLabel = new JLabel("New PIN (4 digits):");
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        panel.add(newPinLabel, gbc);
//
//        JPasswordField newPinField = new JPasswordField(15);
//        gbc.gridx = 1;
//        panel.add(newPinField, gbc);
//
//        JButton resetButton = new JButton("Reset Password");
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        gbc.gridwidth = 2;
//        panel.add(resetButton, gbc);
//
//        resetButton.addActionListener(e -> {
//            String accountNumber = accountField.getText();
//            String newPin = new String(newPinField.getPassword());
//
//            if (accountNumber.isEmpty() || newPin.isEmpty()) {
//                JOptionPane.showMessageDialog(this,
//                        "Please fill in all fields",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            if (!newPin.matches("\\d{4}")) {
//                JOptionPane.showMessageDialog(this,
//                        "PIN must be 4 digits",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            if (bankSystem.resetUserPassword(bankSystem.getAdminId(), bankSystem.getAdminPassword(),
//                    accountNumber, newPin)) {
//                JOptionPane.showMessageDialog(this,
//                        "Password reset successfully for account: " + accountNumber,
//                        "Success", JOptionPane.INFORMATION_MESSAGE);
//                refreshUserTable();
//                dialog.dispose();
//            } else {
//                JOptionPane.showMessageDialog(this,
//                        "Failed to reset password. Check account number.",
//                        "Error", JOptionPane.ERROR_MESSAGE);
//            }
//        });
//
//        return panel;
//    }
//
//    private void refreshUserTable() {
//        updateTableModel(bankSystem.getAllUsers());
//    }
//
//    private void updateTableModel(List<User> users) {
//        tableModel.setRowCount(0);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//
//        for (User user : users) {
//            tableModel.addRow(new Object[]{
//                    user.getAccountNumber(),
//                    user.getName(),
//                    user.getAccountType(),
//                    String.format("$%.2f", user.getBalance()),
//                    String.format("$%.2f", user.getLoanAmount()),
//                    user.isAccountActive() ? "Active" : "Inactive",
//                    sdf.format(user.getLastLoginDate())
//            });
//        }
//    }
//
//    private void toggleUserStatus() {
//        int row = userTable.getSelectedRow();
//        if (row >= 0) {
//            String accountNumber = (String) tableModel.getValueAt(row, 0);
//            User user = bankSystem.getAllUsers().stream()
//                    .filter(u -> u.getAccountNumber().equals(accountNumber))
//                    .findFirst()
//                    .orElse(null);
//            if (user != null) {
//                user.setAccountActive(!user.isAccountActive());
//                bankSystem.saveUsers();
//                refreshUserTable();
//            }
//        } else {
//            JOptionPane.showMessageDialog(this,
//                    "Please select a user first",
//                    "No Selection", JOptionPane.WARNING_MESSAGE);
//        }
//    }
//
//    private void viewUserDetails() {
//        int row = userTable.getSelectedRow();
//        if (row >= 0) {
//            String accountNumber = (String) tableModel.getValueAt(row, 0);
//            User user = bankSystem.getAllUsers().stream()
//                    .filter(u -> u.getAccountNumber().equals(accountNumber))
//                    .findFirst()
//                    .orElse(null);
//            if (user != null) {
//                showUserDetails(user);
//            }
//        } else {
//            JOptionPane.showMessageDialog(this,
//                    "Please select a user first",
//                    "No Selection", JOptionPane.WARNING_MESSAGE);
//        }
//    }
//
//    private void showUserDetails(User user) {
//        JTextArea textArea = new JTextArea(20, 50);
//        textArea.setEditable(false);
//        textArea.setText(user.generateStatement(
//                new java.util.Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
//                new java.util.Date()
//        ));
//
//        JScrollPane scrollPane = new JScrollPane(textArea);
//        JOptionPane.showMessageDialog(this, scrollPane,
//                "Account Details: " + user.getAccountNumber(),
//                JOptionPane.INFORMATION_MESSAGE);
//    }
//}