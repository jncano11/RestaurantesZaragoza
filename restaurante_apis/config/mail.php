<?php
// ============================================================
//  CONFIGURACIÓN DE CORREO — PHPMailer + Mailtrap
//
//  Las credenciales se leen del .env en la raíz del proyecto.
//  Los tokens de email se generan desde config/tokens.php.
//
//  Para pasar a producción cambia en .env:
//    MAIL_HOST=live.smtp.mailtrap.io
//    MAIL_PORT=587
// ============================================================

// Las funciones generarTokenReserva / validarTokenReserva vienen de tokens.php
require_once __DIR__ . '/tokens.php';

$autoloadPath = __DIR__ . '/../../vendor/autoload.php';
if (file_exists($autoloadPath)) {
    require_once $autoloadPath;
    $MAIL_DISPONIBLE = true;
} else {
    $MAIL_DISPONIBLE = false;
}

// Cargar .env desde la raíz del proyecto
if ($MAIL_DISPONIBLE && class_exists(\Dotenv\Dotenv::class)) {
    try {
        $dotenv = \Dotenv\Dotenv::createImmutable(__DIR__ . '/../../');
        $dotenv->safeLoad();
    } catch (\Throwable $e) {
        error_log('dotenv parse error: ' . $e->getMessage());
    }
}

define('MAIL_HOST',      $_ENV['MAIL_HOST']       ?? 'sandbox.smtp.mailtrap.io');
define('MAIL_PORT',      (int)($_ENV['MAIL_PORT'] ?? 2525));
define('MAIL_USER',      $_ENV['MAIL_USER']       ?? '');
define('MAIL_PASS',      $_ENV['MAIL_PASS']       ?? '');
define('MAIL_FROM',      $_ENV['MAIL_FROM']       ?? 'no-reply@restauranteszaragoza.test');
define('MAIL_FROM_NAME', $_ENV['MAIL_FROM_NAME']  ?? 'R-eats Zaragoza');
define('MAIL_SECURE',    'tls');
define('APP_BASE_URL',   $_ENV['APP_BASE_URL']    ?? 'http://localhost/restaurantes_api');

/**
 * Envía un email HTML usando PHPMailer + Mailtrap.
 */
function enviarEmail(string $to, string $subject, string $htmlBody): bool {
    global $MAIL_DISPONIBLE;

    if (!$MAIL_DISPONIBLE) {
        error_log("Mailer no disponible: vendor/autoload.php no existe");
        return false;
    }

    try {
        $m = new \PHPMailer\PHPMailer\PHPMailer(true);
        $m->isSMTP();
        $m->Host       = MAIL_HOST;
        $m->SMTPAuth   = true;
        $m->Username   = MAIL_USER;
        $m->Password   = MAIL_PASS;
        $m->SMTPSecure = MAIL_SECURE;
        $m->Port       = MAIL_PORT;
        $m->CharSet    = 'UTF-8';
        $m->setFrom(MAIL_FROM, MAIL_FROM_NAME);
        $m->addAddress($to);
        $m->isHTML(true);
        $m->Subject  = $subject;
        $m->Body     = $htmlBody;
        $m->AltBody  = strip_tags($htmlBody);
        $m->send();
        return true;
    } catch (\Exception $e) {
        error_log("Error al enviar email a {$to}: " . $e->getMessage());
        return false;
    }
}
