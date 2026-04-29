<?php
// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/crear.php
// Crea un nuevo restaurante
// ============================================================

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
require_once '../config/db.php';

$data = json_decode(file_get_contents("php://input"), true);
$nombre      = trim($data['nombre']      ?? '');
$descripcion = trim($data['descripcion'] ?? '');
$direccion   = trim($data['direccion']   ?? '');
$categoria   = trim($data['categoria']   ?? '');
$telefono    = trim($data['telefono']    ?? '');
$email       = trim($data['email_contact'] ?? '');
$precio      = floatval($data['precio_medio'] ?? 0);
$aforo       = intval($data['aforo_total']    ?? 50);
$usuario_id  = intval($data['usuario_id']     ?? 0);

if (!$nombre || !$direccion || !$usuario_id) {
    echo json_encode(['success' => false, 'message' => 'Faltan campos obligatorios']);
    exit;
}

$stmt = $pdo->prepare("INSERT INTO restaurantes (usuario_id,nombre,descripcion,direccion,categoria,telefono,email_contacto,precio_medio,aforo_total) VALUES (?,?,?,?,?,?,?,?,?)");
$ok = $stmt->execute([$usuario_id,$nombre,$descripcion,$direccion,$categoria,$telefono,$email,$precio,$aforo]);
echo json_encode(['success' => $ok, 'message' => $ok ? 'Restaurante creado' : 'Error al crear']);
