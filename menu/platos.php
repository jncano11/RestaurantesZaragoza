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

$stmt = $pdo->prepare("
    SELECT p.*, COALESCE(mc.nombre,'Otros') AS categoria_nombre
    FROM menu_platos p
    LEFT JOIN menu_categorias mc ON mc.id = p.categoria_id
    WHERE p.restaurante_id = ?
    ORDER BY mc.orden, p.nombre
");
$stmt->execute([$restaurante_id]);
echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
