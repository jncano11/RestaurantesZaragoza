<?php
// ============================================================
//  GET /restaurantes/mi_restaurante.php?usuario_id=5
//  Returns: el restaurante cuyo usuario_id coincide
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$usuarioId = (int)($_GET['usuario_id'] ?? 0);
if ($usuarioId <= 0) {
    jsonResponse(['success' => false, 'message' => 'usuario_id inválido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("
    SELECT
        r.id                                        AS restaurante_id,
        r.nombre, r.descripcion, r.direccion,
        r.latitud, r.longitud, r.telefono,
        r.email_contacto,
        r.categoria,
        CONCAT(r.precio_medio, '€')                 AS precio_medio,
        r.aforo_total, r.usuario_id, r.activo,
        COALESCE(ROUND(AVG(v.puntuacion), 1), 0)    AS rating_global,
        COUNT(DISTINCT v.id)                        AS num_valoraciones
    FROM restaurantes r
    LEFT JOIN valoraciones v ON v.restaurante_id = r.id
    WHERE r.usuario_id = ?
    GROUP BY r.id
    LIMIT 1
");
$stmt->execute([$usuarioId]);
$restaurante = $stmt->fetch();

if (!$restaurante) {
    jsonResponse(['success' => false, 'message' => 'No tienes ningún restaurante registrado'], 404);
}

$restaurante['restaurante_id']   = (int)$restaurante['restaurante_id'];
$restaurante['aforo_total']      = (int)$restaurante['aforo_total'];
$restaurante['usuario_id']       = (int)$restaurante['usuario_id'];
$restaurante['rating_global']    = (float)$restaurante['rating_global'];
$restaurante['num_valoraciones'] = (int)$restaurante['num_valoraciones'];

echo json_encode($restaurante, JSON_UNESCAPED_UNICODE);
