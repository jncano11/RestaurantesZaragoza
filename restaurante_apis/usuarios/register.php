<?php
// ============================================================
//  POST /usuarios/register.php
//  Body: { nombre, apellidos, email, contrasena, telefono }
//  Returns: { success, message }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';



if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body     = getJsonBody();
$nombre   = trim($body['nombre']    ?? '');
$apellidos= trim($body['apellidos'] ?? '');
$email    = trim($body['email']     ?? '');
$contrasena = trim($body['contrasena'] ?? '');
$telefono = trim($body['telefono']  ?? '');

// Validaciones
if (empty($nombre) || empty($email) || empty($contrasena)) {
    jsonResponse(['success' => false, 'message' => 'Nombre, email y contraseña son obligatorios'], 400);
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    jsonResponse(['success' => false, 'message' => 'Email no válido'], 400);
}
if (strlen($contrasena) < 6) {
    jsonResponse(['success' => false, 'message' => 'La contraseña debe tener al menos 6 caracteres'], 400);
}

$pdo = getDB();

// Comprobar si el email ya existe
$check = $pdo->prepare("SELECT id FROM usuarios WHERE email = ?");
$check->execute([$email]);
if ($check->fetch()) {
    jsonResponse(['success' => false, 'message' => 'El email ya está registrado'], 409);
}

$hash = password_hash($contrasena, PASSWORD_BCRYPT);
$stmt = $pdo->prepare("
    INSERT INTO usuarios (nombre, apellidos, email, password_hash, telefono, rol)
    VALUES (?, ?, ?, ?, ?, 'usuario')
");
$stmt->execute([$nombre, $apellidos, $email, $hash, $telefono]);

jsonResponse(['success' => true, 'message' => 'Cuenta creada correctamente']);
