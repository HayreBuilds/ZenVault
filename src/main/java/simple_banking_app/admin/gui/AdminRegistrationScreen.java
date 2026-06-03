package simple_banking_app.admin.gui;

import simple_banking_app.client.gui.WelcomeScreen;
import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminRegistrationScreen extends JFrame {
    private BankSystem bankSystem;
    private JTextField adminIdField;
    private JPasswordField adminPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // #EF4444

    public AdminRegistrationScreen(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Admin Registration");
        setSize(600, 500);
        setMinimumSize(new Dimension(500, 400));
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
        JLabel titleLabel = new JLabel("Register New Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel (card)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Admin ID
        JLabel adminIdLabel = new JLabel("Admin ID:");
        adminIdLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminIdLabel.setForeground(TEXT_COLOR);
        adminIdLabel.setToolTipText("Enter new admin ID");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        contentPanel.add(adminIdLabel, gbc);

        adminIdField = new JTextField(20);
        adminIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminIdField.setToolTipText("Enter new admin ID");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(adminIdField, gbc);

        // Admin Password
        JLabel adminPasswordLabel = new JLabel("Password:");
        adminPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminPasswordLabel.setForeground(TEXT_COLOR);
        adminPasswordLabel.setToolTipText("Enter new password (minimum 4 characters)");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        contentPanel.add(adminPasswordLabel, gbc);

        adminPasswordField = new JPasswordField(20);
        adminPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminPasswordField.setToolTipText("Enter new password (minimum 4 characters)");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(adminPasswordField, gbc);

        // Confirm Password
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        confirmPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        confirmPasswordLabel.setForeground(TEXT_COLOR);
        confirmPasswordLabel.setToolTipText("Re-enter the password");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        contentPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        confirmPasswordField.setToolTipText("Re-enter the password");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(confirmPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        registerButton = createStyledButton("Register");
        registerButton.setEnabled(false);
        JButton backButton = createStyledButton("Back");

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 1.0;
        contentPanel.add(buttonPanel, gbc);

        // Scroll pane for content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Real-time validation
        DocumentListener validationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateRegisterButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateRegisterButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateRegisterButtonState();
            }
        };

        adminIdField.getDocument().addDocumentListener(validationListener);
        adminPasswordField.getDocument().addDocumentListener(validationListener);
        confirmPasswordField.getDocument().addDocumentListener(validationListener);

        // Register button action
        registerButton.addActionListener(e -> {
            String adminId = adminIdField.getText().trim();
            String password = new String(adminPasswordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            // Validate password match
            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return;
            }

            // Validate password length
            if (password.length() < 4) {
                JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return;
            }

            // If no admins exist, register directly
            if (!bankSystem.hasAdmins()) {
                if (bankSystem.registerAdmin(adminId, password)) {
                    JOptionPane.showMessageDialog(this, "Admin registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new AdminLoginScreen(bankSystem).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Admin ID already exists or registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                    adminIdField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ERROR_COLOR),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                }
                return;
            }

            // Show confirmation dialog for admin credentials
            showAdminConfirmationDialog(adminId, password);
        });

        // Back button action
        backButton.addActionListener(e -> {
            new AdminLoginScreen(bankSystem).setVisible(true);
            dispose();
        });

        // Set initial focus
        adminIdField.requestFocusInWindow();

        add(mainPanel);
    }

    private void showAdminConfirmationDialog(String newAdminId, String newAdminPassword) {
        JDialog dialog = new JDialog(this, "Confirm Admin Credentials", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        // Dialog panel
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
        JLabel titleLabel = new JLabel("Enter Admin Credentials", SwingConstants.CENTER);
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
        adminIdLabel.setToolTipText("Enter an existing admin ID");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        panel.add(adminIdLabel, gbc);

        JTextField adminIdField = new JTextField(20);
        adminIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminIdField.setToolTipText("Enter an existing admin ID");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(adminIdField, gbc);

        // Admin Password
        JLabel adminPasswordLabel = new JLabel("Admin Password:");
        adminPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminPasswordLabel.setForeground(TEXT_COLOR);
        adminPasswordLabel.setToolTipText("Enter admin password (minimum 4 characters)");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        panel.add(adminPasswordLabel, gbc);

        JPasswordField adminPasswordField = new JPasswordField(20);
        adminPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminPasswordField.setToolTipText("Enter admin password (minimum 4 characters)");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(adminPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton confirmButton = createStyledButton("Confirm");
        confirmButton.setEnabled(false);
        JButton cancelButton = createStyledButton("Cancel");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        panel.add(buttonPanel, gbc);

        // Real-time validation for dialog
        DocumentListener validationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateConfirmButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateConfirmButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateConfirmButtonState();
            }

            private void updateConfirmButtonState() {
                String adminId = adminIdField.getText().trim();
                String password = new String(adminPasswordField.getPassword()).trim();
                boolean isValid = !adminId.isEmpty() && !password.isEmpty() && password.length() >= 4;
                confirmButton.setEnabled(isValid);

                // Reset borders
                adminIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        };

        adminIdField.getDocument().addDocumentListener(validationListener);
        adminPasswordField.getDocument().addDocumentListener(validationListener);

        // Confirm button action
        confirmButton.addActionListener(e -> {
            String adminId = adminIdField.getText().trim();
            String adminPassword = new String(adminPasswordField.getPassword()).trim();

            // Validate password length
            if (adminPassword.length() < 4) {
                JOptionPane.showMessageDialog(dialog, "Admin password must be at least 4 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return;
            }

            // Verify admin credentials
            if (!bankSystem.adminLogin(adminId, adminPassword)) {
                JOptionPane.showMessageDialog(dialog, "Invalid admin ID or password", "Error", JOptionPane.ERROR_MESSAGE);
                adminIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return;
            }

            // Proceed with registration
            if (bankSystem.registerAdmin(newAdminId, newAdminPassword)) {
                JOptionPane.showMessageDialog(this, "Admin registered successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                new AdminLoginScreen(bankSystem).setVisible(true);
                dialog.dispose();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Admin ID already exists or registration failed", "Error", JOptionPane.ERROR_MESSAGE);
                adminIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        });

        // Cancel button action
        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void updateRegisterButtonState() {
        String adminId = adminIdField.getText().trim();
        String password = new String(adminPasswordField.getPassword()).trim();
        String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

        boolean isValid = !adminId.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty() && password.equals(confirmPassword);
        registerButton.setEnabled(isValid);

        // Reset borders if valid
        if (!adminId.isEmpty()) {
            adminIdField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        }
        if (!password.isEmpty()) {
            adminPasswordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        }
        if (!confirmPassword.isEmpty()) {
            confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setToolTipText(text);

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

        return button;
    }
}