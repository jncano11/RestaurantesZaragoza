<?php
// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/estadisticas.php
// Estadísticas de un restaurante específico
// ============================================================

header("Content-Type: application/json");
header("Access-Control-Allow-Origin: *");
require_once '../config/db.php';

$restaurante_id = intval($_GET['restaurante_id'] ?? 0);
if (!$restaurante_id) { echo json_encode([]); exit; }

// Total reservas
$stmt = $pdo->prepare("SELECT COUNT(*) FROM reservas WHERE restaurante_id=?");
$stmt->execute([$restaurante_id]);
$total_reservas = $stmt->fetchColumn();

// Reservas hoy
$stmt = $pdo->prepare("SELECT COUNT(*) FROM reservas WHERE restaurante_id=? AND fecha=CURDATE() AND estado IN('pendiente','confirmada')");
$stmt->execute([$restaurante_id]);
$reservas_hoy = $stmt->fetchColumn();

// Reservas semana
$stmt = $pdo->prepare("SELECT COUNT(*) FROM reservas WHERE restaurante_id=? AND fecha BETWEEN CURDATE() AND DATE_ADD(CURDATE(),INTERVAL 7 DAY)");
$stmt->execute([$restaurante_id]);
$reservas_semana = $stmt->fetchColumn();

// Total propinas
$stmt = $pdo->prepare("SELECT COALESCE(SUM(cantidad),0) FROM propinas WHERE restaurante_id=?");
$stmt->execute([$restaurante_id]);
$total_propinas = floatval($stmt->fetchColumn());

// Valoración media
$stmt = $pdo->prepare("SELECT COALESCE(AVG(puntuacion),0), COUNT(*) FROM valoraciones WHERE restaurante_id=?");
$stmt->execute([$restaurante_id]);
$row = $stmt->fetch(PDO::FETCH_NUM);
$valoracion_media = round(floatval($row[0]), 1);
$num_valoraciones = intval($row[1]);

echo json_encode([
    'restaurante_id'   => $restaurante_id,
    'total_reservas'   => intval($total_reservas),
    'reservas_hoy'     => intval($reservas_hoy),
    'reservas_semana'  => intval($reservas_semana),
    'total_propinas'   => $total_propinas,
    'valoracion_media' => $valoracion_media,
    'num_valoraciones' => $num_valoraciones
]);