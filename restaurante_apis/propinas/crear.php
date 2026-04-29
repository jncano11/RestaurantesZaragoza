<?php
// ============================================================
// ARCHIVO: restaurantes_api/propinas/crear.php
// ============================================================

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
require_once '../config/db.php';

$data           = json_decode(file_get_contents("php://input"), true);
$usuario_id     = intval($data['usuario_id']     ?? 0);
$restaurante_id = intval($data['restaurante_id'] ?? 0);
$reserva_id     = intval($data['reserva_id']     ?? 0) ?: null;
$cantidad       = floatval($data['cantidad']      ?? 0);
$mensaje        = trim($data['mensaje'] ?? '');

if (!$usuario_id || !$restaurante_id || $cantidad <= 0) {
    echo json_encode(['success'=>false,'message'=>'Datos inválidos']); exit;
}

$stmt = $pdo->prepare("INSERT INTO propinas (reserva_id,usuario_id,restaurante_id,cantidad,mensaje) VALUES (?,?,?,?,?)");
$ok = $stmt->execute([$reserva_id,$usuario_id,$restaurante_id,$cantidad,$mensaje]);
echo json_encode(['success'=>$ok]);