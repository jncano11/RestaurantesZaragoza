<?php
// ============================================================
// GET /reservas/rechazar_email.php?id=X&token=Y
// Cancela una reserva pendiente desde el enlace del email.
// Responde con HTML (lo abre el navegador del usuario).
// ============================================================
ini_set('display_errors', '0');
error_reporting(0);

try {
    require_once '../config/db.php';
    require_once '../config/mail.php';
} catch (\Throwable $e) {
    echo "<!doctype html><html lang='es'><head><meta charset='utf-8'><title>Error</title></head>"
       . "<body style='font-family:Arial;padding:32px;background:#fff'>"
       . "<h2 style='color:#c62828'>Error de configuración</h2>"
       . "<p>" . htmlspecialchars($e->getMessage()) . "</p></body></html>";
    exit;
}

$id    = (int)($_GET['id']    ?? 0);
$token = $_GET['token'] ?? '';

if ($id <= 0 || $token === '') {
    http_response_code(400);
    echo htmlRespuesta('error', 'Enlace inválido', 'El enlace que has usado no contiene los parámetros necesarios.');
    exit;
}

if (!validarTokenReserva($id, 'rechazar', $token)) {
    http_response_code(403);
    echo htmlRespuesta('error', 'Enlace no válido o caducado', 'El token de este enlace no es correcto o ya no es válido.');
    exit;
}

$pdo = getDB();

$stmt = $pdo->prepare("
    SELECT res.id, res.estado, res.fecha, res.hora, res.num_personas, res.notas,
           r.nombre AS rNombre, r.direccion AS rDir, r.email_contacto AS rEmail,
           CONCAT(u.nombre, ' ', u.apellidos) AS uNombre
    FROM reservas res
    JOIN restaurantes r ON r.id = res.restaurante_id
    JOIN usuarios     u ON u.id = res.usuario_id
    WHERE res.id = ?
");
$stmt->execute([$id]);
$reserva = $stmt->fetch();

if (!$reserva) {
    http_response_code(404);
    echo htmlRespuesta('error', 'Reserva no encontrada', 'No existe ninguna reserva con ese identificador.');
    exit;
}

if ($reserva['estado'] !== 'esperando_usuario') {
    $estadoTexto = htmlspecialchars($reserva['estado']);
    echo htmlRespuesta('info', 'Reserva ya procesada', "Esta reserva ya está <strong>{$estadoTexto}</strong> y no se puede modificar de nuevo.");
    exit;
}

$pdo->prepare("UPDATE reservas SET estado='cancelada' WHERE id=?")->execute([$id]);

// Notificar al restaurante que el usuario rechazó
if (!empty($reserva['rEmail'])) {
    $fechaF = (new DateTime($reserva['fecha']))->format('d/m/Y');
    $horaF  = (new DateTime($reserva['hora']))->format('H:i');
    $htmlR  = "
    <div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;border-radius:12px;overflow:hidden;background:#f9f9f9;border:1px solid #ddd'>
      <div style='background:linear-gradient(135deg,#e65100,#ff6d00);padding:20px;text-align:center'>
        <h1 style='color:#fff;margin:0;font-size:20px'>⚠️ Reserva cancelada por el usuario</h1>
      </div>
      <div style='padding:20px'>
        <p><strong>" . htmlspecialchars($reserva['uNombre']) . "</strong> ha rechazado la reserva:</p>
        <table style='width:100%;border-collapse:collapse'>
          <tr style='background:#fff3e0'><td style='padding:8px;font-weight:bold;color:#e65100'>📅 Fecha</td><td style='padding:8px'>{$fechaF}</td></tr>
          <tr><td style='padding:8px;font-weight:bold;color:#e65100'>🕐 Hora</td><td style='padding:8px'>{$horaF}</td></tr>
          <tr style='background:#fff3e0'><td style='padding:8px;font-weight:bold;color:#e65100'>👥 Personas</td><td style='padding:8px'>{$reserva['num_personas']}</td></tr>
        </table>
        <p style='color:#555;font-size:13px;margin-top:16px'>La plaza ha quedado libre en ese horario.</p>
      </div>
    </div>";
    enviarEmail($reserva['rEmail'], "Reserva cancelada por {$reserva['uNombre']}", $htmlR);
}

$fechaFormateada = (new DateTime($reserva['fecha']))->format('d/m/Y');
$horaFormateada  = (new DateTime($reserva['hora']))->format('H:i');

echo "<!doctype html>
<html lang='es'>
<head>
  <meta charset='utf-8'>
  <meta name='viewport' content='width=device-width,initial-scale=1'>
  <title>Reserva rechazada — R-eats Zaragoza</title>
</head>
<body style='font-family:Arial,sans-serif;background:#fafafa;margin:0;padding:32px 16px'>
  <div style='max-width:500px;margin:auto;background:#fff;border-radius:14px;overflow:hidden;box-shadow:0 4px 16px rgba(0,0,0,.1)'>
    <div style='background:linear-gradient(135deg,#b71c1c,#e53935);padding:28px;text-align:center'>
      <div style='font-size:48px'>❌</div>
      <h1 style='color:#fff;margin:8px 0 0;font-size:22px'>Reserva rechazada</h1>
    </div>
    <div style='padding:28px'>
      <p>Hola, <strong>" . htmlspecialchars((string)$reserva['uNombre']) . "</strong>. Has cancelado tu reserva. Si cambias de opinión puedes hacer una nueva reserva desde la app R-eats.</p>
      <table style='width:100%;border-collapse:collapse;margin-top:12px'>
        <tr style='background:#fce4ec'>
          <td style='padding:10px;font-weight:bold;color:#b71c1c;width:40%'>Restaurante</td>
          <td style='padding:10px'>" . htmlspecialchars($reserva['rNombre']) . "</td>
        </tr>
        <tr>
          <td style='padding:10px;font-weight:bold;color:#b71c1c'>Dirección</td>
          <td style='padding:10px'>" . htmlspecialchars($reserva['rDir']) . "</td>
        </tr>
        <tr style='background:#fce4ec'>
          <td style='padding:10px;font-weight:bold;color:#b71c1c'>Fecha</td>
          <td style='padding:10px'>{$fechaFormateada}</td>
        </tr>
        <tr>
          <td style='padding:10px;font-weight:bold;color:#b71c1c'>Hora</td>
          <td style='padding:10px'>{$horaFormateada}</td>
        </tr>
        <tr style='background:#fce4ec'>
          <td style='padding:10px;font-weight:bold;color:#b71c1c'>Personas</td>
          <td style='padding:10px'>{$reserva['num_personas']}</td>
        </tr>
      </table>
      <p style='color:#555;margin-top:20px;font-size:14px'>El cliente ha sido notificado del estado desde la app R-eats.</p>
    </div>
  </div>
</body>
</html>";

// Genera una página HTML de respuesta genérica (error o info)
function htmlRespuesta(string $tipo, string $titulo, string $mensaje): string {
    $colores = [
        'error' => ['#b71c1c', '#ffebee'],
        'info'  => ['#546e7a', '#eceff1'],
    ];
    [$cabecera, $fondo] = $colores[$tipo] ?? $colores['info'];

    return "<!doctype html>
<html lang='es'>
<head>
  <meta charset='utf-8'>
  <meta name='viewport' content='width=device-width,initial-scale=1'>
  <title>{$titulo} — R-eats Zaragoza</title>
</head>
<body style='font-family:Arial,sans-serif;background:{$fondo};margin:0;padding:32px 16px'>
  <div style='max-width:460px;margin:auto;background:#fff;border-radius:14px;overflow:hidden;box-shadow:0 4px 16px rgba(0,0,0,.1)'>
    <div style='background:{$cabecera};padding:24px;text-align:center'>
      <h1 style='color:#fff;margin:0;font-size:20px'>{$titulo}</h1>
    </div>
    <div style='padding:24px;color:#333'>
      <p>{$mensaje}</p>
    </div>
  </div>
</body>
</html>";
}
