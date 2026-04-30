<?php
// ============================================================
//  POST /restaurantes/completar_restaurante.php
//  Completa los datos opcionales del restaurante (aforo,
//  email de contacto y precio medio) buscando por usuario_id.
//  Body: { "usuario_id": 5, "aforo_total": 40,
//          "email_contacto": "...", "precio_medio": 15 }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body        = getJsonBody();
$usuario_id  = intval($body['usuario_id']     ?? 0);
$aforo       = intval($body['aforo_total']    ?? 0);
$email       = trim($body['email_contacto']   ?? '');
$precio      = floatval($body['precio_medio'] ?? 0);

if ($usuario_id <= 0) {
    jsonResponse(['success' => false, 'message' => 'usuario_id inválido'], 400);
}

$pdo = getDB();

// Comprobar si ya tiene restaurante
$check = $pdo->prepare("SELECT id FROM restaurantes WHERE usuario_id = ?");
$check->execute([$usuario_id]);
$existente = $check->fetch();

if ($existente) {
    // Actualizar los campos que faltan
    $stmt = $pdo->prepare("
        UPDATE restaurantes
        SET aforo_total = ?, email_contacto = ?, precio_medio = ?
        WHERE usuario_id = ?
    ");
    $ok = $stmt->execute([$aforo, $email, $precio, $usuario_id]);
} else {
    // Obtener nombre del usuario para usarlo como nombre del restaurante por defecto
    $uStmt = $pdo->prepare("SELECT nombre FROM usuarios WHERE id = ?");
    $uStmt->execute([$usuario_id]);
    $usuario = $uStmt->fetch();
    $nombreDefault = ($usuario['nombre'] ?? 'Mi Restaurante') . ' - Restaurante';

    $stmt = $pdo->prepare("
        INSERT INTO restaurantes
            (usuario_id, nombre, direccion, aforo_total, email_contacto, precio_medio)
        VALUES
            (?, ?, 'Zaragoza', ?, ?, ?)
    ");
    $ok = $stmt->execute([$usuario_id, $nombreDefault, $aforo, $email, $precio]);
}

jsonResponse([
    'success' => $ok,
    'message' => $ok ? 'Datos completados correctamente' : 'Error al guardar los datos'
]);
