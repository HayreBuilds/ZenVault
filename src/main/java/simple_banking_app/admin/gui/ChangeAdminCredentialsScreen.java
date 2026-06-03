package simple_banking_app.admin.gui;

import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChangeAdminCredentialsScreen extends JFrame {
    private BankSystem bankSystem;
    private JTextField currentIdField;
    private JPasswordField currentPasswordField;
    private JTextField newIdField;
    private JPasswordField newPasswordField;
    private JButton changeButton;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488
    private static final Color ERROR_COLOR = new Color(239, 68, 68); // #EF4444

    public ChangeAdminCredentialsScreen(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Change Admin Credentials");
        setSize(600, 550);
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
        JLabel titleLabel = new JLabel("Change Admin Credentials", SwingConstants.CENTER);
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

        // Current Admin ID
        JLabel currentIdLabel = new JLabel("Current Admin ID:");
        currentIdLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        currentIdLabel.setForeground(TEXT_COLOR);
        currentIdLabel.setToolTipText("Enter your current admin ID");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        contentPanel.add(currentIdLabel, gbc);

        currentIdField = new JTextField(20);
        currentIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
        currentIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        currentIdField.setToolTipText("Enter your current admin ID");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(currentIdField, gbc);

        // Current Password
        JLabel currentPasswordLabel = new JLabel("Current Password:");
        currentPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        currentPasswordLabel.setForeground(TEXT_COLOR);
        currentPasswordLabel.setToolTipText("Enter your current password");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        contentPanel.add(currentPasswordLabel, gbc);

        currentPasswordField = new JPasswordField(20);
        currentPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        currentPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        currentPasswordField.setToolTipText("Enter your current password");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(currentPasswordField, gbc);

        // New Admin ID
        JLabel newIdLabel = new JLabel("New Admin ID:");
        newIdLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        newIdLabel.setForeground(TEXT_COLOR);
        newIdLabel.setToolTipText("Enter your new admin ID");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        contentPanel.add(newIdLabel, gbc);

        newIdField = new JTextField(20);
        newIdField.setFont(new Font("Roboto", Font.PLAIN, 16));
        newIdField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        newIdField.setToolTipText("Enter your new admin ID");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(newIdField, gbc);

        // New Password
        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        newPasswordLabel.setForeground(TEXT_COLOR);
        newPasswordLabel.setToolTipText("Enter your new password");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        contentPanel.add(newPasswordLabel, gbc);

        newPasswordField = new JPasswordField(20);
        newPasswordField.setFont(new Font("Roboto", Font.PLAIN, 16));
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        newPasswordField.setToolTipText("Enter your new password");
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(newPasswordField, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        changeButton = createStyledButton("Change Credentials");
        changeButton.setEnabled(false);
        JButton backButton = createStyledButton("Back to Dashboard");

        buttonPanel.add(changeButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 4;
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
                updateChangeButtonState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateChangeButtonState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateChangeButtonState();
            }
        };

        currentIdField.getDocument().addDocumentListener(validationListener);
        currentPasswordField.getDocument().addDocumentListener(validationListener);
        newIdField.getDocument().addDocumentListener(validationListener);
        newPasswordField.getDocument().addDocumentListener(validationListener);

        // Change button action
        changeButton.addActionListener(e -> {
            String currentAdminId = currentIdField.getText().trim();
            String currentPassword = new String(currentPasswordField.getPassword()).trim();
            String newAdminId = newIdField.getText().trim();
            String newPassword = new String(newPasswordField.getPassword()).trim();

            if (bankSystem.changeAdminCredentials(currentAdminId, currentPassword, newAdminId, newPassword)) {
                JOptionPane.showMessageDialog(this, "Admin credentials changed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                new AdminDashboardScreen(bankSystem).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to change credentials. Verify current ID/password.", "Error", JOptionPane.ERROR_MESSAGE);
                currentIdField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
                currentPasswordField.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ERROR_COLOR),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            }
        });

        // Back button action
        backButton.addActionListener(e -> {
            new AdminDashboardScreen(bankSystem).setVisible(true);
            dispose();
        });

        // Set initial focus
        currentIdField.requestFocusInWindow();

        add(mainPanel);
    }

    private void updateChangeButtonState() {
        String currentAdminId = currentIdField.getText().trim();
        String currentPassword = new String(currentPasswordField.getPassword()).trim();
        String newAdminId = newIdField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword()).trim();

        boolean isValid = !currentAdminId.isEmpty() && !currentPassword.isEmpty() &&
                !newAdminId.isEmpty() && !newPassword.isEmpty();
        changeButton.setEnabled(isValid);

        // Reset borders if valid
        if (isValid) {
            currentIdField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            currentPasswordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            newIdField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)));
            newPasswordField.setBorder(BorderFactory.createCompoundBorder(
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