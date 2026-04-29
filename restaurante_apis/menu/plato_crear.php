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
$nombre         = trim($data['nombre']   ?? '');
$descripcion    = trim($data['descripcion'] ?? '');
$precio         = floatval($data['precio'] ?? 0);
$cat_nombre     = trim($data['categoria_nombre'] ?? 'Otros');
$alergenos      = trim($data['alergenos'] ?? '');

if (!$nombre || !$restaurante_id) {
    echo json_encode(['success'=>false,'message'=>'Faltan campos']); exit;
}

// Buscar o crear categoría
$stmt = $pdo->prepare("SELECT id FROM menu_categorias WHERE restaurante_id=? AND nombre=?");
$stmt->execute([$restaurante_id, $cat_nombre]);
$cat = $stmt->fetchColumn();
if (!$cat) {
    $ins = $pdo->prepare("INSERT INTO menu_categorias (restaurante_id,nombre) VALUES (?,?)");
    $ins->execute([$restaurante_id, $cat_nombre]);
    $cat = $pdo->lastInsertId();
}

$stmt = $pdo->prepare("INSERT INTO menu_platos (categoria_id,restaurante_id,nombre,descripcion,precio,alergenos) VALUES (?,?,?,?,?,?)");
$ok = $stmt->execute([$cat,$restaurante_id,$nombre,$descripcion,$precio,$alergenos]);
echo json_encode(['success'=>$ok]);
