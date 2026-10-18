# Gutierrez Music
Proyecto Kotlin + Jetpack Compose + AndroidX Media3 con reproductor de música local y locución de hora ZaraRadio.

## Crear APK desde GitHub (sin PC)
1. Crear un repositorio nuevo en GitHub (preferiblemente privado).
2. Subir **el contenido** de este ZIP a la raíz del repositorio (incluida la carpeta oculta `.github`).
3. Abrir **Actions** > **Build Gutierrez Music APK** > **Run workflow**.
4. Esperar a que termine y abrir el resultado de la ejecución.
5. En **Artifacts**, descargar `Gutierrez-Music-APK` y extraer `app-debug.apk`.
6. Instalarlo en Android permitiendo instalar APK de esa fuente.

## Alcance y límites
- Esta entrega es un proyecto inicial para compilar; no se ha ejecutado la compilación en este entorno.
- La locución solo se entrega a una instancia de PlaybackService que siga activa en el proceso. Si Android destruye el proceso, no se anuncia la hora. Se requiere un diseño más robusto para garantía de funcionamiento con pantalla apagada y en reposo.
- La biblioteca de álbumes/artistas/carpetas aún es simplificada, no hay pantalla Now Playing completa ni extracción de Palette.
- El APK generado es **debug**, para pruebas, no firmado para Play Store.
- El motor acepta `HH_MM.mp3`, `horas/HH.mp3` + `minutos/MM.mp3`, `HH/MM.mp3` y `MM.mp3`.
- Los dispositivos pueden exigir conceder acceso especial a alarmas exactas.
