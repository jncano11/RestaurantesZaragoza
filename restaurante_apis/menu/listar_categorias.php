<?php
header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
require_once '../config/db.php';

$restaurante_id = intval($_GET['restaurante_id'] ?? 0);
if (!$restaurante_id) { echo json_encode([]); exit; }

$pdo  = getDB();
$stmt = $pdo->prepare("SELECT id, nombre, orden FROM menu_categorias WHERE restaurante_id = ? ORDER BY orden, nombre");
$stmt->execute([$restaurante_id]);
echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
