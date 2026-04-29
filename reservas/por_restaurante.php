<?php
// ============================================================
//  GET /reservas/por_restaurante.php?restaurante_id=1
//  Returns: array de reservas del restaurante con nombre de usuario
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

$restauranteId = (int)($_GET['restaurante_id'] ?? 0);
if ($restauranteId <= 0) {
    jsonResponse(['success' => false, 'message' => 'restaurante_id inválido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("
    SELECT
        res.id,
        res.usuario_id,
        res.restaurante_id,
        CONCAT(u.nombre, ' ', u.apellidos)  AS nombre_usuario,
        r.nombre                            AS nombre_restaurante,
        res.fecha,
        res.hora,
        res.num_personas,
        res.estado,
        res.notas,
        res.fecha_creacion
    FROM reservas res
    JOIN usuarios u      ON u.id  = res.usuario_id
    JOIN restaurantes r  ON r.id  = res.restaurante_id
    WHERE res.restaurante_id = ?
    ORDER BY res.fecha DESC, res.hora ASC
");
$stmt->execute([$restauranteId]);
$reservas = $stmt->fetchAll();

foreach ($reservas as &$r) {
    $r['id']             = (int)$r['id'];
    $r['usuario_id']     = (int)$r['usuario_id'];
    $r['restaurante_id'] = (int)$r['restaurante_id'];
    $r['num_personas']   = (int)$r['num_personas'];
}

echo json_encode(array_values($reservas), JSON_UNESCAPED_UNICODE);
