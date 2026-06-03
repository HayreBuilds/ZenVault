package simple_banking_app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {
    private final String url;
    private final String username;
    private final String password;

    public DatabaseConfig(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    // Factory method for default configuration
    public static DatabaseConfig getDefault() {
        return new DatabaseConfig(
            "jdbc:mysql://localhost:3306/banking_app",
            "root",
            "hiredin"
        );
    }
} 