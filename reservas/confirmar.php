<?php
// ============================================================
//  POST /reservas/confirmar.php  — confirma y notifica al cliente
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

$autoloadPath   = __DIR__ . '/../../vendor/autoload.php';
$mailDisponible = file_exists($autoloadPath);
if ($mailDisponible) require_once $autoloadPath;

if ($_SERVER['REQUEST_METHOD'] !== 'POST')
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);

$body      = getJsonBody();
$reservaId = (int)($body['reserva_id'] ?? 0);
if ($reservaId <= 0) jsonResponse(['success' => false, 'message' => 'reserva_id inválido'], 400);

$pdo = getDB();
$check = $pdo->prepare("SELECT id, estado FROM reservas WHERE id=?");
$check->execute([$reservaId]);
$reserva = $check->fetch();
if (!$reserva) jsonResponse(['success' => false, 'message' => 'Reserva no encontrada'], 404);
if ($reserva['estado'] !== 'pendiente') jsonResponse(['success' => false, 'message' => 'Solo reservas pendientes'], 409);

$pdo->prepare("UPDATE reservas SET estado='confirmada' WHERE id=?")->execute([$reservaId]);

$infoStmt = $pdo->prepare("
    SELECT res.fecha, res.hora, res.num_personas,
           r.nombre AS rNombre, r.direccion AS rDir,
           CONCAT(u.nombre,' ',u.apellidos) AS uNombre, u.email AS uEmail
    FROM reservas res JOIN restaurantes r ON r.id=res.restaurante_id JOIN usuarios u ON u.id=res.usuario_id
    WHERE res.id=?");
$infoStmt->execute([$reservaId]);
$info = $infoStmt->fetch();

if ($mailDisponible && $info && !empty($info['uEmail'])) {
    try {
        $m = new PHPMailer(true);
        $m->isSMTP(); $m->Host='smtp.gmail.com'; $m->SMTPAuth=true;
        $m->Username='tuemail@gmail.com'; $m->Password='tu_app_password'; // ← CAMBIA
        $m->SMTPSecure=PHPMailer::ENCRYPTION_STARTTLS; $m->Port=587; $m->CharSet='UTF-8';
        $m->setFrom('tuemail@gmail.com','R-eats Zaragoza');
        $m->addAddress($info['uEmail'], $info['uNombre']);
        $m->isHTML(true);
        $m->Subject = "🎉 ¡Reserva confirmada! — {$info['rNombre']}";
        $m->Body = "
        <div style='font-family:Arial,sans-serif;max-width:520px;margin:auto;border-radius:12px;overflow:hidden;background:#f9f9f9'>
          <div style='background:linear-gradient(135deg,#1B5E20,#43A047);padding:24px;text-align:center'>
            <h1 style='color:#fff;margin:0'>✅ ¡Reserva confirmada!</h1>
          </div>
          <div style='padding:24px'>
            <p>Hola, <strong>{$info['uNombre']}</strong>. El restaurante ha confirmado tu reserva.</p>
            <table style='width:100%;border-collapse:collapse'>
              <tr style='background:#e8f5e9'><td style='padding:8px;font-weight:bold;color:#1B5E20'>🍽️ Restaurante</td><td style='padding:8px'>{$info['rNombre']}</td></tr>
              <tr><td style='padding:8px;font-weight:bold;color:#1B5E20'>📍 Dirección</td><td style='padding:8px'>{$info['rDir']}</td></tr>
              <tr style='background:#e8f5e9'><td style='padding:8px;font-weight:bold;color:#1B5E20'>📅 Fecha</td><td style='padding:8px'>{$info['fecha']}</td></tr>
              <tr><td style='padding:8px;font-weight:bold;color:#1B5E20'>🕐 Hora</td><td style='padding:8px'>{$info['hora']}</td></tr>
              <tr style='background:#e8f5e9'><td style='padding:8px;font-weight:bold;color:#1B5E20'>👥 Personas</td><td style='padding:8px'>{$info['num_personas']}</td></tr>
            </table>
            <p style='color:#888;font-size:12px;margin-top:16px'>Puedes cancelar desde la app R-eats si lo necesitas.</p>
          </div>
        </div>";
        $m->send();
    } catch (Exception $e) { error_log("Email confirmacion: " . $e->getMessage()); }
}

jsonResponse(['success' => true, 'message' => 'Reserva confirmada correctamente']);