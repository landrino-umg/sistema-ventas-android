package gt.edu.umg.sistema.ventas.movil;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity {

    // Botones del menú superior
    private Button btnMenuNuevaOrden;
    private Button btnMenuModificar;
    private Button btnMenuStock;

    // Paneles
    private LinearLayout panelNuevaOrden;
    private LinearLayout panelModificarOrden;
    private LinearLayout panelStock;

    // Formulario Nueva Orden
    private EditText txtNombre;
    private EditText txtDireccion;
    private EditText txtTelefono;
    private EditText txtProductoId;
    private EditText txtCantidad;
    private Button btnGuardarOrden;

    // Formulario Modificar Orden
    private EditText txtModificarId;
    private Spinner cmbEstado;
    private Button btnModificarOrden;

    // Formulario Consultar Stock
    private Button btnConsultarStock;
    private TextView txtResultadoStock;

    // URL base de la API
    private String urlServidor = "http://10.0.2.2:8080/api";
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("config_ventas", MODE_PRIVATE);
        urlServidor = prefs.getString("url_api", "http://10.0.2.2:8080/api");

        // Inicializar vistas de menú
        btnMenuNuevaOrden = findViewById(R.id.btnMenuNuevaOrden);
        btnMenuModificar = findViewById(R.id.btnMenuModificar);
        btnMenuStock = findViewById(R.id.btnMenuStock);

        panelNuevaOrden = findViewById(R.id.panelNuevaOrden);
        panelModificarOrden = findViewById(R.id.panelModificarOrden);
        panelStock = findViewById(R.id.panelStock);

        // Inicializar Panel 1: Nueva Orden
        txtNombre = findViewById(R.id.txtNombre);
        txtDireccion = findViewById(R.id.txtDireccion);
        txtTelefono = findViewById(R.id.txtTelefono);
        txtProductoId = findViewById(R.id.txtProductoId);
        txtCantidad = findViewById(R.id.txtCantidad);
        btnGuardarOrden = findViewById(R.id.btnGuardarOrden);

        // Inicializar Panel 2: Modificar Orden
        txtModificarId = findViewById(R.id.txtModificarId);
        cmbEstado = findViewById(R.id.cmbEstado);
        btnModificarOrden = findViewById(R.id.btnModificarOrden);

        String[] estados = new String[]{"PENDIENTE", "CONFIRMADO", "CANCELADO"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        cmbEstado.setAdapter(estadoAdapter);

        // Inicializar Panel 3: Stock
        btnConsultarStock = findViewById(R.id.btnConsultarStock);
        txtResultadoStock = findViewById(R.id.txtResultadoStock);

        // Configurar navegación de menús
        btnMenuNuevaOrden.setOnClickListener(v -> mostrarPanel(1));
        btnMenuModificar.setOnClickListener(v -> mostrarPanel(2));
        btnMenuStock.setOnClickListener(v -> mostrarPanel(3));

        // Acciones de los botones
        btnGuardarOrden.setOnClickListener(v -> guardarOrden());
        btnModificarOrden.setOnClickListener(v -> modificarOrden());
        btnConsultarStock.setOnClickListener(v -> consultarStock());

        mostrarPanel(1);
    }

    private void mostrarPanel(int opcion) {
        panelNuevaOrden.setVisibility(opcion == 1 ? View.VISIBLE : View.GONE);
        panelModificarOrden.setVisibility(opcion == 2 ? View.VISIBLE : View.GONE);
        panelStock.setVisibility(opcion == 3 ? View.VISIBLE : View.GONE);

        btnMenuNuevaOrden.setText(opcion == 1 ? "• Nueva Orden •" : "Nueva Orden");
        btnMenuModificar.setText(opcion == 2 ? "• Modificar •" : "Modificar");
        btnMenuStock.setText(opcion == 3 ? "• Stock •" : "Stock");
    }

    // ==========================================
    // 1. NUEVA ORDEN DE COMPRA (POST)
    // ==========================================
    private void guardarOrden() {
        String nombre = txtNombre.getText().toString().trim();
        String direccion = txtDireccion.getText().toString().trim();
        String telefono = txtTelefono.getText().toString().trim();
        String productoIdStr = txtProductoId.getText().toString().trim();
        String cantidadStr = txtCantidad.getText().toString().trim();

        if (nombre.isEmpty()) {
            mostrarMensaje("Validación", "Por favor ingrese el nombre del comprador.");
            return;
        }
        if (productoIdStr.isEmpty()) {
            mostrarMensaje("Validación", "Por favor ingrese el código del producto.");
            return;
        }
        if (cantidadStr.isEmpty()) {
            mostrarMensaje("Validación", "Por favor ingrese la cantidad.");
            return;
        }

        try {
            int prodId = Integer.parseInt(productoIdStr);
            int cant = Integer.parseInt(cantidadStr);

            JSONObject json = new JSONObject();
            json.put("nombreCliente", nombre);
            json.put("direccionEnvio", direccion.isEmpty() ? "Ciudad" : direccion);
            json.put("telefono", telefono.isEmpty() ? "N/A" : telefono);
            json.put("productoId", prodId);
            json.put("cantidad", cant);
            json.put("estado", "PENDIENTE");

            peticionHttp(urlServidor + "/pedidos", "POST", json.toString(), (exito, respuesta) -> {
                if (exito) {
                    mostrarMensaje("Orden Guardada", "La orden de compra se registró con éxito en la base de datos.");
                    limpiarFormularioNuevaOrden();
                } else {
                    mostrarMensaje("Error", "No se pudo registrar la orden:\n" + respuesta);
                }
            });

        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "El código de producto y la cantidad deben ser números enteros.");
        } catch (Exception e) {
            mostrarMensaje("Error", "Error al procesar la solicitud: " + e.getMessage());
        }
    }

    private void limpiarFormularioNuevaOrden() {
        txtNombre.setText("");
        txtDireccion.setText("");
        txtTelefono.setText("");
        txtProductoId.setText("");
        txtCantidad.setText("1");
    }

    // ==========================================
    // 2. MODIFICAR ORDEN (PUT)
    // ==========================================
    private void modificarOrden() {
        String idStr = txtModificarId.getText().toString().trim();
        String estado = cmbEstado.getSelectedItem().toString();

        if (idStr.isEmpty()) {
            mostrarMensaje("Validación", "Ingrese el número de orden a modificar.");
            return;
        }

        try {
            int id = Integer.parseInt(idStr);

            JSONObject json = new JSONObject();
            json.put("estado", estado);

            peticionHttp(urlServidor + "/pedidos/" + id, "PUT", json.toString(), (exito, respuesta) -> {
                if (exito) {
                    mostrarMensaje("Orden Actualizada", "La orden #" + id + " fue actualizada a estado: " + estado);
                    txtModificarId.setText("");
                } else {
                    mostrarMensaje("Error", "No se pudo actualizar la orden:\n" + respuesta);
                }
            });

        } catch (NumberFormatException e) {
            mostrarMensaje("Error", "El número de orden debe ser un valor numérico.");
        } catch (Exception e) {
            mostrarMensaje("Error", "Error: " + e.getMessage());
        }
    }

    // ==========================================
    // 3. CONSULTAR STOCK DE PRODUCTOS (GET)
    // ==========================================
    private void consultarStock() {
        txtResultadoStock.setText("Consultando disponibilidad de stock...");

        peticionHttp(urlServidor + "/productos", "GET", null, (exito, respuesta) -> {
            if (exito) {
                try {
                    JSONObject obj = new JSONObject(respuesta);
                    JSONArray arr = obj.optJSONArray("data");

                    if (arr != null && arr.length() > 0) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject p = arr.getJSONObject(i);
                            sb.append("ID: ").append(p.optInt("id"))
                                    .append(" - ").append(p.optString("nombre"))
                                    .append("\n  Precio: Q. ").append(String.format("%.2f", p.optDouble("precio")))
                                    .append(" | Stock disponible: ").append(p.optInt("stock"))
                                    .append("\n\n");
                        }
                        txtResultadoStock.setText(sb.toString().trim());
                    } else {
                        txtResultadoStock.setText("No hay productos disponibles.");
                    }
                } catch (Exception e) {
                    txtResultadoStock.setText(respuesta);
                }
            } else {
                txtResultadoStock.setText("Error al consultar el inventario:\n" + respuesta);
            }
        });
    }

    // ==========================================
    // MENÚ DE OPCIONES (CONFIGURAR URL / PROBAR)
    // ==========================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_probar) {
            probarConexion();
            return true;
        } else if (id == R.id.menu_url) {
            abrirDialogoUrl();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void probarConexion() {
        peticionHttp(urlServidor + "/status", "GET", null, (exito, respuesta) -> {
            if (exito) {
                mostrarMensaje("Conexión Exitosa", "Servidor conectado y base de datos lista.\n\n" + urlServidor);
            } else {
                mostrarMensaje("Error de Conexión", "No se pudo conectar al servidor:\n" + respuesta);
            }
        });
    }

    private void abrirDialogoUrl() {
        final EditText input = new EditText(this);
        input.setText(urlServidor);

        new AlertDialog.Builder(this)
                .setTitle("Configurar URL del Servidor")
                .setMessage("Ingrese la URL de la API (ej: ngrok o 10.0.2.2):")
                .setView(input)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevaUrl = input.getText().toString().trim();
                    if (!nuevaUrl.isEmpty()) {
                        if (nuevaUrl.endsWith("/")) {
                            nuevaUrl = nuevaUrl.substring(0, nuevaUrl.length() - 1);
                        }
                        urlServidor = nuevaUrl;
                        prefs.edit().putString("url_api", urlServidor).apply();
                        Toast.makeText(this, "URL guardada", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // ==========================================
    // COMUNICACIÓN HTTP
    // ==========================================
    interface HttpCallback {
        void onResponse(boolean exito, String respuesta);
    }

    private void peticionHttp(String urlStr, String metodo, String body, HttpCallback callback) {
        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod(metodo);
                conn.setConnectTimeout(8000);
                conn.setReadTimeout(8000);
                conn.setRequestProperty("Accept", "application/json");

                if (body != null && (metodo.equals("POST") || metodo.equals("PUT"))) {
                    conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) {
                        os.write(body.getBytes(StandardCharsets.UTF_8));
                    }
                }

                int code = conn.getResponseCode();
                InputStream is = (code >= 200 && code < 400) ? conn.getInputStream() : conn.getErrorStream();

                StringBuilder sb = new StringBuilder();
                if (is != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line).append("\n");
                        }
                    }
                }

                boolean exito = (code >= 200 && code < 300);
                String respuesta = sb.toString().trim();

                runOnUiThread(() -> callback.onResponse(exito, respuesta));

            } catch (Exception e) {
                runOnUiThread(() -> callback.onResponse(false, e.getMessage()));
            }
        }).start();
    }

    private void mostrarMensaje(String titulo, String mensaje) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(mensaje)
                .setPositiveButton("Aceptar", null)
                .show();
    }
}
