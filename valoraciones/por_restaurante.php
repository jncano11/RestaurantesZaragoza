<?php
// ============================================================
//  GET /valoraciones/por_restaurante.php?restaurante_id=1
//  Returns: array de valoraciones con nombre de usuario
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
        v.id,
        v.usuario_id,
        v.restaurante_id,
        CONCAT(u.nombre, ' ', u.apellidos)  AS nombre_usuario,
        v.puntuacion,
        v.comentario,
        DATE_FORMAT(v.fecha, '%d/%m/%Y')    AS fecha
    FROM valoraciones v
    JOIN usuarios u ON u.id = v.usuario_id
    WHERE v.restaurante_id = ?
    ORDER BY v.fecha DESC
");
$stmt->execute([$restauranteId]);
$valoraciones = $stmt->fetchAll();

foreach ($valoraciones as &$v) {
    $v['id']             = (int)$v['id'];
    $v['usuario_id']     = (int)$v['usuario_id'];
    $v['restaurante_id'] = (int)$v['restaurante_id'];
    $v['puntuacion']     = (int)$v['puntuacion'];
}

echo json_encode(array_values($valoraciones), JSON_UNESCAPED_UNICODE);
