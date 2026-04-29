<?php
// ============================================================
//  GET /reservas/mis_reservas.php?usuario_id=5
//  Returns: array de reservas del usuario con nombre del restaurante
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

$usuarioId = (int)($_GET['usuario_id'] ?? 0);
if ($usuarioId <= 0) {
    jsonResponse(['success' => false, 'message' => 'usuario_id inválido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("
    SELECT
        res.id,
        res.usuario_id,
        res.restaurante_id,
        r.nombre        AS nombre_restaurante,
        res.fecha,
        res.hora,
        res.num_personas,
        res.estado,
        res.notas,
        res.fecha_creacion
    FROM reservas res
    JOIN restaurantes r ON r.id = res.restaurante_id
    WHERE res.usuario_id = ?
    ORDER BY res.fecha DESC, res.hora DESC
");
$stmt->execute([$usuarioId]);
$reservas = $stmt->fetchAll();

foreach ($reservas as &$r) {
    $r['id']             = (int)$r['id'];
    $r['usuario_id']     = (int)$r['usuario_id'];
    $r['restaurante_id'] = (int)$r['restaurante_id'];
    $r['num_personas']   = (int)$r['num_personas'];
}

echo json_encode(array_values($reservas), JSON_UNESCAPED_UNICODE);
