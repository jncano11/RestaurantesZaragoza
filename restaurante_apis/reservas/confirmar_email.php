<?php
// ============================================================
// GET /reservas/confirmar_email.php?id=X&token=Y
// Usuario confirma su asistencia desde el enlace del email.
// ============================================================
ini_set('display_errors', '0');
error_reporting(0);

require_once '../config/db.php';
require_once '../config/tokens.php';

$id    = (int)($_GET['id']    ?? 0);
$token = trim($_GET['token']  ?? '');

if ($id <= 0 || $token === '') {
    http_response_code(400);
    echo paginaError('Enlace inválido', 'El enlace no contiene los parámetros necesarios.');
    exit;
}

if (!validarTokenReserva($id, 'confirmar', $token)) {
    http_response_code(403);
    echo paginaError('Enlace no válido', 'El token de seguridad no es correcto.');
    exit;
}

$pdo = getDB();
$stmt = $pdo->prepare("
    SELECT res.estado, res.fecha, res.hora, res.num_personas,
           r.nombre AS rNombre, r.direccion AS rDir,
           CONCAT(u.nombre, ' ', COALESCE(u.apellidos,'')) AS uNombre
    FROM reservas res
    JOIN restaurantes r ON r.id = res.restaurante_id
    JOIN usuarios     u ON u.id = res.usuario_id
    WHERE res.id = ?
");
$stmt->execute([$id]);
$reserva = $stmt->fetch(PDO::FETCH_ASSOC);

if (!$reserva) {
    http_response_code(404);
    echo paginaError('Reserva no encontrada', 'No existe ninguna reserva con ese identificador.');
    exit;
}

if ($reserva['estado'] !== 'esperando_usuario') {
    echo paginaInfo('Ya procesada', 'Esta reserva ya fue procesada anteriormente (estado: ' . htmlspecialchars($reserva['estado']) . ').');
    exit;
}

$pdo->prepare("UPDATE reservas SET estado='confirmada' WHERE id=?")->execute([$id]);

$fecha = (new DateTime($reserva['fecha']))->format('d/m/Y');
$hora  = (new DateTime($reserva['hora']))->format('H:i');

echo "<!doctype html>
<html lang='es'>
<head>
  <meta charset='utf-8'>
  <meta name='viewport' content='width=device-width,initial-scale=1'>
  <title>Reserva confirmada — R-eats</title>
  <style>*{box-sizing:border-box}body{font-family:Arial,sans-serif;background:#f1f8e9;margin:0;padding:32px 16px}</style>
</head>
<body>
  <div style='max-width:480px;margin:auto;background:#fff;border-radius:14px;overflow:hidden;box-shadow:0 4px 16px rgba(0,0,0,.1)'>
    <div style='background:linear-gradient(135deg,#1B5E20,#43A047);padding:28px;text-align:center'>
      <div style='font-size:52px;line-height:1'>&#10003;</div>
      <h1 style='color:#fff;margin:10px 0 0;font-size:22px'>Reserva confirmada</h1>
    </div>
    <div style='padding:28px'>
      <p>Hola, <strong>" . htmlspecialchars((string)$reserva['uNombre']) . "</strong>. Tu asistencia ha quedado confirmada.</p>
      <table style='width:100%;border-collapse:collapse;margin-top:12px'>
        <tr style='background:#e8f5e9'><td style='padding:10px;font-weight:bold;color:#1B5E20'>Restaurante</td><td style='padding:10px'>" . htmlspecialchars((string)$reserva['rNombre']) . "</td></tr>
        <tr><td style='padding:10px;font-weight:bold;color:#1B5E20'>Direccion</td><td style='padding:10px'>" . htmlspecialchars((string)$reserva['rDir']) . "</td></tr>
        <tr style='background:#e8f5e9'><td style='padding:10px;font-weight:bold;color:#1B5E20'>Fecha</td><td style='padding:10px'>{$fecha}</td></tr>
        <tr><td style='padding:10px;font-weight:bold;color:#1B5E20'>Hora</td><td style='padding:10px'>{$hora}</td></tr>
        <tr style='background:#e8f5e9'><td style='padding:10px;font-weight:bold;color:#1B5E20'>Personas</td><td style='padding:10px'>" . (int)$reserva['num_personas'] . "</td></tr>
      </table>
      <p style='color:#555;margin-top:20px;font-size:14px'>Nos vemos pronto. Puedes consultar tu reserva en la app R-eats.</p>
    </div>
  </div>
</body>
</html>";

function paginaError(string $titulo, string $mensaje): string {
    return "<!doctype html><html lang='es'><head><meta charset='utf-8'><title>{$titulo}</title></head>
<body style='font-family:Arial;background:#ffebee;padding:32px'>
<div style='max-width:440px;margin:auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,.1)'>
  <div style='background:#c62828;padding:20px;text-align:center'><h2 style='color:#fff;margin:0'>{$titulo}</h2></div>
  <div style='padding:20px;color:#333'><p>{$mensaje}</p></div>
</div></body></html>";
}

function paginaInfo(string $titulo, string $mensaje): string {
    return "<!doctype html><html lang='es'><head><meta charset='utf-8'><title>{$titulo}</title></head>
<body style='font-family:Arial;background:#e3f2fd;padding:32px'>
<div style='max-width:440px;margin:auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 2px 8px rgba(0,0,0,.1)'>
  <div style='background:#1565c0;padding:20px;text-align:center'><h2 style='color:#fff;margin:0'>{$titulo}</h2></div>
  <div style='padding:20px;color:#333'><p>{$mensaje}</p></div>
</div></body></html>";
}
