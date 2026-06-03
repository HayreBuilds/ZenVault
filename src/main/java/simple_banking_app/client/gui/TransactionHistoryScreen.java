package simple_banking_app.client.gui;

import simple_banking_app.model.Transaction;
import simple_banking_app.model.User;
import simple_banking_app.system.BankSystem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TransactionHistoryScreen extends JFrame {
    private BankSystem bankSystem;
    private User user;

    // Color scheme
    private static final Color BACKGROUND_COLOR = new Color(247, 250, 252); // #F7FAFC
    private static final Color TEXT_COLOR = new Color(31, 41, 55); // #1F2937
    private static final Color BUTTON_COLOR = new Color(20, 184, 166); // #14B8A6
    private static final Color BUTTON_HOVER = new Color(13, 148, 136); // #0D9488

    public TransactionHistoryScreen(BankSystem bankSystem, User user) {
        this.bankSystem = bankSystem;
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("xAI Bank - Transaction History");
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
        JLabel titleLabel = new JLabel("Transaction History", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Date filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(Color.WHITE);

        JLabel fromLabel = new JLabel("From:");
        fromLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        fromLabel.setForeground(TEXT_COLOR);
        JTextField fromField = new JTextField(10);
        fromField.setFont(new Font("Roboto", Font.PLAIN, 16));
        fromField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        fromField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000)));

        JLabel toLabel = new JLabel("To:");
        toLabel.setFont(new Font("Roboto", Font.PLAIN, 16));
        toLabel.setForeground(TEXT_COLOR);
        JTextField toField = new JTextField(10);
        toField.setFont(new Font("Roboto", Font.PLAIN, 16));
        toField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        toField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        JButton filterButton = createStyledButton("Filter");
        JButton refreshButton = createStyledButton("Refresh");

        filterPanel.add(fromLabel);
        filterPanel.add(fromField);
        filterPanel.add(toLabel);
        filterPanel.add(toField);
        filterPanel.add(filterButton);
        filterPanel.add(refreshButton);

        // Transaction table
        String[] columnNames = {"Date", "Type", "Amount", "Description"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        JTable transactionTable = new JTable(model);
        transactionTable.setFont(new Font("Roboto", Font.PLAIN, 14));
        transactionTable.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        transactionTable.setRowHeight(30);
        
        // Initial load of transactions from database
        List<Transaction> transactions = bankSystem.getTransactionHistory(user);
        updateTransactionTable(model, transactions);

        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        contentPanel.add(filterPanel, BorderLayout.NORTH);
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);

        JButton exportButton = createStyledButton("Export to File");
        JButton backButton = createStyledButton("Back to Dashboard");

        buttonPanel.add(exportButton);
        buttonPanel.add(backButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Button actions
        filterButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fromDate = sdf.parse(fromField.getText());
                Date toDate = sdf.parse(toField.getText());

                List<Transaction> filtered = bankSystem.getTransactionsByDateRange(user, fromDate, toDate);
                updateTransactionTable(model, filtered);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format (use YYYY-MM-DD)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        refreshButton.addActionListener(e -> {
            List<Transaction> refreshedTransactions = bankSystem.getTransactionHistory(user);
            updateTransactionTable(model, refreshedTransactions);
        });

        exportButton.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date fromDate = sdf.parse(fromField.getText());
                Date toDate = sdf.parse(toField.getText());

                if (bankSystem.exportStatementToFile(user, fromDate, toDate)) {
                    JOptionPane.showMessageDialog(this, "Statement exported successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to export statement", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid date format (use YYYY-MM-DD)", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            new DashboardScreen(bankSystem, user).setVisible(true);
            dispose();
        });

        add(mainPanel);
    }

    private void updateTransactionTable(DefaultTableModel model, List<Transaction> transactions) {
        model.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Transaction t : transactions) {
            String amountStr = String.format("$%.2f", Math.abs(t.getAmount()));
            if (t.getAmount() < 0) {
                amountStr = "-" + amountStr;
            }
            
            model.addRow(new Object[]{
                    sdf.format(t.getDate()),
                    t.getType(),
                    amountStr,
                    t.getDescription()
            });
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