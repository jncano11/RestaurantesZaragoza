<?php
// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/crear.php
// Crea un nuevo restaurante
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$data        = getJsonBody();
$nombre      = trim($data['nombre']        ?? '');
$descripcion = trim($data['descripcion']   ?? '');
$direccion   = trim($data['direccion']     ?? '');
$categoria   = trim($data['categoria']     ?? '');
$telefono    = trim($data['telefono']      ?? '');
$email       = trim($data['email_contacto'] ?? $data['email_contact'] ?? '');
$precio      = floatval($data['precio_medio'] ?? 0);
$aforo       = intval($data['aforo_total']    ?? 50);
$usuario_id  = intval($data['usuario_id']     ?? 0);

if (!$nombre || !$direccion || !$usuario_id) {
    jsonResponse(['success' => false, 'message' => 'Faltan campos obligatorios'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("
    INSERT INTO restaurantes
        (usuario_id, nombre, descripcion, direccion, categoria, telefono, email_contacto, precio_medio, aforo_total)
    VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
");
$ok = $stmt->execute([$usuario_id, $nombre, $descripcion, $direccion, $categoria, $telefono, $email, $precio, $aforo]);
jsonResponse(['success' => $ok, 'message' => $ok ? 'Restaurante creado' : 'Error al crear']);
