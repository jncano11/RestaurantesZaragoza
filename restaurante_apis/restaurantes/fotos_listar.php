<?php
// GET /restaurantes/fotos_listar.php?restaurante_id=X
require_once '../config/cors.php';
require_once '../config/db.php';

$restauranteId = (int)($_GET['restaurante_id'] ?? 0);
if ($restauranteId <= 0) jsonResponse(['success' => false, 'message' => 'restaurante_id inválido'], 400);

$pdo = getDB();
$stmt = $pdo->prepare("SELECT url_foto, descripcion, es_portada FROM fotos_restaurante WHERE restaurante_id = ? ORDER BY es_portada DESC, id ASC");
$stmt->execute([$restauranteId]);
$fotos = $stmt->fetchAll();

$proto   = $_SERVER['HTTP_X_FORWARDED_PROTO']
    ?? (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? 'https' : 'http');
$baseUrl = $proto . '://' . $_SERVER['HTTP_HOST'] . '/restaurantes_api/uploads/';

foreach ($fotos as &$f) {
    $f['url_foto']   = $baseUrl . $f['url_foto'];
    $f['es_portada'] = (bool)$f['es_portada'];
}

jsonResponse(['success' => true, 'fotos' => $fotos]);
