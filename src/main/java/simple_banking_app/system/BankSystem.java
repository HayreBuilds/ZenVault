package simple_banking_app.system;

import simple_banking_app.model.Transaction;
import simple_banking_app.model.User;

import javax.swing.*;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.Timer;
import java.util.stream.Collectors;

public class BankSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/banking_app";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "hiredin";
    private List<User> users;
    private User currentUser;
    private Timer sessionTimer;
    private boolean darkMode = false;
    private int sessionTimeoutMinutes = 5;
    private Map<String, String> admins; // Admin ID to plaintext password

    public BankSystem() {
        this.users = loadUsers();
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
        loadAdminCredentials();
    }

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private void loadAdminCredentials() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT admin_id, password FROM admins")) {
            admins = new HashMap<>();
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

        User user = findUserByAccountNumber(userAccountNumber);
        if (user != null && newPin.matches("\\d{4}")) {
            user.setPin(newPin);
            return saveUsers();
        }
        return false;
    }

    public User registerUser(String name, String pin, double initialDeposit,
                             String accountType, String securityQuestion,
                             String securityAnswer, String phoneNumber,
                             String email, String address) {
        if (!validatePin(pin) || initialDeposit < 0) {
            return null;
        }

        String accountNumber = generateAccountNumber();
        User newUser = new User(
                name,
                accountNumber,
                pin,
                initialDeposit,
                accountType,
                securityQuestion,
                securityAnswer,
                phoneNumber,
                email,
                address
        );

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Insert new user
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO users (account_number, name, pin, balance, account_type, " +
                                "security_question, security_answer, phone_number, email, address, " +
                                "is_active, failed_login_attempts, account_creation_date) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                    pstmt.setString(1, accountNumber);
                    pstmt.setString(2, name);
                    pstmt.setString(3, pin);
                    pstmt.setDouble(4, initialDeposit);
                    pstmt.setString(5, accountType);
                    pstmt.setString(6, securityQuestion);
                    pstmt.setString(7, securityAnswer);
                    pstmt.setString(8, phoneNumber);
                    pstmt.setString(9, email);
                    pstmt.setString(10, address);
                    pstmt.setBoolean(11, true); // is_active
                    pstmt.setInt(12, 0); // failed_login_attempts
                    pstmt.setTimestamp(13, new Timestamp(new Date().getTime())); // account_creation_date
                    pstmt.executeUpdate();
                }

                // Record initial deposit transaction if amount > 0
                if (initialDeposit > 0) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO transactions (account_number, transaction_type, amount, description, transaction_date) " +
                                    "VALUES (?, ?, ?, ?, ?)")) {
                        pstmt.setString(1, accountNumber);
                        pstmt.setString(2, "DEPOSIT");
                        pstmt.setDouble(3, initialDeposit);
                        pstmt.setString(4, "Initial deposit");
                        pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                        pstmt.executeUpdate();
                    }
                }

                conn.commit();
        users.add(newUser);
        return newUser;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error registering user: " + e.getMessage());
                return null;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return null;
        }
    }

    public User login(String accountNumber, String pin) {
        User user = findUserByAccountNumber(accountNumber);
        if (user != null && user.getPin().equals(pin)) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public boolean changePin(User user, String oldPin, String newPin) {
        if (user == null || !user.getPin().equals(oldPin) || !validatePin(newPin)) {
            return false;
        }

        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE users SET pin = ? WHERE account_number = ? AND pin = ?")) {
                pstmt.setString(1, newPin);
                pstmt.setString(2, user.getAccountNumber());
                pstmt.setString(3, oldPin);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
            user.setPin(newPin);
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error changing PIN: " + e.getMessage());
        }
        return false;
    }

    public boolean deactivateAccount(User user, String pin) {
        if (user.getPin().equals(pin)) {
            user.setAccountActive(false);
            return saveUsers();
        }
        return false;
    }

    public boolean transferMoney(User sender, String recipientAccountNumber, double amount) {
        User recipient = findUserByAccountNumber(recipientAccountNumber);
        if (recipient != null && sender != null && !sender.equals(recipient)) {
            return sender.transfer(recipient, amount);
        }
        return false;
    }

    public boolean transferMoney(User sender, String recipientAccountNumber, double amount, String description) {
        User recipient = findUserByAccountNumber(recipientAccountNumber);
        if (recipient == null || sender == null || sender.equals(recipient)) {
            return false;
        }

        if (sender.transfer(recipient, amount)) {
            try (Connection conn = getConnection()) {
                conn.setAutoCommit(false);
                try {
                    // Update sender's balance
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE users SET balance = ? WHERE account_number = ?")) {
                        pstmt.setDouble(1, sender.getBalance());
                        pstmt.setString(2, sender.getAccountNumber());
                        pstmt.executeUpdate();
                    }

                    // Update recipient's balance
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE users SET balance = ? WHERE account_number = ?")) {
                        pstmt.setDouble(1, recipient.getBalance());
                        pstmt.setString(2, recipient.getAccountNumber());
                        pstmt.executeUpdate();
                    }

                    // Save transaction records
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO transactions (account_number, transaction_type, amount, description, transaction_date) VALUES (?, ?, ?, ?, ?)")) {
                        // Save sender's transaction
                        pstmt.setString(1, sender.getAccountNumber());
                        pstmt.setString(2, "TRANSFER_OUT");
                        pstmt.setDouble(3, amount);
                        pstmt.setString(4, description);
                        pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                        pstmt.executeUpdate();

                        // Save recipient's transaction
                        pstmt.setString(1, recipient.getAccountNumber());
                        pstmt.setString(2, "TRANSFER_IN");
                        pstmt.setDouble(3, amount);
                        pstmt.setString(4, description);
                        pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                        pstmt.executeUpdate();
                    }

                    conn.commit();
                    return true;
                } catch (SQLException e) {
                    conn.rollback();
                    System.err.println("Error in transfer: " + e.getMessage());
                    return false;
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                System.err.println("Database connection error: " + e.getMessage());
                return false;
            }
        }
        return false;
    }

    public boolean applyForLoan(User user, double amount) {
        if (user == null || amount <= 0 || user.getLoanAmount() > 0) {
            return false;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update user's loan amount and balance
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE users SET loan_amount = ?, balance = balance + ? WHERE account_number = ? AND loan_amount = 0")) {
                    pstmt.setDouble(1, amount);
                    pstmt.setDouble(2, amount);
                    pstmt.setString(3, user.getAccountNumber());
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Cannot apply for loan - existing loan found or account not found");
                    }
                }

                // Record the loan transaction
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO transactions (account_number, transaction_type, amount, description, transaction_date) " +
                                "VALUES (?, ?, ?, ?, ?)")) {
                    pstmt.setString(1, user.getAccountNumber());
                    pstmt.setString(2, "LOAN");
                    pstmt.setDouble(3, amount);
                    pstmt.setString(4, "Loan disbursement");
                    pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                    pstmt.executeUpdate();
                }

                // Update user object
                user.setLoanAmount(amount);
                user.setBalance(user.getBalance() + amount);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error processing loan application: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public boolean repayLoan(User user, double amount) {
        if (user == null || amount <= 0 || user.getLoanAmount() < amount || user.getBalance() < amount) {
            return false;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Update user's loan amount and balance
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "UPDATE users SET loan_amount = loan_amount - ?, balance = balance - ? " +
                        "WHERE account_number = ? AND loan_amount >= ? AND balance >= ?")) {
                    pstmt.setDouble(1, amount);
                    pstmt.setDouble(2, amount);
                    pstmt.setString(3, user.getAccountNumber());
                    pstmt.setDouble(4, amount);
                    pstmt.setDouble(5, amount);
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new SQLException("Insufficient funds or loan amount for repayment");
                    }
                }

                // Record the loan repayment transaction
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "INSERT INTO transactions (account_number, transaction_type, amount, description, transaction_date) " +
                                "VALUES (?, ?, ?, ?, ?)")) {
                    pstmt.setString(1, user.getAccountNumber());
                    pstmt.setString(2, "LOAN_REPAYMENT");
                    pstmt.setDouble(3, -amount);
                    pstmt.setString(4, "Loan repayment");
                    pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                    pstmt.executeUpdate();
                }

                // Update user object
                user.setLoanAmount(user.getLoanAmount() - amount);
                user.setBalance(user.getBalance() - amount);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error processing loan repayment: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public void applyMonthlyInterest() {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                List<User> users = getAllUsers();
        for (User user : users) {
                    if (user.getBalance() > 0) {
                        double interest = user.getBalance() * user.getInterestRate() / 12;
                        
                        // Update balance in database
                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "UPDATE users SET balance = balance + ? WHERE account_number = ?")) {
                            pstmt.setDouble(1, interest);
                            pstmt.setString(2, user.getAccountNumber());
                            pstmt.executeUpdate();
                        }

                        // Record interest transaction
                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO transactions (account_number, transaction_type, amount, description, transaction_date) " +
                                        "VALUES (?, ?, ?, ?, ?)")) {
                            pstmt.setString(1, user.getAccountNumber());
                            pstmt.setString(2, "INTEREST");
                            pstmt.setDouble(3, interest);
                            pstmt.setString(4, "Monthly interest earned");
                            pstmt.setTimestamp(5, new Timestamp(new Date().getTime()));
                            pstmt.executeUpdate();
                        }

                        // Update user object
                        user.setBalance(user.getBalance() + interest);
                    }
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error applying monthly interest: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
        }
    }

    public boolean validateSecurityAnswer(User user, String answer) {
        return user != null && user.getSecurityAnswer().equalsIgnoreCase(answer.trim());
    }

    public boolean resetPassword(String accountNumber, String securityAnswer, String newPin) {
        User user = findUserByAccountNumber(accountNumber);
        if (user != null && validateSecurityAnswer(user, securityAnswer) && validatePin(newPin)) {
            user.setPin(newPin);
            return saveUsers();
        }
        return false;
    }

    public boolean deleteInactiveAccounts() {
        List<User> inactiveUsers = users.stream()
                .filter(u -> !u.isAccountActive())
                .collect(Collectors.toList());

        if (inactiveUsers.isEmpty()) {
            return false;
        }

        users.removeAll(inactiveUsers);
        return saveUsers();
    }

    public void setSessionTimeout(int minutes) {
        this.sessionTimeoutMinutes = minutes;
    }

    private List<User> loadUsers() {
        List<User> loadedUsers = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {
            
            while (rs.next()) {
                User user = new User(
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
                loadedUsers.add(user);
            }
        } catch (SQLException e) {
            System.err.println("Error loading users: " + e.getMessage());
            return null;
        }
        return loadedUsers;
    }

    public boolean saveUsers() {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                    for (User user : users) {
                    // Check if user exists
                    boolean userExists = false;
                    try (PreparedStatement checkStmt = conn.prepareStatement("SELECT 1 FROM users WHERE account_number = ?")) {
                        checkStmt.setString(1, user.getAccountNumber());
                        ResultSet rs = checkStmt.executeQuery();
                        userExists = rs.next();
                    }

                    if (userExists) {
                        // Update existing user
                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "UPDATE users SET name = ?, pin = ?, balance = ?, account_type = ?, " +
                                "security_question = ?, security_answer = ?, phone_number = ?, " +
                                "email = ?, address = ?, is_active = ?, failed_login_attempts = ?, " +
                                "last_login_date = ?, loan_amount = ?, interest_rate = ? " +
                                "WHERE account_number = ?")) {
                            pstmt.setString(1, user.getName());
                            pstmt.setString(2, user.getPin());
                            pstmt.setDouble(3, user.getBalance());
                            pstmt.setString(4, user.getAccountType());
                            pstmt.setString(5, user.getSecurityQuestion());
                            pstmt.setString(6, user.getSecurityAnswer());
                            pstmt.setString(7, user.getPhoneNumber());
                            pstmt.setString(8, user.getEmail());
                            pstmt.setString(9, user.getAddress());
                            pstmt.setBoolean(10, user.isAccountActive());
                            pstmt.setInt(11, user.getFailedLoginAttempts());
                            pstmt.setTimestamp(12, user.getLastLoginDate() != null ? 
                                new Timestamp(user.getLastLoginDate().getTime()) : null);
                            pstmt.setDouble(13, user.getLoanAmount());
                            pstmt.setDouble(14, user.getInterestRate());
                            pstmt.setString(15, user.getAccountNumber());
                            pstmt.executeUpdate();
                        }
                    } else {
                        // Insert new user
                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO users (account_number, name, pin, balance, account_type, " +
                                "security_question, security_answer, phone_number, email, address, " +
                                "is_active, failed_login_attempts, last_login_date, loan_amount, interest_rate) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
                        pstmt.setString(1, user.getAccountNumber());
                        pstmt.setString(2, user.getName());
                        pstmt.setString(3, user.getPin());
                        pstmt.setDouble(4, user.getBalance());
                        pstmt.setString(5, user.getAccountType());
                        pstmt.setString(6, user.getSecurityQuestion());
                        pstmt.setString(7, user.getSecurityAnswer());
                        pstmt.setString(8, user.getPhoneNumber());
                        pstmt.setString(9, user.getEmail());
                        pstmt.setString(10, user.getAddress());
                        pstmt.setBoolean(11, user.isAccountActive());
                        pstmt.setInt(12, user.getFailedLoginAttempts());
                            pstmt.setTimestamp(13, user.getLastLoginDate() != null ? 
                                new Timestamp(user.getLastLoginDate().getTime()) : null);
                        pstmt.setDouble(14, user.getLoanAmount());
                        pstmt.setDouble(15, user.getInterestRate());
                        pstmt.executeUpdate();
                        }
                    }

                    // Handle transactions
                    if (!user.getTransactions().isEmpty()) {
                        // Insert only new transactions
                        try (PreparedStatement pstmt = conn.prepareStatement(
                                "INSERT INTO transactions (account_number, transaction_type, amount, description, transaction_date) " +
                                        "VALUES (?, ?, ?, ?, ?)")) {
                            for (Transaction tx : user.getTransactions()) {
                                pstmt.setString(1, user.getAccountNumber());
                                pstmt.setString(2, tx.getType());
                                pstmt.setDouble(3, tx.getAmount());
                                pstmt.setString(4, tx.getDescription());
                                pstmt.setTimestamp(5, new Timestamp(tx.getDate().getTime()));
                                pstmt.executeUpdate();
                            }
                        }
                    }
                }
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error saving users: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
            return false;
        }
    }

    private String generateAccountNumber() {
        Random random = new Random();
        return String.format("%08d", random.nextInt(100000000));
    }

    private boolean validatePin(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }

    public User findUserByAccountNumber(String accountNumber) {
        return users.stream()
                .filter(u -> u.getAccountNumber().equals(accountNumber))
                .findFirst()
                .orElse(null);
    }

    public User findUser(String accountNumber) {
        for (User user : users) {
            if (user.getAccountNumber().equals(accountNumber)) return user;
        }
        return null;
    }

    public List<User> searchUsers(String query) {
        List<User> matchedUsers = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM users WHERE name LIKE ? OR account_number LIKE ? OR email LIKE ?")) {
            String searchPattern = "%" + query + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    User user = loadUserFromResultSet(rs);
                    matchedUsers.add(user);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error searching users: " + e.getMessage());
        }
        return matchedUsers;
    }

    private User loadUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User(
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
        user.setAccountActive(rs.getBoolean("is_active"));
        user.setLoanAmount(rs.getDouble("loan_amount"));
        Timestamp lastLogin = rs.getTimestamp("last_login_date");
        if (lastLogin != null) {
            user.updateLastLoginDate();
        }
        Timestamp creationDate = rs.getTimestamp("account_creation_date");
        if (creationDate != null) {
            user.setAccountCreationDate(creationDate);
        }
        return user;
    }

    private void startSessionTimer() {
        cancelSessionTimer();
        sessionTimer = new Timer();
        sessionTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                logout();
                JOptionPane.showMessageDialog(null,
                        "Your session has expired due to inactivity.",
                        "Session Timeout", JOptionPane.WARNING_MESSAGE);
            }
        }, sessionTimeoutMinutes * 60 * 1000);
    }

    private void cancelSessionTimer() {
        if (sessionTimer != null) {
            sessionTimer.cancel();
            sessionTimer = null;
        }
    }

    public void resetSessionTimer() {
        if (currentUser != null) {
            startSessionTimer();
        }
    }

    public void toggleDarkMode() {
        darkMode = !darkMode;
        try {
            if (darkMode) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
            } else {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean exportStatementToFile(User user, Date startDate, Date endDate) {
        try {
            File dir = new File("statements");
            if (!dir.exists()) {
                dir.mkdir();
            }

            String filename = "statements/statement_" + user.getAccountNumber() + "_" +
                    new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".txt";

            try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
                writer.println(user.generateStatement(startDate, endDate));
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProfile(User user, String name, String phoneNumber, String email, String address) {
        if (user == null) {
            return false;
        }

        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE users SET name = ?, phone_number = ?, email = ?, address = ? WHERE account_number = ?")) {
                pstmt.setString(1, name);
                pstmt.setString(2, phoneNumber);
                pstmt.setString(3, email);
                pstmt.setString(4, address);
                pstmt.setString(5, user.getAccountNumber());
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
            user.setName(name);
            user.setPhoneNumber(phoneNumber);
            user.setEmail(email);
            user.setAddress(address);
                    return true;
        }
            }
        } catch (SQLException e) {
            System.err.println("Error updating profile: " + e.getMessage());
        }
        return false;
    }

    public String getAdminId() {
        return admins.isEmpty() ? null : admins.keySet().iterator().next();
    }

    public String getAdminPassword() {
        return null; // Passwords not exposed directly
    }

    public void reloadUsers() {
        this.users = loadUsers();
        if (this.users == null) {
            this.users = new ArrayList<>();
        }
    }

    private void setAccountCreationDate(Date creationDate) {
    }

    private String generateTransactionReference() {
        return "TXN" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));
    }

    public List<Transaction> getTransactionHistory(User user) {
        List<Transaction> transactions = new ArrayList<>();
        String query = "SELECT transaction_date, transaction_type, amount, description " +
                      "FROM transactions " +
                      "WHERE account_number = ? " +
                      "ORDER BY transaction_date DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, user.getAccountNumber());
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getString("transaction_type"),
                    rs.getDouble("amount"),
                    rs.getTimestamp("transaction_date"),
                    rs.getString("description")
                );
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction history: " + e.getMessage());
        }
        return transactions;
    }

    public boolean deposit(User user, double amount) {
        if (amount <= 0 || user == null) {
            return false;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update user's balance
                String updateBalance = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateBalance)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, user.getAccountNumber());
                    pstmt.executeUpdate();
                }

                // 2. Record the transaction
                String insertTransaction = "INSERT INTO transactions (account_number, transaction_type, amount, description) " +
                                        "VALUES (?, 'DEPOSIT', ?, 'Cash deposit')";
                try (PreparedStatement pstmt = conn.prepareStatement(insertTransaction)) {
                    pstmt.setString(1, user.getAccountNumber());
                    pstmt.setDouble(2, amount);
                    pstmt.executeUpdate();
                }

                // 3. Update user object
                user.setBalance(user.getBalance() + amount);
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error processing deposit: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public boolean withdraw(User user, double amount) {
        if (amount <= 0 || user == null || user.getBalance() < amount) {
            return false;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update user's balance
                String updateBalance = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateBalance)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, user.getAccountNumber());
                    pstmt.setDouble(3, amount);
                    pstmt.executeUpdate();
                }

                // 2. Record the transaction
                String insertTransaction = "INSERT INTO transactions (account_number, transaction_type, amount, description) " +
                                        "VALUES (?, 'WITHDRAWAL', ?, 'Cash withdrawal')";
                try (PreparedStatement pstmt = conn.prepareStatement(insertTransaction)) {
                    pstmt.setString(1, user.getAccountNumber());
                    pstmt.setDouble(2, -amount);  // Negative amount for withdrawal
                    pstmt.executeUpdate();
                }

                // 3. Update user object
                user.setBalance(user.getBalance() - amount);
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error processing withdrawal: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public boolean transfer(User sender, String recipientAccountNumber, double amount) {
        if (amount <= 0 || sender == null || sender.getBalance() < amount) {
            return false;
        }

        User recipient = findUserByAccountNumber(recipientAccountNumber);
        if (recipient == null || sender.equals(recipient)) {
            return false;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // 1. Update sender's balance
                String updateSender = "UPDATE users SET balance = balance - ? WHERE account_number = ? AND balance >= ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateSender)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, sender.getAccountNumber());
                    pstmt.setDouble(3, amount);
                    pstmt.executeUpdate();
                }

                // 2. Update recipient's balance
                String updateRecipient = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(updateRecipient)) {
                    pstmt.setDouble(1, amount);
                    pstmt.setString(2, recipientAccountNumber);
                    pstmt.executeUpdate();
                }

                // 3. Record sender's transaction
                String insertSenderTx = "INSERT INTO transactions (account_number, transaction_type, amount, description) " +
                                      "VALUES (?, 'TRANSFER_OUT', ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertSenderTx)) {
                    pstmt.setString(1, sender.getAccountNumber());
                    pstmt.setDouble(2, -amount);
                    pstmt.setString(3, "Transfer to " + recipientAccountNumber);
                    pstmt.executeUpdate();
                }

                // 4. Record recipient's transaction
                String insertRecipientTx = "INSERT INTO transactions (account_number, transaction_type, amount, description) " +
                                         "VALUES (?, 'TRANSFER_IN', ?, ?)";
                try (PreparedStatement pstmt = conn.prepareStatement(insertRecipientTx)) {
                    pstmt.setString(1, recipientAccountNumber);
                    pstmt.setDouble(2, amount);
                    pstmt.setString(3, "Transfer from " + sender.getAccountNumber());
                    pstmt.executeUpdate();
                }

                // 5. Update user objects
                sender.setBalance(sender.getBalance() - amount);
                recipient.setBalance(recipient.getBalance() + amount);

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error processing transfer: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public List<Transaction> getTransactionsByDateRange(User user, Date startDate, Date endDate) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT * FROM transactions WHERE account_number = ? " +
                     "AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date")) {
            pstmt.setString(1, user.getAccountNumber());
            pstmt.setTimestamp(2, new Timestamp(startDate.getTime()));
            pstmt.setTimestamp(3, new Timestamp(endDate.getTime()));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Transaction transaction = new Transaction(
                            rs.getString("transaction_type"),
                            rs.getDouble("amount"),
                            rs.getTimestamp("transaction_date"),
                            rs.getString("description")
                    );
                    transactions.add(transaction);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions by date range: " + e.getMessage());
        }
        return transactions;
    }

    // Admin Operations
    public boolean deactivateAccount(String adminId, String adminPassword, String userAccountNumber) {
        if (!adminLogin(adminId, adminPassword)) {
            return false;
        }

        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE users SET is_active = false WHERE account_number = ?")) {
                pstmt.setString(1, userAccountNumber);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    User user = findUserByAccountNumber(userAccountNumber);
                    if (user != null) {
                        user.setAccountActive(false);
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error deactivating account: " + e.getMessage());
        }
        return false;
    }

    public boolean reactivateAccount(String adminId, String adminPassword, String userAccountNumber) {
        if (!adminLogin(adminId, adminPassword)) {
            return false;
        }

        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "UPDATE users SET is_active = true, failed_login_attempts = 0 WHERE account_number = ?")) {
                pstmt.setString(1, userAccountNumber);
                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    User user = findUserByAccountNumber(userAccountNumber);
                    if (user != null) {
                        user.setAccountActive(true);
                        user.resetFailedLoginAttempts();
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error reactivating account: " + e.getMessage());
        }
        return false;
    }

    public boolean deleteInactiveAccounts(String adminId, String adminPassword) {
        if (!adminLogin(adminId, adminPassword)) {
            return false;
        }

        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // First, delete related transactions
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "DELETE FROM transactions WHERE account_number IN " +
                        "(SELECT account_number FROM users WHERE is_active = false)")) {
                    pstmt.executeUpdate();
                }

                // Then delete inactive users
                try (PreparedStatement pstmt = conn.prepareStatement(
                        "DELETE FROM users WHERE is_active = false")) {
                    int rowsAffected = pstmt.executeUpdate();
                    if (rowsAffected > 0) {
                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
                return false;
            } catch (SQLException e) {
                conn.rollback();
                System.err.println("Error deleting inactive accounts: " + e.getMessage());
                return false;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            return false;
        }
    }

    public Map<String, Object> getSystemStatistics(String adminId, String adminPassword) {
        Map<String, Object> stats = new HashMap<>();
        if (!adminLogin(adminId, adminPassword)) {
            return stats;
        }

        try (Connection conn = getConnection()) {
            // Total number of users
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users")) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalUsers", rs.getInt(1));
                }
            }

            // Total active users
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM users WHERE is_active = true")) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("activeUsers", rs.getInt(1));
                }
            }

            // Total balance across all accounts
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT SUM(balance) FROM users")) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalBalance", rs.getDouble(1));
                }
            }

            // Total loans
            try (PreparedStatement pstmt = conn.prepareStatement("SELECT SUM(loan_amount) FROM users")) {
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("totalLoans", rs.getDouble(1));
                }
            }

            // Transactions in last 24 hours
            try (PreparedStatement pstmt = conn.prepareStatement(
                    "SELECT COUNT(*) FROM transactions WHERE transaction_date >= ?")) {
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, -1);
                pstmt.setTimestamp(1, new Timestamp(cal.getTimeInMillis()));
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    stats.put("recentTransactions", rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting system statistics: " + e.getMessage());
        }
        return stats;
    }
}