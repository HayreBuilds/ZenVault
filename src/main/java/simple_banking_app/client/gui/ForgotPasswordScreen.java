package simple_banking_app.client.gui;

import simple_banking_app.model.User;
import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ForgotPasswordScreen extends JFrame {
    private BankSystem bankSystem;
    private JTextField accountField;
    private JLabel questionLabel;
    private JTextField answerField;
    private JPasswordField newPinField;
    private JButton submitButton;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color SECONDARY_COLOR = new Color(107, 114, 128); // #6B7280
    private static final Color SECONDARY_HOVER = new Color(55, 65, 81); // #374151
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // #EF4444

    public ForgotPasswordScreen(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Password Recovery");
        setSize(600, 550); // Increased from 500 to 550
        setMinimumSize(new Dimension(500, 450)); // Increased from 400 to 450
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
        JLabel titleLabel = new JLabel("Password Recovery", SwingConstants.CENTER);
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

        // Account Number
        JLabel accountLabel = new JLabel("Account Number:");
        accountLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        accountLabel.setForeground(TEXT_COLOR);
        accountLabel.setToolTipText("Enter your account number");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        contentPanel.add(accountLabel, gbc);

        accountField = new JTextField(20);
        accountField.setFont(new Font("Roboto", Font.PLAIN, 16));
        accountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        accountField.setToolTipText("Enter your account number");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(accountField, gbc);

        // Security Question
        questionLabel = new JLabel("Security Question: (Enter account number)");
        questionLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        questionLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        contentPanel.add(questionLabel, gbc);

        // Answer
        JLabel answerLabel = new JLabel("Answer:");
        answerLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        answerLabel.setForeground(TEXT_COLOR);
        answerLabel.setToolTipText("Enter the answer to your security question");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        contentPanel.add(answerLabel, gbc);

        answerField = new JTextField(20);
        answerField.setFont(new Font("Roboto", Font.PLAIN, 16));
        answerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        answerField.setToolTipText("Enter the answer to your security question");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(answerField, gbc);

        // New PIN
        JLabel newPinLabel = new JLabel("New PIN (4 digits):");
        newPinLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        newPinLabel.setForeground(TEXT_COLOR);
        newPinLabel.setToolTipText("Enter a 4-digit PIN");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        contentPanel.add(newPinLabel, gbc);

        newPinField = new JPasswordField(20);
        newPinField.setFont(new Font("Roboto", Font.PLAIN, 16));
        newPinField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        newPinField.setToolTipText("Enter a 4-digit PIN");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(newPinField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        submitButton = createStyledButton("Submit", true);
        submitButton.setEnabled(false);
        JButton backButton = createStyledButton("Back to Login", false);

        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 1.0;
        contentPanel.add(buttonPanel, gbc);

        // Add content panel directly (no JScrollPane)
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Real-time account number validation
        accountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAccountField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAccountField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateAccountField();
            }

            private void updateAccountField() {
                String accountNumber = accountField.getText().trim();
                if (accountNumber.isEmpty()) {
                    questionLabel.setText("Security Question: (Enter account number)");
                    questionLabel.setForeground(TEXT_COLOR);
                    accountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    submitButton.setEnabled(false);
                    return;
                }
                User user = bankSystem.findUserByAccountNumber(accountNumber);
                if (user != null) {
                    questionLabel.setText("Security Question: " + user.getSecurityQuestion());
                    questionLabel.setForeground(TEXT_COLOR);
                    accountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                } else {
                    questionLabel.setText("Security Question: (Invalid account number)");
                    questionLabel.setForeground(ERROR_COLOR);
                    accountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ERROR_COLOR),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    submitButton.setEnabled(false);
                }
                updateSubmitButtonState();
            }
        });

        // Real-time PIN validation
        newPinField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updatePinField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updatePinField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updatePinField();
            }

            private void updatePinField() {
                String newPin = new String(newPinField.getPassword()).trim();
                if (newPin.isEmpty()) {
                    newPinField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    submitButton.setEnabled(false);
                    return;
                }
                if (newPin.length() != 4 || !newPin.matches("\\d+")) {
                    newPinField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ERROR_COLOR),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    submitButton.setEnabled(false);
                } else {
                    newPinField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    updateSubmitButtonState();
                }
            }
        });

        // Answer field validation
        answerField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSubmitButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSubmitButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateSubmitButtonState();
            }
        });

        // Submit button action
        submitButton.addActionListener(e -> {
            String accountNumber = accountField.getText().trim();
            String answer = answerField.getText().trim();
            String newPin = new String(newPinField.getPassword()).trim();

            User user = bankSystem.findUserByAccountNumber(accountNumber);
            if (user == null) {
                JOptionPane.showMessageDialog(this, "Invalid account number", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (answer.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please provide an answer to the security question", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newPin.length() != 4 || !newPin.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "New PIN must be 4 digits", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (bankSystem.resetPassword(accountNumber, answer, newPin)) {
                JOptionPane.showMessageDialog(this, "Password reset successfully. Please login with your new PIN.", "Success", JOptionPane.INFORMATION_MESSAGE);
                new LoginScreen(bankSystem).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Password reset failed. Verify your answer or contact support.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            new LoginScreen(bankSystem).setVisible(true);
            dispose();
        });

        // Set initial focus
        accountField.requestFocusInWindow();

        add(mainPanel);
    }

    private void updateSubmitButtonState() {
        String accountNumber = accountField.getText().trim();
        String answer = answerField.getText().trim();
        String newPin = new String(newPinField.getPassword()).trim();

        if (accountNumber.isEmpty() || answer.isEmpty() || newPin.isEmpty()) {
            submitButton.setEnabled(false);
            return;
        }

        User user = bankSystem.findUserByAccountNumber(accountNumber);
        if (user == null || newPin.length() != 4 || !newPin.matches("\\d+")) {
            submitButton.setEnabled(false);
        } else {
            submitButton.setEnabled(true);
        }
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 36));
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
            button.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR, 1));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setForeground(SECONDARY_HOVER);
                        button.setBorder(BorderFactory.createLineBorder(SECONDARY_HOVER, 1));
                    }
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
}