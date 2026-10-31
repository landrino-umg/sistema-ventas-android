package gt.edu.umg.sistema.ventas.movil.model;

import java.io.Serializable;

/**
 * DTO que representa un item en una solicitud de creación o edición de pedido.
 */
public class ItemPedidoRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int productoId;
    private int cantidad;

    public ItemPedidoRequest() {
    }

    public ItemPedidoRequest(int productoId, int cantidad) {
        this.productoId = productoId;
        this.cantidad = cantidad;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
