<?php
// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/editar.php
// Edita los datos de un restaurante existente
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$data        = getJsonBody();
$id          = intval($data['id']             ?? 0);
$nombre      = trim($data['nombre']           ?? '');
$descripcion = trim($data['descripcion']      ?? '');
$direccion   = trim($data['direccion']        ?? '');
$categoria   = trim($data['categoria']        ?? '');
$telefono    = trim($data['telefono']         ?? '');
$email       = trim($data['email_contacto']   ?? $data['email_contact'] ?? '');
$precio      = floatval($data['precio_medio'] ?? 0);
$aforo       = intval($data['aforo_total']    ?? 50);

if (!$id) {
    jsonResponse(['success' => false, 'message' => 'ID requerido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("
    UPDATE restaurantes
    SET nombre=?, descripcion=?, direccion=?, categoria=?, telefono=?,
        email_contacto=?, precio_medio=?, aforo_total=?
    WHERE id=?
");
$ok = $stmt->execute([$nombre, $descripcion, $direccion, $categoria, $telefono, $email, $precio, $aforo, $id]);
jsonResponse(['success' => $ok, 'message' => $ok ? 'Restaurante actualizado' : 'Error al actualizar']);