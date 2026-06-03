package simple_banking_app.client.gui;

import simple_banking_app.model.User;
import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChangePinScreen extends JFrame {
    private BankSystem bankSystem;
    private User user;
    private JPasswordField oldPinField;
    private JPasswordField newPinField;
    private JPasswordField confirmPinField;
    private JButton submitButton;

    public ChangePinScreen(BankSystem bankSystem, User user) {
        this.bankSystem = bankSystem;
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Change PIN");
        setSize(500, 450);
        setMinimumSize(new Dimension(400, 350));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with light gray background
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(247, 250, 252)); // #F7FAFC
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header panel with gradient
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)) {
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
        JLabel titleLabel = new JLabel("Change Your PIN");
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content card
        JPanel contentCard = new JPanel(new GridBagLayout());
        contentCard.setBackground(Color.WHITE);
        contentCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Old PIN
        JLabel oldPinLabel = new JLabel("Current PIN:");
        oldPinLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        oldPinLabel.setForeground(new Color(31, 41, 55)); // #1F2937
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentCard.add(oldPinLabel, gbc);

        oldPinField = new JPasswordField(15);
        oldPinField.setFont(new Font("Roboto", Font.PLAIN, 16));
        oldPinField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        contentCard.add(oldPinField, gbc);

        // New PIN
        JLabel newPinLabel = new JLabel("New PIN (4 digits):");
        newPinLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        newPinLabel.setForeground(new Color(31, 41, 55));
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentCard.add(newPinLabel, gbc);

        newPinField = new JPasswordField(15);
        newPinField.setFont(new Font("Roboto", Font.PLAIN, 16));
        newPinField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        contentCard.add(newPinField, gbc);

        // Confirm PIN
        JLabel confirmPinLabel = new JLabel("Confirm New PIN:");
        confirmPinLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        confirmPinLabel.setForeground(new Color(31, 41, 55));
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentCard.add(confirmPinLabel, gbc);

        confirmPinField = new JPasswordField(15);
        confirmPinField.setFont(new Font("Roboto", Font.PLAIN, 16));
        confirmPinField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        contentCard.add(confirmPinField, gbc);

        mainPanel.add(contentCard, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(247, 250, 252));
        submitButton = createStyledButton("Submit", new Color(20, 184, 166)); // Teal #14B8A6
        submitButton.setEnabled(false);
        JButton backButton = createStyledButton("Back to Dashboard", new Color(59, 130, 246)); // Blue #3B82F6
        buttonPanel.add(backButton);
        buttonPanel.add(submitButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Real-time validation
        DocumentListener validationListener = new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFieldStates();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFieldStates();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFieldStates();
            }

            private void updateFieldStates() {
                String oldPin = new String(oldPinField.getPassword()).trim();
                String newPin = new String(newPinField.getPassword()).trim();
                String confirmPin = new String(confirmPinField.getPassword()).trim();

                // Update borders
                oldPinField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(oldPin.isEmpty() ? new Color(239, 68, 68) : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                newPinField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder((!newPin.matches("\\d{4}") && !newPin.isEmpty()) ? new Color(239, 68, 68) : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                confirmPinField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder((!confirmPin.equals(newPin) && !confirmPin.isEmpty()) ? new Color(239, 68, 68) : new Color(200, 200, 200)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));

                // Enable submit button if all fields are valid
                submitButton.setEnabled(!oldPin.isEmpty() && newPin.matches("\\d{4}") && newPin.equals(confirmPin));
            }
        };

        oldPinField.getDocument().addDocumentListener(validationListener);
        newPinField.getDocument().addDocumentListener(validationListener);
        confirmPinField.getDocument().addDocumentListener(validationListener);

        // Submit action
        submitButton.addActionListener(e -> {
            String oldPin = new String(oldPinField.getPassword());
            String newPin = new String(newPinField.getPassword());
            String confirmPin = new String(confirmPinField.getPassword());

            if (!oldPin.equals(user.getPin())) {
                JOptionPane.showMessageDialog(ChangePinScreen.this,
                        "Current PIN is incorrect",
                        "Error", JOptionPane.ERROR_MESSAGE);
                oldPinField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(239, 68, 68)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return;
            }

            if (!newPin.equals(confirmPin)) {
                JOptionPane.showMessageDialog(ChangePinScreen.this,
                        "New PINs do not match",
                        "Error", JOptionPane.ERROR_MESSAGE);
                confirmPinField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(239, 68, 68)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return;
            }

            if (newPin.length() != 4 || !newPin.matches("\\d+")) {
                JOptionPane.showMessageDialog(ChangePinScreen.this,
                        "PIN must be 4 digits",
                        "Error", JOptionPane.ERROR_MESSAGE);
                newPinField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(239, 68, 68)),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return;
            }

            if (bankSystem.changePin(user, oldPin, newPin)) {
                JOptionPane.showMessageDialog(ChangePinScreen.this,
                        "PIN changed successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
                new DashboardScreen(bankSystem, user).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(ChangePinScreen.this,
                        "Failed to change PIN",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Back action
        backButton.addActionListener(e -> {
            new DashboardScreen(bankSystem, user).setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}