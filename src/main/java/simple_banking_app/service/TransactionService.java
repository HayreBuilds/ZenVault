package simple_banking_app.service;

import simple_banking_app.model.Transaction;
import simple_banking_app.model.User;
import simple_banking_app.dao.TransactionDAO;
import simple_banking_app.dao.UserDAO;

import java.util.List;

public class TransactionService {
    private final TransactionDAO transactionDAO;
    private final UserDAO userDAO;

    public TransactionService(TransactionDAO transactionDAO, UserDAO userDAO) {
        this.transactionDAO = transactionDAO;
        this.userDAO = userDAO;
    }

    public List<Transaction> getTransactionHistory(User user) {
        return transactionDAO.getTransactionHistory(user.getAccountNumber());
    }

    public boolean deposit(User user, double amount) {
        if (amount <= 0 || user == null) {
            return false;
        }

        // Update balance and record transaction
        boolean success = userDAO.updateBalance(user.getAccountNumber(), amount) &&
                        transactionDAO.recordDeposit(user.getAccountNumber(), amount);

        if (success) {
            user.setBalance(user.getBalance() + amount);
        }
        return success;
    }

    public boolean withdraw(User user, double amount) {
        if (amount <= 0 || user == null || user.getBalance() < amount) {
            return false;
        }

        // Update balance and record transaction
        boolean success = userDAO.updateBalance(user.getAccountNumber(), -amount) &&
                        transactionDAO.recordWithdrawal(user.getAccountNumber(), amount);

        if (success) {
            user.setBalance(user.getBalance() - amount);
        }
        return success;
    }

    public boolean transfer(User sender, String recipientAccountNumber, double amount) {
        if (amount <= 0 || sender == null || sender.getBalance() < amount) {
            return false;
        }

        User recipient = userDAO.findByAccountNumber(recipientAccountNumber);
        if (recipient == null || sender.equals(recipient)) {
            return false;
        }

        // Perform transfer operations
        boolean success = userDAO.transferFunds(sender.getAccountNumber(), recipientAccountNumber, amount) &&
                         transactionDAO.recordTransfer(sender.getAccountNumber(), recipientAccountNumber, amount);

        if (success) {
            sender.setBalance(sender.getBalance() - amount);
            recipient.setBalance(recipient.getBalance() + amount);
        }
        return success;
    }
} 