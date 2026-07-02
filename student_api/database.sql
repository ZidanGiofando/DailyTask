CREATE DATABASE IF NOT EXISTS campus_db
CHARACTER SET utf8mb4
COLLATE utf8mb4_general_ci;

USE campus_db;

CREATE TABLE IF NOT EXISTS students (
 id INT AUTO_INCREMENT PRIMARY KEY,
 student_name VARCHAR(100) NOT NULL,
 student_email VARCHAR(100) NOT NULL,
 created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO students (student_name, student_email)
VALUES
('Budi Santoso','budi@gmail.com'),
('Siti Aminah','siti@gmail.com'),
('Andi Wijaya','andi@gmail.com');