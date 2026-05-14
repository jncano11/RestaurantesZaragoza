<?php
// ============================================================
// ARCHIVO: restaurantes_api/restaurantes/editar.php
// Edita los datos de un restaurante existente
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$data        = getJsonBody();
$id          = intval($data['id']             ?? 0);
$nombre      = trim($data['nombre']           ?? '');
$descripcion = trim($data['descripcion']      ?? '');
$direccion   = trim($data['direccion']        ?? '');
$categoria   = trim($data['categoria']        ?? '');
$telefono    = trim($data['telefono']         ?? '');
$email       = trim($data['email_contacto']   ?? $data['email_contact'] ?? '');
$precio      = floatval($data['precio_medio'] ?? 0);
$aforo       = intval($data['aforo_total']    ?? 50);

if (!$id) {
    jsonResponse(['success' => false, 'message' => 'ID requerido'], 400);
}

$pdo = getDB();

// Recalcular coordenadas si la dirección ha cambiado
$latActual = null;
$lonActual = null;
$checkDir  = $pdo->prepare("SELECT direccion, latitud, longitud FROM restaurantes WHERE id = ?");
$checkDir->execute([$id]);
$actual    = $checkDir->fetch();

if ($actual) {
    if ($actual['direccion'] !== $direccion) {
        // Dirección distinta → geocodificar nueva dirección
        $query   = urlencode($direccion . ', Zaragoza, España');
        $geoUrl  = "https://nominatim.openstreetmap.org/search?q={$query}&format=json&limit=1";
        $context = stream_context_create(['http' => ['header' => "User-Agent: ReatsZaragoza/1.0\r\n", 'timeout' => 5]]);
        $geoRaw  = @file_get_contents($geoUrl, false, $context);
        if ($geoRaw) {
            $geoData = json_decode($geoRaw, true);
            if (!empty($geoData[0])) {
                $latActual = (float) $geoData[0]['lat'];
                $lonActual = (float) $geoData[0]['lon'];
            }
        }
    } else {
        // Misma dirección → conservar coordenadas actuales
        $latActual = $actual['latitud'] !== null ? (float) $actual['latitud'] : null;
        $lonActual = $actual['longitud'] !== null ? (float) $actual['longitud'] : null;
    }
}

$stmt = $pdo->prepare("
    UPDATE restaurantes
    SET nombre=?, descripcion=?, direccion=?, categoria=?, telefono=?,
        email_contacto=?, precio_medio=?, aforo_total=?, latitud=?, longitud=?
    WHERE id=?
");
$ok = $stmt->execute([$nombre, $descripcion, $direccion, $categoria, $telefono, $email, $precio, $aforo, $latActual, $lonActual, $id]);
jsonResponse(['success' => $ok, 'message' => $ok ? 'Restaurante actualizado' : 'Error al actualizar']);