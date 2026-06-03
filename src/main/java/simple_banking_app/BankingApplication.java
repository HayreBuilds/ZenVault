package simple_banking_app;

import simple_banking_app.config.DatabaseConfig;
import simple_banking_app.dao.TransactionDAO;
import simple_banking_app.dao.UserDAO;
import simple_banking_app.service.*;

public class BankingApplication {
    private final UserService userService;
    private final TransactionService transactionService;
    private final LoanService loanService;
    private final AdminService adminService;

    public BankingApplication() {
        // Initialize database configuration
        DatabaseConfig dbConfig = DatabaseConfig.getDefault();

        // Initialize DAOs
        UserDAO userDAO = new UserDAO(dbConfig);
        TransactionDAO transactionDAO = new TransactionDAO(dbConfig);

        // Initialize services
        this.userService = new UserService(userDAO);
        this.transactionService = new TransactionService(transactionDAO, userDAO);
        this.loanService = new LoanService(userDAO, transactionDAO);
        this.adminService = new AdminService(userDAO);
    }

    public UserService getUserService() {
        return userService;
    }

    public TransactionService getTransactionService() {
        return transactionService;
    }

    public LoanService getLoanService() {
        return loanService;
    }

    public AdminService getAdminService() {
        return adminService;
    }

    public static void main(String[] args) {
        BankingApplication app = new BankingApplication();
        // Initialize and show GUI
        // You can add your GUI initialization code here
    }
} 