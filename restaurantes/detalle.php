<?php
// GET /restaurantes/detalle.php?id=1
require_once '../config/cors.php';
require_once '../config/db.php';

$id = (int)($_GET['id'] ?? 0);
if ($id <= 0) jsonResponse(['success' => false, 'message' => 'ID inválido'], 400);

$pdo = getDB();
$stmt = $pdo->prepare("
    SELECT r.id, r.nombre, r.descripcion, r.direccion, r.ciudad,
           r.latitud, r.longitud, r.telefono, r.email_contacto,
           r.categoria, r.precio_medio, r.aforo_total, r.usuario_id, r.activo,
           COALESCE(ROUND(AVG(v.puntuacion),1),0) AS rating_global,
           COUNT(DISTINCT v.id) AS num_valoraciones
    FROM restaurantes r
    LEFT JOIN valoraciones v ON v.restaurante_id = r.id
    WHERE r.id = ? AND r.activo = 1
    GROUP BY r.id
");
$stmt->execute([$id]);
$restaurante = $stmt->fetch();
if (!$restaurante) jsonResponse(['success' => false, 'message' => 'Restaurante no encontrado'], 404);

// Fotos (array de URLs para el carrusel)
$fStmt = $pdo->prepare("SELECT url_foto FROM fotos_restaurante WHERE restaurante_id=? ORDER BY es_portada DESC, id ASC");
$fStmt->execute([$id]);
$restaurante['fotos'] = array_column($fStmt->fetchAll(), 'url_foto');

// Horarios
$hStmt = $pdo->prepare("SELECT dia_semana, hora_apertura, hora_cierre, cerrado FROM horarios WHERE restaurante_id=? ORDER BY dia_semana");
$hStmt->execute([$id]);
$restaurante['horarios'] = $hStmt->fetchAll();

// Tipos correctos
$restaurante['id']               = (int)$restaurante['id'];
$restaurante['precio_medio']     = (float)$restaurante['precio_medio'];
$restaurante['rating_global']    = (float)$restaurante['rating_global'];
$restaurante['num_valoraciones'] = (int)$restaurante['num_valoraciones'];
$restaurante['aforo_total']      = (int)$restaurante['aforo_total'];
$restaurante['usuario_id']       = (int)$restaurante['usuario_id'];
$restaurante['latitud']          = (float)$restaurante['latitud'];
$restaurante['longitud']         = (float)$restaurante['longitud'];

echo json_encode($restaurante, JSON_UNESCAPED_UNICODE);