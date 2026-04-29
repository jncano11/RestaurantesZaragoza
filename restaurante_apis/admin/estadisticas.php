<?php
// ============================================================
//  GET /admin/estadisticas.php
//  Returns: { total_usuarios, total_restaurantes, total_reservas,
//             reservas_hoy, ingresos_propinas }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

$pdo = getDB();

$stats = [];

// Totales
$stats['total_usuarios']     = (int)$pdo->query("SELECT COUNT(*) FROM usuarios")->fetchColumn();
$stats['total_restaurantes'] = (int)$pdo->query("SELECT COUNT(*) FROM restaurantes WHERE activo = 1")->fetchColumn();
$stats['total_reservas']     = (int)$pdo->query("SELECT COUNT(*) FROM reservas")->fetchColumn();

// Reservas de hoy
$hoyStmt = $pdo->prepare("SELECT COUNT(*) FROM reservas WHERE fecha = CURDATE() AND estado IN ('pendiente','confirmada')");
$hoyStmt->execute();
$stats['reservas_hoy'] = (int)$hoyStmt->fetchColumn();

// Ingresos por propinas
$propinasStmt = $pdo->query("SELECT COALESCE(SUM(cantidad), 0) FROM propinas");
$stats['ingresos_propinas'] = (float)$propinasStmt->fetchColumn();

// Reservas por estado (bonus)
$estadoStmt = $pdo->query("
    SELECT estado, COUNT(*) AS total
    FROM reservas
    GROUP BY estado
");
$stats['reservas_por_estado'] = $estadoStmt->fetchAll();

// Últimas reservas (para el dashboard)
$ultimasStmt = $pdo->query("
    SELECT res.id, r.nombre AS nombre_restaurante,
           CONCAT(u.nombre,' ',u.apellidos) AS nombre_usuario,
           res.fecha, res.hora, res.estado
    FROM reservas res
    JOIN restaurantes r ON r.id = res.restaurante_id
    JOIN usuarios u     ON u.id = res.usuario_id
    ORDER BY res.fecha_creacion DESC
    LIMIT 5
");
$stats['ultimas_reservas'] = $ultimasStmt->fetchAll();

echo json_encode($stats, JSON_UNESCAPED_UNICODE);
