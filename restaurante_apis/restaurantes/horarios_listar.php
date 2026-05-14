<?php
// ============================================================
// GET /restaurantes/horarios_listar.php?restaurante_id=X
// Devuelve los horarios de un restaurante
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$restauranteId = intval($_GET['restaurante_id'] ?? 0);
if ($restauranteId <= 0) {
    jsonResponse(['success' => false, 'message' => 'restaurante_id inválido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare(
    "SELECT dia_semana, hora_apertura, hora_cierre, cerrado
     FROM horarios
     WHERE restaurante_id = ?
     ORDER BY dia_semana ASC"
);
$stmt->execute([$restauranteId]);
$horarios = $stmt->fetchAll();

foreach ($horarios as &$h) {
    $h['dia_semana'] = (int) $h['dia_semana'];
    $h['cerrado']    = (bool) $h['cerrado'];
}

echo json_encode(array_values($horarios), JSON_UNESCAPED_UNICODE);
