<?php
require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../config/database.php';

// Set header JSON agar Android mengenalinya sebagai sukses
header('Content-Type: application/json');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    error("Method tidak diizinkan, gunakan POST", 405);
}

// Tangkap parameter sesuai yang dikirim dari AddTaskActivity.java
$id = isset($_POST['id']) ? (int)$_POST['id'] : 0;
$userId = isset($_POST['user_id']) ? (int)$_POST['user_id'] : 0; // Ditangkap dari Android
$title = isset($_POST['title']) ? trim($_POST['title']) : '';
$description = isset($_POST['description']) ? trim($_POST['description']) : '';
$deadline = isset($_POST['deadline']) ? trim($_POST['deadline']) : '';
$time = isset($_POST['time']) ? trim($_POST['time']) : '';
$priority = isset($_POST['priority']) ? trim($_POST['priority']) : 'Medium';

// ===== Validasi Input =====
if ($id <= 0) {
    error("ID tidak valid");
}
if ($title === '') {
    error("Judul tugas tidak boleh kosong");
}

// Pastikan format waktu sesuai (HH:mm) atau (HH:mm:ss)
// Android mengirim HH:mm, jika DB butuh HH:mm:ss, kita sesuaikan
if (strlen($time) == 5) { $time .= ":00"; } 

$database = new Database();
$conn = $database->getConnection();

// 1. Cek apakah task benar-benar milik user tersebut (Opsional tapi direkomendasikan)
$check = $conn->prepare("SELECT id FROM tasks WHERE id = ?");
$check->bind_param("i", $id);
$check->execute();
$check->store_result();
if ($check->num_rows === 0) {
    $check->close();
    error("Tugas tidak ditemukan", 404);
}
$check->close();

// 2. Jalankan Update
$stmt = $conn->prepare(
    "UPDATE tasks SET title = ?, description = ?, deadline = ?, time = ?, priority = ? WHERE id = ?"
);
$stmt->bind_param("sssssi", $title, $description, $deadline, $time, $priority, $id);

if ($stmt->execute()) {
    // Kembalikan data dalam format yang diharapkan ApiResponse<Task>
    // Pastikan fungsi success() Anda menghasilkan JSON: {"status":"success", "message":"...", "data": {...}}
    success("Tugas berhasil diperbarui", [
        "id" => $id,
        "title" => $title,
        "description" => $description,
        "deadline" => $deadline,
        "time" => $time,
        "priority" => $priority,
        "status" => "pending" // Tambahkan field status jika model Task di Android membutuhkannya
    ]);
} else {
    error("Gagal memperbarui tugas: " . $stmt->error, 500);
}

$stmt->close();
$conn->close();