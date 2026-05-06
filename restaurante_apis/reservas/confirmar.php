<?php
// ============================================================
//  POST /reservas/confirmar.php  — restaurante confirma reserva
//  Cambia estado a 'esperando_usuario' y envía email al usuario
//  con botones para que acepte o rechace.
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';
require_once '../config/mail.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST')
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);

$body      = getJsonBody();
$reservaId = (int)($body['reserva_id'] ?? 0);
if ($reservaId <= 0) jsonResponse(['success' => false, 'message' => 'reserva_id inválido'], 400);

$pdo = getDB();

$check = $pdo->prepare("SELECT id, estado FROM reservas WHERE id = ?");
$check->execute([$reservaId]);
$reserva = $check->fetch();
if (!$reserva) jsonResponse(['success' => false, 'message' => 'Reserva no encontrada'], 404);
if ($reserva['estado'] !== 'pendiente')
    jsonResponse(['success' => false, 'message' => 'Solo se pueden confirmar reservas pendientes'], 409);

$pdo->prepare("UPDATE reservas SET estado = 'esperando_usuario' WHERE id = ?")->execute([$reservaId]);

// Obtener datos para el email
$infoStmt = $pdo->prepare("
    SELECT res.fecha, res.hora, res.num_personas, res.notas,
           r.nombre AS rNombre, r.direccion AS rDir,
           CONCAT(u.nombre, ' ', u.apellidos) AS uNombre, u.email AS uEmail
    FROM reservas res
    JOIN restaurantes r ON r.id = res.restaurante_id
    JOIN usuarios     u ON u.id = res.usuario_id
    WHERE res.id = ?
");
$infoStmt->execute([$reservaId]);
$info = $infoStmt->fetch();

if ($info && !empty($info['uEmail'])) {
    $tokenC = generarTokenReserva($reservaId, 'confirmar');
    $tokenR = generarTokenReserva($reservaId, 'rechazar');

    $urlConfirmar = APP_BASE_URL . "/reservas/confirmar_email.php?id={$reservaId}&token={$tokenC}&ngrok-skip-browser-warning=true";
    $urlRechazar  = APP_BASE_URL . "/reservas/rechazar_email.php?id={$reservaId}&token={$tokenR}&ngrok-skip-browser-warning=true";

    $fechaFormateada = (new DateTime($info['fecha']))->format('d/m/Y');
    $horaFormateada  = (new DateTime($info['hora']))->format('H:i');
    $notasHtml = !empty($info['notas'])
        ? "<tr><td style='padding:8px;font-weight:bold;color:#1B5E20'>📝 Notas</td><td style='padding:8px'>" . htmlspecialchars($info['notas']) . "</td></tr>"
        : '';

    $html = "
    <div style='font-family:Arial,sans-serif;max-width:560px;margin:auto;border-radius:12px;overflow:hidden;background:#f9f9f9;border:1px solid #ddd'>
      <div style='background:linear-gradient(135deg,#1B5E20,#43A047);padding:24px;text-align:center'>
        <h1 style='color:#fff;margin:0;font-size:22px'>Tu reserva ha sido aceptada</h1>
        <p style='color:#c8e6c9;margin:8px 0 0'>Confirma o rechaza para completar el proceso</p>
      </div>
      <div style='padding:24px'>
        <p>Hola, <strong>" . htmlspecialchars($info['uNombre']) . "</strong>. El restaurante ha aceptado tu solicitud. Por favor confirma si finalmente asistiras:</p>
        <table style='width:100%;border-collapse:collapse;margin-bottom:20px'>
          <tr style='background:#e8f5e9'>
            <td style='padding:8px;font-weight:bold;color:#1B5E20'>Restaurante</td>
            <td style='padding:8px'>" . htmlspecialchars($info['rNombre']) . "</td>
          </tr>
          <tr>
            <td style='padding:8px;font-weight:bold;color:#1B5E20'>Direccion</td>
            <td style='padding:8px'>" . htmlspecialchars($info['rDir']) . "</td>
          </tr>
          <tr style='background:#e8f5e9'>
            <td style='padding:8px;font-weight:bold;color:#1B5E20'>Fecha</td>
            <td style='padding:8px'>{$fechaFormateada}</td>
          </tr>
          <tr>
            <td style='padding:8px;font-weight:bold;color:#1B5E20'>Hora</td>
            <td style='padding:8px'>{$horaFormateada}</td>
          </tr>
          <tr style='background:#e8f5e9'>
            <td style='padding:8px;font-weight:bold;color:#1B5E20'>Personas</td>
            <td style='padding:8px'>{$info['num_personas']}</td>
          </tr>
          {$notasHtml}
        </table>
        <div style='text-align:center;margin-top:24px'>
          <a href='" . htmlspecialchars($urlConfirmar) . "'
             style='display:inline-block;padding:14px 28px;border-radius:8px;background:#2e7d32;color:#fff;text-decoration:none;font-weight:bold;font-size:15px;margin-right:12px'>
            Confirmo mi asistencia
          </a>
          <a href='" . htmlspecialchars($urlRechazar) . "'
             style='display:inline-block;padding:14px 28px;border-radius:8px;background:#c62828;color:#fff;text-decoration:none;font-weight:bold;font-size:15px'>
            No podre asistir
          </a>
        </div>
        <p style='color:#888;font-size:12px;margin-top:20px;text-align:center'>
          Si no respondes, la reserva se cancelará automáticamente 24h antes de la fecha.
        </p>
      </div>
    </div>";

    $ok = enviarEmail($info['uEmail'], "Tu reserva en {$info['rNombre']} ha sido aceptada", $html);
    if (!$ok) {
        // Fallback: guardar URLs en archivo para probar sin email
        $linea = date('Y-m-d H:i:s') . " | Reserva {$reservaId}\n"
               . "  CONFIRMAR: {$urlConfirmar}\n"
               . "  RECHAZAR:  {$urlRechazar}\n\n";
        file_put_contents(__DIR__ . '/../../ultimo_email.txt', $linea, FILE_APPEND);
    }
}

jsonResponse([
    'success'      => true,
    'message'      => 'Reserva confirmada. Email enviado al usuario.',
    'url_confirmar'=> $urlConfirmar ?? null,
    'url_rechazar' => $urlRechazar  ?? null,
]);
