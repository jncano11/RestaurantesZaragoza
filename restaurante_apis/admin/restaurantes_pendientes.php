<?php
// ============================================================
//  GET /admin/restaurantes_pendientes.php
//  Lista restaurantes con solicitado=1 y aprobado=0
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$pdo  = getDB();
$stmt = $pdo->query("
    SELECT
        r.id            AS restaurante_id,
        r.nombre,
        r.descripcion,
        r.direccion,
        r.categoria,
        r.telefono,
        r.usuario_id,
        r.solicitado,
        r.aprobado,
        u.nombre        AS nombre_propietario,
        u.email         AS email_propietario
    FROM restaurantes r
    JOIN usuarios u ON u.id = r.usuario_id
    WHERE r.solicitado = 1 AND r.aprobado = 0
    ORDER BY r.id DESC
");

$restaurantes = $stmt->fetchAll();
foreach ($restaurantes as &$r) {
    $r['restaurante_id'] = (int)$r['restaurante_id'];
    $r['usuario_id']     = (int)$r['usuario_id'];
    $r['solicitado']     = (int)$r['solicitado'];
    $r['aprobado']       = (int)$r['aprobado'];
}

echo json_encode(array_values($restaurantes), JSON_UNESCAPED_UNICODE);
