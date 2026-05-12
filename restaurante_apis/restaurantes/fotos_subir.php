<?php
// POST /restaurantes/fotos_subir.php
// Multipart: restaurante_id (text), fotos[] (archivos)
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);
}

$restauranteId = (int)($_POST['restaurante_id'] ?? 0);
if ($restauranteId <= 0) jsonResponse(['success' => false, 'message' => 'restaurante_id inválido'], 400);

if (empty($_FILES['fotos'])) jsonResponse(['success' => false, 'message' => 'No se recibieron fotos'], 400);

$pdo = getDB();

$countStmt = $pdo->prepare("SELECT COUNT(*) FROM fotos_restaurante WHERE restaurante_id = ?");
$countStmt->execute([$restauranteId]);
$totalActual = (int)$countStmt->fetchColumn();

$permitidas = 6 - $totalActual;
if ($permitidas <= 0) {
    jsonResponse(['success' => false, 'message' => 'Ya tienes 6 fotos. Elimina alguna antes de subir más.'], 400);
}

$uploadsDir = dirname(__DIR__) . DIRECTORY_SEPARATOR . 'uploads' . DIRECTORY_SEPARATOR;
if (!is_dir($uploadsDir)) {
    mkdir($uploadsDir, 0777, true);
}

// Normalizar $_FILES['fotos'] a array de archivos individuales
$files = [];
if (is_array($_FILES['fotos']['name'])) {
    for ($i = 0; $i < count($_FILES['fotos']['name']); $i++) {
        $files[] = [
            'name'     => $_FILES['fotos']['name'][$i],
            'tmp_name' => $_FILES['fotos']['tmp_name'][$i],
            'error'    => $_FILES['fotos']['error'][$i],
            'size'     => $_FILES['fotos']['size'][$i],
        ];
    }
} else {
    $files[] = $_FILES['fotos'];
}

$files   = array_slice($files, 0, $permitidas);
$subidas = 0;
$errores = [];
$esPrimeraFoto = ($totalActual === 0);

foreach ($files as $i => $file) {
    if ($file['error'] !== UPLOAD_ERR_OK) {
        $errores[] = "Archivo $i: error de subida ({$file['error']})";
        continue;
    }
    if ($file['size'] > 8 * 1024 * 1024) {
        $errores[] = "Archivo $i: supera 8MB";
        continue;
    }

    // Detectar extensión por el nombre original (más fiable en Windows)
    $nombreOriginal = strtolower($file['name']);
    $ext = pathinfo($nombreOriginal, PATHINFO_EXTENSION);
    if (!in_array($ext, ['jpg', 'jpeg', 'png', 'webp', 'gif'])) {
        $ext = 'jpg'; // fallback
    }

    $nombre  = "rest_{$restauranteId}_" . time() . "_{$i}.{$ext}";
    $destino = $uploadsDir . $nombre;

    if (!move_uploaded_file($file['tmp_name'], $destino)) {
        $errores[] = "No se pudo mover el archivo $i a $destino";
        continue;
    }

    $esPortada = ($esPrimeraFoto && $subidas === 0) ? 1 : 0;
    $ins = $pdo->prepare("INSERT INTO fotos_restaurante (restaurante_id, url_foto, descripcion, es_portada) VALUES (?, ?, '', ?)");
    $ins->execute([$restauranteId, $nombre, $esPortada]);
    $subidas++;
}

if ($subidas === 0) {
    $detalle = implode('; ', $errores) ?: 'Los archivos no pudieron procesarse';
    jsonResponse(['success' => false, 'message' => "No se subió ninguna foto: $detalle"], 500);
}

$proto   = $_SERVER['HTTP_X_FORWARDED_PROTO'] ?? (isset($_SERVER['HTTPS']) && $_SERVER['HTTPS'] === 'on' ? 'https' : 'http');
$baseUrl = $proto . '://' . $_SERVER['HTTP_HOST'] . '/restaurantes_api/uploads/';

$fStmt = $pdo->prepare("SELECT url_foto, descripcion, es_portada FROM fotos_restaurante WHERE restaurante_id = ? ORDER BY es_portada DESC, id ASC");
$fStmt->execute([$restauranteId]);
$fotos = $fStmt->fetchAll();
foreach ($fotos as &$f) {
    $f['url_foto']   = $baseUrl . $f['url_foto'];
    $f['es_portada'] = (bool)$f['es_portada'];
}

jsonResponse(['success' => true, 'subidas' => $subidas, 'fotos' => $fotos]);
