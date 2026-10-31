package gt.edu.umg.sistema.ventas.movil.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pedido implements Serializable {
    private int id;
    private int clienteId;
    private String clienteNombre;
    private Integer direccionEnvioId;
    private String direccionTexto;
    private String telefonoContacto;
    private String fecha;
    private String estado;
    private double total;
    private List<DetallePedido> detalles = new ArrayList<>();

    public Pedido() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public void setClienteId(int clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }

    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public Integer getDireccionEnvioId() {
        return direccionEnvioId;
    }

    public void setDireccionEnvioId(Integer direccionEnvioId) {
        this.direccionEnvioId = direccionEnvioId;
    }

    public String getDireccionTexto() {
        return direccionTexto;
    }

    public void setDireccionTexto(String direccionTexto) {
        this.direccionTexto = direccionTexto;
    }

    public String getTelefonoContacto() {
        return telefonoContacto;
    }

    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }
}
