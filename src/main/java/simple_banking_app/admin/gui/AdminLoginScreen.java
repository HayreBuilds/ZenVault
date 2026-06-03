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

public class AdminLoginScreen extends JFrame {
    private BankSystem bankSystem;
    private JTextField adminIdField;
    private JPasswordField passwordField;
    private JButton loginButton;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // #EF4444

    public AdminLoginScreen(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Admin Login");
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
        JLabel titleLabel = new JLabel("Admin Login", SwingConstants.CENTER);
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
        adminIdLabel.setToolTipText("Enter your admin ID");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        contentPanel.add(adminIdLabel, gbc);

        adminIdField = new JTextField(20);
        adminIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
        adminIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        adminIdField.setToolTipText("Enter your admin ID");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(adminIdField, gbc);

        // Password
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        passwordLabel.setForeground(TEXT_COLOR);
        passwordLabel.setToolTipText("Enter your password (minimum 4 characters)");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        contentPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        passwordField.setToolTipText("Enter your password (minimum 4 characters)");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(passwordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        loginButton = createStyledButton("Login");
        loginButton.setEnabled(false);
        JButton registerButton = createStyledButton("Register New Admin");
        JButton  exitButton= createStyledButton("Exit");

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        buttonPanel.add(exitButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
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
                updateLoginButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateLoginButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateLoginButtonState();
            }
        };

        adminIdField.getDocument().addDocumentListener(validationListener);
        passwordField.getDocument().addDocumentListener(validationListener);

        // Login button action
        loginButton.addActionListener(e -> {
            String adminId = adminIdField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (password.length() < 4 && !(adminId.equals("admin") && password.equals("1234"))) {
                JOptionPane.showMessageDialog(this, "Password must be at least 4 characters long", "Error", JOptionPane.ERROR_MESSAGE);
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                return;
            }

            if (!bankSystem.hasAdmins() && adminId.equals("admin") && password.equals("1234")) {
                // First admin login: Register default admin
                if (bankSystem.registerAdmin("admin", "1234")) {
                    new AdminDashboardScreen(bankSystem).setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create default admin", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else if (bankSystem.adminLogin(adminId, password)) {
                // Normal admin login
                new AdminDashboardScreen(bankSystem).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid admin ID or password", "Login Failed", JOptionPane.ERROR_MESSAGE);
                adminIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        });

        // Register button action
        registerButton.addActionListener(e -> {
            new AdminRegistrationScreen(bankSystem).setVisible(true);
            dispose();
        });

        // Back button action

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

        // Set initial focus
        adminIdField.requestFocusInWindow();

        add(mainPanel);
    }

    private void updateLoginButtonState() {
        String adminId = adminIdField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        boolean isValid = !adminId.isEmpty() && !password.isEmpty();
        loginButton.setEnabled(isValid);

        // Reset borders if valid
        if (isValid) {
            adminIdField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            passwordField.setBorder(BorderFactory.createCompoundBorder(
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