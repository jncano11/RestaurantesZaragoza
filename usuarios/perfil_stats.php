<?php
// ============================================================
//  GET /usuarios/perfil_stats.php?usuario_id=5
//  Devuelve estadísticas del usuario: total reservas, restaurantes únicos, etc.
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

$usuarioId = (int)($_GET['usuario_id'] ?? 0);
if ($usuarioId <= 0) {
    jsonResponse(['success' => false, 'message' => 'usuario_id inválido'], 400);
}

$pdo = getDB();

// Datos del usuario
$uStmt = $pdo->prepare("SELECT id, nombre, apellidos, email, telefono, rol, foto_perfil FROM usuarios WHERE id = ?");
$uStmt->execute([$usuarioId]);
$usuario = $uStmt->fetch();
if (!$usuario) {
    jsonResponse(['success' => false, 'message' => 'Usuario no encontrado'], 404);
}

// Stats de reservas
$sStmt = $pdo->prepare("
    SELECT
        COUNT(*)                                                   AS total_reservas,
        COUNT(DISTINCT restaurante_id)                             AS restaurantes_visitados,
        COALESCE(SUM(num_personas), 0)                             AS total_personas,
        COUNT(CASE WHEN estado = 'confirmada'  THEN 1 END)         AS confirmadas,
        COUNT(CASE WHEN estado = 'pendiente'   THEN 1 END)         AS pendientes,
        COUNT(CASE WHEN estado = 'cancelada'   THEN 1 END)         AS canceladas,
        COUNT(CASE WHEN estado = 'completada'  THEN 1 END)         AS completadas
    FROM reservas WHERE usuario_id = ?
");
$sStmt->execute([$usuarioId]);
$stats = $sStmt->fetch();

// Valoraciones del usuario
$vStmt = $pdo->prepare("SELECT COUNT(*) AS total_valoraciones, COALESCE(ROUND(AVG(puntuacion),1),0) AS media_puntuacion FROM valoraciones WHERE usuario_id = ?");
$vStmt->execute([$usuarioId]);
$valStats = $vStmt->fetch();

// Últimas 3 reservas
$rStmt = $pdo->prepare("
    SELECT res.id, r.nombre AS nombre_restaurante, res.fecha, res.hora, res.num_personas, res.estado
    FROM reservas res
    JOIN restaurantes r ON r.id = res.restaurante_id
    WHERE res.usuario_id = ?
    ORDER BY res.fecha DESC LIMIT 3
");
$rStmt->execute([$usuarioId]);
$ultimasReservas = $rStmt->fetchAll();

echo json_encode([
    'usuario'          => $usuario,
    'total_reservas'   => (int)$stats['total_reservas'],
    'restaurantes_visitados' => (int)$stats['restaurantes_visitados'],
    'total_personas'   => (int)$stats['total_personas'],
    'confirmadas'      => (int)$stats['confirmadas'],
    'pendientes'       => (int)$stats['pendientes'],
    'canceladas'       => (int)$stats['canceladas'],
    'completadas'      => (int)$stats['completadas'],
    'total_valoraciones' => (int)$valStats['total_valoraciones'],
    'media_puntuacion' => (float)$valStats['media_puntuacion'],
    'ultimas_reservas' => $ultimasReservas
], JSON_UNESCAPED_UNICODE);
