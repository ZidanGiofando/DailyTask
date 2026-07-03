<?php
require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    error("Method tidak diizinkan, gunakan GET", 405);
}

$userId = isset($_GET['user_id']) ? (int)$_GET['user_id'] : 0;

if ($userId <= 0) {
    error("user_id tidak valid");
}

$database = new Database();
$conn = $database->getConnection();

$stmt = $conn->prepare(
    "SELECT id, user_id, title, description, deadline, time, priority, status, created_at
     FROM tasks WHERE user_id = ? ORDER BY deadline ASC, time ASC"
);
$stmt->bind_param("i", $userId);
$stmt->execute();
$result = $stmt->get_result();

$tasks = [];
while ($row = $result->fetch_assoc()) {
    $row['id'] = (int)$row['id'];
    $row['user_id'] = (int)$row['user_id'];
    $tasks[] = $row;
}

success("Data tugas berhasil diambil", $tasks);

$stmt->close();
$conn->close();
