package com.example.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

// Minimal User class stub for GUI rendering
class User {
    private String name = "John Doe";
    private String accountNumber = "1234567890";
    private double balance = 1000.00;
    private double loanAmount = 500.00;

    public String getName() { return name; }
    public String getAccountNumber() { return accountNumber; }
    public double getBalance() { return balance; }
    public double getLoanAmount() { return loanAmount; }
}

// Minimal BankSystem class stub
class BankSystem {
    // Empty stub to satisfy constructor dependency
}

public class DashboardScreenGUI extends JFrame {
    private BankSystem bankSystem;
    private User user;
    private JLabel accountLabel;
    private JLabel balanceLabel;
    private JLabel loanLabel;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color SECONDARY_COLOR = new Color(107, 114, 128); // #6B7280
    private static final Color SECONDARY_HOVER = new Color(55, 65, 81); // #374151

    public DashboardScreenGUI(BankSystem bankSystem, User user) {
        this.bankSystem = bankSystem;
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Jovani Bank - Dashboard");
        setSize(1080, 750);
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header panel with gradient
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)) {
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
        headerPanel.setPreferredSize(new Dimension(getWidth(), 80));
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName());
        welcomeLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        headerPanel.add(welcomeLabel);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());

        contentPanel.setBackground(BACKGROUND_COLOR);
        contentPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        // Refresh button panel
        JPanel refreshPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        refreshPanel.setBackground(Color.yellow);
//        refreshPanel.setBorder(BorderFactory.createCompoundBorder(
//                BorderFactory.createEmptyBorder(10,20,10,20),
//                BorderFactory.createLineBorder(Color.red)
//        ));
        JButton refreshButton = createStyledButton("Refresh", true);
        refreshButton.setToolTipText("Refresh account information");
        refreshPanel.add(refreshButton);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(refreshPanel, gbc);

        // Account info card
        JPanel infoCard = new JPanel(new GridBagLayout());
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        infoCard.setPreferredSize(new Dimension(getWidth(), 180));

        GridBagConstraints infoGbc = new GridBagConstraints();
        infoGbc.insets = new Insets(10, 10, 10, 10);
        infoGbc.fill = GridBagConstraints.HORIZONTAL;
        infoGbc.gridx = 0;
        infoGbc.gridy = 0;

        JLabel infoTitleLabel = new JLabel("Account Information");
        infoTitleLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        infoTitleLabel.setForeground(TEXT_COLOR);
        infoCard.add(infoTitleLabel, infoGbc);

        accountLabel = new JLabel("Account Number: " + user.getAccountNumber());
        accountLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        infoGbc.gridy = 1;
        infoCard.add(accountLabel, infoGbc);

        balanceLabel = new JLabel("Current Balance: $" + String.format("%.2f", user.getBalance()));
        balanceLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        infoGbc.gridy = 2;
        infoCard.add(balanceLabel, infoGbc);

        loanLabel = new JLabel("Loan Amount: $" + String.format("%.2f", user.getLoanAmount()));
        loanLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        infoGbc.gridy = 3;
        infoCard.add(loanLabel, infoGbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        contentPanel.add(infoCard, gbc);

        // Action cards panel
        JPanel actionsPanel = new JPanel(new GridLayout(2, 4, 15, 15));
        actionsPanel.setBackground(BACKGROUND_COLOR);

        // Action buttons
        JButton depositButton = createStyledButton("Deposit Money", true);
        JButton withdrawButton = createStyledButton("Withdraw Money", true);
        JButton transferButton = createStyledButton("Transfer Money", true);
        JButton loanButton = createStyledButton("Loan Management", true);
        JButton historyButton = createStyledButton("Transaction History", true);
        JButton profileButton = createStyledButton("My Profile", true);
        JButton changePinButton = createStyledButton("Change PIN", true);
        JButton logoutButton = createStyledButton("Logout", false);

        // Add buttons to action cards
        actionsPanel.add(createActionCard("Deposit", depositButton));
        actionsPanel.add(createActionCard("Withdraw", withdrawButton));
        actionsPanel.add(createActionCard("Transfer", transferButton));
        actionsPanel.add(createActionCard("Loan", loanButton));
        actionsPanel.add(createActionCard("History", historyButton));
        actionsPanel.add(createActionCard("Profile", profileButton));
        actionsPanel.add(createActionCard("Change PIN", changePinButton));
        actionsPanel.add(createActionCard("Logout", logoutButton));

        gbc.gridy = 2;
        gbc.weighty = 1.0;
        contentPanel.add(actionsPanel, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createActionCard(String title, JButton button) {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
//        card.setPreferredSize(new Dimension(180, 140));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        card.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(button, gbc);

        return card;
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
//        button.setPreferredSize(new Dimension(140, 36));
//        button.setMaximumSize(new Dimension(140, 36));
//        button.setMinimumSize(new Dimension(140, 36));
        button.setToolTipText(text);
        button.setEnabled(true);
        button.setFocusable(true);
        button.setOpaque(true);

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
            button.setBackground(Color.ORANGE);
            button.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.red),
                    BorderFactory.createEmptyBorder(8,16,8,16)
            ));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (button.isEnabled()) {
                        button.setForeground(SECONDARY_HOVER);
                        button.setBackground(Color.red);
//                        button.setBorder(BorderFactory.createLineBorder(SECONDARY_HOVER, 1));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setForeground(SECONDARY_COLOR);
                    button.setBackground(Color.ORANGE);

//                    button.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR, 1));
                }
            });
        }

        return button;
    }

    public static void main(String[] args) {
        // Ensure GUI is created on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            // Create dummy BankSystem and User for GUI testing
            BankSystem bankSystem = new BankSystem();
            User user = new User();
            DashboardScreenGUI dashboard = new DashboardScreenGUI(bankSystem, user);
            dashboard.setVisible(true);
        });
    }
}