<?php
// ============================================================
//  POST /reservas/cancelar.php
//  Body: { reserva_id, [usuario_id] }   ← usuario_id opcional (para validar propiedad)
//  Returns: { success, message }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body      = getJsonBody();
$reservaId = (int)($body['reserva_id'] ?? 0);
$usuarioId = (int)($body['usuario_id'] ?? 0);  // 0 = petición desde restaurante/admin

if ($reservaId <= 0) {
    jsonResponse(['success' => false, 'message' => 'reserva_id inválido'], 400);
}

$pdo = getDB();

// Si se envía usuario_id, verificar que la reserva le pertenece
if ($usuarioId > 0) {
    $check = $pdo->prepare("SELECT id, estado FROM reservas WHERE id = ? AND usuario_id = ?");
    $check->execute([$reservaId, $usuarioId]);
} else {
    $check = $pdo->prepare("SELECT id, estado FROM reservas WHERE id = ?");
    $check->execute([$reservaId]);
}

$reserva = $check->fetch();
if (!$reserva) {
    jsonResponse(['success' => false, 'message' => 'Reserva no encontrada'], 404);
}
if ($reserva['estado'] === 'cancelada') {
    jsonResponse(['success' => false, 'message' => 'La reserva ya está cancelada'], 409);
}
if ($reserva['estado'] === 'completada') {
    jsonResponse(['success' => false, 'message' => 'No se puede cancelar una reserva completada'], 409);
}

$stmt = $pdo->prepare("UPDATE reservas SET estado = 'cancelada' WHERE id = ?");
$stmt->execute([$reservaId]);

jsonResponse(['success' => true, 'message' => 'Reserva cancelada correctamente']);
