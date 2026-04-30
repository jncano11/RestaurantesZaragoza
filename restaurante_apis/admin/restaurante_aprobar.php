<?php
// ============================================================
//  POST /admin/restaurante_aprobar.php
//  Body: { "restaurante_id": 5, "admin_id": 1, "accion": "aprobar"|"rechazar" }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body           = getJsonBody();
$restaurante_id = (int)($body['restaurante_id'] ?? 0);
$admin_id       = (int)($body['admin_id']       ?? 0);
$accion         = trim($body['accion']          ?? '');

if ($restaurante_id <= 0 || !in_array($accion, ['aprobar', 'rechazar'])) {
    jsonResponse(['success' => false, 'message' => 'Parámetros inválidos'], 400);
}

$pdo = getDB();

if ($accion === 'aprobar') {
    $stmt = $pdo->prepare("
        UPDATE restaurantes
        SET aprobado = 1, solicitado = 1, aprobado_por = ?
        WHERE id = ?
    ");
    $ok = $stmt->execute([$admin_id, $restaurante_id]);
    $msg = 'Restaurante aprobado correctamente.';
} else {
    // Rechazar: vuelve a borrador (solicitado=0) para que el propietario pueda corregir y reenviar
    $stmt = $pdo->prepare("
        UPDATE restaurantes
        SET solicitado = 0, aprobado = 0, aprobado_por = NULL
        WHERE id = ?
    ");
    $ok = $stmt->execute([$restaurante_id]);
    $msg = 'Solicitud rechazada. El propietario podrá corregirla y reenviarla.';
}

jsonResponse(['success' => $ok, 'message' => $msg]);
