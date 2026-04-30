<?php
// ============================================================
//  POST /restaurantes/solicitar_aprobacion.php
//  Body: { "restaurante_id": 5 }
//  Marca el restaurante como solicitado (pendiente de revisión)
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body           = getJsonBody();
$restaurante_id = (int)($body['restaurante_id'] ?? 0);

if ($restaurante_id <= 0) {
    jsonResponse(['success' => false, 'message' => 'restaurante_id inválido'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("UPDATE restaurantes SET solicitado = 1 WHERE id = ? AND aprobado = 0");
$ok   = $stmt->execute([$restaurante_id]);

jsonResponse([
    'success' => $ok && $stmt->rowCount() > 0,
    'message' => ($ok && $stmt->rowCount() > 0)
        ? 'Solicitud enviada. Un administrador la revisará pronto.'
        : 'No se pudo enviar la solicitud.'
]);
