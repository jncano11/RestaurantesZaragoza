<?php
// ============================================================
//  GET /admin/usuarios_listar.php
//  Returns: array de todos los usuarios
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

$pdo  = getDB();
$stmt = $pdo->query("
    SELECT id, nombre, apellidos, email, telefono, rol, activo,
           DATE_FORMAT(fecha_registro, '%d/%m/%Y') AS fecha_registro
    FROM usuarios
    ORDER BY fecha_registro DESC
");
$usuarios = $stmt->fetchAll();

foreach ($usuarios as &$u) {
    $u['id']     = (int)$u['id'];
    $u['activo'] = (bool)$u['activo'];
}

echo json_encode(array_values($usuarios), JSON_UNESCAPED_UNICODE);
