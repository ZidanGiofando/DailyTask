-- ============================================================
-- Database: dailytask_db
-- Untuk aplikasi Android "DailyTask" - UAS Mobile Programming
-- ============================================================

CREATE DATABASE IF NOT EXISTS dailytask_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dailytask_db;

-- ============================================================
-- Tabel users
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- disimpan dalam bentuk hash (bcrypt)
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Tabel tasks
-- Relasi: 1 user memiliki banyak task (one-to-many)
-- ============================================================
CREATE TABLE IF NOT EXISTS tasks (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT NOT NULL,
    deadline DATE NOT NULL,
    time TIME NOT NULL,
    priority ENUM('High', 'Medium', 'Low') NOT NULL DEFAULT 'Medium',
    status ENUM('pending', 'done') NOT NULL DEFAULT 'pending',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tasks_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- ERD (Entity Relationship Diagram) - Representasi teks
-- ============================================================
-- +-----------------+          +--------------------+
-- |     users       |          |       tasks        |
-- +-----------------+          +--------------------+
-- | PK id           |1        *| PK id              |
-- |    name         |----------| FK user_id         |
-- |    email        |          |    title           |
-- |    password     |          |    description     |
-- |    created_at   |          |    deadline         |
-- +-----------------+          |    time             |
--                               |    priority         |
--                               |    status            |
--                               |    created_at        |
--                               +--------------------+
--
-- Relasi: users (1) ---- (N) tasks  [ON DELETE CASCADE]

-- ============================================================
-- Data contoh (opsional, untuk testing)
-- ============================================================
-- INSERT INTO users (name, email, password) VALUES
-- ('Zidan Giofando', 'zidan@gmail.com', '$2y$10$examplehashvalue...');
--
-- INSERT INTO tasks (user_id, title, description, deadline, time, priority, status) VALUES
-- (1, 'Belajar Android', 'Belajar RecyclerView, Retrofit, REST API', '2026-07-05', '19:30', 'High', 'pending');
