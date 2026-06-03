package simple_banking_app.dao;

import simple_banking_app.model.User;
import simple_banking_app.config.DatabaseConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private final DatabaseConfig dbConfig;

    public UserDAO(DatabaseConfig dbConfig) {
        this.dbConfig = dbConfig;
    }

    public List<User> loadAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection conn = dbConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(createUserFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    public User findByAccountNumber(String accountNumber) {
        String query = "SELECT * FROM users WHERE account_number = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return createUserFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("Error finding user: " + e.getMessage());
        }
        return null;
    }

    public boolean updateBalance(String accountNumber, double amount) {
        String sql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
        
        try (Connection conn = dbConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, amount);
            pstmt.setString(2, accountNumber);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error updating balance: " + e.getMessage());
            return false;
        }
    }

    public boolean transferFunds(String fromAccount, String toAccount, double amount) {
        try (Connection conn = dbConfig.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Deduct from sender
                String deductSql = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deductSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, fromAccount);
                    pstmt.setDouble(3, amount);
                    if (pstmt.executeUpdate() == 0) {
                        throw new SQLException("Insufficient funds or sender account not found");
                    }
                }

                // Add to recipient
                String addSql = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(addSql)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, toAccount);
                    if (pstmt.executeUpdate() == 0) {
                        throw new SQLException("Recipient account not found");
                    }
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error transferring funds: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    private User createUserFromResultSet(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("name"),
            rs.getString("account_number"),
            rs.getString("pin"),
            rs.getDouble("balance"),
            rs.getString("account_type"),
            rs.getString("security_question"),
            rs.getString("security_answer"),
            rs.getString("phone_number"),
            rs.getString("email"),
            rs.getString("address")
        );
    }
} 