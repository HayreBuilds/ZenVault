package simple_banking_app.dao;

import simple_banking_app.model.Transaction;
import simple_banking_app.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {
    private final DatabaseConfig dbConfig;

    public TransactionDAO(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public List<Transaction> getTransactionHistory(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT transaction_date, transaction_type, amount, description " +
                      "FROM transactions WHERE account_number = ? ORDER BY transaction_date DESC";

        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(new Transaction(
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("transaction_date"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
        }
        return transactions;
    }

    public boolean recordDeposit(String accountNumber, double amount) {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, description) " +
                    "VALUES (?, 'DEPOSIT', ?, 'Cash deposit')";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setDouble(2, amount);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error recording deposit: " + e.getMessage());
            return false;
        }
    }

    public boolean recordWithdrawal(String accountNumber, double amount) {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, description) " +
                    "VALUES (?, 'WITHDRAWAL', ?, 'Cash withdrawal')";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setDouble(2, -amount);  // Store as negative amount
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error recording withdrawal: " + e.getMessage());
            return false;
        }
    }

    public boolean recordTransfer(String senderAccount, String recipientAccount, double amount) {
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Record sender's transaction
                String senderSql = "INSERT INTO transactions (account_number, transaction_type, amount, description) " +
                                 "VALUES (?, 'TRANSFER_OUT', ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(senderSql)) {
                    pstmt.setString(1, senderAccount);
                    pstmt.setDouble(2, -amount);
                    pstmt.setString(3, "Transfer to " + recipientAccount);
                    pstmt.executeUpdate();
                }

                // Record recipient's transaction
                String recipientSql = "INSERT INTO transactions (account_number, transaction_type, amount, description) " +
                                    "VALUES (?, 'TRANSFER_IN', ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(recipientSql)) {
                    pstmt.setString(1, recipientAccount);
                    pstmt.setDouble(2, amount);
                    pstmt.setString(3, "Transfer from " + senderAccount);
                    pstmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error recording transfer: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }
} 