# 📱 Sistema de Ventas - Aplicación Android

Aplicación Android nativa sencilla desarrollada en **Java** para crear y editar Órdenes de Venta consumiendo una **API REST**.

---

## 🎯 Funcionalidades Principales

1. **Configuración de Conexión (ngrok)**: Permite ingresar o cambiar la URL pública generada por ngrok (o `http://10.0.2.2:8080/api` para emulador).
2. **Probar Conexión (`GET /status`)**: Comprueba que la API y la base de datos MySQL estén respondiendo.
3. **Crear Orden de Venta (`POST /pedidos`)**: Formulario con nombre del cliente, ID del producto y cantidad para generar la orden en la base de datos.
4. **Editar Orden de Venta (`PUT /pedidos/{id}`)**: Permite actualizar el estado de una orden existente (`PENDIENTE`, `COMPLETADA`, `CANCELADA`).
5. **Visor de Respuesta HTTP**: Muestra en pantalla el código de estado (200, 201, 400) y el mensaje de confirmación devuelto por la API.

---

## 🚀 Cómo Ejecutar el Proyecto

1. Abre **Android Studio**.
2. Selecciona **Open** y abre la carpeta `sistema-ventas-android`.
3. Conecta tu teléfono o inicia un emulador Android.
4. Presiona **Run 'app'** (`Shift + F10`).
5. Ingresa la URL de tu API o de ngrok y presiona **"Crear Orden (POST)"**.

---

## 📡 Flujo de Comunicación

```text
Android  --->  ngrok URL pública  --->  API REST (Localhost:8080)  --->  MySQL Base de Datos
```
