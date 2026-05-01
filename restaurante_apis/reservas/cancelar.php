<?php
// ============================================================
//  POST /reservas/cancelar.php
//  Body: { reserva_id, [usuario_id] }
//  Si cancela el restaurante (sin usuario_id) → email aviso al usuario
//  Si cancela el usuario (con usuario_id) → no se envía email
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';
require_once '../config/mail.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST')
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);

$body      = getJsonBody();
$reservaId = (int)($body['reserva_id'] ?? 0);
$usuarioId = (int)($body['usuario_id'] ?? 0); // 0 = petición desde restaurante/admin

if ($reservaId <= 0)
    jsonResponse(['success' => false, 'message' => 'reserva_id inválido'], 400);

$pdo = getDB();

if ($usuarioId > 0) {
    $check = $pdo->prepare("SELECT id, estado FROM reservas WHERE id = ? AND usuario_id = ?");
    $check->execute([$reservaId, $usuarioId]);
} else {
    $check = $pdo->prepare("SELECT id, estado FROM reservas WHERE id = ?");
    $check->execute([$reservaId]);
}

$reserva = $check->fetch();
if (!$reserva)
    jsonResponse(['success' => false, 'message' => 'Reserva no encontrada'], 404);
if ($reserva['estado'] === 'cancelada')
    jsonResponse(['success' => false, 'message' => 'La reserva ya está cancelada'], 409);
if ($reserva['estado'] === 'completada')
    jsonResponse(['success' => false, 'message' => 'No se puede cancelar una reserva completada'], 409);

$pdo->prepare("UPDATE reservas SET estado = 'cancelada' WHERE id = ?")->execute([$reservaId]);

// Si cancela el restaurante → avisar al usuario por email
if ($usuarioId === 0) {
    $infoStmt = $pdo->prepare("
        SELECT res.fecha, res.hora, res.num_personas,
               r.nombre AS rNombre,
               CONCAT(u.nombre, ' ', u.apellidos) AS uNombre, u.email AS uEmail
        FROM reservas res
        JOIN restaurantes r ON r.id = res.restaurante_id
        JOIN usuarios     u ON u.id = res.usuario_id
        WHERE res.id = ?
    ");
    $infoStmt->execute([$reservaId]);
    $info = $infoStmt->fetch();

    if ($info && !empty($info['uEmail'])) {
        $fechaFormateada = (new DateTime($info['fecha']))->format('d/m/Y');
        $horaFormateada  = (new DateTime($info['hora']))->format('H:i');

        $html = "
        <div style='font-family:Arial,sans-serif;max-width:540px;margin:auto;border-radius:12px;overflow:hidden;background:#f9f9f9;border:1px solid #ddd'>
          <div style='background:linear-gradient(135deg,#b71c1c,#e53935);padding:24px;text-align:center'>
            <h1 style='color:#fff;margin:0;font-size:22px'>❌ Reserva cancelada</h1>
            <p style='color:#ffcdd2;margin:8px 0 0'>El restaurante ha cancelado tu reserva</p>
          </div>
          <div style='padding:24px'>
            <p>Hola, <strong>" . htmlspecialchars($info['uNombre']) . "</strong>. Lamentablemente el restaurante ha tenido que cancelar tu reserva:</p>
            <table style='width:100%;border-collapse:collapse;margin:16px 0'>
              <tr style='background:#fce4ec'>
                <td style='padding:8px;font-weight:bold;color:#b71c1c'>🍽️ Restaurante</td>
                <td style='padding:8px'>" . htmlspecialchars($info['rNombre']) . "</td>
              </tr>
              <tr>
                <td style='padding:8px;font-weight:bold;color:#b71c1c'>📅 Fecha</td>
                <td style='padding:8px'>{$fechaFormateada}</td>
              </tr>
              <tr style='background:#fce4ec'>
                <td style='padding:8px;font-weight:bold;color:#b71c1c'>🕐 Hora</td>
                <td style='padding:8px'>{$horaFormateada}</td>
              </tr>
              <tr>
                <td style='padding:8px;font-weight:bold;color:#b71c1c'>👥 Personas</td>
                <td style='padding:8px'>{$info['num_personas']}</td>
              </tr>
            </table>
            <p style='color:#555;font-size:14px'>Puedes buscar otro restaurante disponible desde la app R-eats.</p>
          </div>
        </div>";

        enviarEmail($info['uEmail'], "Tu reserva en {$info['rNombre']} ha sido cancelada", $html);
    }
}

jsonResponse(['success' => true, 'message' => 'Reserva cancelada correctamente']);
