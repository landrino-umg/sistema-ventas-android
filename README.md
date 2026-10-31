# Sistema de Ventas - Aplicación Móvil Android

Proyecto del curso de Programación II - Universidad Mariano Gálvez de Guatemala.

Aplicación móvil desarrollada en Android (Java) que funciona como Portal de Invitado / Comprador para el sistema de ventas, conectándose a través de una API REST.

## Tecnologías utilizadas
* Android Studio (Java)
* Android SDK 34 / Min SDK 24
* Gradle 8.6
* `HttpURLConnection` para consumo de API REST

## Opciones de la aplicación

1. **Nueva Orden**: Formulario para ingresar datos del comprador, seleccionar el producto desde un menú desplegable (muestra stock y precio en tiempo real), ingresar cantidad y guardar la orden de compra.
2. **Modificar**: Permite ingresar el ID de una orden y cambiar su estado (`PENDIENTE`, `CONFIRMADO`, `CANCELADO`).
3. **Stock**: Muestra el catálogo de productos con sus cantidades disponibles en inventario.
4. **Menú de configuración**:
   * *Probar Conexión*: Comprueba que el servidor responda.
   * *Configurar URL*: Permite cambiar la dirección de la API (por ejemplo `http://10.0.2.2:8080/api` para el emulador o la URL pública de ngrok).

## Cómo ejecutar el proyecto

1. Abrir la carpeta `sistema-ventas-android` en **Android Studio**.
2. Esperar a que sincronice el Gradle.
3. Iniciar un emulador o conectar un dispositivo Android.
4. Presionar el botón **Run** (ícono verde de play ▶️).
