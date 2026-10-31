package gt.edu.umg.sistema.ventas.movil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import gt.edu.umg.sistema.ventas.movil.adapter.OrdenAdapter;
import gt.edu.umg.sistema.ventas.movil.model.Pedido;
import gt.edu.umg.sistema.ventas.movil.network.ApiClient;
import gt.edu.umg.sistema.ventas.movil.network.ConfigManager;

/**
 * Pantalla principal que lista las órdenes de venta registradas a través de la API REST.
 * Permite ver el estado, abrir el formulario para crear nuevas órdenes o editar las existentes.
 */
public class MainActivity extends AppCompatActivity implements OrdenAdapter.OnOrdenClickListener {

    private TextView tvUrlActual;
    private MaterialButton btnCambiarUrl;
    private ProgressBar progressBar;
    private View layoutVacio;
    private RecyclerView rvOrdenes;
    private ExtendedFloatingActionButton fabNuevaOrden;

    private OrdenAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        tvUrlActual = findViewById(R.id.tvUrlActual);
        btnCambiarUrl = findViewById(R.id.btnCambiarUrl);
        progressBar = findViewById(R.id.progressBar);
        layoutVacio = findViewById(R.id.layoutVacio);
        rvOrdenes = findViewById(R.id.rvOrdenes);
        fabNuevaOrden = findViewById(R.id.fabNuevaOrden);

        // Configurar RecyclerView
        rvOrdenes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrdenAdapter(this);
        rvOrdenes.setAdapter(adapter);

        // Eventos
        actualizarTextoUrl();
        btnCambiarUrl.setOnClickListener(v -> mostrarDialogoConfigUrl());

        fabNuevaOrden.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, FormularioOrdenActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarTextoUrl();
        cargarOrdenes();
    }

    private void actualizarTextoUrl() {
        String url = ConfigManager.getBaseUrl(this);
        tvUrlActual.setText("API: " + url);
    }

    private void cargarOrdenes() {
        progressBar.setVisibility(View.VISIBLE);
        layoutVacio.setVisibility(View.GONE);

        ApiClient.getInstance().getPedidos(this, new ApiClient.ApiCallback<List<Pedido>>() {
            @Override
            public void onSuccess(List<Pedido> pedidos) {
                progressBar.setVisibility(View.GONE);
                adapter.setLista(pedidos);
                if (pedidos == null || pedidos.isEmpty()) {
                    layoutVacio.setVisibility(View.VISIBLE);
                } else {
                    layoutVacio.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                layoutVacio.setVisibility(View.VISIBLE);
                Snackbar.make(rvOrdenes, "Error API: " + error, Snackbar.LENGTH_LONG)
                        .setAction("Reintentar", v -> cargarOrdenes())
                        .show();
            }
        });
    }

    @Override
    public void onEditarOrden(Pedido pedido) {
        Intent intent = new Intent(MainActivity.this, FormularioOrdenActivity.class);
        intent.putExtra("PEDIDO_ID", pedido.getId());
        startActivity(intent);
    }

    private void mostrarDialogoConfigUrl() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_config_url, null);
        TextInputEditText etUrl = view.findViewById(R.id.etConfigUrl);
        MaterialButton btnEmulador = view.findViewById(R.id.btnPresetEmulador);
        MaterialButton btnLocalhost = view.findViewById(R.id.btnPresetLocalhost);

        etUrl.setText(ConfigManager.getBaseUrl(this));

        btnEmulador.setOnClickListener(v -> etUrl.setText("http://10.0.2.2:8080/api"));
        btnLocalhost.setOnClickListener(v -> etUrl.setText("http://localhost:8080/api"));

        new AlertDialog.Builder(this)
                .setTitle("Configurar Conexión API")
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nuevaUrl = etUrl.getText() != null ? etUrl.getText().toString().trim() : "";
                    if (!nuevaUrl.isEmpty()) {
                        ConfigManager.setBaseUrl(this, nuevaUrl);
                        actualizarTextoUrl();
                        Toast.makeText(this, "URL actualizada", Toast.LENGTH_SHORT).show();
                        cargarOrdenes();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_actualizar) {
            cargarOrdenes();
            return true;
        } else if (id == R.id.action_config_url) {
            mostrarDialogoConfigUrl();
            return true;
        } else if (id == R.id.action_probar_conexion) {
            probarConexionApi();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void probarConexionApi() {
        progressBar.setVisibility(View.VISIBLE);
        ApiClient.getInstance().testConnection(this, new ApiClient.ApiCallback<String>() {
            @Override
            public void onSuccess(String result) {
                progressBar.setVisibility(View.GONE);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Conexión Exitosa ✅")
                        .setMessage(result + "\n\nServidor API REST respondiendo correctamente.")
                        .setPositiveButton("OK", null)
                        .show();
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Error de Conexión ❌")
                        .setMessage("No se pudo conectar con la API REST:\n" + error + "\n\nVerifica que el servidor esté activo y que la URL de ngrok sea correcta.")
                        .setPositiveButton("Configurar URL", (d, w) -> mostrarDialogoConfigUrl())
                        .setNegativeButton("Cerrar", null)
                        .show();
            }
        });
    }
}
