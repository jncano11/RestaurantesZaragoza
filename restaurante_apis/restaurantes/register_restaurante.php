<?php
// ============================================================
//  POST /restaurantes/register_restaurante.php
//  Registra un usuario con rol 'restaurante' y crea su
//  restaurante asociado pendiente de aprobación (aprobado=0).
//
//  Body: {
//    nombre, apellidos, email, contrasena, telefono,
//    nombre_restaurante, descripcion, direccion,
//    categoria, telefono_restaurante
//  }
//  Returns: { success, message }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body = getJsonBody();

// ── Datos del usuario ──────────────────────────────────────
$nombre     = trim($body['nombre']     ?? '');
$apellidos  = trim($body['apellidos']  ?? '');
$email      = trim($body['email']      ?? '');
$contrasena = trim($body['contrasena'] ?? '');
$telefono   = trim($body['telefono']   ?? '');

// ── Datos del restaurante ──────────────────────────────────
$nombre_restaurante    = trim($body['nombre_restaurante']    ?? '');
$descripcion           = trim($body['descripcion']           ?? '');
$direccion             = trim($body['direccion']             ?? '');
$categoria             = trim($body['categoria']             ?? '');
$telefono_restaurante  = trim($body['telefono_restaurante']  ?? '');

// ── Validaciones ───────────────────────────────────────────
if (empty($nombre) || empty($email) || empty($contrasena)) {
    jsonResponse(['success' => false, 'message' => 'Nombre, email y contraseña son obligatorios'], 400);
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(['success' => false, 'message' => 'Email no válido'], 400);
}
if (strlen($contrasena) < 6) {
    jsonResponse(['success' => false, 'message' => 'La contraseña debe tener al menos 6 caracteres'], 400);
}
if (empty($nombre_restaurante) || empty($direccion)) {
    jsonResponse(['success' => false, 'message' => 'Nombre y dirección del restaurante son obligatorios'], 400);
}

$pdo = getDB();

// Comprobar email duplicado
$check = $pdo->prepare("SELECT id FROM usuarios WHERE email = ?");
$check->execute([$email]);
if ($check->fetch()) {
    jsonResponse(['success' => false, 'message' => 'El email ya está registrado'], 409);
}

// ── Transacción: crear usuario + restaurante ───────────────
try {
    $pdo->beginTransaction();

    $hash = password_hash($contrasena, PASSWORD_BCRYPT);
    $stmtUser = $pdo->prepare("
        INSERT INTO usuarios (nombre, apellidos, email, password_hash, telefono, rol)
        VALUES (?, ?, ?, ?, ?, 'restaurante')
    ");
    $stmtUser->execute([$nombre, $apellidos, $email, $hash, $telefono]);
    $usuario_id = $pdo->lastInsertId();

    $stmtRest = $pdo->prepare("
        INSERT INTO restaurantes
            (usuario_id, nombre, descripcion, direccion, categoria, telefono, aprobado, aprobado_por)
        VALUES
            (?, ?, ?, ?, ?, ?, 0, NULL)
    ");
    $stmtRest->execute([
        $usuario_id,
        $nombre_restaurante,
        $descripcion,
        $direccion,
        $categoria,
        $telefono_restaurante
    ]);

    $pdo->commit();
    jsonResponse(['success' => true, 'message' => 'Solicitud enviada. Un administrador revisará tu restaurante.']);

} catch (Exception $e) {
    $pdo->rollBack();
    jsonResponse(['success' => false, 'message' => 'Error al registrar. Inténtalo de nuevo.'], 500);
}
