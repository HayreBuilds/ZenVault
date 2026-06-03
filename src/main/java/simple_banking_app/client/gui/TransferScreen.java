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

public class TransferScreen extends JFrame {
    private BankSystem bankSystem;
    private User user;
    private JTextField recipientField;
    private JTextField amountField;
    private JTextField descriptionField;
    private JLabel recipientNameLabel;
    private JLabel balanceLabel;
    private JButton confirmButton;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // #EF4444

    public TransferScreen(BankSystem bankSystem, User user) {
        this.bankSystem = bankSystem;
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Transfer Money");
        setSize(800, 700); // Increased size for better fit
        setMinimumSize(new Dimension(600, 550));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

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
        headerPanel.setPreferredSize(new Dimension(0, 100));
        JLabel titleLabel = new JLabel("Transfer Money", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel (card)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(30, 30, 30, 30)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Recipient Account Number
        JLabel recipientLabel = new JLabel("Recipient Account Number:");
        recipientLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
        recipientLabel.setForeground(TEXT_COLOR);
        recipientLabel.setToolTipText("Enter the recipient's account number");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        contentPanel.add(recipientLabel, gbc);

        recipientField = new JTextField(20);
        recipientField.setFont(new Font("Roboto", Font.PLAIN, 18));
        recipientField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        recipientField.setToolTipText("Enter the recipient's account number");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(recipientField, gbc);

        // Recipient Name Label
        recipientNameLabel = new JLabel("Recipient Name: (Enter account number)");
        recipientNameLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        recipientNameLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        contentPanel.add(recipientNameLabel, gbc);

        // Amount
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setToolTipText("Enter the amount to transfer");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0;
        contentPanel.add(amountLabel, gbc);

        amountField = new JTextField(20);
        amountField.setFont(new Font("Roboto", Font.PLAIN, 18));
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        amountField.setToolTipText("Enter the amount to transfer");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(amountField, gbc);

        // Description
        JLabel descriptionLabel = new JLabel("Description (Optional):");
        descriptionLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
        descriptionLabel.setForeground(TEXT_COLOR);
        descriptionLabel.setToolTipText("Optional description for the transfer");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        contentPanel.add(descriptionLabel, gbc);

        descriptionField = new JTextField(20);
        descriptionField.setFont(new Font("Roboto", Font.PLAIN, 18));
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        descriptionField.setToolTipText("Optional description for the transfer");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(descriptionField, gbc);

        // Available Balance
        balanceLabel = new JLabel("Available Balance: $" + String.format("%.2f", user.getBalance()));
        balanceLabel.setFont(new Font("Roboto", Font.PLAIN, 18));
        balanceLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        contentPanel.add(balanceLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setBackground(Color.WHITE);

        confirmButton = createStyledButton("Confirm Transfer");
        confirmButton.setEnabled(false);
        JButton backButton = createStyledButton("Back to Dashboard");

        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.weighty = 0; // Remove weighty to prevent pushing out of view
        gbc.insets = new Insets(20, 20, 30, 20); // Extra bottom padding
        contentPanel.add(buttonPanel, gbc);

        // Add content panel directly to main panel
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Real-time recipient validation
        recipientField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateRecipientName();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateRecipientName();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateRecipientName();
            }

            private void updateRecipientName() {
                String accountNumber = recipientField.getText().trim();
                if (accountNumber.isEmpty()) {
                    recipientNameLabel.setText("Recipient Name: (Enter account number)");
                    recipientNameLabel.setForeground(TEXT_COLOR);
                    updateConfirmButtonState();
                    return;
                }
                User recipient = bankSystem.findUserByAccountNumber(accountNumber);
                if (recipient != null) {
                    if (recipient.getAccountNumber().equals(user.getAccountNumber())) {
                        recipientNameLabel.setText("Recipient Name: (Cannot transfer to your own account)");
                        recipientNameLabel.setForeground(ERROR_COLOR);
                    } else {
                        recipientNameLabel.setText("Recipient Name: " + recipient.getName());
                        recipientNameLabel.setForeground(TEXT_COLOR);
                    }
                } else {
                    recipientNameLabel.setText("Recipient Name: (Invalid account number)");
                    recipientNameLabel.setForeground(ERROR_COLOR);
                }
                updateConfirmButtonState();
            }
        });

        // Real-time amount validation
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateAmountField();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateAmountField();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateAmountField();
            }

            private void updateAmountField() {
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    amountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                    updateConfirmButtonState();
                    return;
                }
                try {
                    double amount = Double.parseDouble(amountText);
                    if (amount <= 0) {
                        amountField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(ERROR_COLOR),
                                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                    } else if (amount > user.getBalance()) {
                        amountField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(ERROR_COLOR),
                                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                        balanceLabel.setForeground(ERROR_COLOR);
                    } else {
                        amountField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                        balanceLabel.setForeground(TEXT_COLOR);
                    }
                } catch (NumberFormatException ex) {
                    amountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ERROR_COLOR),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
                }
                updateConfirmButtonState();
            }
        });

        // Confirm button action
        confirmButton.addActionListener(e -> {
            String recipientAccount = recipientField.getText().trim();
            String amountText = amountField.getText().trim();
            String description = descriptionField.getText().trim();

            try {
                double amount = Double.parseDouble(amountText);
                User recipient = bankSystem.findUserByAccountNumber(recipientAccount);

                if (recipient == null) {
                    JOptionPane.showMessageDialog(TransferScreen.this,
                            "Invalid recipient account number",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (recipient.getAccountNumber().equals(user.getAccountNumber())) {
                    JOptionPane.showMessageDialog(TransferScreen.this,
                            "Cannot transfer to your own account",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(TransferScreen.this,
                            "Please enter a positive amount",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (amount > user.getBalance()) {
                    JOptionPane.showMessageDialog(TransferScreen.this,
                            "Insufficient balance for transfer",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (bankSystem.transferMoney(user, recipientAccount, amount, description)) {
                    JOptionPane.showMessageDialog(TransferScreen.this,
                            String.format("$%.2f transferred successfully to %s", amount, recipient.getName()),
                            "Success", JOptionPane.INFORMATION_MESSAGE);
                    new DashboardScreen(bankSystem, user).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(TransferScreen.this,
                            "Transfer failed. Please try again.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(TransferScreen.this,
                        "Please enter a valid amount",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            new DashboardScreen(bankSystem, user).setVisible(true);
            dispose();
        });

        // Set initial focus
        recipientField.requestFocusInWindow();

        add(mainPanel);
    }

    private void updateConfirmButtonState() {
        String recipientAccount = recipientField.getText().trim();
        String amountText = amountField.getText().trim();

        if (recipientAccount.isEmpty() || amountText.isEmpty()) {
            confirmButton.setEnabled(false);
            return;
        }

        User recipient = bankSystem.findUserByAccountNumber(recipientAccount);
        if (recipient == null || recipient.getAccountNumber().equals(user.getAccountNumber())) {
            confirmButton.setEnabled(false);
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0 || amount > user.getBalance()) {
                confirmButton.setEnabled(false);
            } else {
                confirmButton.setEnabled(true);
            }
        } catch (NumberFormatException ex) {
            confirmButton.setEnabled(false);
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(BUTTON_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
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