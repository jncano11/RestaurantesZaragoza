<?php
// ============================================================
//  POST /admin/usuario_cambiar_rol.php
//  Body: { "usuario_id": 5, "rol": "restaurante" }
//  Returns: { success, message }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body       = getJsonBody();
$usuarioId  = (int) ($body['usuario_id'] ?? 0);
$nuevoRol   = trim($body['rol'] ?? '');

$rolesPermitidos = ['usuario', 'restaurante', 'admin'];

if ($usuarioId <= 0) {
    jsonResponse(['success' => false, 'message' => 'usuario_id inválido'], 400);
}

if (!in_array($nuevoRol, $rolesPermitidos)) {
    jsonResponse(['success' => false, 'message' => 'Rol no válido. Usa: usuario, restaurante o admin'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("UPDATE usuarios SET rol = ? WHERE id = ?");
$stmt->execute([$nuevoRol, $usuarioId]);

if ($stmt->rowCount() === 0) {
    jsonResponse(['success' => false, 'message' => 'Usuario no encontrado'], 404);
}

jsonResponse(['success' => true, 'message' => "Rol actualizado a '$nuevoRol'"]);