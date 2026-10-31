package gt.edu.umg.sistema.ventas.movil.network;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import gt.edu.umg.sistema.ventas.movil.model.ApiResponse;
import gt.edu.umg.sistema.ventas.movil.model.CrearPedidoRequest;
import gt.edu.umg.sistema.ventas.movil.model.Pedido;
import gt.edu.umg.sistema.ventas.movil.model.Producto;

/**
 * Cliente HTTP asíncrono para consumir la API REST del Sistema de Ventas.
 * Ejecuta las peticiones en hilos secundarios y entrega respuestas en el hilo principal (UI).
 */
public class ApiClient {

    private static ApiClient instance;
    private final ExecutorService executor;
    private final Handler mainHandler;
    private final Gson gson;

    private static final int TIMEOUT_MS = 15000;

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }

    private ApiClient() {
        this.executor = Executors.newFixedThreadPool(4);
        this.mainHandler = new Handler(Looper.getMainLooper());
        this.gson = new Gson();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    /**
     * Prueba de conectividad con GET /api/status.
     */
    public void testConnection(Context context, ApiCallback<String> callback) {
        String url = ConfigManager.getBaseUrl(context) + "/status";
        executor.execute(() -> {
            try {
                String responseJson = executeRequest(url, "GET", null);
                Type type = new TypeToken<ApiResponse<Object>>() {}.getType();
                ApiResponse<Object> response = gson.fromJson(responseJson, type);
                postSuccess(callback, response.getMessage());
            } catch (Exception e) {
                postError(callback, "Error de conexión: " + e.getMessage());
            }
        });
    }

    /**
     * Obtiene el catálogo de productos disponibles: GET /api/productos.
     */
    public void getProductos(Context context, ApiCallback<List<Producto>> callback) {
        String url = ConfigManager.getBaseUrl(context) + "/productos";
        executor.execute(() -> {
            try {
                String responseJson = executeRequest(url, "GET", null);
                Type type = new TypeToken<ApiResponse<List<Producto>>>() {}.getType();
                ApiResponse<List<Producto>> response = gson.fromJson(responseJson, type);
                if (response.isSuccess()) {
                    postSuccess(callback, response.getData());
                } else {
                    postError(callback, response.getMessage());
                }
            } catch (Exception e) {
                postError(callback, "Error al cargar productos: " + e.getMessage());
            }
        });
    }

    /**
     * Obtiene la lista de órdenes registradas: GET /api/pedidos.
     */
    public void getPedidos(Context context, ApiCallback<List<Pedido>> callback) {
        String url = ConfigManager.getBaseUrl(context) + "/pedidos";
        executor.execute(() -> {
            try {
                String responseJson = executeRequest(url, "GET", null);
                Type type = new TypeToken<ApiResponse<List<Pedido>>>() {}.getType();
                ApiResponse<List<Pedido>> response = gson.fromJson(responseJson, type);
                if (response.isSuccess()) {
                    postSuccess(callback, response.getData());
                } else {
                    postError(callback, response.getMessage());
                }
            } catch (Exception e) {
                postError(callback, "Error al cargar pedidos: " + e.getMessage());
            }
        });
    }

    /**
     * Obtiene el detalle de un pedido por su ID: GET /api/pedidos/{id}.
     */
    public void getPedido(Context context, int id, ApiCallback<Pedido> callback) {
        String url = ConfigManager.getBaseUrl(context) + "/pedidos/" + id;
        executor.execute(() -> {
            try {
                String responseJson = executeRequest(url, "GET", null);
                Type type = new TypeToken<ApiResponse<Pedido>>() {}.getType();
                ApiResponse<Pedido> response = gson.fromJson(responseJson, type);
                if (response.isSuccess()) {
                    postSuccess(callback, response.getData());
                } else {
                    postError(callback, response.getMessage());
                }
            } catch (Exception e) {
                postError(callback, "Error al cargar detalle del pedido: " + e.getMessage());
            }
        });
    }

    /**
     * Crea un nuevo pedido: POST /api/pedidos.
     */
    public void crearPedido(Context context, CrearPedidoRequest request, ApiCallback<Pedido> callback) {
        String url = ConfigManager.getBaseUrl(context) + "/pedidos";
        String body = gson.toJson(request);
        executor.execute(() -> {
            try {
                String responseJson = executeRequest(url, "POST", body);
                Type type = new TypeToken<ApiResponse<Pedido>>() {}.getType();
                ApiResponse<Pedido> response = gson.fromJson(responseJson, type);
                if (response.isSuccess()) {
                    postSuccess(callback, response.getData());
                } else {
                    postError(callback, response.getMessage());
                }
            } catch (Exception e) {
                postError(callback, "Error al crear pedido: " + e.getMessage());
            }
        });
    }

    /**
     * Actualiza un pedido existente: PUT /api/pedidos/{id}.
     */
    public void actualizarPedido(Context context, int id, CrearPedidoRequest request, ApiCallback<Pedido> callback) {
        String url = ConfigManager.getBaseUrl(context) + "/pedidos/" + id;
        String body = gson.toJson(request);
        executor.execute(() -> {
            try {
                String responseJson = executeRequest(url, "PUT", body);
                Type type = new TypeToken<ApiResponse<Pedido>>() {}.getType();
                ApiResponse<Pedido> response = gson.fromJson(responseJson, type);
                if (response.isSuccess()) {
                    postSuccess(callback, response.getData());
                } else {
                    postError(callback, response.getMessage());
                }
            } catch (Exception e) {
                postError(callback, "Error al actualizar pedido: " + e.getMessage());
            }
        });
    }

    /**
     * Elimina un pedido y cancela sus reservas: DELETE /api/pedidos/{id}.
     */
    public void eliminarPedido(Context context, int id, ApiCallback<Void> callback) {
        String url = ConfigManager.getBaseUrl(context) + "/pedidos/" + id;
        executor.execute(() -> {
            try {
                String responseJson = executeRequest(url, "DELETE", null);
                Type type = new TypeToken<ApiResponse<Object>>() {}.getType();
                ApiResponse<Object> response = gson.fromJson(responseJson, type);
                if (response.isSuccess()) {
                    postSuccess(callback, null);
                } else {
                    postError(callback, response.getMessage());
                }
            } catch (Exception e) {
                postError(callback, "Error al eliminar pedido: " + e.getMessage());
            }
        });
    }

    // ==========================================
    // MÉTODOS AUXILIARES HTTP
    // ==========================================

    private String executeRequest(String urlStr, String method, String jsonBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setConnectTimeout(TIMEOUT_MS);
        conn.setReadTimeout(TIMEOUT_MS);
        conn.setRequestProperty("Accept", "application/json");

        if (jsonBody != null && (method.equals("POST") || method.equals("PUT"))) {
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int statusCode = conn.getResponseCode();
        InputStream is;
        if (statusCode >= 200 && statusCode < 400) {
            is = conn.getInputStream();
        } else {
            is = conn.getErrorStream();
            if (is == null) {
                throw new Exception("HTTP " + statusCode + ": " + conn.getResponseMessage());
            }
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
        }

        if (statusCode >= 400) {
            // Intentamos parsear ApiResponse con mensaje de error del servidor
            try {
                Type type = new TypeToken<ApiResponse<Object>>() {}.getType();
                ApiResponse<Object> errResponse = gson.fromJson(response.toString(), type);
                if (errResponse != null && errResponse.getMessage() != null) {
                    throw new Exception(errResponse.getMessage());
                }
            } catch (Exception ignored) {
            }
            throw new Exception("HTTP " + statusCode + ": " + response);
        }

        return response.toString();
    }

    private <T> void postSuccess(ApiCallback<T> callback, T result) {
        mainHandler.post(() -> {
            if (callback != null) {
                callback.onSuccess(result);
            }
        });
    }

    private <T> void postError(ApiCallback<T> callback, String error) {
        mainHandler.post(() -> {
            if (callback != null) {
                callback.onError(error);
            }
        });
    }
}
