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

## 5. Crear el archivo .env

Crea un archivo `.env` en la raíz del proyecto con este contenido:

```env
MAIL_HOST=sandbox.smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USER=tu_usuario_mailtrap
MAIL_PASS=tu_contrasena_mailtrap
MAIL_FROM=no-reply@restauranteszaragoza.test
MAIL_FROM_NAME="R-eats Zaragoza"

APP_BASE_URL=http://TU_IP_LAN/restaurantes_api
```

**MAIL_USER y MAIL_PASS:** créate una cuenta gratuita en https://mailtrap.io → Inboxes → copia las credenciales SMTP del inbox que te creen por defecto.

**APP_BASE_URL:** pon la IP local de tu PC (no `localhost`). Para saber tu IP ejecuta `ipconfig` en Windows y busca la IPv4 de tu adaptador WiFi. Ejemplo: `http://192.168.1.50/restaurantes_api`

---

## 6. Configurar la app Android

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

## 7. Ejecutar la app

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

## 8. Crear usuario administrador

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
5. Crear .env con credenciales Mailtrap y tu IP
6. Cambiar IP en RetrofitClient.kt
7. Run desde Android Studio
```

---

## Problemas frecuentes

**"Error de base de datos"** → MySQL no está arrancado o las credenciales en `db.php` son incorrectas.

**"No se pudo cargar"** → La IP en `RetrofitClient.kt` no es la correcta o Apache no está corriendo.

**El email no llega** → Revisa las credenciales SMTP en `.env` y que `APP_BASE_URL` usa tu IP de LAN (no `localhost`).

**404 en la API** → El `Alias` de Apache no está bien configurado o Apache no se reinició después de editarlo.
