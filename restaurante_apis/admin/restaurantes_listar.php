<?php
// ============================================================
//  GET /admin/restaurantes_listar.php
//  Devuelve TODOS los restaurantes (activos e inactivos) para admin
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$pdo  = getDB();
$stmt = $pdo->query("
    SELECT
        r.id                                        AS restaurante_id,
        r.nombre, r.descripcion, r.direccion,
        r.latitud, r.longitud, r.telefono,
        r.email_contacto, r.categoria,
        CONCAT(r.precio_medio, '€')                 AS precio_medio,
        r.aforo_total, r.usuario_id, r.activo,
        COALESCE(ROUND(AVG(v.puntuacion), 1), 0)    AS rating_global,
        COUNT(DISTINCT v.id)                        AS num_valoraciones
    FROM restaurantes r
    LEFT JOIN valoraciones v ON v.restaurante_id = r.id
    GROUP BY r.id
    ORDER BY r.activo DESC, r.nombre ASC
");
$restaurantes = $stmt->fetchAll();

foreach ($restaurantes as &$r) {
    $r['restaurante_id']   = (int)$r['restaurante_id'];
    $r['aforo_total']      = (int)$r['aforo_total'];
    $r['usuario_id']       = (int)$r['usuario_id'];
    $r['activo']           = (int)$r['activo'];
    $r['rating_global']    = (float)$r['rating_global'];
    $r['num_valoraciones'] = (int)$r['num_valoraciones'];
}

echo json_encode(array_values($restaurantes), JSON_UNESCAPED_UNICODE);
