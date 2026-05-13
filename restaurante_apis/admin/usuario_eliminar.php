<?php
// ============================================================
//  POST /admin/usuario_eliminar.php
//  Body: { usuario_id }
//  Elimina un usuario (no permite eliminar admins)
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

$pdo = getDB();

$check = $pdo->prepare("SELECT rol FROM usuarios WHERE id = ?");
$check->execute([$usuarioId]);
$rol = $check->fetchColumn();

if ($rol === false) {
    jsonResponse(['success' => false, 'message' => 'Usuario no encontrado'], 404);
}

if ($rol === 'admin') {
    jsonResponse(['success' => false, 'message' => 'No se puede eliminar un administrador'], 403);
}

$stmt = $pdo->prepare("DELETE FROM usuarios WHERE id = ?");
$stmt->execute([$usuarioId]);

jsonResponse(['success' => true, 'message' => 'Usuario eliminado correctamente']);
