<?php
// ============================================================
//  POST /admin/restaurante_toggle.php
//  Body: { restaurante_id }
//  Activa/desactiva un restaurante
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body          = getJsonBody();
$restauranteId = (int)($body['restaurante_id'] ?? 0);

if ($restauranteId <= 0) {
    jsonResponse(['success' => false, 'message' => 'restaurante_id inválido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("UPDATE restaurantes SET activo = NOT activo WHERE id = ?");
$stmt->execute([$restauranteId]);

if ($stmt->rowCount() === 0) {
    jsonResponse(['success' => false, 'message' => 'Restaurante no encontrado'], 404);
}

$check = $pdo->prepare("SELECT activo FROM restaurantes WHERE id = ?");
$check->execute([$restauranteId]);
$nuevoEstado = (bool)$check->fetchColumn();

jsonResponse([
    'success' => true,
    'message' => $nuevoEstado ? 'Restaurante activado' : 'Restaurante desactivado',
    'activo'  => $nuevoEstado
]);
