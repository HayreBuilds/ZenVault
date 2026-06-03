package simple_banking_app.admin.gui;

import simple_banking_app.client.gui.WelcomeScreen;
import simple_banking_app.system.AdminSystem;
import simple_banking_app.system.BankSystem;
import simple_banking_app.model.User;
import simple_banking_app.model.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminDashboardScreen extends JFrame {
    private BankSystem bankSystem;
    private AdminSystem adminSystem;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton toggleStatusButton;
    private JButton viewDetailsButton;
    private JButton resetPasswordButton;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color SECONDARY_COLOR = new Color(107, 114, 128); // #6B7280
    private static final Color SECONDARY_HOVER = new Color(55, 65, 81); // #374151
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // #EF4444
    private static final Color TABLE_ALT_ROW = new Color(249, 250, 251); // #F9FAFB

    public AdminDashboardScreen(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        this.adminSystem = new AdminSystem(bankSystem);
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Admin Dashboard");
        setSize(1200, 700);
        setMinimumSize(new Dimension(800, 500));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Header panel with gradient
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 58, 138), 0, getHeight(), new Color(59, 130, 246));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        headerPanel.setPreferredSize(new Dimension(0, 80));
        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel (card)
        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Initialize table model
        String[] columnNames = {"Account Number", "Name", "Account Type", "Balance", "Loan", "Status", "Last Login"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Create the table
        userTable = new JTable(tableModel);
        userTable.setFont(new Font("Roboto", Font.PLAIN, 14));
        userTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        userTable.setRowHeight(25);
        userTable.setToolTipText("Select a user to perform actions");
        refreshUserTable();

        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Create top panel with search and action buttons
        JPanel topPanel = createTopPanel();
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Add table with scroll pane
        JScrollPane tableScrollPane = new JScrollPane(userTable);
        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Add content panel to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Bottom panel with Back button
        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Table selection listener
        userTable.getSelectionModel().addListSelectionListener(e -> updateActionButtonStates());

        // Set initial focus
        searchField.requestFocusInWindow();

        add(mainPanel);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(BACKGROUND_COLOR);
        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Actions menu
        JMenu actionsMenu = new JMenu("Actions");
        actionsMenu.setFont(new Font("Roboto", Font.PLAIN, 14));

        JMenuItem deleteInactiveItem = new JMenuItem("Delete Inactive Accounts");
        deleteInactiveItem.setFont(new Font("Roboto", Font.PLAIN, 14));
        deleteInactiveItem.setToolTipText("Delete all inactive accounts");
        deleteInactiveItem.addActionListener(e -> showDeleteInactiveAccountsDialog());
        actionsMenu.add(deleteInactiveItem);

        JMenuItem changeCredentialsItem = new JMenuItem("Change Admin Credentials");
        changeCredentialsItem.setFont(new Font("Roboto", Font.PLAIN, 14));
        changeCredentialsItem.setToolTipText("Change your admin credentials");
        changeCredentialsItem.addActionListener(e -> {
            new ChangeAdminCredentialsScreen(bankSystem).setVisible(true);
            dispose();
        });
        actionsMenu.add(changeCredentialsItem);

        menuBar.add(actionsMenu);
        return menuBar;
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout(15, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search panel (left side)
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        searchPanel.setBackground(Color.WHITE);
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        searchLabel.setForeground(TEXT_COLOR);
        searchLabel.setToolTipText("Search by account number or name");
        searchField = new JTextField(15);
        searchField.setFont(new Font("Roboto", Font.PLAIN, 16));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        searchField.setToolTipText("Enter account number or name to search");
        searchButton = createStyledButton("Search", true);
        searchButton.setEnabled(false);

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Action buttons panel (right side)
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        actionButtonsPanel.setBackground(Color.WHITE);

        // Add Refresh button
        JButton refreshButton = createStyledButton("Refresh", true);
        refreshButton.setToolTipText("Refresh the user table");
        refreshButton.addActionListener(e -> {
            refreshUserTable();
        });
        actionButtonsPanel.add(refreshButton);

        toggleStatusButton = createStyledButton("Toggle Status", true);
        toggleStatusButton.setEnabled(false);
        toggleStatusButton.addActionListener(e -> toggleUserStatus());

        viewDetailsButton = createStyledButton("View Details", true);
        viewDetailsButton.setEnabled(false);
        viewDetailsButton.addActionListener(e -> viewUserDetails());

        resetPasswordButton = createStyledButton("Reset Password", true);
        resetPasswordButton.setEnabled(false);
        resetPasswordButton.addActionListener(e -> showPasswordResetDialog());

        actionButtonsPanel.add(toggleStatusButton);
        actionButtonsPanel.add(viewDetailsButton);
        actionButtonsPanel.add(resetPasswordButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(actionButtonsPanel, BorderLayout.EAST);

        // Real-time search validation
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSearchButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSearchButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSearchButtonState();
            }

            private void updateSearchButtonState() {
                String query = searchField.getText().trim();
                searchButton.setEnabled(!query.isEmpty());
                searchField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        });

        searchButton.addActionListener(e -> {
            String query = searchField.getText().trim();
            List<User> results = bankSystem.searchUsers(query);
            updateTableModel(results);
            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No users found for query: " + query, "No Results", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return topPanel;
    }

    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setBackground(BACKGROUND_COLOR);
        JButton backButton = createStyledButton("Back to Login", false);
        backButton.setToolTipText("Return to the admin login screen");
        backButton.addActionListener(e -> {
            new AdminLoginScreen(bankSystem).setVisible(true);
            dispose();
        });
        bottomPanel.add(backButton);
        return bottomPanel;
    }

    private void showPasswordResetDialog() {
        JDialog dialog = new JDialog(this, "Reset User Password", true);
        dialog.setSize(600, 550);
        dialog.setLocationRelativeTo(this);
        dialog.add(createPasswordResetPanel(dialog));
        dialog.setVisible(true);
    }

    private JPanel createPasswordResetPanel(JDialog dialog) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("Reset User Password", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Admin ID
        JLabel adminIdLabel = new JLabel("Admin ID:");
        adminIdLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminIdLabel.setForeground(TEXT_COLOR);
        adminIdLabel.setToolTipText("Enter your admin ID");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(adminIdLabel, gbc);

        JTextField adminIdField = new JTextField(20);
        adminIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminIdField.setToolTipText("Enter your admin ID");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(adminIdField, gbc);

        // Admin Password
        JLabel adminPasswordLabel = new JLabel("Admin Password:");
        adminPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminPasswordLabel.setForeground(TEXT_COLOR);
        adminPasswordLabel.setToolTipText("Enter your admin password");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(adminPasswordLabel, gbc);

        JPasswordField adminPasswordField = new JPasswordField(20);
        adminPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminPasswordField.setToolTipText("Enter your admin password");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(adminPasswordField, gbc);

        // Account Number
        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        accountLabel.setForeground(TEXT_COLOR);
        accountLabel.setToolTipText("Enter the user's account number");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        panel.add(accountLabel, gbc);

        JTextField accountField = new JTextField(20);
        accountField.setFont(new Font("Roboto", Font.PLAIN, 16));
        accountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        accountField.setToolTipText("Enter the user's account number");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        // Pre-fill account number from selected row
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            String accountNumber = (String) tableModel.getValueAt(row, 0);
            accountField.setText(accountNumber);
            accountField.setEditable(false);
        }
        panel.add(accountField, gbc);

        // New PIN
        JLabel newPinLabel = new JLabel("New PIN (4 digits):");
        newPinLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        newPinLabel.setForeground(TEXT_COLOR);
        newPinLabel.setToolTipText("Enter a 4-digit PIN");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0;
        panel.add(newPinLabel, gbc);

        JPasswordField newPinField = new JPasswordField(20);
        newPinField.setFont(new Font("Roboto", Font.PLAIN, 16));
        newPinField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        newPinField.setToolTipText("Enter a 4-digit PIN");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(newPinField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton resetButton = createStyledButton("Reset Password", true);
        resetButton.setEnabled(false);
        buttonPanel.add(resetButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel.add(buttonPanel, gbc);

        // Real-time validation
        DocumentListener validationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateResetButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateResetButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateResetButtonState();
            }

            private void updateResetButtonState() {
                String adminId = adminIdField.getText().trim();
                String adminPassword = new String(adminPasswordField.getPassword()).trim();
                String accountNumber = accountField.getText().trim();
                String newPin = new String(newPinField.getPassword()).trim();
                boolean isValid = !adminId.isEmpty() && !adminPassword.isEmpty() && !accountNumber.isEmpty() && newPin.matches("\\d{4}");
                resetButton.setEnabled(isValid);

                // Update borders
                adminIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(adminId.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(adminPassword.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                accountField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(accountNumber.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                newPinField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(!newPin.matches("\\d{4}") && !newPin.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        };

        adminIdField.getDocument().addDocumentListener(validationListener);
        adminPasswordField.getDocument().addDocumentListener(validationListener);
        accountField.getDocument().addDocumentListener(validationListener);
        newPinField.getDocument().addDocumentListener(validationListener);

        resetButton.addActionListener(e -> {
            String adminId = adminIdField.getText().trim();
            String adminPassword = new String(adminPasswordField.getPassword()).trim();
            String accountNumber = accountField.getText().trim();
            String newPin = new String(newPinField.getPassword()).trim();

            if (adminSystem.resetUserPassword(adminId, adminPassword, accountNumber, newPin)) {
                JOptionPane.showMessageDialog(dialog, "Password reset successfully for account: " + accountNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to reset password. Verify admin credentials or account number.", "Error", JOptionPane.ERROR_MESSAGE);
                adminIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                accountField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        });

        return panel;
    }

    private void showDeleteInactiveAccountsDialog() {
        JDialog dialog = new JDialog(this, "Delete Inactive Accounts", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.add(createDeleteInactiveAccountsPanel(dialog));
        dialog.setVisible(true);
    }

    private JPanel createDeleteInactiveAccountsPanel(JDialog dialog) {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Title
        JLabel titleLabel = new JLabel("Delete Inactive Accounts", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        // Admin ID
        JLabel adminIdLabel = new JLabel("Admin ID:");
        adminIdLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminIdLabel.setForeground(TEXT_COLOR);
        adminIdLabel.setToolTipText("Enter your admin ID");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(adminIdLabel, gbc);

        JTextField adminIdField = new JTextField(20);
        adminIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminIdField.setToolTipText("Enter your admin ID");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(adminIdField, gbc);

        // Admin Password
        JLabel adminPasswordLabel = new JLabel("Admin Password:");
        adminPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminPasswordLabel.setForeground(TEXT_COLOR);
        adminPasswordLabel.setToolTipText("Enter your admin password");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(adminPasswordLabel, gbc);

        JPasswordField adminPasswordField = new JPasswordField(20);
        adminPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminPasswordField.setToolTipText("Enter your admin password");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(adminPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton deleteButton = createStyledButton("Delete Inactive Accounts", true);
        deleteButton.setEnabled(false);
        buttonPanel.add(deleteButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel.add(buttonPanel, gbc);

        // Real-time validation
        DocumentListener validationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDeleteButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateDeleteButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateDeleteButtonState();
            }

            private void updateDeleteButtonState() {
                String adminId = adminIdField.getText().trim();
                String adminPassword = new String(adminPasswordField.getPassword()).trim();
                boolean isValid = !adminId.isEmpty() && !adminPassword.isEmpty();
                deleteButton.setEnabled(isValid);

                // Update borders
                adminIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(adminId.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(adminPassword.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        };

        adminIdField.getDocument().addDocumentListener(validationListener);
        adminPasswordField.getDocument().addDocumentListener(validationListener);

        deleteButton.addActionListener(e -> {
            String adminId = adminIdField.getText().trim();
            String adminPassword = new String(adminPasswordField.getPassword()).trim();

            if (adminSystem.deleteInactiveAccounts(adminId, adminPassword)) {
                JOptionPane.showMessageDialog(dialog, "Inactive accounts deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshUserTable();
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Failed to delete inactive accounts. Verify admin credentials.", "Error", JOptionPane.ERROR_MESSAGE);
                adminIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        });

        return panel;
    }

    private void refreshUserTable() {
        try {
            bankSystem.reloadUsers();
            List<User> users = bankSystem.getAllUsers();
            updateTableModel(users);
            tableModel.fireTableDataChanged();
        } catch (Exception e) {
            System.err.println("Error refreshing table: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Failed to refresh table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateTableModel(List<User> users) {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (User user : users) {
            tableModel.addRow(new Object[]{
                    user.getAccountNumber(),
                    user.getName(),
                    user.getAccountType(),
                    String.format("$%.2f", user.getBalance()),
                    String.format("$%.2f", user.getLoanAmount()),
                    user.isAccountActive() ? "Active" : "Inactive",
                    sdf.format(user.getLastLoginDate())
            });
        }
    }

    private void toggleUserStatus() {
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            int modelRow = userTable.convertRowIndexToModel(row);
            String accountNumber = (String) tableModel.getValueAt(modelRow, 0);
            User user = bankSystem.findUserByAccountNumber(accountNumber);
            if (user != null) {
                user.setAccountActive(!user.isAccountActive());
                bankSystem.saveUsers();
                refreshUserTable();
            } else {
                JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewUserDetails() {
        int row = userTable.getSelectedRow();
        if (row >= 0) {
            int modelRow = userTable.convertRowIndexToModel(row);
            String accountNumber = (String) tableModel.getValueAt(modelRow, 0);
            User user = bankSystem.findUserByAccountNumber(accountNumber);
            if (user != null) {
                System.out.println("Opening details for user: " + user.getAccountNumber() + " with " + user.getTransactions().size() + " transactions");
                showUserDetails(user);
            } else {
                JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a user", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showUserDetails(User user) {
        JDialog dialog = new JDialog(this, "Account Details: " + user.getAccountNumber(), true);
        dialog.setSize(700, 650);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Summary panel
        JPanel summaryPanel = new JPanel(new GridBagLayout());
        summaryPanel.setBackground(Color.WHITE);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Account Summary"),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        summaryPanel.setPreferredSize(new Dimension(0, 150));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 5, 1, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Summary fields
        String[] labels = {"Account Number:", "Name:", "Account Type:", "Balance:", "Loan Amount:", "Status:", "Last Login:"};
        String[] values = {
                user.getAccountNumber(),
                user.getName(),
                user.getAccountType(),
                String.format("$%.2f", user.getBalance()),
                String.format("$%.2f", user.getLoanAmount()),
                user.isAccountActive() ? "Active" : "Inactive",
                new SimpleDateFormat("yyyy-MM-dd").format(user.getLastLoginDate())
        };

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i]);
            label.setFont(new Font("Roboto", Font.BOLD, 14));
            label.setForeground(TEXT_COLOR);
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            summaryPanel.add(label, gbc);

            JLabel value = new JLabel(values[i]);
            value.setFont(new Font("Roboto", Font.PLAIN, 14));
            value.setForeground(TEXT_COLOR);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            summaryPanel.add(value, gbc);
        }

        mainPanel.add(summaryPanel, BorderLayout.NORTH);

        // Transaction table
        String[] columnNames = {"Date", "Type", "Amount", "Description"};
        DefaultTableModel transactionModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable transactionTable = new JTable(transactionModel);
        transactionTable.setFont(new Font("Roboto", Font.PLAIN, 14));
        transactionTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        transactionTable.setRowHeight(25);
        transactionTable.setToolTipText("Transaction history");
        transactionTable.setPreferredScrollableViewportSize(new Dimension(650, 250));

        // Set column widths
        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Date
        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Type
        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
        transactionTable.getColumnModel().getColumn(3).setPreferredWidth(330); // Description

        // Center-align Amount column
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        transactionTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Alternating row colors
        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW);
                }
                return c;
            }
        });

        // Populate transactions
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        transactionModel.setRowCount(0);
        List<Transaction> transactions = user.getTransactions();
        for (Transaction tx : transactions) {
            transactionModel.addRow(new Object[]{
                    sdf.format(tx.getDate()),
                    tx.getType(),
                    String.format("$%.2f", tx.getAmount()),
                    tx.getDescription()
            });
        }

        JScrollPane transactionScrollPane = new JScrollPane(transactionTable);
        transactionScrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Transaction History"),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        transactionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        transactionScrollPane.setPreferredSize(new Dimension(650, 400));
        mainPanel.add(transactionScrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setPreferredSize(new Dimension(0, 40));
        JButton closeButton = createStyledButton("Close", false);
        closeButton.addActionListener(e -> dialog.dispose());
        buttonPanel.add(closeButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private void updateActionButtonStates() {
        boolean isRowSelected = userTable.getSelectedRow() >= 0;
        toggleStatusButton.setEnabled(isRowSelected);
        viewDetailsButton.setEnabled(isRowSelected);
        resetPasswordButton.setEnabled(isRowSelected);
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(text);

        if (isPrimary) {
            button.setForeground(Color.WHITE);
            button.setBackground(BUTTON_COLOR);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setBackground(BUTTON_HOVER);
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(BUTTON_COLOR);
                }
            });
        } else {
            button.setForeground(SECONDARY_COLOR);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
            ));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setForeground(SECONDARY_HOVER);
                        button.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(SECONDARY_HOVER, 1),
                                BorderFactory.createEmptyBorder(8, 16, 8, 16)
                        ));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setForeground(SECONDARY_COLOR);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                            BorderFactory.createEmptyBorder(8, 16, 8, 16)
                    ));
                }
            });
        }

        return button;
    }
}

//package simple_banking_app.admin.gui;
//
//import simple_banking_app.client.gui.WelcomeScreen;
//import simple_banking_app.system.AdminSystem;
//import simple_banking_app.system.BankSystem;
//import simple_banking_app.model.User;
//
//import javax.swing.*;
//import javax.swing.border.EmptyBorder;
//import javax.swing.event.DocumentEvent;
//import javax.swing.event.DocumentListener;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.DefaultTableModel;
//import java.awt.*;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.List;
//
//public class AdminDashboardScreen extends JFrame {
//    private BankSystem bankSystem;
//    private AdminSystem adminSystem;
//    private JTable userTable;
//    private DefaultTableModel tableModel;
//    private JTextField searchField;
//    private JButton searchButton;
//    private JButton toggleStatusButton;
//    private JButton viewDetailsButton;
//    private JButton resetPasswordButton;
//
//    // Color scheme
//    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
//    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
//    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
//    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
//    private static final Color SECONDARY_COLOR = new Color(107, 114, 128); // #6B7280
//    private static final Color SECONDARY_HOVER = new Color(55, 65, 81); // #374151
//    private static final Color ERROR_COLOR = new Color(239, 68, 68); // #EF4444
//    private static final Color TABLE_ALT_ROW = new Color(249, 250, 251); // #F9FAFB
//
//    public AdminDashboardScreen(BankSystem bankSystem) {
//        this.bankSystem = bankSystem;
//        this.adminSystem = new AdminSystem(bankSystem);
//        initializeUI();
//    }
//
//    private void initializeUI() {
//        setTitle("xAI Bank - Admin Dashboard");
//        setSize(1200, 700);
//        setMinimumSize(new Dimension(800, 500));
//        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setLocationRelativeTo(null);
//        setResizable(false);
//
//        // Main panel
//        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
//        mainPanel.setBackground(BACKGROUND_COLOR);
//        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
//
//        // Header panel with gradient
//        JPanel headerPanel = new JPanel(new BorderLayout()) {
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//                Graphics2D g2d = (Graphics2D) g;
//                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//                GradientPaint gp = new GradientPaint(0, 0, new Color(30, 58, 138), 0, getHeight(), new Color(59, 130, 246));
//                g2d.setPaint(gp);
//                g2d.fillRect(0, 0, getWidth(), getHeight());
//            }
//        };
//        headerPanel.setPreferredSize(new Dimension(0, 80));
//        JLabel titleLabel = new JLabel("Admin Dashboard", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
//        titleLabel.setForeground(Color.WHITE);
//        headerPanel.add(titleLabel, BorderLayout.CENTER);
//        mainPanel.add(headerPanel, BorderLayout.NORTH);
//
//        // Content panel (card)
//        JPanel contentPanel = new JPanel(new BorderLayout(15, 15));
//        contentPanel.setBackground(Color.WHITE);
//        contentPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
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
//        userTable.setFont(new Font("Roboto", Font.PLAIN, 14));
//        userTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
//        userTable.setRowHeight(25);
//        userTable.setToolTipText("Select a user to perform actions");
//        refreshUserTable();
//
//        // Create menu bar
//        JMenuBar menuBar = createMenuBar();
//        setJMenuBar(menuBar);
//
//        // Create top panel with search and action buttons
//        JPanel topPanel = createTopPanel();
//        contentPanel.add(topPanel, BorderLayout.NORTH);
//
//        // Add table with scroll pane
//        JScrollPane tableScrollPane = new JScrollPane(userTable);
//        tableScrollPane.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
//        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
//
//        // Add content panel to main panel
//        mainPanel.add(contentPanel, BorderLayout.CENTER);
//
//        // Bottom panel with Back button
//        JPanel bottomPanel = createBottomPanel();
//        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
//
//        // Table selection listener
//        userTable.getSelectionModel().addListSelectionListener(e -> updateActionButtonStates());
//
//        // Set initial focus
//        searchField.requestFocusInWindow();
//
//        add(mainPanel);
//    }
//
//    private JMenuBar createMenuBar() {
//        JMenuBar menuBar = new JMenuBar();
//        menuBar.setBackground(BACKGROUND_COLOR);
//        menuBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
//
//        // Actions menu
//        JMenu actionsMenu = new JMenu("Actions");
//        actionsMenu.setFont(new Font("Roboto", Font.PLAIN, 14));
//
//        JMenuItem deleteInactiveItem = new JMenuItem("Delete Inactive Accounts");
//        deleteInactiveItem.setFont(new Font("Roboto", Font.PLAIN, 14));
//        deleteInactiveItem.setToolTipText("Delete all inactive accounts");
//        deleteInactiveItem.addActionListener(e -> showDeleteInactiveAccountsDialog());
//        actionsMenu.add(deleteInactiveItem);
//
//        JMenuItem changeCredentialsItem = new JMenuItem("Change Admin Credentials");
//        changeCredentialsItem.setFont(new Font("Roboto", Font.PLAIN, 14));
//        changeCredentialsItem.setToolTipText("Change your admin credentials");
//        changeCredentialsItem.addActionListener(e -> {
//            new ChangeAdminCredentialsScreen(bankSystem).setVisible(true);
//            dispose();
//        });
//        actionsMenu.add(changeCredentialsItem);
//
//        menuBar.add(actionsMenu);
//        return menuBar;
//    }
//
//    private JPanel createTopPanel() {
//        JPanel topPanel = new JPanel(new BorderLayout(15, 10));
//        topPanel.setBackground(Color.WHITE);
//        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//        // Search panel (left side)
//        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
//        searchPanel.setBackground(Color.WHITE);
//        JLabel searchLabel = new JLabel("Search:");
//        searchLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
//        searchLabel.setForeground(TEXT_COLOR);
//        searchLabel.setToolTipText("Search by account number or name");
//        searchField = new JTextField(15);
//        searchField.setFont(new Font("Roboto", Font.PLAIN, 16));
//        searchField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//        searchField.setToolTipText("Enter account number or name to search");
//        searchButton = createStyledButton("Search", true);
//        searchButton.setEnabled(false);
//
//        searchPanel.add(searchLabel);
//        searchPanel.add(searchField);
//        searchPanel.add(searchButton);
//
//        // Action buttons panel (right side)
//        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
//        actionButtonsPanel.setBackground(Color.WHITE);
//
//        // Add Refresh button
//        JButton refreshButton = createStyledButton("Refresh", true);
//        refreshButton.setToolTipText("Refresh the user table");
//        refreshButton.addActionListener(e -> {
//            refreshUserTable();
//        });
//        actionButtonsPanel.add(refreshButton);
//
//        toggleStatusButton = createStyledButton("Toggle Status", true);
//        toggleStatusButton.setEnabled(false);
//        toggleStatusButton.addActionListener(e -> toggleUserStatus());
//
//        viewDetailsButton = createStyledButton("View Details", true);
//        viewDetailsButton.setEnabled(false);
//        viewDetailsButton.addActionListener(e -> viewUserDetails());
//
//        resetPasswordButton = createStyledButton("Reset Password", true);
//        resetPasswordButton.setEnabled(false);
//        resetPasswordButton.addActionListener(e -> showPasswordResetDialog());
//
//        actionButtonsPanel.add(toggleStatusButton);
//        actionButtonsPanel.add(viewDetailsButton);
//        actionButtonsPanel.add(resetPasswordButton);
//
//        topPanel.add(searchPanel, BorderLayout.WEST);
//        topPanel.add(actionButtonsPanel, BorderLayout.EAST);
//
//        // Real-time search validation
//        searchField.getDocument().addDocumentListener(new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                updateSearchButtonState();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                updateSearchButtonState();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                updateSearchButtonState();
//            }
//
//            private void updateSearchButtonState() {
//                String query = searchField.getText().trim();
//                searchButton.setEnabled(!query.isEmpty());
//                searchField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//            }
//        });
//
//        searchButton.addActionListener(e -> {
//            String query = searchField.getText().trim();
//            List<User> results = bankSystem.searchUsers(query);
//            updateTableModel(results);
//            if (results.isEmpty()) {
//                JOptionPane.showMessageDialog(this, "No users found for query: " + query, "No Results", JOptionPane.INFORMATION_MESSAGE);
//            }
//        });
//
//        return topPanel;
//    }
//
//    private JPanel createBottomPanel() {
//        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
//        bottomPanel.setBackground(BACKGROUND_COLOR);
//        JButton backButton = createStyledButton("Back to Login", false);
//        backButton.setToolTipText("Return to the admin login screen");
//        backButton.addActionListener(e -> {
//            new AdminLoginScreen(bankSystem).setVisible(true);
//            dispose();
//        });
//        bottomPanel.add(backButton);
//        return bottomPanel;
//    }
//
//    private void showPasswordResetDialog() {
//        JDialog dialog = new JDialog(this, "Reset User Password", true);
//        dialog.setSize(600, 550); // Increased from 500 to 550
//        dialog.setLocationRelativeTo(this);
//        dialog.add(createPasswordResetPanel(dialog));
//        dialog.setVisible(true);
//    }
//
//    private JPanel createPasswordResetPanel(JDialog dialog) {
//        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(15, 15, 15, 15);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.anchor = GridBagConstraints.WEST;
//
//        // Title
//        JLabel titleLabel = new JLabel("Reset User Password", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Roboto", Font.BOLD, 16));
//        titleLabel.setForeground(TEXT_COLOR);
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        panel.add(titleLabel, gbc);
//
//        // Admin ID
//        JLabel adminIdLabel = new JLabel("Admin ID:");
//        adminIdLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
//        adminIdLabel.setForeground(TEXT_COLOR);
//        adminIdLabel.setToolTipText("Enter your admin ID");
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.gridwidth = 1;
//        panel.add(adminIdLabel, gbc);
//
//        JTextField adminIdField = new JTextField(20);
//        adminIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
//        adminIdField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//        adminIdField.setToolTipText("Enter your admin ID");
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        panel.add(adminIdField, gbc);
//
//        // Admin Password
//        JLabel adminPasswordLabel = new JLabel("Admin Password:");
//        adminPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
//        adminPasswordLabel.setForeground(TEXT_COLOR);
//        adminPasswordLabel.setToolTipText("Enter your admin password");
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        gbc.weightx = 0;
//        panel.add(adminPasswordLabel, gbc);
//
//        JPasswordField adminPasswordField = new JPasswordField(20);
//        adminPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
//        adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//        adminPasswordField.setToolTipText("Enter your admin password");
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        panel.add(adminPasswordField, gbc);
//
//        // Account Number
//        JLabel accountLabel = new JLabel("Account Number:");
//        accountLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
//        accountLabel.setForeground(TEXT_COLOR);
//        accountLabel.setToolTipText("Enter the user's account number");
//        gbc.gridx = 0;
//        gbc.gridy = 3;
//        gbc.weightx = 0;
//        panel.add(accountLabel, gbc);
//
//        JTextField accountField = new JTextField(20);
//        accountField.setFont(new Font("Roboto", Font.PLAIN, 16));
//        accountField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//        accountField.setToolTipText("Enter the user's account number");
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        // Pre-fill account number from selected row
//        int row = userTable.getSelectedRow();
//        if (row >= 0) {
//            String accountNumber = (String) tableModel.getValueAt(row, 0);
//            accountField.setText(accountNumber);
//            accountField.setEditable(false);
//        }
//        panel.add(accountField, gbc);
//
//        // New PIN
//        JLabel newPinLabel = new JLabel("New PIN (4 digits):");
//        newPinLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
//        newPinLabel.setForeground(TEXT_COLOR);
//        newPinLabel.setToolTipText("Enter a 4-digit PIN");
//        gbc.gridx = 0;
//        gbc.gridy = 4;
//        gbc.weightx = 0;
//        panel.add(newPinLabel, gbc);
//
//        JPasswordField newPinField = new JPasswordField(20);
//        newPinField.setFont(new Font("Roboto", Font.PLAIN, 16));
//        newPinField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//        newPinField.setToolTipText("Enter a 4-digit PIN");
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        panel.add(newPinField, gbc);
//
//        // Button panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
//        buttonPanel.setBackground(Color.WHITE);
//        JButton resetButton = createStyledButton("Reset Password", true);
//        resetButton.setEnabled(false);
//        buttonPanel.add(resetButton);
//
//        gbc.gridx = 0;
//        gbc.gridy = 5;
//        gbc.gridwidth = 2;
//        gbc.weighty = 1.0;
//        panel.add(buttonPanel, gbc);
//
//        // Real-time validation
//        DocumentListener validationListener = new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                updateResetButtonState();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                updateResetButtonState();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                updateResetButtonState();
//            }
//
//            private void updateResetButtonState() {
//                String adminId = adminIdField.getText().trim();
//                String adminPassword = new String(adminPasswordField.getPassword()).trim();
//                String accountNumber = accountField.getText().trim();
//                String newPin = new String(newPinField.getPassword()).trim();
//                boolean isValid = !adminId.isEmpty() && !adminPassword.isEmpty() && !accountNumber.isEmpty() && newPin.matches("\\d{4}");
//                resetButton.setEnabled(isValid);
//
//                // Update borders
//                adminIdField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(adminId.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(adminPassword.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//                accountField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(accountNumber.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//                newPinField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(!newPin.matches("\\d{4}") && !newPin.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//            }
//        };
//
//        adminIdField.getDocument().addDocumentListener(validationListener);
//        adminPasswordField.getDocument().addDocumentListener(validationListener);
//        accountField.getDocument().addDocumentListener(validationListener);
//        newPinField.getDocument().addDocumentListener(validationListener);
//
//        resetButton.addActionListener(e -> {
//            String adminId = adminIdField.getText().trim();
//            String adminPassword = new String(adminPasswordField.getPassword()).trim();
//            String accountNumber = accountField.getText().trim();
//            String newPin = new String(newPinField.getPassword()).trim();
//
//            if (adminSystem.resetUserPassword(adminId, adminPassword, accountNumber, newPin)) {
//                JOptionPane.showMessageDialog(dialog, "Password reset successfully for account: " + accountNumber, "Success", JOptionPane.INFORMATION_MESSAGE);
//                refreshUserTable();
//                dialog.dispose();
//            } else {
//                JOptionPane.showMessageDialog(dialog, "Failed to reset password. Verify admin credentials or account number.", "Error", JOptionPane.ERROR_MESSAGE);
//                adminIdField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(ERROR_COLOR),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(ERROR_COLOR),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//                accountField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(ERROR_COLOR),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//            }
//        });
//
//        return panel;
//    }
//
//    private void showDeleteInactiveAccountsDialog() {
//        JDialog dialog = new JDialog(this, "Delete Inactive Accounts", true);
//        dialog.setSize(600, 400);
//        dialog.setLocationRelativeTo(this);
//        dialog.add(createDeleteInactiveAccountsPanel(dialog));
//        dialog.setVisible(true);
//    }
//
//    private JPanel createDeleteInactiveAccountsPanel(JDialog dialog) {
//        JPanel panel = new JPanel(new GridBagLayout());
//        panel.setBackground(Color.WHITE);
//        panel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(15, 15, 15, 15);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.anchor = GridBagConstraints.WEST;
//
//        // Title
//        JLabel titleLabel = new JLabel("Delete Inactive Accounts", SwingConstants.CENTER);
//        titleLabel.setFont(new Font("Roboto", Font.BOLD, 16));
//        titleLabel.setForeground(TEXT_COLOR);
//        gbc.gridx = 0;
//        gbc.gridy = 0;
//        gbc.gridwidth = 2;
//        panel.add(titleLabel, gbc);
//
//        // Admin ID
//        JLabel adminIdLabel = new JLabel("Admin ID:");
//        adminIdLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
//        adminIdLabel.setForeground(TEXT_COLOR);
//        adminIdLabel.setToolTipText("Enter your admin ID");
//        gbc.gridx = 0;
//        gbc.gridy = 1;
//        gbc.gridwidth = 1;
//        panel.add(adminIdLabel, gbc);
//
//        JTextField adminIdField = new JTextField(20);
//        adminIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
//        adminIdField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//        adminIdField.setToolTipText("Enter your admin ID");
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        panel.add(adminIdField, gbc);
//
//        // Admin Password
//        JLabel adminPasswordLabel = new JLabel("Admin Password:");
//        adminPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
//        adminPasswordLabel.setForeground(TEXT_COLOR);
//        adminPasswordLabel.setToolTipText("Enter your admin password");
//        gbc.gridx = 0;
//        gbc.gridy = 2;
//        gbc.weightx = 0;
//        panel.add(adminPasswordLabel, gbc);
//
//        JPasswordField adminPasswordField = new JPasswordField(20);
//        adminPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
//        adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createLineBorder(new Color(200, 200, 200)),
//                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//        adminPasswordField.setToolTipText("Enter your admin password");
//        gbc.gridx = 1;
//        gbc.weightx = 1.0;
//        panel.add(adminPasswordField, gbc);
//
//        // Button panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
//        buttonPanel.setBackground(Color.WHITE);
//        JButton deleteButton = createStyledButton("Delete Inactive Accounts", true);
//        deleteButton.setEnabled(false);
//        buttonPanel.add(deleteButton);
//
//        gbc.gridx = 0;
//        gbc.gridy = 3;
//        gbc.gridwidth = 2;
//        gbc.weighty = 1.0;
//        panel.add(buttonPanel, gbc);
//
//        // Real-time validation
//        DocumentListener validationListener = new DocumentListener() {
//            @Override
//            public void insertUpdate(DocumentEvent e) {
//                updateDeleteButtonState();
//            }
//
//            @Override
//            public void removeUpdate(DocumentEvent e) {
//                updateDeleteButtonState();
//            }
//
//            @Override
//            public void changedUpdate(DocumentEvent e) {
//                updateDeleteButtonState();
//            }
//
//            private void updateDeleteButtonState() {
//                String adminId = adminIdField.getText().trim();
//                String adminPassword = new String(adminPasswordField.getPassword()).trim();
//                boolean isValid = !adminId.isEmpty() && !adminPassword.isEmpty();
//                deleteButton.setEnabled(isValid);
//
//                // Update borders
//                adminIdField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(adminId.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(adminPassword.isEmpty() ? ERROR_COLOR : new Color(200, 200, 200)),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//            }
//        };
//
//        adminIdField.getDocument().addDocumentListener(validationListener);
//        adminPasswordField.getDocument().addDocumentListener(validationListener);
//
//        deleteButton.addActionListener(e -> {
//            String adminId = adminIdField.getText().trim();
//            String adminPassword = new String(adminPasswordField.getPassword()).trim();
//
//            if (adminSystem.deleteInactiveAccounts(adminId, adminPassword)) {
//                JOptionPane.showMessageDialog(dialog, "Inactive accounts deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
//                refreshUserTable();
//                dialog.dispose();
//            } else {
//                JOptionPane.showMessageDialog(dialog, "Failed to delete inactive accounts. Verify admin credentials.", "Error", JOptionPane.ERROR_MESSAGE);
//                adminIdField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(ERROR_COLOR),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
//                        BorderFactory.createLineBorder(ERROR_COLOR),
//                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
//            }
//        });
//
//        return panel;
//    }
//
//    private void refreshUserTable() {
//        try {
//            bankSystem.reloadUsers();
//            List<User> users = bankSystem.getAllUsers();
//            updateTableModel(users);
//            tableModel.fireTableDataChanged();
//        } catch (Exception e) {
//            System.err.println("Error refreshing table: " + e.getMessage());
//            JOptionPane.showMessageDialog(this, "Failed to refresh table: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//        }
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
//        }
//    }
//
//    private void showUserDetails(User user) {
//        JDialog dialog = new JDialog(this, "Account Details: " + user.getAccountNumber(), true);
//        dialog.setSize(700, 650);
//        dialog.setLocationRelativeTo(this);
//        dialog.setResizable(false);
//
//        // Main panel
//        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
//        mainPanel.setBackground(BACKGROUND_COLOR);
//        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        // Summary panel
//        JPanel summaryPanel = new JPanel(new GridBagLayout());
//        summaryPanel.setBackground(Color.WHITE);
//        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Account Summary"),
//                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
//        summaryPanel.setPreferredSize(new Dimension(0, 150));
//
//        GridBagConstraints gbc = new GridBagConstraints();
//        gbc.insets = new Insets(1, 5, 1, 5);
//        gbc.fill = GridBagConstraints.HORIZONTAL;
//        gbc.anchor = GridBagConstraints.WEST;
//
//        // Summary fields
//        String[] labels = {"Account Number:", "Name:", "Account Type:", "Balance:", "Loan Amount:", "Status:", "Last Login:"};
//        String[] values = {
//                user.getAccountNumber(),
//                user.getName(),
//                user.getAccountType(),
//                String.format("$%.2f", user.getBalance()),
//                String.format("$%.2f", user.getLoanAmount()),
//                user.isAccountActive() ? "Active" : "Inactive",
//                new SimpleDateFormat("yyyy-MM-dd").format(user.getLastLoginDate())
//        };
//
//        for (int i = 0; i < labels.length; i++) {
//            JLabel label = new JLabel(labels[i]);
//            label.setFont(new Font("Roboto", Font.BOLD, 14));
//            label.setForeground(TEXT_COLOR);
//            gbc.gridx = 0;
//            gbc.gridy = i;
//            gbc.weightx = 0;
//            summaryPanel.add(label, gbc);
//
//            JLabel value = new JLabel(values[i]);
//            value.setFont(new Font("Roboto", Font.PLAIN, 14));
//            value.setForeground(TEXT_COLOR);
//            gbc.gridx = 1;
//            gbc.weightx = 1.0;
//            summaryPanel.add(value, gbc);
//        }
//
//        mainPanel.add(summaryPanel, BorderLayout.NORTH);
//
//        // Transaction table
//        String[] columnNames = {"Date", "Description", "Amount"};
//        DefaultTableModel transactionModel = new DefaultTableModel(columnNames, 0) {
//            @Override
//            public boolean isCellEditable(int row, int column) {
//                return false;
//            }
//        };
//
//        JTable transactionTable = new JTable(transactionModel);
//        transactionTable.setFont(new Font("Roboto", Font.PLAIN, 14));
//        transactionTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
//        transactionTable.setRowHeight(25);
//        transactionTable.setToolTipText("Transaction history for the last 30 days");
//        transactionTable.setPreferredScrollableViewportSize(new Dimension(650, 250)); // 10 rows at 25px
//
//        // Set column widths
//        transactionTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
//        transactionTable.getColumnModel().getColumn(1).setPreferredWidth(350); // Description
//        transactionTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
//
//        // Center-align Amount column
//        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
//        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
//        transactionTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
//
//        // Alternating row colors
//        transactionTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
//            @Override
//            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//                if (!isSelected) {
//                    c.setBackground(row % 2 == 0 ? Color.WHITE : TABLE_ALT_ROW);
//                }
//                return c;
//            }
//        });
//
//        // Mock or parse transactions
//        List<String[]> transactions = parseTransactions(user);
//        for (String[] tx : transactions) {
//            transactionModel.addRow(tx);
//        }
//
//        JScrollPane transactionScrollPane = new JScrollPane(transactionTable);
//        transactionScrollPane.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)), "Transaction History"),
//                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
//        transactionScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
//        transactionScrollPane.setPreferredSize(new Dimension(650, 400));
//        mainPanel.add(transactionScrollPane, BorderLayout.CENTER);
//
//        // Button panel
//        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
//        buttonPanel.setBackground(Color.WHITE);
//        buttonPanel.setPreferredSize(new Dimension(0, 40));
//        JButton closeButton = createStyledButton("Close", false);
//        closeButton.addActionListener(e -> dialog.dispose());
//        buttonPanel.add(closeButton);
//
//        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
//
//        dialog.add(mainPanel);
//        dialog.setVisible(true);
//    }
//
//    private List<String[]> parseTransactions(User user) {
//        List<String[]> transactions = new ArrayList<>();
//        // Mock transactions (replace with actual parsing if User provides getTransactions)
//        String statement = user.generateStatement(
//                new java.util.Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000),
//                new java.util.Date()
//        );
//
//        // Assume statement format like:
//        // Account: 12345
//        // ...
//        // Transactions:
//        // 2025-05-01 Deposit $500.00
//        // 2025-05-02 Withdrawal -$200.00
//        String[] lines = statement.split("\n");
//        boolean inTransactions = false;
//        for (String line : lines) {
//            if (line.trim().equals("Transactions:")) {
//                inTransactions = true;
//                continue;
//            }
//            if (inTransactions && !line.trim().isEmpty()) {
//                // Parse line like "2025-05-01 Deposit $500.00"
//                String[] parts = line.trim().split("\\s+", 3);
//                if (parts.length == 3) {
//                    transactions.add(new String[]{parts[0], parts[1], parts[2]});
//                }
//            }
//        }
//
//        // Fallback mock data if no transactions parsed
//        if (transactions.isEmpty()) {
//            transactions.add(new String[]{"2025-05-01", "Deposit", "$500.00"});
//            transactions.add(new String[]{"2025-05-02", "Withdrawal", "-$200.00"});
//            transactions.add(new String[]{"2025-05-03", "Transfer", "$300.00"});
//            transactions.add(new String[]{"2025-05-04", "Payment", "-$100.00"});
//            transactions.add(new String[]{"2025-05-05", "Deposit", "$200.00"});
//            transactions.add(new String[]{"2025-05-06", "Withdrawal", "-$150.00"});
//            transactions.add(new String[]{"2025-05-07", "Transfer", "$250.00"});
//            transactions.add(new String[]{"2025-05-08", "Payment", "-$50.00"});
//            transactions.add(new String[]{"2025-05-09", "Deposit", "$400.00"});
//            transactions.add(new String[]{"2025-05-10", "Withdrawal", "-$300.00"});
//            transactions.add(new String[]{"2025-05-11", "Transfer", "$150.00"});
//            transactions.add(new String[]{"2025-05-12", "Payment", "-$75.00"});
//            transactions.add(new String[]{"2025-05-13", "Deposit", "$600.00"});
//            transactions.add(new String[]{"2025-05-14", "Withdrawal", "-$250.00"});
//            transactions.add(new String[]{"2025-05-15", "Transfer", "$200.00"});
//        }
//
//        return transactions;
//    }
//
//    private void updateActionButtonStates() {
//        boolean isRowSelected = userTable.getSelectedRow() >= 0;
//        toggleStatusButton.setEnabled(isRowSelected);
//        viewDetailsButton.setEnabled(isRowSelected);
//        resetPasswordButton.setEnabled(isRowSelected);
//    }
//
//    private JButton createStyledButton(String text, boolean isPrimary) {
//        JButton button = new JButton(text);
//        button.setFont(new Font("Roboto", Font.BOLD, 14));
//        button.setFocusPainted(false);
//        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
//        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
////        button.setPreferredSize(new Dimension(140, 36));
//        button.setToolTipText(text);
//
//        if (isPrimary) {
//            button.setForeground(Color.WHITE);
//            button.setBackground(BUTTON_COLOR);
//            button.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseEntered(MouseEvent e) {
//                    if (button.isEnabled()) {
//                        button.setBackground(BUTTON_HOVER);
//                    }
//                }
//
//                @Override
//                public void mouseExited(MouseEvent e) {
//                    button.setBackground(BUTTON_COLOR);
//                }
//            });
//        } else {
//            button.setForeground(SECONDARY_COLOR);
//            button.setBackground(Color.WHITE);
//            button.setBorder(BorderFactory.createCompoundBorder(
//                    BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
//                    BorderFactory.createEmptyBorder(8, 16, 8, 16)
//            ));
//            button.addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseEntered(MouseEvent e) {
//                    if (button.isEnabled()) {
//                        button.setForeground(SECONDARY_HOVER);
//                        button.setBorder(BorderFactory.createCompoundBorder(
//                                BorderFactory.createLineBorder(SECONDARY_HOVER, 1),
//                                BorderFactory.createEmptyBorder(8, 16, 8, 16)
//                        ));                    }
//                }
//
//                @Override
//                public void mouseExited(MouseEvent e) {
//                    button.setForeground(SECONDARY_COLOR);
//                    button.setBorder(BorderFactory.createCompoundBorder(
//                            BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
//                            BorderFactory.createEmptyBorder(8, 16, 8, 16)
//                    ));                }
//            });
//        }
//
//        return button;
//    }
//}

