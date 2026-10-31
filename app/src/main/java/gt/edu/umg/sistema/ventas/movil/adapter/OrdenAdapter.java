package gt.edu.umg.sistema.ventas.movil.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gt.edu.umg.sistema.ventas.movil.R;
import gt.edu.umg.sistema.ventas.movil.model.Pedido;

/**
 * Adaptador para listar pedidos/órdenes en MainActivity.
 */
public class OrdenAdapter extends RecyclerView.Adapter<OrdenAdapter.OrdenViewHolder> {

    public interface OnOrdenClickListener {
        void onEditarOrden(Pedido pedido);
    }

    private final List<Pedido> lista = new ArrayList<>();
    private final OnOrdenClickListener listener;

    public OrdenAdapter(OnOrdenClickListener listener) {
        this.listener = listener;
    }

    public void setLista(List<Pedido> nuevosPedidos) {
        this.lista.clear();
        if (nuevosPedidos != null) {
            this.lista.addAll(nuevosPedidos);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrdenViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_orden, parent, false);
        return new OrdenViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdenViewHolder holder, int position) {
        Pedido pedido = lista.get(position);
        Context ctx = holder.itemView.getContext();

        holder.tvNumeroOrden.setText("Orden #" + pedido.getId());

        String estado = (pedido.getEstado() != null) ? pedido.getEstado() : "PENDIENTE";
        holder.tvBadgeEstado.setText(estado);

        // Estilo según estado
        if ("COMPLETADA".equalsIgnoreCase(estado)) {
            holder.tvBadgeEstado.setBackgroundResource(R.drawable.badge_completada);
            holder.tvBadgeEstado.setTextColor(ContextCompat.getColor(ctx, R.color.badge_completada_text));
        } else if ("CANCELADA".equalsIgnoreCase(estado)) {
            holder.tvBadgeEstado.setBackgroundResource(R.drawable.badge_cancelada);
            holder.tvBadgeEstado.setTextColor(ContextCompat.getColor(ctx, R.color.badge_cancelada_text));
        } else {
            holder.tvBadgeEstado.setBackgroundResource(R.drawable.badge_pendiente);
            holder.tvBadgeEstado.setTextColor(ContextCompat.getColor(ctx, R.color.badge_pendiente_text));
        }

        String cliente = (pedido.getClienteNombre() != null && !pedido.getClienteNombre().isEmpty())
                ? pedido.getClienteNombre() : "Consumidor Final";
        holder.tvCliente.setText("👤 Cliente: " + cliente);

        if (pedido.getDireccionTexto() != null && !pedido.getDireccionTexto().isEmpty()) {
            holder.tvDireccion.setVisibility(View.VISIBLE);
            holder.tvDireccion.setText("📍 " + pedido.getDireccionTexto());
        } else {
            holder.tvDireccion.setVisibility(View.GONE);
        }

        if (pedido.getFecha() != null) {
            holder.tvFecha.setText("📅 " + pedido.getFecha());
        } else {
            holder.tvFecha.setText("📅 Reciente");
        }

        holder.tvTotal.setText(String.format(Locale.getDefault(), "Total: Q %.2f", pedido.getTotal()));

        holder.btnEditar.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarOrden(pedido);
            }
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEditarOrden(pedido);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    static class OrdenViewHolder extends RecyclerView.ViewHolder {
        TextView tvNumeroOrden;
        TextView tvBadgeEstado;
        TextView tvCliente;
        TextView tvDireccion;
        TextView tvFecha;
        TextView tvTotal;
        MaterialButton btnEditar;

        public OrdenViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroOrden = itemView.findViewById(R.id.tvNumeroOrden);
            tvBadgeEstado = itemView.findViewById(R.id.tvBadgeEstado);
            tvCliente = itemView.findViewById(R.id.tvCliente);
            tvDireccion = itemView.findViewById(R.id.tvDireccion);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            btnEditar = itemView.findViewById(R.id.btnEditar);
        }
    }
}
