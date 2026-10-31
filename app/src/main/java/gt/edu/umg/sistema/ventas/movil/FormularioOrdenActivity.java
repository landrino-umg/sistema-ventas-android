package gt.edu.umg.sistema.ventas.movil;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gt.edu.umg.sistema.ventas.movil.adapter.CarritoAdapter;
import gt.edu.umg.sistema.ventas.movil.model.CrearPedidoRequest;
import gt.edu.umg.sistema.ventas.movil.model.DetallePedido;
import gt.edu.umg.sistema.ventas.movil.model.Pedido;
import gt.edu.umg.sistema.ventas.movil.model.Producto;
import gt.edu.umg.sistema.ventas.movil.network.ApiClient;

/**
 * Actividad para la creación y edición de órdenes de venta.
 * En modo creación realiza un HTTP POST /api/pedidos.
 * En modo edición realiza un HTTP PUT /api/pedidos/{id}.
 */
public class FormularioOrdenActivity extends AppCompatActivity implements CarritoAdapter.OnItemEliminarListener {

    private TextInputEditText etNombreCliente;
    private TextInputEditText etDireccionEnvio;
    private TextInputEditText etTelefono;
    private LinearLayout layoutEstado;
    private Spinner spEstado;

    private Spinner spProductos;
    private TextView tvStockInfo;
    private TextInputEditText etCantidad;
    private TextInputEditText etPrecioUnitario;
    private MaterialButton btnAgregarItem;

    private RecyclerView rvDetalleOrden;
    private TextView tvCarritoVacio;
    private TextView tvTotalOrden;
    private ProgressBar pbEnviando;
    private MaterialButton btnConfirmarEnviar;
    private MaterialButton btnLimpiar;

    private CarritoAdapter carritoAdapter;
    private final List<Producto> listaProductos = new ArrayList<>();
    private Producto productoSeleccionado = null;

    private int pedidoId = -1; // -1 = Creación, > 0 = Edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_orden);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        pedidoId = getIntent().getIntExtra("PEDIDO_ID", -1);

        inicializarVistas();
        configurarRecyclerView();
        configurarSpinners();
        configurarEventos();

        if (pedidoId > 0) {
            setTitle("✏️ Editar Orden #" + pedidoId);
            layoutEstado.setVisibility(View.VISIBLE);
            btnConfirmarEnviar.setText("Guardar Cambios en Orden #" + pedidoId);
            cargarDatosOrdenExistente();
        } else {
            setTitle("➕ Nueva Orden de Venta");
            layoutEstado.setVisibility(View.GONE);
            btnConfirmarEnviar.setText("Crear Orden (POST /api/pedidos)");
        }

        cargarCatalogoProductos();
    }

    private void inicializarVistas() {
        etNombreCliente = findViewById(R.id.etNombreCliente);
        etDireccionEnvio = findViewById(R.id.etDireccionEnvio);
        etTelefono = findViewById(R.id.etTelefono);
        layoutEstado = findViewById(R.id.layoutEstado);
        spEstado = findViewById(R.id.spEstado);

        spProductos = findViewById(R.id.spProductos);
        tvStockInfo = findViewById(R.id.tvStockInfo);
        etCantidad = findViewById(R.id.etCantidad);
        etPrecioUnitario = findViewById(R.id.etPrecioUnitario);
        btnAgregarItem = findViewById(R.id.btnAgregarItem);

        rvDetalleOrden = findViewById(R.id.rvDetalleOrden);
        tvCarritoVacio = findViewById(R.id.tvCarritoVacio);
        tvTotalOrden = findViewById(R.id.tvTotalOrden);
        pbEnviando = findViewById(R.id.pbEnviando);
        btnConfirmarEnviar = findViewById(R.id.btnConfirmarEnviar);
        btnLimpiar = findViewById(R.id.btnLimpiar);
    }

    private void configurarRecyclerView() {
        carritoAdapter = new CarritoAdapter(this);
        rvDetalleOrden.setLayoutManager(new LinearLayoutManager(this));
        rvDetalleOrden.setAdapter(carritoAdapter);
        actualizarVisibilidadCarrito();
    }

    private void configurarSpinners() {
        // Spinner de Estado
        String[] estados = new String[]{"PENDIENTE", "COMPLETADA", "CANCELADA"};
        ArrayAdapter<String> estadoAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, estados);
        spEstado.setAdapter(estadoAdapter);

        // Evento cambio de producto seleccionado
        spProductos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < listaProductos.size()) {
                    productoSeleccionado = listaProductos.get(position);
                    etPrecioUnitario.setText(String.format(Locale.US, "%.2f", productoSeleccionado.getPrecio()));
                    tvStockInfo.setText(String.format(Locale.getDefault(), "Stock disponible: %d unidades | %s",
                            productoSeleccionado.getStock(), productoSeleccionado.getCategoria()));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                productoSeleccionado = null;
            }
        });
    }

    private void configurarEventos() {
        btnAgregarItem.setOnClickListener(v -> agregarProductoAOrden());
        btnConfirmarEnviar.setOnClickListener(v -> guardarOrden());
        btnLimpiar.setOnClickListener(v -> limpiarFormulario());
    }

    private void cargarCatalogoProductos() {
        tvStockInfo.setText("Cargando catálogo de productos...");
        ApiClient.getInstance().getProductos(this, new ApiClient.ApiCallback<List<Producto>>() {
            @Override
            public void onSuccess(List<Producto> productos) {
                listaProductos.clear();
                if (productos != null && !productos.isEmpty()) {
                    listaProductos.addAll(productos);
                    List<String> nombres = new ArrayList<>();
                    for (Producto p : listaProductos) {
                        nombres.add(p.getNombre() + " (Q " + String.format(Locale.US, "%.2f", p.getPrecio()) + ")");
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(FormularioOrdenActivity.this,
                            android.R.layout.simple_spinner_dropdown_item, nombres);
                    spProductos.setAdapter(adapter);
                } else {
                    tvStockInfo.setText("No hay productos en inventario.");
                }
            }

            @Override
            public void onError(String error) {
                tvStockInfo.setText("Error al cargar productos: " + error);
            }
        });
    }

    private void cargarDatosOrdenExistente() {
        pbEnviando.setVisibility(View.VISIBLE);
        ApiClient.getInstance().getPedido(this, pedidoId, new ApiClient.ApiCallback<Pedido>() {
            @Override
            public void onSuccess(Pedido pedido) {
                pbEnviando.setVisibility(View.GONE);
                if (pedido != null) {
                    etNombreCliente.setText(pedido.getClienteNombre());
                    etDireccionEnvio.setText(pedido.getDireccionTexto());
                    etTelefono.setText(pedido.getTelefonoContacto());

                    if (pedido.getEstado() != null) {
                        if ("COMPLETADA".equalsIgnoreCase(pedido.getEstado())) {
                            spEstado.setSelection(1);
                        } else if ("CANCELADA".equalsIgnoreCase(pedido.getEstado())) {
                            spEstado.setSelection(2);
                        } else {
                            spEstado.setSelection(0);
                        }
                    }

                    if (pedido.getDetalles() != null) {
                        carritoAdapter.setItems(pedido.getDetalles());
                        actualizarVisibilidadCarrito();
                    }
                }
            }

            @Override
            public void onError(String error) {
                pbEnviando.setVisibility(View.GONE);
                Toast.makeText(FormularioOrdenActivity.this, "Error al cargar orden: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void agregarProductoAOrden() {
        if (productoSeleccionado == null) {
            Toast.makeText(this, "Selecciona un producto del catálogo", Toast.LENGTH_SHORT).show();
            return;
        }

        String cantStr = etCantidad.getText() != null ? etCantidad.getText().toString().trim() : "";
        if (cantStr.isEmpty()) {
            Toast.makeText(this, "Ingresa una cantidad válida", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Cantidad numérica inválida", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cantidad <= 0) {
            Toast.makeText(this, "La cantidad debe ser mayor a cero", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cantidad > productoSeleccionado.getStock()) {
            Toast.makeText(this, "Stock insuficiente. Disponible: " + productoSeleccionado.getStock(), Toast.LENGTH_LONG).show();
            return;
        }

        DetallePedido detalle = new DetallePedido(
                productoSeleccionado.getId(),
                productoSeleccionado.getNombre(),
                cantidad,
                productoSeleccionado.getPrecio()
        );

        carritoAdapter.agregarItem(detalle);
        actualizarVisibilidadCarrito();
        etCantidad.setText("1");
        Toast.makeText(this, "Producto añadido a la orden", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEliminar(int position, DetallePedido item) {
        carritoAdapter.removerItem(position);
        actualizarVisibilidadCarrito();
    }

    private void actualizarVisibilidadCarrito() {
        if (carritoAdapter.getItemCount() == 0) {
            tvCarritoVacio.setVisibility(View.VISIBLE);
            rvDetalleOrden.setVisibility(View.GONE);
        } else {
            tvCarritoVacio.setVisibility(View.GONE);
            rvDetalleOrden.setVisibility(View.VISIBLE);
        }
        double total = carritoAdapter.calcularTotal();
        tvTotalOrden.setText(String.format(Locale.getDefault(), "TOTAL: Q %.2f", total));
    }

    private void guardarOrden() {
        String nombre = etNombreCliente.getText() != null ? etNombreCliente.getText().toString().trim() : "";
        String direccion = etDireccionEnvio.getText() != null ? etDireccionEnvio.getText().toString().trim() : "";
        String telefono = etTelefono.getText() != null ? etTelefono.getText().toString().trim() : "";

        if (nombre.isEmpty()) {
            etNombreCliente.setError("El nombre del comprador es obligatorio");
            etNombreCliente.requestFocus();
            return;
        }

        if (carritoAdapter.getItems().isEmpty()) {
            Toast.makeText(this, "Debes agregar al menos un producto a la orden", Toast.LENGTH_LONG).show();
            return;
        }

        String estado = spEstado.getSelectedItem() != null ? spEstado.getSelectedItem().toString() : "PENDIENTE";

        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setNombreCliente(nombre);
        request.setDireccionEnvio(direccion);
        request.setTelefono(telefono);
        request.setEstado(estado);

        for (DetallePedido item : carritoAdapter.getItems()) {
            request.addItem(item.getProductoId(), item.getCantidad());
        }

        setBloqueoUI(true);

        if (pedidoId > 0) {
            // Edición HTTP PUT
            ApiClient.getInstance().actualizarPedido(this, pedidoId, request, new ApiClient.ApiCallback<Pedido>() {
                @Override
                public void onSuccess(Pedido pedido) {
                    setBloqueoUI(false);
                    new AlertDialog.Builder(FormularioOrdenActivity.this)
                            .setTitle("Orden Actualizada ✅")
                            .setMessage("La orden #" + pedido.getId() + " ha sido actualizada correctamente en el servidor.")
                            .setPositiveButton("Volver", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                }

                @Override
                public void onError(String error) {
                    setBloqueoUI(false);
                    mostrarErrorEnvio("Error al actualizar orden #" + pedidoId, error);
                }
            });
        } else {
            // Creación HTTP POST
            ApiClient.getInstance().crearPedido(this, request, new ApiClient.ApiCallback<Pedido>() {
                @Override
                public void onSuccess(Pedido pedido) {
                    setBloqueoUI(false);
                    new AlertDialog.Builder(FormularioOrdenActivity.this)
                            .setTitle("¡Orden Creada con Éxito! 🎉")
                            .setMessage("Se generó la Orden #" + pedido.getId() + "\nTotal: Q " + String.format(Locale.getDefault(), "%.2f", pedido.getTotal()) +
                                    "\n\nGuardada en la base de datos central a través del API REST.")
                            .setPositiveButton("Listo", (dialog, which) -> finish())
                            .setCancelable(false)
                            .show();
                }

                @Override
                public void onError(String error) {
                    setBloqueoUI(false);
                    mostrarErrorEnvio("Error al registrar orden en el API", error);
                }
            });
        }
    }

    private void setBloqueoUI(boolean cargando) {
        pbEnviando.setVisibility(cargando ? View.VISIBLE : View.GONE);
        btnConfirmarEnviar.setEnabled(!cargando);
        btnAgregarItem.setEnabled(!cargando);
        btnLimpiar.setEnabled(!cargando);
    }

    private void mostrarErrorEnvio(String titulo, String error) {
        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setMessage(error)
                .setPositiveButton("Entendido", null)
                .show();
    }

    private void limpiarFormulario() {
        etNombreCliente.setText("");
        etDireccionEnvio.setText("");
        etTelefono.setText("");
        etCantidad.setText("1");
        carritoAdapter.limpiar();
        actualizarVisibilidadCarrito();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
