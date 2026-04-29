<?php
// ============================================================
//  POST /valoraciones/crear.php
//  Body: { usuario_id, restaurante_id, puntuacion, comentario }
//  Returns: { success, message }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body          = getJsonBody();
$usuarioId     = (int)($body['usuario_id']     ?? 0);
$restauranteId = (int)($body['restaurante_id'] ?? 0);
$puntuacion    = (int)($body['puntuacion']      ?? 0);
$comentario    = trim($body['comentario']       ?? '');

if ($usuarioId <= 0 || $restauranteId <= 0) {
    jsonResponse(['success' => false, 'message' => 'Datos incompletos'], 400);
}
if ($puntuacion < 1 || $puntuacion > 5) {
    jsonResponse(['success' => false, 'message' => 'La puntuación debe ser entre 1 y 5'], 400);
}

$pdo = getDB();

// Comprobar si ya valoró este restaurante (UNIQUE key en la tabla)
$check = $pdo->prepare("SELECT id FROM valoraciones WHERE usuario_id = ? AND restaurante_id = ?");
$check->execute([$usuarioId, $restauranteId]);
if ($check->fetch()) {
    // Actualizar en lugar de insertar
    $stmt = $pdo->prepare("UPDATE valoraciones SET puntuacion = ?, comentario = ?, fecha = NOW() WHERE usuario_id = ? AND restaurante_id = ?");
    $stmt->execute([$puntuacion, $comentario, $usuarioId, $restauranteId]);
    jsonResponse(['success' => true, 'message' => 'Valoración actualizada correctamente']);
}

$stmt = $pdo->prepare("
    INSERT INTO valoraciones (usuario_id, restaurante_id, puntuacion, comentario)
    VALUES (?, ?, ?, ?)
");
$stmt->execute([$usuarioId, $restauranteId, $puntuacion, $comentario]);

jsonResponse(['success' => true, 'message' => 'Valoración enviada correctamente']);
