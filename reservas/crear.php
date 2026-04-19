<?php
// ============================================================
// ARCHIVO: restaurantes_api/reservas/crear.php
// ACTUALIZADO: valida que no se supere el aforo en ese slot
// ============================================================

header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once '../config/db.php';

$data           = json_decode(file_get_contents("php://input"), true);
$usuario_id     = intval($data['usuario_id']     ?? 0);
$restaurante_id = intval($data['restaurante_id'] ?? 0);
$fecha          = $data['fecha']          ?? '';
$hora           = $data['hora']           ?? '';
$num_personas   = intval($data['num_personas']   ?? 1);
$notas          = $data['notas']          ?? '';

if (!$usuario_id || !$restaurante_id || !$fecha || !$hora) {
    echo json_encode(['success' => false, 'message' => 'Faltan campos obligatorios']);
    exit;
}

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

if ($ya_reservadas + $num_personas > $aforo_total) {
    echo json_encode([
        'success' => false,
        'message' => "No hay plazas suficientes. Solo quedan " . max(0, $aforo_total - $ya_reservadas) . " plazas disponibles."
    ]);
    exit;
}

// Crear la reserva
$stmt = $pdo->prepare("INSERT INTO reservas (usuario_id, restaurante_id, fecha, hora, num_personas, estado, notas) VALUES (?, ?, ?, ?, ?, 'pendiente', ?)");
$ok = $stmt->execute([$usuario_id, $restaurante_id, $fecha, $hora, $num_personas, $notas]);

echo json_encode([
    'success' => $ok,
    'message' => $ok ? 'Reserva creada con éxito' : 'Error al crear la reserva'
]);