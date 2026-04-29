<?php
// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/editar.php  (ACTUALIZADO)
// Edita los datos de un restaurante existente
// ============================================================

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
require_once '../config/db.php';

$data = json_decode(file_get_contents("php://input"), true);
$id          = intval($data['id']          ?? 0);
$nombre      = trim($data['nombre']        ?? '');
$descripcion = trim($data['descripcion']   ?? '');
$direccion   = trim($data['direccion']     ?? '');
$categoria   = trim($data['categoria']     ?? '');
$telefono    = trim($data['telefono']      ?? '');
$email       = trim($data['email_contact'] ?? '');
$precio      = floatval($data['precio_medio'] ?? 0);
$aforo       = intval($data['aforo_total']    ?? 50);

if (!$id) { echo json_encode(['success' => false, 'message' => 'ID requerido']); exit; }

$stmt = $pdo->prepare("UPDATE restaurantes SET nombre=?,descripcion=?,direccion=?,categoria=?,telefono=?,email_contacto=?,precio_medio=?,aforo_total=? WHERE id=?");
$ok = $stmt->execute([$nombre,$descripcion,$direccion,$categoria,$telefono,$email,$precio,$aforo,$id]);
echo json_encode(['success' => $ok]);