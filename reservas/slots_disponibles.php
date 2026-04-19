<?php
// ============================================================
// ARCHIVO: restaurantes_api/reservas/slots_disponibles.php
// Devuelve los slots horarios disponibles para una fecha/restaurante,
// descontando aforo ya reservado. La app usa esto para mostrar
// solo las horas con plazas libres.
// ============================================================
header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");
 
require_once '../config/db.php';
 
$restaurante_id = intval($_GET['restaurante_id'] ?? 0);
$fecha          = $_GET['fecha']          ?? '';
$num_personas   = intval($_GET['num_personas'] ?? 1);
 
if (!$restaurante_id || !$fecha) {
    echo json_encode([]);
    exit;
}
 
// 1. Obtener aforo total del restaurante
$stmt = $pdo->prepare("SELECT aforo_total FROM restaurantes WHERE id = ?");
$stmt->execute([$restaurante_id]);
$aforo_total = intval($stmt->fetchColumn() ?: 50);
 
// 2. Obtener el día de la semana de la fecha solicitada (0=Lunes…6=Domingo)
$ts         = strtotime($fecha);
$dia_semana = (int)date('N', $ts) - 1;   // N devuelve 1=Lun…7=Dom → 0..6
 
// 3. Obtener horarios del restaurante para ese día
$stmt = $pdo->prepare("
    SELECT hora_apertura, hora_cierre, cerrado
    FROM horarios
    WHERE restaurante_id = ? AND dia_semana = ?
    ORDER BY hora_apertura
");
$stmt->execute([$restaurante_id, $dia_semana]);
$turnos = $stmt->fetchAll(PDO::FETCH_ASSOC);
 
// Si el restaurante está cerrado ese día o no tiene horarios, devolvemos vacío
if (empty($turnos) || $turnos[0]['cerrado']) {
    echo json_encode([]);
    exit;
}
 
// 4. Generar todos los slots posibles a intervalos de 30 minutos dentro de cada turno
$slots_posibles = [];
foreach ($turnos as $turno) {
    if ($turno['cerrado']) continue;
    $apertura = strtotime($turno['hora_apertura']);
    $cierre   = strtotime($turno['hora_cierre']);
    // El último slot debe permitir al menos 30 min antes del cierre
    $limite   = $cierre - 30 * 60;
    $t        = $apertura;
    while ($t <= $limite) {
        $slots_posibles[] = date('H:i', $t);
        $t += 30 * 60;   // Intervalos de 30 minutos
    }
}
 
if (empty($slots_posibles)) {
    echo json_encode([]);
    exit;
}
 
// 5. Para cada slot, calcular cuántas personas ya están reservadas
$result = [];
foreach ($slots_posibles as $hora) {
    // Reservas confirmadas o pendientes en esa hora
    $stmt = $pdo->prepare("
        SELECT COALESCE(SUM(num_personas), 0) AS ocupadas
        FROM reservas
        WHERE restaurante_id = ?
          AND fecha = ?
          AND hora  = ?
          AND estado IN ('pendiente', 'confirmada')
    ");
    $stmt->execute([$restaurante_id, $fecha, $hora . ':00']);
    $ocupadas     = intval($stmt->fetchColumn());
    $plazas_libres = $aforo_total - $ocupadas;
 
    // Solo incluimos el slot si hay suficientes plazas para el grupo
    if ($plazas_libres >= $num_personas) {
        $result[] = [
            'hora'          => $hora,
            'plazas_libres' => $plazas_libres
        ];
    }
}
 
echo json_encode($result);