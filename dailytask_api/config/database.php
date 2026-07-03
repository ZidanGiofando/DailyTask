<?php
/**
 * Konfigurasi koneksi database MySQL/MariaDB.
 * Sesuaikan DB_HOST, DB_USER, DB_PASS, DB_NAME dengan environment Anda
 * (XAMPP/Laragon default: host=localhost, user=root, pass=kosong).
 */

class Database {
    private $host = "localhost";
    private $db_name = "dailytask_db";
    private $username = "root";
    private $password = "";
    public $conn;

    public function getConnection() {
        $this->conn = null;

        try {
            $this->conn = new mysqli($this->host, $this->username, $this->password, $this->db_name);
            if ($this->conn->connect_error) {
                throw new Exception("Connection failed: " . $this->conn->connect_error);
            }
            $this->conn->set_charset("utf8mb4");
        } catch (Exception $e) {
            http_response_code(500);
            echo json_encode([
                "status" => "error",
                "message" => "Koneksi database gagal: " . $e->getMessage(),
                "data" => null
            ]);
            exit();
        }

        return $this->conn;
    }
}
