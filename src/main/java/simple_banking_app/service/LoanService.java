package simple_banking_app.service;

import simple_banking_app.model.User;
import simple_banking_app.dao.UserDAO;
import simple_banking_app.dao.TransactionDAO;

public class LoanService {
    private final UserDAO userDAO;
    private final TransactionDAO transactionDAO;

    public LoanService(UserDAO userDAO, TransactionDAO transactionDAO) {
        this.userDAO = userDAO;
        this.transactionDAO = transactionDAO;
    }

    public boolean applyForLoan(User user, double amount) {
        if (user == null || amount <= 0 || user.getLoanAmount() > 0) {
            return false;
        }

        // Update user's loan amount and balance
        user.setLoanAmount(amount);
        user.setBalance(user.getBalance() + amount);

        // Record the loan transaction
        boolean success = transactionDAO.recordDeposit(user.getAccountNumber(), amount);
        if (!success) {
            // Rollback changes if transaction recording fails
            user.setLoanAmount(0);
            user.setBalance(user.getBalance() - amount);
            return false;
        }

        return true;
    }

    public boolean repayLoan(User user, double amount) {
        if (user == null || amount <= 0 || user.getLoanAmount() < amount || user.getBalance() < amount) {
            return false;
        }

        // Update user's loan amount and balance
        user.setLoanAmount(user.getLoanAmount() - amount);
        user.setBalance(user.getBalance() - amount);

        // Record the loan repayment transaction
        boolean success = transactionDAO.recordWithdrawal(user.getAccountNumber(), amount);
        if (!success) {
            // Rollback changes if transaction recording fails
            user.setLoanAmount(user.getLoanAmount() + amount);
            user.setBalance(user.getBalance() + amount);
            return false;
        }

        return true;
    }
} 