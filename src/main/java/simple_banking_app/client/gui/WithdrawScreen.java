package simple_banking_app.client.gui;

import simple_banking_app.model.User;
import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WithdrawScreen extends JFrame {
    private BankSystem bankSystem;
    private User user;
    private JTextField amountField;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488

    public WithdrawScreen(BankSystem bankSystem, User user) {
        this.bankSystem = bankSystem;
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Withdraw Money");
        setSize(600, 400);
        setMinimumSize(new Dimension(500, 350));
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
        JLabel titleLabel = new JLabel("Withdraw Money", SwingConstants.CENTER);
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

        JLabel amountLabel = new JLabel("Amount to Withdraw:");
        amountLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        amountLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        contentPanel.add(amountLabel, gbc);

        amountField = new JTextField(20);
        amountField.setFont(new Font("Roboto", Font.PLAIN, 16));
        amountField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        contentPanel.add(amountField, gbc);

        JLabel balanceLabel = new JLabel("Available Balance: $" + String.format("%.2f", user.getBalance()));
        balanceLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        balanceLabel.setForeground(TEXT_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        contentPanel.add(balanceLabel, gbc);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton confirmButton = createStyledButton("Confirm");
        JButton backButton = createStyledButton("Back to Dashboard");

        buttonPanel.add(confirmButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 0;
        contentPanel.add(buttonPanel, gbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Button actions
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String amountText = amountField.getText();
                try {
                    double amount = Double.parseDouble(amountText);
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(WithdrawScreen.this,
                                "Please enter a positive amount",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (user.withdraw(amount)) {
                        bankSystem.saveUsers();
                        JOptionPane.showMessageDialog(WithdrawScreen.this,
                                String.format("$%.2f withdrawn successfully", amount),
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        new DashboardScreen(bankSystem, user).setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(WithdrawScreen.this,
                                "Insufficient funds",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(WithdrawScreen.this,
                            "Please enter a valid number",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DashboardScreen(bankSystem, user).setVisible(true);
                dispose();
            }
        });

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

        return button;
    }
}