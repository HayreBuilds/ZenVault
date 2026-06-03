-- Backup existing transactions
CREATE TABLE IF NOT EXISTS transactions_backup AS SELECT * FROM transactions;

-- Drop existing transactions table
DROP TABLE IF EXISTS transactions;

-- Create new transactions table with simpler structure (matching the file-based system)
CREATE TABLE transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_number VARCHAR(20) NOT NULL,
    transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    transaction_type VARCHAR(20) NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    description VARCHAR(255),
    FOREIGN KEY (account_number) REFERENCES users(account_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create index for better performance
CREATE INDEX idx_account_date ON transactions(account_number, transaction_date);

-- Migrate existing data from backup (if exists)
INSERT INTO transactions (
    account_number,
    transaction_date,
    transaction_type,
    amount,
    description
)
SELECT 
    account_number,
    transaction_date,
    transaction_type,
    amount,
    description
FROM transactions_backup; 