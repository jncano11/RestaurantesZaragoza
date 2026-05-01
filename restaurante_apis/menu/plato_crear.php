<?php
// ============================================================
// ARCHIVO: restaurantes_api/menu/plato_crear.php
// Crea un plato (busca o crea la categoría automáticamente)
// ============================================================

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
require_once '../config/db.php';

$data           = json_decode(file_get_contents("php://input"), true);
$restaurante_id = intval($data['restaurante_id'] ?? 0);
$categoria_id   = intval($data['categoria_id']   ?? 0);
$nombre         = trim($data['nombre']      ?? '');
$descripcion    = trim($data['descripcion'] ?? '');
$precio         = floatval($data['precio']  ?? 0);
$alergenos      = trim($data['alergenos']   ?? '');

if (!$nombre || !$restaurante_id || !$categoria_id) {
    echo json_encode(['success' => false, 'message' => 'Faltan campos']); exit;
}

$stmt = $pdo->prepare("INSERT INTO menu_platos (categoria_id, restaurante_id, nombre, descripcion, precio, alergenos) VALUES (?, ?, ?, ?, ?, ?)");
$ok   = $stmt->execute([$categoria_id, $restaurante_id, $nombre, $descripcion, $precio, $alergenos]);
echo json_encode(['success' => $ok]);
