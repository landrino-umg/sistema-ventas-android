package gt.edu.umg.sistema.ventas.movil;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Aplicación Android para la integración con la API REST del Sistema de Ventas.
 * Permite probar conexión (GET), crear una orden de venta (POST) y editar una orden (PUT).
 */
public class MainActivity extends AppCompatActivity {

    private EditText etUrlApi;
    private Button btnProbarConexion;

    // Crear Orden
    private EditText etNombreCliente;
    private EditText etProductoId;
    private EditText etCantidad;
    private Button btnCrearOrden;

    // Editar Orden
    private EditText etIdOrdenEditar;
    private Spinner spNuevoEstado;
    private Button btnEditarOrden;

    // Resultado
    private TextView tvRespuestaApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        etUrlApi = findViewById(R.id.etUrlApi);
        btnProbarConexion = findViewById(R.id.btnProbarConexion);

        etNombreCliente = findViewById(R.id.etNombreCliente);
        etProductoId = findViewById(R.id.etProductoId);
        etCantidad = findViewById(R.id.etCantidad);
        btnCrearOrden = findViewById(R.id.btnCrearOrden);

        etIdOrdenEditar = findViewById(R.id.etIdOrdenEditar);
        spNuevoEstado = findViewById(R.id.spNuevoEstado);
        btnEditarOrden = findViewById(R.id.btnEditarOrden);

        tvRespuestaApi = findViewById(R.id.tvRespuestaApi);

        // Configurar opciones del spinner de estado
        String[] estados = new String[]{"PENDIENTE", "COMPLETADA", "CANCELADA"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spNuevoEstado.setAdapter(adapter);

        // Eventos de botones
        btnProbarConexion.setOnClickListener(v -> probarConexion());
        btnCrearOrden.setOnClickListener(v -> crearOrdenVenta());
        btnEditarOrden.setOnClickListener(v -> editarOrdenVenta());
    }

    /**
     * 1. GET /api/status - Verifica que la API REST y la Base de Datos respondan.
     */
    private void probarConexion() {
        String baseUrl = obtenerBaseUrl();
        tvRespuestaApi.setText("Conectando con el servidor...");

        ejecutarPeticionHttp(baseUrl + "/status", "GET", null);
    }

    /**
     * 2. POST /api/pedidos - Requerimiento principal: Crear una Orden de Venta.
     */
    private void crearOrdenVenta() {
        String cliente = etNombreCliente.getText().toString().trim();
        String prodIdStr = etProductoId.getText().toString().trim();
        String cantStr = etCantidad.getText().toString().trim();

        if (cliente.isEmpty() || prodIdStr.isEmpty() || cantStr.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos para crear la orden", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int productoId = Integer.parseInt(prodIdStr);
            int cantidad = Integer.parseInt(cantStr);

            // Construir payload JSON
            JSONObject json = new JSONObject();
            json.put("nombreCliente", cliente);
            json.put("productoId", productoId);
            json.put("cantidad", cantidad);
            json.put("estado", "PENDIENTE");

            String baseUrl = obtenerBaseUrl();
            tvRespuestaApi.setText("Enviando orden a la API REST (POST)...");

            ejecutarPeticionHttp(baseUrl + "/pedidos", "POST", json.toString());

        } catch (Exception e) {
            Toast.makeText(this, "Error en los datos numéricos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 3. PUT /api/pedidos/{id} - Requerimiento complementario: Editar una Orden.
     */
    private void editarOrdenVenta() {
        String idOrdenStr = etIdOrdenEditar.getText().toString().trim();
        String nuevoEstado = spNuevoEstado.getSelectedItem().toString();

        if (idOrdenStr.isEmpty()) {
            Toast.makeText(this, "Ingresa el ID de la orden a editar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int idOrden = Integer.parseInt(idOrdenStr);

            // Construir payload JSON
            JSONObject json = new JSONObject();
            json.put("estado", nuevoEstado);

            String baseUrl = obtenerBaseUrl();
            tvRespuestaApi.setText("Actualizando orden #" + idOrden + " (PUT)...");

            ejecutarPeticionHttp(baseUrl + "/pedidos/" + idOrden, "PUT", json.toString());

        } catch (Exception e) {
            Toast.makeText(this, "ID de orden inválido", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Método auxiliar de red: ejecuta la petición HTTP en segundo plano
     * y entrega la respuesta del servidor en el hilo principal de la interfaz gráfica.
     */
    private void ejecutarPeticionHttp(String urlStr, String metodo, String jsonBody) {
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(metodo);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                conn.setRequestProperty("Accept", "application/json");

                if (jsonBody != null && (metodo.equals("POST") || metodo.equals("PUT"))) {
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
                    }
                }

                int statusCode = conn.getResponseCode();
                InputStream is = (statusCode >= 200 && statusCode < 400) ? conn.getInputStream() : conn.getErrorStream();

                StringBuilder response = new StringBuilder();
                if (is != null) {
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            response.append(line).append("\n");
                        }
                    }
                }

                String resultadoFinal = "HTTP " + statusCode + " (" + metodo + ")\n\n" + response.toString();

                runOnUiThread(() -> {
                    tvRespuestaApi.setText(resultadoFinal);
                    if (statusCode >= 200 && statusCode < 300) {
                        Toast.makeText(MainActivity.this, "Operación exitosa (HTTP " + statusCode + ")", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Respuesta del servidor: HTTP " + statusCode, Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    String errorMsg = "Error de conexión:\n" + e.getMessage() + "\n\nVerifica que la API REST esté activa y la URL de ngrok sea correcta.";
                    tvRespuestaApi.setText(errorMsg);
                    Toast.makeText(MainActivity.this, "Fallo al conectar: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        }).start();
    }

    private String obtenerBaseUrl() {
        String url = etUrlApi.getText().toString().trim();
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
