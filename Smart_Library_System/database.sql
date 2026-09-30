  CREATE DATABASE IF NOT EXISTS library_erp;
USE library_erp;

DROP TABLE IF EXISTS payments;
DROP TABLE IF EXISTS fines;
DROP TABLE IF EXISTS issued_books;
DROP TABLE IF EXISTS books;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS admins;

CREATE TABLE students (
    student_id VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    semester INT NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(150),
    phone VARCHAR(20),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE admins (
    admin_id VARCHAR(30) PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL
);

CREATE TABLE books (
    book_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    author VARCHAR(150),
    isbn VARCHAR(30),
    department_code CHAR(1) NOT NULL,
    department_name VARCHAR(100) NOT NULL,
    rack_no VARCHAR(30),
    shelf_no VARCHAR(30),
    total_copies INT NOT NULL DEFAULT 1,
    available_copies INT NOT NULL DEFAULT 1
);

CREATE TABLE issued_books (
    issue_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(30) NOT NULL,
    book_id INT NOT NULL,
    issue_date DATE NOT NULL,
    due_date DATE NOT NULL,
    return_date DATE NULL,
    renewal_count INT NOT NULL DEFAULT 0,
    status ENUM('ISSUED','RETURNED') NOT NULL DEFAULT 'ISSUED',
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (book_id) REFERENCES books(book_id)
);

CREATE TABLE fines (
    fine_id INT PRIMARY KEY AUTO_INCREMENT,
    issue_id INT NOT NULL,
    student_id VARCHAR(30) NOT NULL,
    fine_date DATE NOT NULL,
    late_days INT NOT NULL DEFAULT 0,
    amount DECIMAL(10,2) NOT NULL DEFAULT 0,
    paid BOOLEAN NOT NULL DEFAULT FALSE,
    paid_date DATE NULL,
    UNIQUE KEY uq_issue_unpaid (issue_id, paid),
    FOREIGN KEY (issue_id) REFERENCES issued_books(issue_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id)
);

CREATE TABLE payments (
    payment_id INT PRIMARY KEY AUTO_INCREMENT,
    student_id VARCHAR(30) NOT NULL,
    fine_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    method VARCHAR(30) DEFAULT 'ERP_SIMULATED',
    transaction_ref VARCHAR(100),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    FOREIGN KEY (fine_id) REFERENCES fines(fine_id)
);

INSERT INTO students(student_id,name,department,semester,password,email,phone) VALUES
('STU001','Sarthak','Computer Science',5,'1234','sarthak@example.com','9999999999'),
('STU002','Rahul','Information Technology',5,'1234','rahul@example.com','8888888888');

INSERT INTO admins(admin_id,name,password) VALUES
('ADMIN001','Library Admin','admin123');

INSERT INTO books(title,author,isbn,department_code,department_name,rack_no,shelf_no,total_copies,available_copies) VALUES
('Java: The Complete Reference','Herbert Schildt','9781260440218','A','Computer Science','A-01','2',5,5),
('Database System Concepts','Silberschatz','9780073523323','A','Computer Science','A-02','1',4,4),
('Operating System Concepts','Silberschatz','9781119456339','A','Computer Science','A-03','3',3,3),
('Computer Networks','Andrew S. Tanenbaum','9780132126953','A','Computer Science','A-04','2',4,4),
('Web Technology','Uttam Kumar Roy','9788120348988','B','Information Technology','B-01','1',3,3),
('Digital Electronics','R.P. Jain','9780074517864','C','Electronics','C-01','2',4,4),
('Thermodynamics','P.K. Nag','9780070151313','D','Mechanical','D-01','1',3,3),
('Engineering Mathematics','B.S. Grewal','9788195675432','E','General','E-01','1',6,6);
