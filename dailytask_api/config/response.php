<?php
/**
 * Helper untuk mengirim response JSON dengan format konsisten:
 * { "status": "success"|"error", "message": "...", "data": ... }
 */

header("Content-Type: application/json; charset=UTF-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");

if ($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    http_response_code(200);
    exit();
}

function send_response($status_code, $status, $message, $data = null) {
    http_response_code($status_code);
    echo json_encode([
        "status" => $status,
        "message" => $message,
        "data" => $data
    ]);
    exit();
}

function success($message, $data = null, $code = 200) {
    send_response($code, "success", $message, $data);
}

function error($message, $code = 400) {
    send_response($code, "error", $message, null);
}
