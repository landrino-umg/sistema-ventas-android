# 📱 Sistema de Ventas - Aplicación Móvil Android

Aplicación móvil nativa en **Android con Java** diseñada para la creación y edición de órdenes de compra/venta para clientes e invitados. Se comunica directamente mediante peticiones HTTP JSON (`GET`, `POST`, `PUT`, `DELETE`) con el servicio **API REST** del Sistema de Ventas, interactuando con la base de datos central MySQL sin intermediación directa de drivers SQL en el dispositivo móvil.

---

## 🚀 Características Principales

1. **Pantalla Principal (`MainActivity`)**:
   - Listado en tiempo real de todas las órdenes registradas consumiendo `GET /api/pedidos`.
   - Badges visuales de estado (`PENDIENTE`, `COMPLETADA`, `CANCELADA`).
   - Información de cliente, dirección, fecha y total en Quetzales.
   - Acceso directo a edición o detalle.
   - Botón de prueba de conectividad y actualización manual.

2. **Módulo de Órdenes (`FormularioOrdenActivity`)**:
   - **Creación de Nueva Orden**: Petición `POST /api/pedidos` con payload JSON que incluye cabecera y listado dinámico de ítems.
   - **Edición de Orden Existente**: Petición `PUT /api/pedidos/{id}` para actualizar datos del cliente, estado y detalle de productos.
   - **Catálogo Dinámico**: Carga asíncrona de productos desde `GET /api/productos` para seleccionar productos, consultar precios unitarios y validar stock disponible en tiempo real.
   - **Cálculo Automático**: Subtotales en vivo por producto y total general de la orden.

3. **Selector Dinámico de URL / ngrok (`ConfigManager`)**:
   - Diálogo interactivo accesible desde la barra superior para cambiar la URL base del API REST al vuelo sin tener que recompilar la aplicación.
   - Atajos directos para:
     - **Emulador Android**: `http://10.0.2.2:8080/api`
     - **Dispositivo Físico / ngrok**: `https://xxxx.ngrok-free.app/api`
     - **Red Local Wi-Fi**: `http://192.168.x.x:8080/api`

---

## 🛠️ Tecnologías y Librerías

- **Lenguaje**: Java 17 / Java 8 compatibility (Android)
- **Min SDK**: 24 (Android 7.0 Nougat)
- **Target SDK**: 34 (Android 14)
- **UI / Componentes**: Google Material Components 1.11.0, RecyclerView, CardView, FloatingActionButton
- **Serialización JSON**: Google Gson 2.10.1
- **Networking**: `HttpURLConnection` con `ExecutorService` para concurrencia asíncrona segura fuera del Main Thread.

---

## 📂 Estructura del Proyecto

```text
sistema-ventas-android/
├── app/
│   ├── build.gradle
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/gt/edu/umg/sistema/ventas/movil/
│       │   ├── MainActivity.java
│       │   ├── FormularioOrdenActivity.java
│       │   ├── adapter/
│       │   │   ├── OrdenAdapter.java
│       │   │   └── CarritoAdapter.java
│       │   ├── model/
│       │   │   ├── ApiResponse.java
│       │   │   ├── Producto.java
│       │   │   ├── Pedido.java
│       │   │   ├── DetallePedido.java
│       │   │   ├── ItemPedidoRequest.java
│       │   │   └── CrearPedidoRequest.java
│       │   └── network/
│       │       ├── ApiClient.java
│       │       └── ConfigManager.java
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml
│           │   ├── activity_formulario_orden.xml
│           │   ├── item_orden.xml
│           │   ├── item_carrito.xml
│           │   └── dialog_config_url.xml
│           ├── menu/
│           │   └── menu_main.xml
│           └── values/
│               ├── colors.xml
│               ├── strings.xml
│               └── themes.xml
├── build.gradle
├── settings.gradle
└── README.md
```

---

## ⚙️ Instrucciones de Ejecución

### 1. Requisitos Previos
- **Android Studio** (Hedgehog, Iguana, Koala o superior).
- JDK 17 configurado en Android Studio (`Settings > Build, Execution, Deployment > Build Tools > Gradle > Gradle JDK`).
- El servicio **API REST** (`sistema-ventas-api-rest`) debe estar ejecutándose en tu computadora.

### 2. Abrir en Android Studio
1. Abre Android Studio y selecciona **Open**.
2. Navega a `c:\Users\Luis\Desktop\Universidad\sistema-ventas-android` y haz clic en **OK**.
3. Espera a que Gradle sincronice las dependencias del proyecto.

### 3. Ejecutar la Aplicación
1. Conecta un dispositivo físico con Depuración USB habilitada o inicia un Emulador Android (AVD).
2. Presiona **Run 'app'** (`Shift + F10`).

### 4. Configurar Conexión con el API REST
- **Si usas Emulador Android**:
  - Por defecto la app intentará conectar a `http://10.0.2.2:8080/api` (la dirección de loopback del host en el emulador).
- **Si usas un Teléfono Físico**:
  - En tu computadora levanta el túnel ngrok:
    ```bash
    ngrok http 8080
    ```
  - Copia la URL pública generada (ej: `https://abcd-1234.ngrok-free.app`).
  - En la app Android, toca el botón **"Cambiar"** o el menú de tres puntos > **Configurar URL Servidor**.
  - Pega la URL seguida de `/api` (ej: `https://abcd-1234.ngrok-free.app/api`) y presiona **Guardar**.
  - Toca **"Probar Conexión API"** para verificar el estado en verde.

---

## 📡 Endpoints Consumidos por la App

| Método | Endpoint | Acción |
|---|---|---|
| `GET` | `/api/status` | Verifica el estado de salud del servidor y la conexión a MySQL |
| `GET` | `/api/productos` | Obtiene el catálogo completo de productos con stock y precios |
| `GET` | `/api/pedidos` | Obtiene el listado de todas las órdenes de venta |
| `GET` | `/api/pedidos/{id}` | Carga el detalle completo de una orden específica |
| `POST` | `/api/pedidos` | Registra una nueva orden con detalle de productos |
| `PUT` | `/api/pedidos/{id}` | Modifica los datos de una orden existente y ajusta el inventario |
| `DELETE`| `/api/pedidos/{id}` | Cancela y elimina una orden |

---

## 👤 Autor
- **Universidad Mariano Gálvez de Guatemala**
- **Curso**: Programación II
- **Evaluación Final**: Integración de Aplicación Android con API REST
