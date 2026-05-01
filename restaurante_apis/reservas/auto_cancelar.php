<?php
// ============================================================
//  GET /reservas/auto_cancelar.php
//  Cancela reservas en estado 'esperando_usuario' cuya fecha/hora
//  está a menos de 24 horas y el usuario no ha respondido.
//  Invocar desde Windows Task Scheduler cada hora:
//    curl http://localhost/restaurantes_api/reservas/auto_cancelar.php
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';
require_once '../config/mail.php';

$pdo = getDB();

// Buscar reservas esperando respuesta del usuario con menos de 24h
$stmt = $pdo->prepare("
    SELECT res.id, res.fecha, res.hora, res.num_personas,
           r.nombre AS rNombre, r.email_contacto AS rEmail,
           CONCAT(u.nombre, ' ', u.apellidos) AS uNombre, u.email AS uEmail
    FROM reservas res
    JOIN restaurantes r ON r.id = res.restaurante_id
    JOIN usuarios     u ON u.id = res.usuario_id
    WHERE res.estado = 'esperando_usuario'
      AND TIMESTAMP(res.fecha, res.hora) <= DATE_ADD(NOW(), INTERVAL 24 HOUR)
");
$stmt->execute();
$vencidas = $stmt->fetchAll();

$canceladas = 0;

foreach ($vencidas as $r) {
    $pdo->prepare("UPDATE reservas SET estado = 'cancelada' WHERE id = ?")->execute([$r['id']]);
    $canceladas++;

    $fechaF = (new DateTime($r['fecha']))->format('d/m/Y');
    $horaF  = (new DateTime($r['hora']))->format('H:i');

    // Email al usuario
    if (!empty($r['uEmail'])) {
        $html = "
        <div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;border-radius:12px;overflow:hidden;background:#f9f9f9;border:1px solid #ddd'>
          <div style='background:linear-gradient(135deg,#546e7a,#78909c);padding:20px;text-align:center'>
            <h1 style='color:#fff;margin:0;font-size:20px'>⏰ Reserva cancelada por tiempo</h1>
          </div>
          <div style='padding:20px'>
            <p>Hola, <strong>" . htmlspecialchars($r['uNombre']) . "</strong>. Tu reserva en <strong>" . htmlspecialchars($r['rNombre']) . "</strong> ha sido cancelada automáticamente porque no confirmaste tu asistencia antes de las 24h previas.</p>
            <table style='width:100%;border-collapse:collapse;margin:12px 0'>
              <tr style='background:#eceff1'><td style='padding:8px;font-weight:bold'>📅 Fecha</td><td style='padding:8px'>{$fechaF}</td></tr>
              <tr><td style='padding:8px;font-weight:bold'>🕐 Hora</td><td style='padding:8px'>{$horaF}</td></tr>
              <tr style='background:#eceff1'><td style='padding:8px;font-weight:bold'>👥 Personas</td><td style='padding:8px'>{$r['num_personas']}</td></tr>
            </table>
            <p style='color:#555;font-size:13px'>Puedes hacer una nueva reserva desde la app R-eats.</p>
          </div>
        </div>";
        enviarEmail($r['uEmail'], "Tu reserva en {$r['rNombre']} ha sido cancelada automáticamente", $html);
    }

    // Email al restaurante
    if (!empty($r['rEmail'])) {
        $htmlR = "
        <div style='font-family:Arial,sans-serif;max-width:500px;margin:auto;border-radius:12px;overflow:hidden;background:#f9f9f9;border:1px solid #ddd'>
          <div style='background:linear-gradient(135deg,#546e7a,#78909c);padding:20px;text-align:center'>
            <h1 style='color:#fff;margin:0;font-size:18px'>⏰ Reserva cancelada automáticamente</h1>
          </div>
          <div style='padding:20px'>
            <p>La reserva de <strong>" . htmlspecialchars($r['uNombre']) . "</strong> ha sido cancelada automáticamente por no confirmar a tiempo.</p>
            <table style='width:100%;border-collapse:collapse;margin:12px 0'>
              <tr style='background:#eceff1'><td style='padding:8px;font-weight:bold'>📅 Fecha</td><td style='padding:8px'>{$fechaF}</td></tr>
              <tr><td style='padding:8px;font-weight:bold'>🕐 Hora</td><td style='padding:8px'>{$horaF}</td></tr>
              <tr style='background:#eceff1'><td style='padding:8px;font-weight:bold'>👥 Personas</td><td style='padding:8px'>{$r['num_personas']}</td></tr>
            </table>
            <p style='color:#555;font-size:13px'>La plaza ha quedado libre en ese horario.</p>
          </div>
        </div>";
        enviarEmail($r['rEmail'], "Reserva de {$r['uNombre']} cancelada por tiempo", $htmlR);
    }
}

jsonResponse(['success' => true, 'canceladas' => $canceladas]);
