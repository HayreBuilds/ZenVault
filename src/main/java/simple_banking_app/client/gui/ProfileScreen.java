package simple_banking_app.client.gui;

import simple_banking_app.model.User;
import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

public class ProfileScreen extends JFrame {
    private BankSystem bankSystem;
    private User user;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488

    public ProfileScreen(BankSystem bankSystem, User user) {
        this.bankSystem = bankSystem;
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - My Profile");
        setSize(900, 700);
        setMinimumSize(new Dimension(700, 500));
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
        JLabel titleLabel = new JLabel("My Profile", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Account Information Section
        JLabel accountInfoLabel = new JLabel("Account Information");
        accountInfoLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        accountInfoLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        contentPanel.add(accountInfoLabel, gbc);

        addLabelAndValue(contentPanel, gbc, 1, "Account Number:", user.getAccountNumber());
        addLabelAndValue(contentPanel, gbc, 2, "Account Type:", user.getAccountType());
        addLabelAndValue(contentPanel, gbc, 3, "Account Created:",
                new SimpleDateFormat("yyyy-MM-dd").format(user.getAccountCreationDate()));

        // Personal Information Section
        JLabel personalInfoLabel = new JLabel("Personal Information");
        personalInfoLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        personalInfoLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        contentPanel.add(personalInfoLabel, gbc);

        JTextField nameField = addLabelAndEditableField(contentPanel, gbc, 5, "Full Name:", user.getName());
        JTextField phoneField = addLabelAndEditableField(contentPanel, gbc, 6, "Phone Number:", user.getPhoneNumber());
        JTextField emailField = addLabelAndEditableField(contentPanel, gbc, 7, "Email:", user.getEmail());
        JTextField addressField = addLabelAndEditableField(contentPanel, gbc, 8, "Address:", user.getAddress());

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton updateButton = createStyledButton("Update Profile", BUTTON_COLOR, BUTTON_HOVER);
        JButton backButton = createStyledButton("Back to Dashboard", BUTTON_COLOR, BUTTON_HOVER);

        buttonPanel.add(updateButton);
        buttonPanel.add(backButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        updateButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();

            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || address.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields",
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

            if (bankSystem.updateProfile(user, name, phone, email, address)) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully",
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            new DashboardScreen(bankSystem, user).setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private void addLabelAndValue(JPanel panel, GridBagConstraints gbc, int row, String labelText, String valueText) {
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
        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Roboto", Font.PLAIN, 16));
        value.setForeground(TEXT_COLOR);
        panel.add(value, gbc);
    }

    private JTextField addLabelAndEditableField(JPanel panel, GridBagConstraints gbc, int row, String labelText, String fieldValue) {
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
        JTextField textField = new JTextField(fieldValue, 20);
        textField.setFont(new Font("Roboto", Font.PLAIN, 16));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        panel.add(textField, gbc);

        return textField;
    }

    private JButton createStyledButton(String text, Color bgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Roboto", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }
}