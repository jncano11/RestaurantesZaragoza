<?php
// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/horarios_guardar.php
// Guarda/actualiza los horarios de un restaurante.
// Recibe JSON: { restaurante_id, horarios: "[{dia,cerrado,...}]" }
// ============================================================

header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once '../config/db.php';

$data           = json_decode(file_get_contents("php://input"), true);
$restaurante_id = intval($data['restaurante_id'] ?? 0);
$horarios_json  = $data['horarios'] ?? '[]';

if (!$restaurante_id) {
    echo json_encode(['success' => false, 'message' => 'ID requerido']);
    exit;
}

$horarios = json_decode($horarios_json, true);
if (!is_array($horarios)) {
    echo json_encode(['success' => false, 'message' => 'Formato inválido']);
    exit;
}

// Borrar horarios existentes del restaurante
$del = $pdo->prepare("DELETE FROM horarios WHERE restaurante_id = ?");
$del->execute([$restaurante_id]);

// Insertar nuevos horarios
$ins = $pdo->prepare("INSERT INTO horarios (restaurante_id, dia_semana, hora_apertura, hora_cierre, cerrado) VALUES (?, ?, ?, ?, ?)");

foreach ($horarios as $h) {
    $dia     = intval($h['dia'] ?? 0);
    $cerrado = ($h['cerrado'] ?? false) ? 1 : 0;

    if ($cerrado) {
        $ins->execute([$restaurante_id, $dia, null, null, 1]);
    } else {
        // Turno comida
        if (!empty($h['comida_apertura']) && !empty($h['comida_cierre'])) {
            $ins->execute([$restaurante_id, $dia, $h['comida_apertura'], $h['comida_cierre'], 0]);
        }
        // Turno cena
        if (!empty($h['cena_apertura']) && !empty($h['cena_cierre'])) {
            $ins->execute([$restaurante_id, $dia, $h['cena_apertura'], $h['cena_cierre'], 0]);
        }
    }
}

echo json_encode(['success' => true, 'message' => 'Horarios guardados']);



// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/mis_restaurantes.php
// Devuelve los restaurantes del usuario logueado
// ============================================================

header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");

require_once '../config/db.php';

$usuario_id = intval($_GET['usuario_id'] ?? 0);
if (!$usuario_id) { echo json_encode([]); exit; }

$stmt = $pdo->prepare("SELECT * FROM restaurantes WHERE usuario_id = ? ORDER BY fecha_registro DESC");
$stmt->execute([$usuario_id]);
echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));



// ============================================================
// ARCHIVO: restaurantes_api/reservas/crear.php
// ACTUALIZADO: valida que no se supere el aforo en ese slot
// ============================================================

header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");

require_once '../config/db.php';

$data           = json_decode(file_get_contents("php://input"), true);
$usuario_id     = intval($data['usuario_id']     ?? 0);
$restaurante_id = intval($data['restaurante_id'] ?? 0);
$fecha          = $data['fecha']          ?? '';
$hora           = $data['hora']           ?? '';
$num_personas   = intval($data['num_personas']   ?? 1);
$notas          = $data['notas']          ?? '';

if (!$usuario_id || !$restaurante_id || !$fecha || !$hora) {
    echo json_encode(['success' => false, 'message' => 'Faltan campos obligatorios']);
    exit;
}

// Validar que no se supera el aforo
$stmt = $pdo->prepare("SELECT aforo_total FROM restaurantes WHERE id = ?");
$stmt->execute([$restaurante_id]);
$aforo_total = intval($stmt->fetchColumn() ?: 50);

$stmt = $pdo->prepare("
    SELECT COALESCE(SUM(num_personas), 0)
    FROM reservas
    WHERE restaurante_id = ? AND fecha = ? AND hora = ?
      AND estado IN ('pendiente', 'confirmada')
");
$stmt->execute([$restaurante_id, $fecha, $hora]);
$ya_reservadas = intval($stmt->fetchColumn());

if ($ya_reservadas + $num_personas > $aforo_total) {
    echo json_encode([
        'success' => false,
        'message' => "No hay plazas suficientes. Solo quedan " . max(0, $aforo_total - $ya_reservadas) . " plazas disponibles."
    ]);
    exit;
}

// Crear la reserva
$stmt = $pdo->prepare("INSERT INTO reservas (usuario_id, restaurante_id, fecha, hora, num_personas, estado, notas) VALUES (?, ?, ?, ?, ?, 'pendiente', ?)");
$ok = $stmt->execute([$usuario_id, $restaurante_id, $fecha, $hora, $num_personas, $notas]);

echo json_encode([
    'success' => $ok,
    'message' => $ok ? 'Reserva creada con éxito' : 'Error al crear la reserva'
]);



// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/listar.php
// ACTUALIZADO: incluye plazas_disponibles_hoy desde la vista
// ============================================================

header("Content-Type: application/json; charset=utf-8");
header("Access-Control-Allow-Origin: *");

require_once '../config/db.php';

$categoria  = $_GET['categoria']  ?? null;
$busqueda   = $_GET['q']          ?? null;
$precio_max = isset($_GET['precio_max']) ? floatval($_GET['precio_max']) : null;

// Usamos la vista vista_restaurantes que ya calcula plazas_disponibles_hoy
$sql    = "SELECT * FROM vista_restaurantes WHERE 1=1";
$params = [];

if ($categoria) { $sql .= " AND categoria = ?"; $params[] = $categoria; }
if ($busqueda)  { $sql .= " AND (nombre LIKE ? OR categoria LIKE ?)"; $params[] = "%$busqueda%"; $params[] = "%$busqueda%"; }
if ($precio_max) { $sql .= " AND precio_medio <= ?"; $params[] = $precio_max; }

$sql .= " ORDER BY valoracion_media DESC, nombre ASC";

$stmt = $pdo->prepare($sql);
$stmt->execute($params);
echo json_encode($stmt->fetchAll(PDO::FETCH_ASSOC));
