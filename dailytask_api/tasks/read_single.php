<?php
require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    error("Method tidak diizinkan, gunakan GET", 405);
}

$id = isset($_GET['id']) ? (int)$_GET['id'] : 0;

if ($id <= 0) {
    error("id tidak valid");
}

$database = new Database();
$conn = $database->getConnection();

$stmt = $conn->prepare(
    "SELECT id, user_id, title, description, deadline, time, priority, status, created_at
     FROM tasks WHERE id = ?"
);
$stmt->bind_param("i", $id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    error("Tugas tidak ditemukan", 404);
}

$task = $result->fetch_assoc();
$task['id'] = (int)$task['id'];
$task['user_id'] = (int)$task['user_id'];

success("Data tugas berhasil diambil", $task);

$stmt->close();
$conn->close();
