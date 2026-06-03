package simple_banking_app.service;

import simple_banking_app.model.User;
import simple_banking_app.dao.UserDAO;
import java.util.List;
import java.util.Random;

public class UserService {
    private final UserDAO userDAO;
    private User currentUser;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String accountNumber, String pin) {
        User user = userDAO.findByAccountNumber(accountNumber);
        if (user != null && user.getPin().equals(pin)) {
            currentUser = user;
            return user;
        }
        return null;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
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

        // Save user to database
        // Note: In a real application, you would have a createUser method in UserDAO
        return newUser;
    }

    public boolean changePin(User user, String oldPin, String newPin) {
        if (user == null || !user.getPin().equals(oldPin) || !validatePin(newPin)) {
            return false;
        }

        user.setPin(newPin);
        // Update in database
        return true;
    }

    public boolean validateSecurityAnswer(User user, String answer) {
        return user != null && user.getSecurityAnswer().equalsIgnoreCase(answer.trim());
    }

    public List<User> searchUsers(String query) {
        // Implement search functionality using UserDAO
        return null;
    }

    private String generateAccountNumber() {
        Random random = new Random();
        return String.format("%08d", random.nextInt(100000000));
    }

    private boolean validatePin(String pin) {
        return pin != null && pin.matches("\\d{4}");
    }
} 