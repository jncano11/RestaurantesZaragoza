<?php
// ============================================================
//  GET /restaurantes/listar.php
// ============================================================
require_once '../config/cors.php';
require_once '../config/db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'GET')
    jsonResponse(['success' => false, 'message' => 'Método no permitido'], 405);

$pdo       = getDB();
$categoria = trim($_GET['categoria'] ?? '');
$busqueda  = trim($_GET['q']         ?? '');

$sql = "
    SELECT
        r.id                                        AS id,
        r.nombre,
        r.descripcion,
        r.direccion,
        r.ciudad,
        r.latitud,
        r.longitud,
        r.telefono,
        r.email_contacto,
        r.categoria,
        r.precio_medio,
        r.aforo_total,
        r.usuario_id,
        r.activo,
        COALESCE(ROUND(AVG(v.puntuacion), 1), 0)    AS rating_global,
        COUNT(DISTINCT v.id)                        AS num_valoraciones,
        (SELECT f.url_foto
           FROM fotos_restaurante f
          WHERE f.restaurante_id = r.id AND f.es_portada = 1
          LIMIT 1)                                  AS imagen_url
    FROM restaurantes r
    LEFT JOIN valoraciones v ON v.restaurante_id = r.id
    WHERE r.activo = 1
";

$params = [];
if (!empty($categoria)) { $sql .= " AND r.categoria = ?"; $params[] = $categoria; }
if (!empty($busqueda))  {
    $sql    .= " AND (r.nombre LIKE ? OR r.descripcion LIKE ? OR r.categoria LIKE ?)";
    $like    = "%$busqueda%";
    $params  = array_merge($params, [$like, $like, $like]);
}
$sql .= " GROUP BY r.id ORDER BY rating_global DESC";

$stmt = $pdo->prepare($sql);
$stmt->execute($params);
$restaurantes = $stmt->fetchAll();

foreach ($restaurantes as &$r) {
    $r['id']               = (int)   $r['id'];
    $r['precio_medio']     = (float) $r['precio_medio'];
    $r['rating_global']    = (float) $r['rating_global'];
    $r['num_valoraciones'] = (int)   $r['num_valoraciones'];
    $r['aforo_total']      = (int)   $r['aforo_total'];
    $r['usuario_id']       = (int)   $r['usuario_id'];
    $r['activo']           = (int)   $r['activo'];
    // imagen_url puede ser null si no hay fotos, lo dejamos como string vacío
    $r['imagen_url'] = $r['imagen_url'] ?? '';
}

echo json_encode(array_values($restaurantes), JSON_UNESCAPED_UNICODE);