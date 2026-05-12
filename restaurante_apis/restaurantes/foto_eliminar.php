<?php
// POST /restaurantes/foto_eliminar.php
// Body JSON: { "restaurante_id": int, "nombre_foto": "rest_1_xxx.jpg" }
require_once '../config/cors.php';
require_once '../config/db.php';

$body          = getJsonBody();
$restauranteId = (int)($body['restaurante_id'] ?? 0);
$nombreFoto    = trim($body['nombre_foto'] ?? '');

if ($restauranteId <= 0 || empty($nombreFoto)) {
    jsonResponse(['success' => false, 'message' => 'Datos incompletos'], 400);
}

// Sanitizar nombre: solo permitir caracteres seguros
if (!preg_match('/^[a-zA-Z0-9_.\-]+$/', $nombreFoto)) {
    jsonResponse(['success' => false, 'message' => 'Nombre de archivo inválido'], 400);
}

$pdo = getDB();

$check = $pdo->prepare("SELECT id, es_portada FROM fotos_restaurante WHERE restaurante_id = ? AND url_foto = ?");
$check->execute([$restauranteId, $nombreFoto]);
$foto = $check->fetch();
if (!$foto) jsonResponse(['success' => false, 'message' => 'Foto no encontrada'], 404);

$eraPortada = (bool)$foto['es_portada'];

$del = $pdo->prepare("DELETE FROM fotos_restaurante WHERE id = ?");
$del->execute([$foto['id']]);

$ruta = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'uploads' . DIRECTORY_SEPARATOR . $nombreFoto;
if (file_exists($ruta)) unlink($ruta);

// Si era portada, promover la siguiente foto
if ($eraPortada) {
    $next = $pdo->prepare("SELECT id FROM fotos_restaurante WHERE restaurante_id = ? ORDER BY id ASC LIMIT 1");
    $next->execute([$restauranteId]);
    $nextId = $next->fetchColumn();
    if ($nextId) {
        $pdo->prepare("UPDATE fotos_restaurante SET es_portada = 1 WHERE id = ?")->execute([$nextId]);
    }
}

jsonResponse(['success' => true, 'message' => 'Foto eliminada correctamente']);
