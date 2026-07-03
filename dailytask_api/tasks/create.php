<?php
require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    error("Method tidak diizinkan, gunakan POST", 405);
}

$userId = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0;
$title = isset($_POST['title']) ? trim($_POST['title']) : '';
$description = isset($_POST['description']) ? trim($_POST['description']) : '';
$deadline = isset($_POST['deadline']) ? trim($_POST['deadline']) : '';
$time = isset($_POST['time']) ? trim($_POST['time']) : '';
$priority = isset($_POST['priority']) ? trim($_POST['priority']) : 'Medium';

// ===== Validasi Input =====
if ($userId <= 0) {
    error("user_id tidak valid");
}
if ($title === '') {
    error("Judul tugas tidak boleh kosong");
}
if ($description === '') {
    error("Deskripsi tidak boleh kosong");
}
if ($deadline === '' || !DateTime::createFromFormat('Y-m-d', $deadline)) {
    error("Format tanggal deadline tidak valid (YYYY-MM-DD)");
}
if ($time === '' || !preg_match('/^([01]\d|2[0-3]):([0-5]\d)$/', $time)) {
    error("Format jam tidak valid (HH:mm)");
}
if (!in_array($priority, ['High', 'Medium', 'Low'])) {
    $priority = 'Medium';
}

$database = new Database();
$conn = $database->getConnection();

$stmt = $conn->prepare(
    "INSERT INTO tasks (user_id, title, description, deadline, time, priority, status, created_at)
     VALUES (?, ?, ?, ?, ?, ?, 'pending', NOW())"
);
$stmt->bind_param("isssss", $userId, $title, $description, $deadline, $time, $priority);

if ($stmt->execute()) {
    $newId = $stmt->insert_id;
    success("Tugas berhasil ditambahkan", [
        "id" => $newId,
        "user_id" => $userId,
        "title" => $title,
        "description" => $description,
        "deadline" => $deadline,
        "time" => $time,
        "priority" => $priority,
        "status" => "pending"
    ], 201);
} else {
    error("Gagal menambahkan tugas: " . $stmt->error, 500);
}

$stmt->close();
$conn->close();
