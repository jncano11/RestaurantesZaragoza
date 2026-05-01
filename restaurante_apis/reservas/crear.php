<?php
// ============================================================
// ARCHIVO: restaurantes_api/reservas/crear.php
// Crea la reserva y envía email al usuario con botones de
// confirmación / rechazo para el propietario.
// ============================================================
ob_start(); // captura cualquier salida inesperada antes del JSON

require_once '../config/cors.php';
require_once '../config/db.php';
require_once '../config/mail.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST')
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);

$pdo = getDB();

$data           = getJsonBody();
$usuario_id     = intval($data['usuario_id']     ?? 0);
$restaurante_id = intval($data['restaurante_id'] ?? 0);
$fecha          = $data['fecha']          ?? '';
$hora           = $data['hora']           ?? '';
$num_personas   = intval($data['num_personas']   ?? 1);
$notas          = $data['notas']          ?? '';

if (!$usuario_id || !$restaurante_id || !$fecha || !$hora)
    jsonResponse(['success' => false, 'message' => 'Faltan campos obligatorios'], 400);

// Validar que no se supera el aforo
$stmt = $pdo->prepare("SELECT aforo_total FROM restaurantes WHERE id = ?");
$stmt->execute([$restaurante_id]);
$aforo_total = intval($stmt->fetchColumn() ?: 50);

$stmt = $pdo->prepare("
    SELECT COALESCE(SUM(num_personas), 0)
    FROM reservas
    WHERE restaurante_id = ? AND fecha = ? AND hora = ?
      AND estado IN ('pendiente', 'confirmada')
");
$stmt->execute([$restaurante_id, $fecha, $hora]);
$ya_reservadas = intval($stmt->fetchColumn());

if ($ya_reservadas + $num_personas > $aforo_total)
    jsonResponse([
        'success' => false,
        'message' => "No hay plazas suficientes. Solo quedan " . max(0, $aforo_total - $ya_reservadas) . " plazas disponibles."
    ], 409);

// Crear la reserva
$stmt = $pdo->prepare("INSERT INTO reservas (usuario_id, restaurante_id, fecha, hora, num_personas, estado, notas) VALUES (?, ?, ?, ?, ?, 'pendiente', ?)");
$ok = $stmt->execute([$usuario_id, $restaurante_id, $fecha, $hora, $num_personas, $notas]);

if ($ok) {
    $reservaId = (int)$pdo->lastInsertId();
    // El email al usuario se envía cuando el RESTAURANTE confirme la reserva, no aquí
}

ob_end_clean(); // descarta cualquier output espurio acumulado
jsonResponse([
    'success' => $ok,
    'message' => $ok ? 'Reserva creada con éxito' : 'Error al crear la reserva'
], $ok ? 200 : 500);
