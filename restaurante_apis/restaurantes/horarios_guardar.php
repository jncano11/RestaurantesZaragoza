<?php
// ============================================================
// POST /restaurantes/horarios_guardar.php
// Body: { restaurante_id, horarios: "[{dia_semana, hora_apertura, hora_cierre, cerrado}]" }
// Reemplaza todos los horarios del restaurante
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$data           = getJsonBody();
$restauranteId  = intval($data['restaurante_id'] ?? 0);
$horariosJson   = $data['horarios'] ?? '[]';

if ($restauranteId <= 0) {
    jsonResponse(['success' => false, 'message' => 'restaurante_id inválido'], 400);
}

$horarios = json_decode($horariosJson, true);
if (!is_array($horarios)) {
    jsonResponse(['success' => false, 'message' => 'Formato de horarios inválido'], 400);
}

$pdo = getDB();

$pdo->prepare("DELETE FROM horarios WHERE restaurante_id = ?")->execute([$restauranteId]);

$ins = $pdo->prepare(
    "INSERT INTO horarios (restaurante_id, dia_semana, hora_apertura, hora_cierre, cerrado) VALUES (?, ?, ?, ?, ?)"
);

foreach ($horarios as $h) {
    $dia     = intval($h['dia_semana'] ?? 0);
    $cerrado = !empty($h['cerrado']) ? 1 : 0;
    $apertura = $cerrado ? null : (trim($h['hora_apertura'] ?? '') ?: null);
    $cierre   = $cerrado ? null : (trim($h['hora_cierre']   ?? '') ?: null);
    $ins->execute([$restauranteId, $dia, $apertura, $cierre, $cerrado]);
}

jsonResponse(['success' => true, 'message' => 'Horarios guardados correctamente']);
