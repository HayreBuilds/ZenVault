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

public class LoanScreen extends JFrame {
    private BankSystem bankSystem;
    private User user;
    private JTextField amountField;
    private JTextField repayAmountField;
    private JButton applyButton;
    private JButton repayButton;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // #EF4444

    public LoanScreen(BankSystem bankSystem, User user) {
        this.bankSystem = bankSystem;
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Loan Management");
        setSize(650, 700);
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
        JLabel titleLabel = new JLabel("Loan Management", SwingConstants.CENTER);
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

        // Current Loan
        JLabel loanLabel = new JLabel("Current Loan: $" + String.format("%.2f", user.getLoanAmount()));
        loanLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        loanLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(loanLabel, gbc);

        // Tabbed Pane
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Roboto", Font.PLAIN, 14));

        // Apply for Loan Panel
        JPanel applyPanel = new JPanel(new GridBagLayout());
        applyPanel.setBackground(Color.WHITE);
        GridBagConstraints gbcApply = new GridBagConstraints();
        gbcApply.insets = new Insets(15, 15, 15, 15);
        gbcApply.fill = GridBagConstraints.HORIZONTAL;

        JLabel applyLabel = new JLabel("Apply for Loan");
        applyLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        applyLabel.setForeground(TEXT_COLOR);
        gbcApply.gridx = 0;
        gbcApply.gridy = 0;
        gbcApply.gridwidth = 2;
        applyPanel.add(applyLabel, gbcApply);

        JLabel amountLabel = new JLabel("Loan Amount:");
        amountLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setToolTipText("Enter the loan amount");
        gbcApply.gridx = 0;
        gbcApply.gridy = 1;
        gbcApply.gridwidth = 1;
        applyPanel.add(amountLabel, gbcApply);

        amountField = new JTextField(20);
        amountField.setFont(new Font("Roboto", Font.PLAIN, 16));
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        amountField.setToolTipText("Enter the loan amount");
        gbcApply.gridx = 1;
        gbcApply.weightx = 1.0;
        applyPanel.add(amountField, gbcApply);

        applyButton = createStyledButton("Apply");
        applyButton.setEnabled(false);
        gbcApply.gridx = 0;
        gbcApply.gridy = 2;
        gbcApply.gridwidth = 2;
        gbcApply.weightx = 0;
        applyPanel.add(applyButton, gbcApply);

        // Repay Loan Panel
        JPanel repayPanel = new JPanel(new GridBagLayout());
        repayPanel.setBackground(Color.WHITE);
        GridBagConstraints gbcRepay = new GridBagConstraints();
        gbcRepay.insets = new Insets(15, 15, 15, 15);
        gbcRepay.fill = GridBagConstraints.HORIZONTAL;

        JLabel repayLabel = new JLabel("Repay Loan");
        repayLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        repayLabel.setForeground(TEXT_COLOR);
        gbcRepay.gridx = 0;
        gbcRepay.gridy = 0;
        gbcRepay.gridwidth = 2;
        repayPanel.add(repayLabel, gbcRepay);

        JLabel repayAmountLabel = new JLabel("Repayment Amount:");
        repayAmountLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        repayAmountLabel.setForeground(TEXT_COLOR);
        repayAmountLabel.setToolTipText("Enter the repayment amount");
        gbcRepay.gridx = 0;
        gbcRepay.gridy = 1;
        gbcRepay.gridwidth = 1;
        repayPanel.add(repayAmountLabel, gbcRepay);

        repayAmountField = new JTextField(20);
        repayAmountField.setFont(new Font("Roboto", Font.PLAIN, 16));
        repayAmountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        repayAmountField.setToolTipText("Enter the repayment amount");
        gbcRepay.gridx = 1;
        gbcRepay.weightx = 1.0;
        repayPanel.add(repayAmountField, gbcRepay);

        JLabel balanceLabel = new JLabel("Available Balance: $" + String.format("%.2f", user.getBalance()));
        balanceLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        balanceLabel.setForeground(TEXT_COLOR);
        gbcRepay.gridx = 0;
        gbcRepay.gridy = 2;
        gbcRepay.gridwidth = 2;
        gbcRepay.weightx = 0;
        repayPanel.add(balanceLabel, gbcRepay);

        repayButton = createStyledButton("Repay");
        repayButton.setEnabled(false);
        gbcRepay.gridx = 0;
        gbcRepay.gridy = 3;
        gbcRepay.gridwidth = 2;
        repayPanel.add(repayButton, gbcRepay);

        tabbedPane.addTab("Apply", applyPanel);
        tabbedPane.addTab("Repay", repayPanel);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        contentPanel.add(tabbedPane, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        JButton backButton = createStyledButton("Back to Dashboard");
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weighty = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        contentPanel.add(buttonPanel, gbc);

        // Scroll pane for content
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Real-time validation for Apply Loan
        amountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateApplyButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateApplyButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateApplyButtonState();
            }

            private void updateApplyButtonState() {
                String amountText = amountField.getText().trim();
                if (amountText.isEmpty()) {
                    amountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    applyButton.setEnabled(false);
                    return;
                }
                try {
                    double amount = Double.parseDouble(amountText);
                    if (amount <= 0) {
                        amountField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(ERROR_COLOR),
                                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                        applyButton.setEnabled(false);
                    } else {
                        amountField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                        applyButton.setEnabled(true);
                    }
                } catch (NumberFormatException ex) {
                    amountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ERROR_COLOR),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    applyButton.setEnabled(false);
                }
            }
        });

        // Real-time validation for Repay Loan
        repayAmountField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateRepayButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateRepayButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateRepayButtonState();
            }

            private void updateRepayButtonState() {
                String amountText = repayAmountField.getText().trim();
                if (amountText.isEmpty()) {
                    repayAmountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(new Color(200, 200, 200)),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    repayButton.setEnabled(false);
                    balanceLabel.setForeground(TEXT_COLOR);
                    return;
                }
                try {
                    double amount = Double.parseDouble(amountText);
                    if (amount <= 0 || amount > user.getBalance() || amount > user.getLoanAmount()) {
                        repayAmountField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(ERROR_COLOR),
                                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                        repayButton.setEnabled(false);
                        balanceLabel.setForeground(amount > user.getBalance() ? ERROR_COLOR : TEXT_COLOR);
                    } else {
                        repayAmountField.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                        repayButton.setEnabled(true);
                        balanceLabel.setForeground(TEXT_COLOR);
                    }
                } catch (NumberFormatException ex) {
                    repayAmountField.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(ERROR_COLOR),
                            BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                    repayButton.setEnabled(false);
                }
            }
        });

        // Apply button action
        applyButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive amount", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (bankSystem.applyForLoan(user, amount)) {
                    JOptionPane.showMessageDialog(this, String.format("Loan of $%.2f approved!", amount), "Success", JOptionPane.INFORMATION_MESSAGE);
                    new DashboardScreen(bankSystem, user).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Loan application failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Repay button action
        repayButton.addActionListener(e -> {
            try {
                double amount = Double.parseDouble(repayAmountField.getText().trim());
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive amount", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (amount > user.getBalance()) {
                    JOptionPane.showMessageDialog(this, "Insufficient balance for repayment", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (amount > user.getLoanAmount()) {
                    JOptionPane.showMessageDialog(this, "Repayment amount exceeds current loan", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (bankSystem.repayLoan(user, amount)) {
                    JOptionPane.showMessageDialog(this, String.format("Repayment of $%.2f successful!", amount), "Success", JOptionPane.INFORMATION_MESSAGE);
                    new DashboardScreen(bankSystem, user).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Repayment failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            new DashboardScreen(bankSystem, user).setVisible(true);
            dispose();
        });

        // Set initial focus
        amountField.requestFocusInWindow();

        add(mainPanel);
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