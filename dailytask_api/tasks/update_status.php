<?php
require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    error("Method tidak diizinkan, gunakan POST", 405);
}

$id = isset($_POST['id']) ? (int)$_POST['id'] : 0;
$status = isset($_POST['status']) ? trim($_POST['status']) : '';

if ($id <= 0) {
    error("id tidak valid");
}
if (!in_array($status, ['pending', 'done'])) {
    error("Status harus 'pending' atau 'done'");
}

$database = new Database();
$conn = $database->getConnection();

$stmt = $conn->prepare("UPDATE tasks SET status = ? WHERE id = ?");
$stmt->bind_param("si", $status, $id);

if ($stmt->execute()) {
    if ($stmt->affected_rows === 0) {
        // Bisa jadi status sudah sama, tetap anggap sukses idempotent
    }
    success("Status tugas berhasil diperbarui", [
        "id" => $id,
        "status" => $status
    ]);
} else {
    error("Gagal memperbarui status: " . $stmt->error, 500);
}

$stmt->close();
$conn->close();
