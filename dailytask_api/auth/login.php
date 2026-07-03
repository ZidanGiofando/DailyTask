<?php
require_once __DIR__ . '/../config/response.php';
require_once __DIR__ . '/../config/database.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    error("Method tidak diizinkan, gunakan POST", 405);
}

$email = isset($_POST['email']) ? trim($_POST['email']) : '';
$password = isset($_POST['password']) ? trim($_POST['password']) : '';

if ($email === '' || $password === '') {
    error("Email dan password wajib diisi");
}

if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    error("Format email tidak valid");
}

$database = new Database();
$conn = $database->getConnection();

$stmt = $conn->prepare("SELECT id, name, email, password FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    error("Email tidak terdaftar", 401);
}

$user = $result->fetch_assoc();

if (!password_verify($password, $user['password'])) {
    error("Password salah", 401);
}

success("Login berhasil", [
    "id" => (int)$user['id'],
    "name" => $user['name'],
    "email" => $user['email']
]);

$stmt->close();
$conn->close();
