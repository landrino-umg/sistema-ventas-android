package gt.edu.umg.sistema.ventas.movil.model;

import java.io.Serializable;

public class DetallePedido implements Serializable {
    private int id;
    private int pedidoId;
    private int productoId;
    private String productoNombre;
    private int cantidad;
    private double precio;
    private double subtotal;

    public DetallePedido() {}

    public DetallePedido(int productoId, String productoNombre, int cantidad, double precio) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precio = precio;
        this.subtotal = Math.round(cantidad * precio * 100.0) / 100.0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(int pedidoId) {
        this.pedidoId = pedidoId;
    }

    public int getProductoId() {
        return productoId;
    }

    public void setProductoId(int productoId) {
        this.productoId = productoId;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        this.subtotal = Math.round(this.cantidad * this.precio * 100.0) / 100.0;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
        this.subtotal = Math.round(this.cantidad * this.precio * 100.0) / 100.0;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }
}
