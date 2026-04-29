<?php
// ============================================================
// ARCHIVO: restaurantes_api/propinas/por_restaurante.php
// ============================================================

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
require_once '../config/db.php';

$restaurante_id = intval($_GET['restaurante_id'] ?? 0);
if (!$restaurante_id) { echo json_encode([]); exit; }

$stmt = $pdo->prepare("SELECT * FROM propinas WHERE restaurante_id=? ORDER BY fecha DESC");
$stmt->execute([$restaurante_id]);
echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));