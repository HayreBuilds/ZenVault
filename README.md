# 🏦 ZenVault Banking System

[![Java](https://img.shields.io/badge/Language-Java_17-red?style=for-the-badge&logo=java)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/Database-MySQL-4479A1?style=for-the-badge&logo=mysql)](https://www.mysql.com/)
[![Swing](https://img.shields.io/badge/UI-Java_Swing-orange?style=for-the-badge)](https://docs.oracle.com/javase/tutorial/uiswing/)

**ZenVault** is a secure, enterprise-grade desktop banking application designed for comprehensive financial management. It features robust client and admin dashboards, secure transaction processing, and persistent data management using MySQL.

## 🌟 Core Features

- **🔐 Secure Authentication**: Multi-layered login system with encrypted session management and role-based access control (RBAC).
- **💼 Comprehensive Dashboards**: 
  - **Client Portal**: View balances, transaction history, and manage multi-currency accounts.
  - **Admin Dashboard**: Oversee user accounts, process registrations, and generate financial reports.
- **💸 Transaction Engine**: High-fidelity processing for transfers, deposits, and withdrawals with concurrency handling.
- **📊 Financial Analytics**: Advanced search and filtering for transaction history with real-time balance updates.
- **🛡️ Data Persistence**: Optimized MySQL integration with connection pooling and automated daily backups.

## 🛠️ Technical Stack

- **Core**: Java 17 (LTS)
- **UI Framework**: Java Swing with custom look-and-feel enhancements
- **Database**: MySQL 8.0
- **Persistence**: JDBC with optimized connection pooling
- **Build Tool**: Maven
- **Security**: Custom JWT-inspired token handling and password hashing

## 🚀 Getting Started

### Prerequisites
- JDK 17 or higher
- MySQL Server 8.0+
- Maven 3.8+

### Installation
1. **Clone the repository**:
   ```bash
   git clone https://github.com/HayreBuilds/ZenVault.git
   ```

2. **Configure the database**:
   - Create a database named `zenvault`.
   - Run the provided SQL scripts in `src/main/resources/sql/` to initialize tables.

3. **Update credentials**:
   - Edit `src/main/resources/db.properties` with your MySQL username and password.

4. **Build and Run**:
   ```bash
   mvn clean install
   mvn exec:java -Dexec.mainClass="com.zenvault.Main"
   ```

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

---
Securely engineered by [Hayredin Mohammed](https://github.com/HayreBuilds)
