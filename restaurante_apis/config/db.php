<?php
// ============================================================
//  CONFIGURACIÓN DE BASE DE DATOS
//  Ruta: restaurantes_api/config/db.php
// ============================================================

define('DB_HOST', '127.0.0.1');
define('DB_USER', 'root');         // Cambia por tu usuario MySQL
define('DB_PASS', '');             // Cambia por tu contraseña MySQL
define('DB_NAME', 'restaurantes_zaragoza');
define('DB_CHARSET', 'utf8mb4');

function getDB(): PDO {
    static $pdo = null;
    if ($pdo === null) {
        try {
            $dsn = "mysql:host=" . DB_HOST . ";dbname=" . DB_NAME . ";charset=" . DB_CHARSET;
            $pdo = new PDO($dsn, DB_USER, DB_PASS, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                PDO::ATTR_EMULATE_PREPARES   => false,
            ]);
        } catch (PDOException $e) {
            http_response_code(500);
            die(json_encode(['success' => false, 'message' => 'Error de base de datos: ' . $e->getMessage()]));
        }
    }
    return $pdo;
}
