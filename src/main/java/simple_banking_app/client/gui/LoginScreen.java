package simple_banking_app.client.gui;

import simple_banking_app.model.User;
import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginScreen extends JFrame {
    private BankSystem bankSystem;
    private JTextField accountNumberField;
    private JPasswordField pinField;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color SECONDARY_COLOR = new Color(107, 114, 128); // #6B7280
    private static final Color SECONDARY_HOVER = new Color(55, 65, 81); // #374151

    public LoginScreen(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Login");
        setSize(600, 500);
        setMinimumSize(new Dimension(500, 350));
        // Alternative size for testing: uncomment to try
        // setSize(650, 450);
        // setMinimumSize(new Dimension(550, 400));
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
        JLabel titleLabel = new JLabel("Login to Your Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel (card)
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15))); // Reduced padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12); // Reduced spacing
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Account Number
        JLabel accountNumberLabel = new JLabel("Account Number:");
        accountNumberLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        accountNumberLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        contentPanel.add(accountNumberLabel, gbc);

        accountNumberField = new JTextField(15); // Reduced from 20 to 15 columns
        accountNumberField.setFont(new Font("Roboto", Font.PLAIN, 16));
        accountNumberField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(accountNumberField, gbc);

        // PIN
        JLabel pinLabel = new JLabel("PIN:");
        pinLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        pinLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(pinLabel, gbc);

        pinField = new JPasswordField(15); // Reduced from 20 to 15 columns
        pinField.setFont(new Font("Roboto", Font.PLAIN, 16));
        pinField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        contentPanel.add(pinField, gbc);

        // Forgot Password (text link)
        JLabel forgotPasswordLink = new JLabel("Forgot Password?");
        forgotPasswordLink.setFont(new Font("Roboto", Font.PLAIN, 14));
        forgotPasswordLink.setForeground(SECONDARY_COLOR);
        forgotPasswordLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                forgotPasswordLink.setForeground(SECONDARY_HOVER);
                forgotPasswordLink.setText("<html><u>Forgot Password?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                forgotPasswordLink.setForeground(SECONDARY_COLOR);
                forgotPasswordLink.setText("Forgot Password?");
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                new ForgotPasswordScreen(bankSystem).setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(forgotPasswordLink, gbc);

        // Login button
        JButton loginButton = createStyledButton("Login", true);
        loginButton.setPreferredSize(new Dimension(140, 36));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(loginButton, gbc);

        // Back button
        JButton backButton = createStyledButton("Back", false);
        backButton.setPreferredSize(new Dimension(80, 36));
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        contentPanel.add(backButton, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Button actions
        loginButton.addActionListener(e -> {
            String accountNumber = accountNumberField.getText();
            String pin = new String(pinField.getPassword());

            if (accountNumber.isEmpty() || pin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both account number and PIN", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User user = bankSystem.login(accountNumber, pin);
            if (user != null) {
                new DashboardScreen(bankSystem, user).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid account number or PIN", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            new WelcomeScreen(bankSystem).setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private JButton createStyledButton(String text, boolean isPrimary) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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
}