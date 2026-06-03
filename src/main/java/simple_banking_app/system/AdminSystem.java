package simple_banking_app.system;

import simple_banking_app.model.User;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "hiredin";
    private Map<String, String> admins; // Admin ID to plaintext password
    private BankSystem bankSystem;

    public AdminSystem(BankSystem bankSystem) {
        this.bankSystem = bankSystem;
        this.admins = new HashMap<>();
        loadAdminCredentials();
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadAdminCredentials() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT admin_id, password FROM admins")) {
            admins.clear();
            while (rs.next()) {
                admins.put(rs.getString("admin_id"), rs.getString("password"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading admin credentials: " + e.getMessage());
            admins = new HashMap<>();
        }
    }

    private void saveAdminCredentials() {
        try (Connection conn = getConnection()) {
            // Clear existing admin records
            try (Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("DELETE FROM admins");
            }
            // Insert all admin credentials
            try (PreparedStatement pstmt = conn.prepareStatement("INSERT INTO admins (admin_id, password) VALUES (?, ?)")) {
                for (Map.Entry<String, String> entry : admins.entrySet()) {
                    pstmt.setString(1, entry.getKey());
                    pstmt.setString(2, entry.getValue());
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error saving admin credentials: " + e.getMessage());
        }
    }

    public boolean hasAdmins() {
        return !admins.isEmpty();
    }

    public boolean adminLogin(String adminId, String password) {
        if (!hasAdmins() && adminId.equals("admin") && password.equals("1234")) {
            // Default login: Create first admin
            return registerAdmin("admin", "1234");
        }

        String storedPassword = admins.get(adminId);
        return storedPassword != null && storedPassword.equals(password);
    }

    public boolean registerAdmin(String adminId, String password) {
        if (adminId == null || adminId.isEmpty() || password == null || password.isEmpty()) {
            return false;
        }

        if (admins.containsKey(adminId)) {
            return false; // Admin ID already exists
        }

        admins.put(adminId, password);
        saveAdminCredentials();
        return true;
    }

    public boolean changeAdminCredentials(String currentAdminId, String currentPassword,
                                          String newAdminId, String newPassword) {
        if (adminLogin(currentAdminId, currentPassword)) {
            if (newAdminId == null || newAdminId.isEmpty() || newPassword == null || newPassword.isEmpty()) {
                return false;
            }

            if (!newAdminId.equals(currentAdminId) && admins.containsKey(newAdminId)) {
                return false; // New admin ID already exists
            }

            admins.remove(currentAdminId); // Remove old credentials
            admins.put(newAdminId, newPassword);
            saveAdminCredentials();
            return true;
        }
        return false;
    }

    public boolean resetUserPassword(String adminId, String adminPassword,
                                     String userAccountNumber, String newPin) {
        if (!adminLogin(adminId, adminPassword)) {
            return false;
        }

        User user = bankSystem.findUser(userAccountNumber);
        if (user != null && newPin.matches("\\d{4}")) {
            user.setPin(newPin);
            return bankSystem.saveUsers();
        }
        return false;
    }

    public boolean deleteInactiveAccounts(String adminId, String adminPassword) {
        if (!adminLogin(adminId, adminPassword)) {
            return false;
        }

        List<User> inactiveUsers = bankSystem.getAllUsers().stream()
                .filter(u -> !u.isAccountActive())
                .collect(Collectors.toList());

        if (inactiveUsers.isEmpty()) {
            return false;
        }

        bankSystem.getAllUsers().removeAll(inactiveUsers);
        return bankSystem.saveUsers();
    }

    public String getAdminId() {
        return admins.isEmpty() ? null : admins.keySet().iterator().next();
    }

    public String getAdminPassword() {
        return null; // Passwords not exposed directly
    }
}











//package simple_banking_app.system;
//
//import simple_banking_app.model.User;
//
//import java.io.*;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class AdminSystem {
//    private static final String ADMIN_DATA_FILE = "admin_credentials.dat";
//    private Map<String, String> admins; // Admin ID to plaintext password
//    private BankSystem bankSystem;
//
//    public AdminSystem(BankSystem bankSystem) {
//        this.bankSystem = bankSystem;
//        this.admins = new HashMap<>();
//        loadAdminCredentials();
//    }
//
//    private void loadAdminCredentials() {
//        File file = new File(ADMIN_DATA_FILE);
//        if (!file.exists()) {
//            this.admins = new HashMap<>();
//            return;
//        }
//
//        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ADMIN_DATA_FILE))) {
//            Object obj = ois.readObject();
//            if (obj instanceof Map) {
//                this.admins = (Map<String, String>) obj;
//            } else if (obj instanceof String) {
//                // Legacy format: adminId followed by adminPassword
//                String legacyAdminId = (String) obj;
//                String legacyAdminPassword = (String) ois.readObject();
//                this.admins = new HashMap<>();
//                this.admins.put(legacyAdminId, legacyAdminPassword);
//                saveAdminCredentials(); // Convert to new Map format
//            } else {
//                System.err.println("Invalid data in admin_credentials.dat: Expected Map or String");
//                this.admins = new HashMap<>();
//            }
//        } catch (EOFException e) {
//            System.err.println("Corrupted or empty admin_credentials.dat: " + e.getMessage());
//            this.admins = new HashMap<>();
//        } catch (Exception e) {
//            System.err.println("Error loading admin credentials: " + e.getMessage());
//            this.admins = new HashMap<>();
//        }
//    }
//
//    private void saveAdminCredentials() {
//        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ADMIN_DATA_FILE))) {
//            oos.writeObject(this.admins);
//        } catch (Exception e) {
//            System.err.println("Error saving admin credentials: " + e.getMessage());
//        }
//    }
//
//    public boolean hasAdmins() {
//        return !admins.isEmpty();
//    }
//
//    public boolean adminLogin(String adminId, String password) {
//        if (!hasAdmins() && adminId.equals("admin") && password.equals("1234")) {
//            // Default login: Create first admin
//            return registerAdmin("admin", "1234");
//        }
//
//        String storedPassword = admins.get(adminId);
//        return storedPassword != null && storedPassword.equals(password);
//    }
//
//    public boolean registerAdmin(String adminId, String password) {
//        if (adminId == null || adminId.isEmpty() || password == null || password.isEmpty()) {
//            return false;
//        }
//
//        if (admins.containsKey(adminId)) {
//            return false; // Admin ID already exists
//        }
//
//        admins.put(adminId, password);
//        saveAdminCredentials();
//        return true;
//    }
//
//    public boolean changeAdminCredentials(String currentAdminId, String currentPassword,
//                                          String newAdminId, String newPassword) {
//        if (adminLogin(currentAdminId, currentPassword)) {
//            if (newAdminId == null || newAdminId.isEmpty() || newPassword == null || newPassword.isEmpty()) {
//                return false;
//            }
//
//            if (!newAdminId.equals(currentAdminId) && admins.containsKey(newAdminId)) {
//                return false; // New admin ID already exists
//            }
//
//            admins.remove(currentAdminId); // Remove old credentials
//            admins.put(newAdminId, newPassword);
//            saveAdminCredentials();
//            return true;
//        }
//        return false;
//    }
//
//    public boolean resetUserPassword(String adminId, String adminPassword,
//                                     String userAccountNumber, String newPin) {
//        if (!adminLogin(adminId, adminPassword)) {
//            return false;
//        }
//
//        User user = bankSystem.findUser(userAccountNumber);
//        if (user != null && newPin.matches("\\d{4}")) {
//            user.setPin(newPin);
//            return bankSystem.saveUsers();
//        }
//        return false;
//    }
//
//    public boolean deleteInactiveAccounts(String adminId, String adminPassword) {
//        if (!adminLogin(adminId, adminPassword)) {
//            return false;
//        }
//
//        List<User> inactiveUsers = bankSystem.getAllUsers().stream()
//                .filter(u -> !u.isAccountActive())
//                .collect(Collectors.toList());
//
//        if (inactiveUsers.isEmpty()) {
//            return false;
//        }
//
//        bankSystem.getAllUsers().removeAll(inactiveUsers);
//        return bankSystem.saveUsers();
//    }
//
//    public String getAdminId() {
//        return admins.isEmpty() ? null : admins.keySet().iterator().next();
//    }
//
//    public String getAdminPassword() {
//        return null; // Passwords not exposed directly
//    }
//}