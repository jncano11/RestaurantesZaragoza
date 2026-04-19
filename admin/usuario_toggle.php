<?php
// ============================================================
//  POST /admin/usuario_toggle.php
//  Body: { usuario_id }
//  Activa/desactiva un usuario
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body      = getJsonBody();
$usuarioId = (int)($body['usuario_id'] ?? 0);

if ($usuarioId <= 0) {
    jsonResponse(['success' => false, 'message' => 'usuario_id inválido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("UPDATE usuarios SET activo = NOT activo WHERE id = ? AND rol != 'admin'");
$stmt->execute([$usuarioId]);

if ($stmt->rowCount() === 0) {
    jsonResponse(['success' => false, 'message' => 'Usuario no encontrado o no modificable'], 404);
}

// Devolver el nuevo estado
$check = $pdo->prepare("SELECT activo FROM usuarios WHERE id = ?");
$check->execute([$usuarioId]);
$nuevoEstado = (bool)$check->fetchColumn();

jsonResponse([
    'success' => true,
    'message' => $nuevoEstado ? 'Usuario activado' : 'Usuario desactivado',
    'activo'  => $nuevoEstado
]);
