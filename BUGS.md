# Bugs conocidos y soluciones — R-eats Zaragoza

---

## BUG: Botones del email de confirmación de reserva no cargan la página

### Síntoma
Al pulsar "Confirmo mi asistencia" o "No podré asistir" desde el email de Mailtrap, el navegador no muestra la página de confirmación/rechazo.

### Causa raíz
**Mailtrap reescribe los links del email para su sistema de click-tracking.** Cuando el usuario clica, la petición pasa primero por los servidores de Mailtrap, que intentan redirigir a la URL original. Si esa URL usa `localhost` o una IP privada de LAN (`192.168.1.72`), los servidores de Mailtrap no pueden alcanzarla y la redirección falla. La petición nunca llega a Apache/XAMPP.

Señal de diagnóstico: en `C:\xampp2\apache\logs\access.log` no aparece ninguna entrada para `confirmar_email.php` ni `rechazar_email.php` al pulsar los botones.

### Solución para entorno de desarrollo (TFG)
Las URLs de confirmación se incluyen en la respuesta JSON de `POST /reservas/confirmar.php`:

```json
{
  "success": true,
  "message": "Reserva confirmada. Email enviado al usuario.",
  "url_confirmar": "http://192.168.1.72/restaurantes_api/reservas/confirmar_email.php?id=X&token=Y",
  "url_rechazar":  "http://192.168.1.72/restaurantes_api/reservas/rechazar_email.php?id=X&token=Y"
}
```

Como OkHttp loguea las respuestas en nivel BODY, las URLs aparecen en el **Logcat** de Android Studio/IntelliJ. Copia la URL y pégala en el navegador directamente.

### Solución definitiva (producción)
Cambiar a **Mailtrap Email Sending** (envío real) en `.env`:
```
MAIL_HOST=live.smtp.mailtrap.io
MAIL_PORT=587
```
Con envío a correos reales, el usuario recibe el email en su cliente (Gmail, Outlook…) y los links van directamente al servidor sin pasar por el tracking de Mailtrap.

### Archivos afectados
- `restaurante_apis/reservas/confirmar.php` — respuesta JSON incluye URLs de prueba
- `restaurante_apis/reservas/confirmar_email.php` — endpoint HTML de confirmación
- `restaurante_apis/reservas/rechazar_email.php` — endpoint HTML de rechazo
- `.env` — `APP_BASE_URL` y credenciales SMTP

---

## BUG: `.env` falla al parsear (InvalidFileException)

### Síntoma
Todos los endpoints que incluyen `mail.php` fallaban con `Fatal error: Uncaught Dotenv\Exception\InvalidFileException: Failed to parse dotenv file`.

### Causa raíz
El `.env` tenía una línea `MAIL_TOKEN_SECRET` con los caracteres `#` y `ñ` que el parser de phpdotenv interpretaba mal. El `#` sin escapar dentro de un valor se trata como inicio de comentario, corrompiendo el parseo del archivo completo.

### Solución
1. Eliminada la línea `MAIL_TOKEN_SECRET` del `.env` (el secreto ya está hardcodeado en `config/tokens.php`).
2. Envuelto el `safeLoad()` en try/catch en `config/mail.php` para que un fallo de parseo no mate todo el endpoint.

### Archivos afectados
- `.env` — eliminada línea problemática
- `restaurante_apis/config/mail.php` — try/catch alrededor de `safeLoad()`

---

## BUG: Página en blanco al acceder a confirmar_email.php

### Síntoma
Al navegar a `confirmar_email.php` el navegador mostraba una página completamente vacía (`<body></body>` en el inspector).

### Causa raíz
El navegador tenía cacheada una respuesta vacía de una petición anterior (cuando el archivo aún no generaba output). Al recargar se servía desde caché sin hacer nueva petición al servidor.

### Solución
Abrir la URL en una **nueva pestaña** (`Ctrl+T` → pegar URL → Enter) para forzar una petición nueva sin caché.

---

## BUG: "SMTP Error: data not accepted" al enviar email

### Síntoma
Los emails fallaban con `Error al enviar email a X: SMTP Error: data not accepted` en el error log de Apache.

### Causa raíz
Los emojis (`🎉`, `🍽️`, `📅`, etc.) en el asunto y cuerpo del email causaban que Mailtrap sandbox rechazara el mensaje.

### Solución
Eliminados los emojis del asunto y del cuerpo HTML del email en `confirmar.php`.

### Archivos afectados
- `restaurante_apis/reservas/confirmar.php`
