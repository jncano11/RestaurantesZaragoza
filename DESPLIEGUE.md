# Despliegue — R-eats Zaragoza

## Estrategia de URL para emulador + físico simultáneo

El emulador Android puede alcanzar la IP de LAN del host igual que el dispositivo físico.
Por tanto, usar la IP física (`192.168.1.72`) funciona para **ambos** con una sola build.

| Contexto | URL |
|---|---|
| Solo emulador | `http://10.0.2.2/restaurantes_api/` |
| Físico o ambos | `http://192.168.1.72/restaurantes_api/` |

---

## Pasos del despliegue `/despliegue`

### 1. Cambiar URL en RetrofitClient.kt

Archivo: `app/src/main/java/com/example/restauranteszaragoza/network/RetrofitClient.kt`

Dejar así (IP física, sirve para los dos):
```kotlin
// const val BASE_URL = "http://10.0.2.2/restaurantes_api/"       // Emulador
const val BASE_URL = "http://192.168.1.72/restaurantes_api/"   // Físico + emulador
```

> Si la IP del PC cambia, actualizar aquí y en este archivo.

### 2. Instalar en todos los dispositivos

```bash
./gradlew installDebug
```

Requisitos:
- Emulador arrancado en Android Studio / IntelliJ
- Móvil conectado por USB con depuración USB activada
- Verificar con `adb devices` que aparecen los dos

---

## Otros comandos útiles

```bash
./gradlew assembleDebug   # solo compila el APK
./gradlew clean           # limpia artefactos
adb devices               # lista dispositivos conectados
```
