<?php
require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    error("Method tidak diizinkan, gunakan POST", 405);
}

$name = isset($_POST['name']) ? trim($_POST['name']) : '';
$email = isset($_POST['email']) ? trim($_POST['email']) : '';
$password = isset($_POST['password']) ? trim($_POST['password']) : '';

// ===== Validasi Input (server-side, melengkapi validasi di sisi Android) =====
if ($name === '' || $email === '' || $password === '') {
    error("Nama, email, dan password wajib diisi");
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    error("Format email tidak valid");
}

if (strlen($password) < 6) {
    error("Password minimal 6 karakter");
}

$database = new Database();
$conn = $database->getConnection();

// Cek apakah email sudah terdaftar
$stmt = $conn->prepare("SELECT id FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    $stmt->close();
    error("Email sudah terdaftar, silakan gunakan email lain");
}
$stmt->close();

$hashedPassword = password_hash($password, PASSWORD_BCRYPT);

$stmt = $conn->prepare("INSERT INTO users (name, email, password, created_at) VALUES (?, ?, ?, NOW())");
$stmt->bind_param("sss", $name, $email, $hashedPassword);

if ($stmt->execute()) {
    $newId = $stmt->insert_id;
    success("Registrasi berhasil", [
        "id" => $newId,
        "name" => $name,
        "email" => $email
    ], 201);
} else {
    error("Registrasi gagal: " . $stmt->error, 500);
}

$stmt->close();
$conn->close();
