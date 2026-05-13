<?php
// ============================================================
// ARCHIVO: restaurantes_api/menu/platos.php
// Lista platos de un restaurante con nombre de categoría
// ============================================================

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
require_once '../config/db.php';

$restaurante_id = intval($_GET['restaurante_id'] ?? 0);
if (!$restaurante_id) { echo json_encode([]); exit; }

$pdo  = getDB();
$stmt = $pdo->prepare("
    SELECT
        p.id, p.categoria_id, p.restaurante_id,
        p.nombre,
        COALESCE(p.descripcion, '') AS descripcion,
        p.precio,
        COALESCE(p.foto_url, '')   AS foto_url,
        p.disponible,
        COALESCE(p.alergenos, '')  AS alergenos,
        COALESCE(mc.nombre, 'Otros') AS categoria_nombre
    FROM menu_platos p
    LEFT JOIN menu_categorias mc ON mc.id = p.categoria_id
    WHERE p.restaurante_id = ?
    ORDER BY mc.orden, p.nombre
");
$stmt->execute([$restaurante_id]);
echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
