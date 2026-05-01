<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
require_once '../config/db.php';

$data           = json_decode(file_get_contents("php://input"), true);
$restaurante_id = intval($data['restaurante_id'] ?? 0);
$nombre         = trim($data['nombre'] ?? '');

if (!$restaurante_id || !$nombre) {
    echo json_encode(['success' => false, 'message' => 'Faltan campos']); exit;
}

$pdo = getDB();

$check = $pdo->prepare("SELECT id FROM menu_categorias WHERE restaurante_id = ? AND nombre = ?");
$check->execute([$restaurante_id, $nombre]);
if ($check->fetchColumn()) {
    echo json_encode(['success' => false, 'message' => 'La categoría ya existe']); exit;
}

$orden_stmt = $pdo->prepare("SELECT COALESCE(MAX(orden), 0) + 1 FROM menu_categorias WHERE restaurante_id = ?");
$orden_stmt->execute([$restaurante_id]);
$orden = (int)$orden_stmt->fetchColumn();

$ins = $pdo->prepare("INSERT INTO menu_categorias (restaurante_id, nombre, orden) VALUES (?, ?, ?)");
$ok  = $ins->execute([$restaurante_id, $nombre, $orden]);

echo json_encode([
    'success' => $ok,
    'id'      => (int)$pdo->lastInsertId(),
    'nombre'  => $nombre,
    'orden'   => $orden
]);
