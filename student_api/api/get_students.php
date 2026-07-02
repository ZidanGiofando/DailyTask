<?php

header("Content-Type: application/json");

require_once __DIR__ . "/../config/database.php";
require_once __DIR__ . "/../models/Student.php";

/*
|--------------------------------------------------------------------------
| Validasi Method Request
|--------------------------------------------------------------------------
*/

if ($_SERVER["REQUEST_METHOD"] !== "GET") {

    http_response_code(405);

    echo json_encode([
        "success" => false,
        "message" => "Only GET method is allowed"
    ]);

    exit;
}

/*
|--------------------------------------------------------------------------
| Koneksi Database
|--------------------------------------------------------------------------
*/

$database = new Database();
$connection = $database->connect();

/*
|--------------------------------------------------------------------------
| Ambil Data Mahasiswa
|--------------------------------------------------------------------------
*/

$student = new Student($connection);

$students = $student->getAll();

/*
|--------------------------------------------------------------------------
| Response JSON
|--------------------------------------------------------------------------
*/

echo json_encode([
    "success" => true,
    "data" => $students
]);

/*
|--------------------------------------------------------------------------
| Tutup Koneksi Database
|--------------------------------------------------------------------------
*/

$connection->close();