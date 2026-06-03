# 🏦 Professional Banking Application

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)](https://www.mysql.com/)

A robust, full-featured desktop banking application built with Java Swing, implementing modern software engineering principles and a professional GUI. This system provides a comprehensive suite of banking services for both clients and administrators.

## 🚀 Key Features

### 👤 Client Features
- **Secure Authentication**: PIN-based login and account registration.
- **Account Management**: View balance, manage profile, and change security PIN.
- **Financial Transactions**:
    - Instant deposits and withdrawals.
    - Peer-to-peer fund transfers.
    - Loan application and management system.
- **Transaction History**: Real-time statement generation and history tracking.
- **Dynamic UI**: Support for Dark/Light modes with a responsive design.

### 🔐 Administrative Features
- **Admin Dashboard**: Comprehensive overview of the banking system.
- **User Management**: Oversee client accounts and credentials.
- **Transaction Oversight**: Monitor all system-wide financial movements.
- **Secure Access**: Dedicated admin login with credential management.

## 🛠️ Technical Stack
- **Language**: Java 17
- **GUI Framework**: Java Swing (with custom Theme Management)
- **Database**: MySQL 8.0
- **Build Tool**: Maven
- **Persistence**: JDBC (DAO Pattern)

## 📁 Project Structure
```text
src/main/java/simple_banking_app/
├── admin/          # Admin dashboard and controls
├── client/         # Client-side UI and logic
├── config/         # Database and system configurations
├── dao/            # Data Access Objects (MySQL integration)
├── model/          # Core entities (User, Transaction)
├── service/        # Business logic layer
└── system/         # Core system engine
```

## ⚙️ Installation & Setup

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- MySQL Server 8.0+
- Maven

### Database Setup
1. Create a database named `banking_app` in MySQL.
2. Execute the migration script located in `src/main/resources/database/migration.sql`.

### Configuration
Update the database credentials in `src/main/java/simple_banking_app/config/DatabaseConfig.java`:
```java
public static DatabaseConfig getDefault() {
    return new DatabaseConfig(
        "jdbc:mysql://localhost:3306/banking_app",
        "root",
        "YOUR_PASSWORD"
    );
}
```

### Build and Run
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="simple_banking_app.BankingApp"
```

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author
**Hayredin Mohammed**
- GitHub: [@HayreKhan750](https://github.com/HayreKhan750)
- LinkedIn: [Your Profile]
