package simple_banking_app.service;

import simple_banking_app.model.User;
import simple_banking_app.dao.UserDAO;
import java.util.HashMap;
import java.util.Map;

public class AdminService {
    private final UserDAO userDAO;
    private final Map<String, String> admins; // Admin ID to password mapping

    public AdminService(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.admins = new HashMap<>();
        loadAdminCredentials();
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

            admins.remove(currentAdminId);
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

        User user = userDAO.findByAccountNumber(userAccountNumber);
        if (user != null && newPin.matches("\\d{4}")) {
            user.setPin(newPin);
            // Update in database
            return true;
        }
        return false;
    }

    public boolean deactivateAccount(String adminId, String adminPassword, String userAccountNumber) {
        if (!adminLogin(adminId, adminPassword)) {
            return false;
        }

        User user = userDAO.findByAccountNumber(userAccountNumber);
        if (user != null) {
            user.setAccountActive(false);
            // Update in database
            return true;
        }
        return false;
    }

    public boolean reactivateAccount(String adminId, String adminPassword, String userAccountNumber) {
        if (!adminLogin(adminId, adminPassword)) {
            return false;
        }

        User user = userDAO.findByAccountNumber(userAccountNumber);
        if (user != null) {
            user.setAccountActive(true);
            user.resetFailedLoginAttempts();
            // Update in database
            return true;
        }
        return false;
    }

    public Map<String, Object> getSystemStatistics(String adminId, String adminPassword) {
        Map<String, Object> stats = new HashMap<>();
        if (!adminLogin(adminId, adminPassword)) {
            return stats;
        }

        // Implement statistics gathering using UserDAO
        return stats;
    }

    private boolean hasAdmins() {
        return !admins.isEmpty();
    }

    private void loadAdminCredentials() {
        // Load admin credentials from database
    }

    private void saveAdminCredentials() {
        // Save admin credentials to database
    }
} 