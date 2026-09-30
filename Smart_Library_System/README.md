# Smart Library ERP

A Java Swing + MySQL minor project for college use.

## Features
- Student login and ERP profile
- Admin/librarian login
- Department-wise book classification (A, B, C, D, E...)
- Book search by title, author or ISBN
- Rack and shelf location
- Issue and return books
- Book renewal
- Maximum 2 renewals
- 14-day renewal period
- Automatic overdue calculation
- Automatic ₹5/day fine
- Fine synchronized to the student's ERP view
- Simulated fine payment and transaction reference
- Student issue/renewal/return history
- Admin book management
- Admin student management
- Reports for issued, overdue and fine records

## Requirements
- JDK 17+
- MySQL 8+
- Maven 3.9+
- IntelliJ IDEA / Eclipse / NetBeans / VS Code

## Setup
1. Create a MySQL database by running `database.sql`.
2. Open `src/main/java/com/libraryerp/config/DB.java`.
3. Change the MySQL USER and PASSWORD to your local MySQL credentials.
4. Open the project as a Maven project.
5. Run:
   `mvn clean compile exec:java`
   or run `com.libraryerp.Main` from your IDE.

## Sample login
Student:
- ID: STU001
- Password: 1234

Second student:
- ID: STU002
- Password: 1234

Admin:
- ID: ADMIN001
- Password: admin123

## Library rules
- Default issue period: 14 days
- Maximum renewals: 2
- Overdue books cannot be renewed
- Late fine: ₹5 per overdue day
- Payment in this academic version is simulated inside the ERP.

## Important
This is an educational/minor-project implementation. Passwords are stored as plain text for simplicity. A production system should use password hashing, role-based authorization, audit logs, HTTPS, and a real payment gateway.
