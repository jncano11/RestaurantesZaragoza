# Guía de instalación — R-eats Zaragoza

Pasos para desplegar el proyecto en un ordenador nuevo desde cero.

---

## Requisitos previos

Instala esto antes de empezar:

| Herramienta | Versión mínima | Descarga |
|-------------|----------------|----------|
| XAMPP | 8.x | https://www.apachefriends.org |
| Java JDK | 11 | https://adoptium.net |
| Android Studio / IntelliJ IDEA | Reciente | https://developer.android.com/studio |
| Composer | 2.x | https://getcomposer.org |
| Git | Cualquiera | https://git-scm.com |

---

## 1. Clonar el repositorio

```bash
git clone <URL-del-repositorio>
cd RestaurantesZaragoza
```

---

## 2. Base de datos

1. Abre **XAMPP** → inicia **Apache** y **MySQL**
2. Entra en **phpMyAdmin** → `http://localhost/phpmyadmin`
3. Crea una base de datos llamada `restaurantes_zaragoza` con charset `utf8mb4`
4. Selecciona esa base de datos → pestaña **Importar**
5. Importa el archivo `restaurantes_zaragoza (3).sql` que está en la raíz del proyecto
6. Importa también `restaurante_apis/sql/alter_restaurantes_aprobado.sql`

Si tu MySQL tiene contraseña, edita `restaurante_apis/config/db.php` y cambia:
```php
define('DB_USER', 'root');
define('DB_PASS', 'tu_contraseña');
```

---

## 3. Configurar Apache para servir la API

Abre el archivo de configuración de Apache. En XAMPP suele estar en:
```
C:\xampp\apache\conf\httpd.conf
```

Añade estas líneas al final del archivo (ajusta la ruta al lugar donde clonaste el repo):

```apache
# R-eats Zaragoza — API REST
Alias /restaurantes_api "C:/ruta/al/repo/RestaurantesZaragoza/restaurante_apis"
<Directory "C:/ruta/al/repo/RestaurantesZaragoza/restaurante_apis">
    Options Indexes FollowSymLinks
    AllowOverride All
    Require all granted
</Directory>
```

Guarda el archivo y **reinicia Apache** desde el panel de XAMPP.

Verifica que funciona abriendo: `http://localhost/restaurantes_api/restaurantes/listar.php`
Debe devolver un JSON (array vacío `[]` si la BD está vacía es correcto).

---

## 4. Instalar dependencias PHP

Desde la raíz del proyecto ejecuta:

```bash
composer install
```

Esto instala PHPMailer y phpdotenv en la carpeta `vendor/`.

---

## 5. Configurar ngrok

Los emails de confirmación contienen links que Mailtrap pasa por sus servidores para rastrear clics. Si la URL apunta a una IP local, los botones del email no funcionan. Ngrok expone el servidor local con una URL pública.

1. Crea una cuenta gratuita en https://ngrok.com e instala el ejecutable
2. Autentica ngrok con tu token (solo la primera vez):
   ```bash
   ngrok config add-authtoken TU_TOKEN
   ```
3. Antes de cada sesión de desarrollo, lanza:
   ```bash
   ngrok http 80
   ```
4. Ngrok mostrará una URL tipo `https://xxxx.ngrok-free.app` — cópiala para el paso siguiente

> La URL cambia cada vez que reinicias ngrok (plan gratuito). Actualiza `APP_BASE_URL` en `.env` en cada sesión.

---

## 6. Crear el archivo .env

Crea un archivo `.env` en la raíz del proyecto con este contenido:

```env
MAIL_HOST=live.smtp.mailtrap.io
MAIL_PORT=587
MAIL_USER=api
MAIL_PASS=tu_token_de_mailtrap_email_sending
MAIL_FROM=hello@demomailtrap.co
MAIL_FROM_NAME="R-eats Zaragoza"

APP_BASE_URL=https://xxxx.ngrok-free.app/restaurantes_api
```

**MAIL_PASS:** en Mailtrap → Email Sending → SMTP credentials → copia el token (el usuario siempre es `api`).

**MAIL_FROM:** usa el dominio demo que Mailtrap asignó a tu cuenta (visible en Email Sending → Sending Domains). El de este proyecto es `hello@demomailtrap.co`.

**APP_BASE_URL:** la URL pública de ngrok del paso anterior, con `/restaurantes_api` al final y sin barra final.

---

## 7. Configurar la app Android

Abre el archivo:
```
app/src/main/java/com/example/restauranteszaragoza/network/RetrofitClient.kt
```

En las líneas 21-22 cambia la IP por la de tu PC:

```kotlin
// const val BASE_URL = "http://10.0.2.2/restaurantes_api/"   // Emulador
const val BASE_URL = "http://192.168.1.XX/restaurantes_api/"   // Tu IP
```

> Usa `10.0.2.2` si solo vas a probar en el emulador. Usa tu IP de LAN si quieres probar en móvil físico o en ambos a la vez.

---

## 8. Ejecutar la app

### En emulador

1. Abre el proyecto en Android Studio / IntelliJ IDEA
2. Crea un AVD (emulador) si no tienes uno: **Device Manager → Create Device**
3. Pulsa **Run** (el triángulo verde)

### En dispositivo físico

1. Activa **Opciones de desarrollador** en el móvil (pulsa 7 veces en "Número de compilación" en Ajustes)
2. Activa **Depuración USB**
3. Conecta el móvil por USB
4. Selecciónalo en el desplegable de dispositivos y pulsa **Run**

### En emulador y físico a la vez

```bash
./gradlew installDebug
```

Esto instala en todos los dispositivos conectados simultáneamente.

---

## 9. Crear usuario administrador

La base de datos importada puede no tener usuarios. Crea uno desde phpMyAdmin ejecutando esta query en la tabla `usuarios`:

```sql
INSERT INTO usuarios (nombre, apellidos, email, contrasena, rol, activo)
VALUES ('Admin', 'Admin', 'admin@reats.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'admin', 1);
```

La contraseña es `password`. Cámbiala desde la app después del primer login.

---

## Resumen rápido

```
1. Clonar repo
2. Importar SQL en phpMyAdmin
3. Añadir Alias en httpd.conf y reiniciar Apache
4. composer install
5. Instalar ngrok y lanzar: ngrok http 80
6. Crear .env con credenciales Mailtrap Email Sending y URL de ngrok
7. Cambiar IP en RetrofitClient.kt
8. Run desde Android Studio
```

---

## Problemas frecuentes

**"Error de base de datos"** → MySQL no está arrancado o las credenciales en `db.php` son incorrectas.

**"No se pudo cargar"** → La IP en `RetrofitClient.kt` no es la correcta o Apache no está corriendo.

**El email no llega** → Revisa las credenciales en `.env` (MAIL_USER=`api`, MAIL_PASS=token de Email Sending) y que MAIL_FROM usa el dominio demo de tu cuenta Mailtrap.

**Los botones del email no funcionan** → Ngrok no está corriendo o la URL de `APP_BASE_URL` en `.env` está desactualizada. Lanza `ngrok http 80` y actualiza la URL.

**404 en la API** → El `Alias` de Apache no está bien configurado o Apache no se reinició después de editarlo.
