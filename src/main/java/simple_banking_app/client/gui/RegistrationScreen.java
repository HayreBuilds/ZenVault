package simple_banking_app.client.gui;

import simple_banking_app.model.User;
import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.regex.Pattern;

public class RegistrationScreen extends JFrame {
    private BankSystem bankSystem;
    private JTextField nameField, securityQuestionField, securityAnswerField;
    private JPasswordField pinField, confirmPinField;
    private JTextField initialDepositField, phoneField, emailField, addressField;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color SECONDARY_COLOR = new Color(107, 114, 128); // #6B7280
    private static final Color SECONDARY_HOVER = new Color(55, 65, 81); // #374151

    public RegistrationScreen(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Register New Account");
        setSize(750, 600); // Adjusted to 750 from 800
        setMinimumSize(new Dimension(700, 550)); // Adjusted to 550 from 600
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
        JLabel titleLabel = new JLabel("Create New Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15); // Restored to original 15px
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Personal Information Section
        JLabel personalInfoLabel = new JLabel("Personal Information");
        personalInfoLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        personalInfoLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formPanel.add(personalInfoLabel, gbc);

        nameField = addFormField(formPanel, gbc, 1, "Full Name:");
        phoneField = addFormField(formPanel, gbc, 2, "Phone Number:");
        emailField = addFormField(formPanel, gbc, 3, "Email:");
        addressField = addFormField(formPanel, gbc, 4, "Address:");

        // Account Information Section
        JLabel accountInfoLabel = new JLabel("Account Information");
        accountInfoLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        accountInfoLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        formPanel.add(accountInfoLabel, gbc);

        initialDepositField = addFormField(formPanel, gbc, 6, "Initial Deposit ($):");

        // Security Information Section
        JLabel securityInfoLabel = new JLabel("Security Information");
        securityInfoLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        securityInfoLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        formPanel.add(securityInfoLabel, gbc);

        pinField = addPasswordField(formPanel, gbc, 8, "PIN (4 digits):");
        confirmPinField = addPasswordField(formPanel, gbc, 9, "Confirm PIN:");
        securityQuestionField = addFormField(formPanel, gbc, 10, "Security Question:");
        securityAnswerField = addFormField(formPanel, gbc, 11, "Security Answer:");

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton registerButton = createStyledButton("Create Account", true);
        JButton backButton = createStyledButton("Back", false);

        buttonPanel.add(registerButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        registerButton.addActionListener(this::registerAction);

        backButton.addActionListener(e -> {
            new WelcomeScreen(bankSystem).setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private JTextField addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Roboto", Font.PLAIN, 16));
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Roboto", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panel.add(textField, gbc);

        return textField;
    }

    private JPasswordField addPasswordField(JPanel panel, GridBagConstraints gbc, int row, String labelText) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Roboto", Font.PLAIN, 16));
        label.setForeground(TEXT_COLOR);
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panel.add(passwordField, gbc);

        return passwordField;
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 36));

        if (isPrimary) {
            button.setForeground(Color.WHITE);
            button.setBackground(BUTTON_COLOR);
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(BUTTON_HOVER);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(BUTTON_COLOR);
                }
            });
        } else {
            button.setForeground(SECONDARY_COLOR);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR, 1));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setForeground(SECONDARY_HOVER);
                    button.setBorder(BorderFactory.createLineBorder(SECONDARY_HOVER, 1));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setForeground(SECONDARY_COLOR);
                    button.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR, 1));
                }
            });
        }

        return button;
    }

    private void registerAction(ActionEvent e) {
        String name = nameField.getText().trim();
        String pin = new String(pinField.getPassword());
        String confirmPin = new String(confirmPinField.getPassword());
        String depositText = initialDepositField.getText().trim();
        String securityQuestion = securityQuestionField.getText().trim();
        String securityAnswer = securityAnswerField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();

        // Validation
        if (name.isEmpty() || pin.isEmpty() || confirmPin.isEmpty() ||
                depositText.isEmpty() || securityQuestion.isEmpty() ||
                securityAnswer.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!pin.equals(confirmPin)) {
            JOptionPane.showMessageDialog(this, "PINs do not match",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (pin.length() != 4 || !pin.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "PIN must be 4 digits",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email (e.g., someone@gmail.com)",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!phone.matches("^\\d{10}$")) {
            JOptionPane.showMessageDialog(this, "Phone number must be exactly 10 digits (e.g., 0940784596)",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!name.matches("[A-Za-z\\s]+")) {
            JOptionPane.showMessageDialog(this, "Name must contain only letters and spaces",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            double initialDeposit = Double.parseDouble(depositText);
            if (initialDeposit < 0) {
                JOptionPane.showMessageDialog(this, "Initial deposit cannot be negative",
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Default to Savings account type
            String accountType = "Savings";
            User newUser = bankSystem.registerUser(
                    name, pin, initialDeposit, accountType,
                    securityQuestion, securityAnswer, phone, email, address
            );

            if (newUser != null) {
                JOptionPane.showMessageDialog(this,
                        "Account created successfully!\nAccount Number: " + newUser.getAccountNumber(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                new WelcomeScreen(bankSystem).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Account creation failed",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number for initial deposit",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}