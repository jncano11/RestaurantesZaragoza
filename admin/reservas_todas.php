<?php
// ============================================================
//  GET /admin/reservas_todas.php
//  Params opcionales: ?estado=pendiente  &fecha=2025-04-10
//  Returns: todas las reservas del sistema
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

$pdo    = getDB();
$estado = trim($_GET['estado'] ?? '');
$fecha  = trim($_GET['fecha']  ?? '');

$sql = "
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
    JOIN usuarios u      ON u.id = res.usuario_id
    JOIN restaurantes r  ON r.id = res.restaurante_id
    WHERE 1=1
";

$params = [];
if (!empty($estado)) {
    $sql    .= " AND res.estado = ?";
    $params[] = $estado;
}
if (!empty($fecha)) {
    $sql    .= " AND res.fecha = ?";
    $params[] = $fecha;
}
$sql .= " ORDER BY res.fecha DESC, res.hora ASC LIMIT 200";

$stmt = $pdo->prepare($sql);
$stmt->execute($params);
$reservas = $stmt->fetchAll();

foreach ($reservas as &$r) {
    $r['id']             = (int)$r['id'];
    $r['usuario_id']     = (int)$r['usuario_id'];
    $r['restaurante_id'] = (int)$r['restaurante_id'];
    $r['num_personas']   = (int)$r['num_personas'];
}

echo json_encode(array_values($reservas), JSON_UNESCAPED_UNICODE);
