<?php

header("Content-Type: application/json");

require_once __DIR__ . "/../config/database.php";
require_once __DIR__ . "/../models/Student.php";

/*
|--------------------------------------------------------------------------
| Validasi Method Request
|--------------------------------------------------------------------------
*/

if ($_SERVER["REQUEST_METHOD"] !== "POST") {

    http_response_code(405);

    echo json_encode([
        "success" => false,
        "message" => "Only POST method is allowed"
    ]);

    exit;
}

/*
|--------------------------------------------------------------------------
| Ambil Data dari Form
|--------------------------------------------------------------------------
*/

$name  = isset($_POST["name"])
    ? trim($_POST["name"])
    : "";

$email = isset($_POST["email"])
    ? trim($_POST["email"])
    : "";

/*
|--------------------------------------------------------------------------
| Validasi Input
|--------------------------------------------------------------------------
*/

if ($name === "" || $email === "") {

    http_response_code(400);

    echo json_encode([
        "success" => false,
        "message" => "Validation failed"
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
| Simpan Data Mahasiswa
|--------------------------------------------------------------------------
*/

$student = new Student($connection);

$isCreated = $student->create($name, $email);

/*
|--------------------------------------------------------------------------
| Response
|--------------------------------------------------------------------------
*/

if ($isCreated) {

    http_response_code(201);

    echo json_encode([
        "success" => true,
        "message" => "Student created successfully"
    ]);

} else {

    http_response_code(500);

    echo json_encode([
        "success" => false,
        "message" => "Failed to create student"
    ]);
}

/*
|--------------------------------------------------------------------------
| Tutup Koneksi
|--------------------------------------------------------------------------
*/

$connection->close();