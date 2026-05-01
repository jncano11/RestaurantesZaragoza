<?php
// ============================================================
// ARCHIVO: restaurantes_api/menu/plato_eliminar.php
// ============================================================

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
require_once '../config/db.php';

$data     = json_decode(file_get_contents("php://input"), true);
$plato_id = intval($data['plato_id'] ?? 0);
if (!$plato_id) { echo json_encode(['success'=>false]); exit; }

$pdo  = getDB();
$stmt = $pdo->prepare("DELETE FROM menu_platos WHERE id=?");
$ok   = $stmt->execute([$plato_id]);
echo json_encode(['success' => $ok]);