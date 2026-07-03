<?php
require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    error("Method tidak diizinkan, gunakan POST", 405);
}

$id = isset($_POST['id']) ? (int)$_POST['id'] : 0;

if ($id <= 0) {
    error("id tidak valid");
}

$database = new Database();
$conn = $database->getConnection();

$stmt = $conn->prepare("DELETE FROM tasks WHERE id = ?");
$stmt->bind_param("i", $id);

if ($stmt->execute()) {
    if ($stmt->affected_rows === 0) {
        error("Tugas tidak ditemukan", 404);
    }
    success("Tugas berhasil dihapus", ["id" => $id]);
} else {
    error("Gagal menghapus tugas: " . $stmt->error, 500);
}

$stmt->close();
$conn->close();
