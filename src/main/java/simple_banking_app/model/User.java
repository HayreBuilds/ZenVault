package simple_banking_app.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User {
    private String name;
    private String accountNumber;
    private String pin;
    private double balance;
    private List<Transaction> transactions;
    private int failedLoginAttempts;
    private boolean accountActive;
    private Date lastLoginDate;
    private String accountType;
    private double loanAmount;
    private double interestRate;
    private String securityQuestion;
    private String securityAnswer;
    private Date accountCreationDate;
    private String phoneNumber;
    private String email;
    private String address;

    public User(String name, String accountNumber, String pin, double initialDeposit,
                String accountType, String securityQuestion, String securityAnswer,
                String phoneNumber, String email, String address) {
        this.name = name;
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = initialDeposit;
        this.transactions = new ArrayList<>();
        this.failedLoginAttempts = 0;
        this.accountActive = true;
        this.lastLoginDate = new Date();
        this.accountType = accountType;
        this.loanAmount = 0;
        this.interestRate = accountType.equals("Savings") ? 0.02 : 0.01;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
        this.accountCreationDate = new Date();
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        this.addTransaction("Initial Deposit", initialDeposit, "Initial deposit to account");
    }

    // Getters
    public String getName() { return name; }
    public String getAccountNumber() { return accountNumber; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public List<Transaction> getTransactions() { return new ArrayList<>(transactions); }
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public boolean isAccountActive() { return accountActive; }
    public Date getLastLoginDate() { return lastLoginDate; }
    public String getAccountType() { return accountType; }
    public double getLoanAmount() { return loanAmount; }
    public double getInterestRate() { return interestRate; }
    public String getSecurityQuestion() { return securityQuestion; }
    public String getSecurityAnswer() { return securityAnswer; }
    public Date getAccountCreationDate() { return accountCreationDate; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setPin(String pin) { this.pin = pin; }
    public void incrementFailedLoginAttempts() { failedLoginAttempts++; }
    public void resetFailedLoginAttempts() { failedLoginAttempts = 0; }
    public void setAccountActive(boolean active) { accountActive = active; }
    public void updateLastLoginDate() { lastLoginDate = new Date(); }
    public void setLoanAmount(double amount) { loanAmount = amount; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setAccountCreationDate(Date accountCreationDate) {
        this.accountCreationDate = accountCreationDate != null ? new Date(accountCreationDate.getTime()) : null;
    }

    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            addTransaction("Deposit", amount, "Deposit to account");
        }
    }

    public boolean withdraw(double amount) {
        if (amount > 0 && balance >= amount) {
            balance -= amount;
            addTransaction("Withdrawal", -amount, "Withdrawal from account");
            return true;
        }
        return false;
    }

    public boolean transfer(User recipient, double amount) {
        if (amount > 0 && balance >= amount && recipient != null && !recipient.equals(this)) {
            balance -= amount;
            recipient.deposit(amount);
            addTransaction("Transfer to " + recipient.getAccountNumber(), -amount,
                    "Transfer to account " + recipient.getAccountNumber());
            recipient.addTransaction("Transfer from " + accountNumber, amount,
                    "Transfer from account " + accountNumber);
            return true;
        }
        return false;
    }

    public boolean applyForLoan(double amount) {
        if (loanAmount == 0 && amount > 0) {
            loanAmount = amount;
            balance += amount;
            addTransaction("Loan Received", amount, "Loan disbursed to account");
            return true;
        }
        return false;
    }

    public boolean repayLoan(double amount) {
        if (loanAmount > 0 && amount > 0 && amount <= loanAmount && balance >= amount) {
            loanAmount -= amount;
            balance -= amount;
            addTransaction("Loan Repayment", -amount, "Loan repayment");
            return true;
        }
        return false;
    }

    public void applyInterest() {
        if (balance > 0) {
            double interest = balance * interestRate / 12;
            balance += interest;
            addTransaction("Interest Earned", interest, "Monthly interest earned");
        }
    }

    private void addTransaction(String type, double amount, String description) {
        transactions.add(new Transaction(type, amount, new Date(), description));
    }

    public List<Transaction> getTransactionsByDateRange(Date startDate, Date endDate) {
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : transactions) {
            if (!t.getDate().before(startDate) && !t.getDate().after(endDate)) {
                filtered.add(t);
            }
        }
        return filtered;
    }

    public String generateStatement(Date startDate, Date endDate) {
        StringBuilder sb = new StringBuilder();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sb.append("Account Statement\n");
        sb.append("=================\n");
        sb.append("Name: ").append(name).append("\n");
        sb.append("Account Number: ").append(accountNumber).append("\n");
        sb.append("Account Type: ").append(accountType).append("\n");
        sb.append("Statement Period: ").append(sdf.format(startDate)).append(" to ").append(sdf.format(endDate)).append("\n\n");
        sb.append("Current Balance: $").append(String.format("%.2f", balance)).append("\n");

        if (loanAmount > 0) {
            sb.append("Outstanding Loan: $").append(String.format("%.2f", loanAmount)).append("\n");
        }

        sb.append("\nTransaction History:\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("Date                | Type          | Amount    | Description\n");
        sb.append("------------------------------------------------------------\n");

        List<Transaction> statementTransactions = getTransactionsByDateRange(startDate, endDate);
        for (Transaction t : statementTransactions) {
            sb.append(String.format("%-20s| %-14s| $%-9.2f| %s\n",
                    sdf.format(t.getDate()),
                    t.getType(),
                    t.getAmount(),
                    t.getDescription() != null ? t.getDescription() : ""));
        }

        return sb.toString();
    }

    private double getStartingBalance(Date startDate) {
        double balance = 0;
        for (Transaction t : transactions) {
            if (t.getDate().before(startDate)) {
                balance += t.getAmount();
            }
        }
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = new ArrayList<>(transactions);
    }

    public void clearTransactions() {
        this.transactions.clear();
    }

    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
    }
}