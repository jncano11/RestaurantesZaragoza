<?php
// ============================================================
//  TOKEN SECRET — debe coincidir con MAIL_TOKEN_SECRET del .env
//  Se define aquí directamente para no depender de Dotenv en
//  los endpoints HTML (confirmar_email, rechazar_email).
// ============================================================

define('TOKEN_SECRET', 'widdwipbvqwirvpzwqfn0qcefwecwe2334rwef_pdkmceqefe2ffe4radf2ef_reats');

function generarTokenReserva(int $reservaId, string $accion): string {
    return hash_hmac('sha256', "{$reservaId}:{$accion}", TOKEN_SECRET);
}

function validarTokenReserva(int $reservaId, string $accion, string $token): bool {
    return hash_equals(generarTokenReserva($reservaId, $accion), $token);
}
