package gt.edu.umg.sistema.ventas.movil.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO para enviar al endpoint POST o PUT de pedidos.
 */
public class CrearPedidoRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreCliente;
    private String direccionEnvio;
    private String telefono;
    private String estado;
    private List<ItemPedidoRequest> items = new ArrayList<>();

    public CrearPedidoRequest() {
        this.estado = "PENDIENTE";
    }

    public CrearPedidoRequest(String nombreCliente, String direccionEnvio, String telefono, String estado, List<ItemPedidoRequest> items) {
        this.nombreCliente = nombreCliente;
        this.direccionEnvio = direccionEnvio;
        this.telefono = telefono;
        this.estado = estado;
        this.items = items;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<ItemPedidoRequest> getItems() {
        return items;
    }

    public void setItems(List<ItemPedidoRequest> items) {
        this.items = items;
    }

    public void addItem(int productoId, int cantidad) {
        this.items.add(new ItemPedidoRequest(productoId, cantidad));
    }
}
