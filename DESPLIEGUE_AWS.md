# Despliegue en AWS EC2 — R-eats Zaragoza

Registro del proceso seguido para crear y configurar el servidor de producción en AWS.

---

## Datos del servidor

| Campo | Valor |
|---|---|
| IP pública | `13.48.57.113` |
| SO | Ubuntu Server 24.04 LTS |
| Tipo de instancia | t3.micro (Free Tier) |
| Region | eu-north-1 (Estocolmo) |
| API base URL | `http://13.48.57.113/` |

---

## 1. Crear la instancia EC2

1. Accede a la consola de AWS → **EC2 → Launch Instance**
2. **AMI:** Ubuntu Server 24.04 LTS (64-bit x86)
3. **Tipo:** t3.micro
4. **Par de claves:** crear nuevo → tipo RSA → formato `.ppk` (para PuTTY) → guardar en lugar seguro fuera del repositorio
5. **Grupo de seguridad:** crear nuevo con estas reglas de entrada:

| Tipo | Protocolo | Puerto | Origen |
|---|---|---|---|
| SSH | TCP | 22 | 0.0.0.0/0 |
| HTTP | TCP | 80 | 0.0.0.0/0 |
| HTTPS | TCP | 443 | 0.0.0.0/0 |

> El puerto 3306 (MySQL) no se abre — solo se accede desde dentro del servidor.

6. **Almacenamiento:** 8 GB (por defecto)
7. **Lanzar instancia**

---

## 2. Conectarse por SSH (PuTTY)

1. Abre PuTTY
2. **Host Name:** `13.48.57.113` · **Port:** `22`
3. Ve a **Connection → SSH → Auth → Credentials**
4. Carga el archivo `.ppk`
5. Guarda la sesión con un nombre (ej. `restaurantes-aws`)
6. Pulsa **Open** → usuario: `ubuntu`

---

## 3. Instalar el stack del servidor

```bash
sudo apt update && sudo apt upgrade -y
sudo apt install -y apache2 php php-mysql php-mbstring php-curl unzip git mysql-server
```

Instalar Composer:
```bash
curl -sS https://getcomposer.org/installer | php
sudo mv composer.phar /usr/local/bin/composer
```

---

## 4. Configurar MySQL

Arrancar y habilitar MySQL:
```bash
sudo systemctl start mysql
sudo systemctl enable mysql
sudo mysql_secure_installation
```

Al ejecutar `mysql_secure_installation`:
- Validación de contraseña: `n`
- Eliminar usuarios anónimos: `y`
- Deshabilitar login remoto de root: `y`
- Eliminar base de datos test: `y`
- Recargar privilegios: `y`

Crear usuario y base de datos:
```bash
sudo mysql
```

```sql
CREATE USER 'adminReats'@'localhost' IDENTIFIED BY 'adminReats@';
GRANT ALL PRIVILEGES ON *.* TO 'adminReats'@'localhost';
FLUSH PRIVILEGES;
CREATE DATABASE restaurantes_zaragoza CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
EXIT;
```

Importar el dump SQL (después de subirlo con WinSCP):
```bash
mysql -u adminReats -p restaurantes_zaragoza < /var/www/restaurantes_api/restaurantes_zaragoza.sql
```

---

## 5. Configurar Apache

Crear el directorio de la API:
```bash
sudo mkdir -p /var/www/restaurantes_api
sudo chown -R ubuntu:ubuntu /var/www/restaurantes_api
```

Crear el Virtual Host:
```bash
sudo nano /etc/apache2/sites-available/restaurantes_api.conf
```

Contenido:
```apache
<VirtualHost *:80>
    ServerName 13.48.57.113
    DocumentRoot /var/www/restaurantes_api

    <Directory /var/www/restaurantes_api>
        AllowOverride All
        Require all granted
    </Directory>
</VirtualHost>
```

Activar el sitio:
```bash
sudo a2ensite restaurantes_api.conf
sudo a2enmod rewrite
sudo a2dissite 000-default.conf
sudo systemctl restart apache2
```

---

## 6. Subir la API (WinSCP)

1. Protocolo: **SFTP** · Host: `13.48.57.113` · Puerto: `22`
2. Usuario: `ubuntu` · sin contraseña
3. **Avanzado → SSH → Autenticación** → cargar el `.ppk`
4. Subir todo el contenido de `restaurante_apis/` a `/var/www/restaurantes_api/`

Instalar dependencias PHP:
```bash
cd /var/www/restaurantes_api
composer install
```

Ajustar permisos:
```bash
sudo chown -R www-data:www-data /var/www/restaurantes_api
sudo chmod -R 755 /var/www/restaurantes_api
```

> Para volver a subir archivos con WinSCP, dar permisos a ubuntu primero:
> ```bash
> sudo chown -R ubuntu:ubuntu /var/www/restaurantes_api
> ```
> Y tras subir, restaurar permisos de Apache:
> ```bash
> sudo chown -R www-data:www-data /var/www/restaurantes_api
> ```

---

## 7. Configurar db.php en el servidor

```bash
nano /var/www/restaurantes_api/config/db.php
```

Cambiar las credenciales a las del servidor:
```php
define('DB_HOST', 'localhost');
define('DB_NAME', 'restaurantes_zaragoza');
define('DB_USER', 'adminReats');
define('DB_PASS', 'adminReats@');
```

> `db.php` está en `.gitignore` y no se sube al repositorio. Cada entorno tiene el suyo.

---

## 8. Configurar .env en el servidor

```bash
nano /var/www/restaurantes_api/.env
```

```env
MAIL_HOST=live.smtp.mailtrap.io
MAIL_PORT=587
MAIL_USER=api
MAIL_PASS=<token de Mailtrap Email Sending>
MAIL_FROM=hello@demomailtrap.co
MAIL_FROM_NAME="R-eats Zaragoza"
APP_BASE_URL=http://13.48.57.113/restaurantes_api
```

> A diferencia del entorno local, **no se necesita ngrok**. La IP pública del servidor es fija y permanente.

---

## 9. Verificar que funciona

Abre en el navegador:
```
http://13.48.57.113/restaurantes/listar.php
```

Debe devolver un JSON con los restaurantes.

---

## 10. Configurar la app Android para AWS

En `RetrofitClient.kt`:
```kotlin
const val BASE_URL = "http://13.48.57.113/"  // AWS producción
```

---

## Actualizar la API después de cambios

1. Con WinSCP, dar permisos de escritura:
   ```bash
   sudo chown -R ubuntu:ubuntu /var/www/restaurantes_api
   ```
2. Subir los archivos modificados con WinSCP
3. Restaurar permisos:
   ```bash
   sudo chown -R www-data:www-data /var/www/restaurantes_api
   ```

---

## Comandos útiles en el servidor

```bash
sudo systemctl status apache2      # estado de Apache
sudo systemctl restart apache2     # reiniciar Apache
sudo tail -f /var/log/apache2/error.log  # ver errores de Apache en tiempo real
mysql -u adminReats -p             # entrar a MySQL
```
