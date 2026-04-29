<?php
// ============================================================
//  POST /usuarios/login.php
//  Body: { "email": "...", "contrasena": "..." }
//  Returns: { success, usuario: { id, nombre, apellidos, email, rol, telefono } }
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$body = getJsonBody();
$email     = trim($body['email']     ?? '');
$contrasena = trim($body['contrasena'] ?? '');

if (empty($email) || empty($contrasena)) {
    jsonResponse(['success' => false, 'message' => 'Email y contraseña son obligatorios'], 400);
}

$pdo  = getDB();
$stmt = $pdo->prepare("SELECT id, nombre, apellidos, email, password_hash, rol, telefono, foto_perfil, activo FROM usuarios WHERE email = ?");
$stmt->execute([$email]);
$usuario = $stmt->fetch();

if (!$usuario) {
    jsonResponse(['success' => false, 'message' => 'Credenciales incorrectas'], 401);
}

if (!$usuario['activo']) {
    jsonResponse(['success' => false, 'message' => 'Cuenta desactivada. Contacta con el administrador.'], 403);
}

if (!password_verify($contrasena, $usuario['password_hash'])) {
    jsonResponse(['success' => false, 'message' => 'Credenciales incorrectas'], 401);
}

// No devolver el hash de contraseña
unset($usuario['password_hash'], $usuario['activo']);

jsonResponse([
    'success' => true,
    'usuario' => $usuario
]);
