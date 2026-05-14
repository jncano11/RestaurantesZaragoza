<?php
// ============================================================
// POST /restaurantes/actualizar_aforo_precio.php
// Body: { restaurante_id, aforo_total, precio_medio }
// Permite al restaurante actualizar su aforo y precio medio
// sin tocar el resto de campos
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$data         = getJsonBody();
$id           = intval($data['restaurante_id'] ?? 0);
$aforo        = intval($data['aforo_total']    ?? 0);
$precioMedio  = floatval($data['precio_medio'] ?? 0);

if ($id <= 0) {
    jsonResponse(['success' => false, 'message' => 'restaurante_id inválido'], 400);
}
if ($aforo <= 0) {
    jsonResponse(['success' => false, 'message' => 'El aforo debe ser mayor que 0'], 400);
}
if ($precioMedio < 0) {
    jsonResponse(['success' => false, 'message' => 'Precio medio inválido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("UPDATE restaurantes SET aforo_total = ?, precio_medio = ? WHERE id = ?");
$ok   = $stmt->execute([$aforo, $precioMedio, $id]);

if (!$ok) {
    jsonResponse(['success' => false, 'message' => 'Error al actualizar'], 500);
}

jsonResponse([
    'success'      => true,
    'message'      => 'Datos actualizados correctamente',
    'aforo_total'  => $aforo,
    'precio_medio' => $precioMedio
]);
