package simple_banking_app.client.gui;

import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WelcomeScreen extends JFrame {
    private BankSystem bankSystem;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color EXIT_BUTTON_COLOR = new Color(107, 114, 128); // #6B7280
    private static final Color EXIT_BUTTON_HOVER = new Color(55, 65, 81); // #374151

    public WelcomeScreen(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Welcome");
        setSize(600, 450);
        setMinimumSize(new Dimension(500, 350));
        // Alternative size for testing: uncomment to try
        // setSize(650, 450);
        // setMinimumSize(new Dimension(550, 400));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        JLabel titleLabel = new JLabel("Welcome to xAI Bank", SwingConstants.CENTER);
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
        gbc.anchor = GridBagConstraints.CENTER;

        // Login button
        JButton loginButton = createStyledButton("Login", true);
        loginButton.setPreferredSize(new Dimension(140, 36));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        contentPanel.add(loginButton, gbc);

        // Register button
        JButton registerButton = createStyledButton("Register", true);
        registerButton.setPreferredSize(new Dimension(140, 36));
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(registerButton, gbc);

        // Exit button
        JButton exitButton = createStyledButton("Exit", false);
        exitButton.setPreferredSize(new Dimension(80, 36)); // Larger to match primary width
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(exitButton, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Button actions
        loginButton.addActionListener(e -> {
            new LoginScreen(bankSystem).setVisible(true);
            dispose();
        });

        registerButton.addActionListener(e -> {
            new RegistrationScreen(bankSystem).setVisible(true);
            dispose();
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
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
            button.setForeground(EXIT_BUTTON_COLOR);
            button.setBackground(Color.WHITE);
            button.setBorder(BorderFactory.createLineBorder(EXIT_BUTTON_COLOR, 1));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setForeground(BUTTON_HOVER);
                    button.setBorder(BorderFactory.createLineBorder(BUTTON_HOVER, 1));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setForeground(EXIT_BUTTON_COLOR);
                    button.setBorder(BorderFactory.createLineBorder(EXIT_BUTTON_COLOR, 1));
                }
            });
        }

        return button;
    }
}