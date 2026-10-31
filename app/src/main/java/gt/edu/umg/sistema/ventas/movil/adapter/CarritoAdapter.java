package gt.edu.umg.sistema.ventas.movil.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import gt.edu.umg.sistema.ventas.movil.R;
import gt.edu.umg.sistema.ventas.movil.model.DetallePedido;

/**
 * Adaptador para listar los ítems que componen la orden en FormularioOrdenActivity.
 */
public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    public interface OnItemEliminarListener {
        void onEliminar(int position, DetallePedido item);
    }

    private final List<DetallePedido> items = new ArrayList<>();
    private final OnItemEliminarListener listener;

    public CarritoAdapter(OnItemEliminarListener listener) {
        this.listener = listener;
    }

    public List<DetallePedido> getItems() {
        return items;
    }

    public void setItems(List<DetallePedido> nuevosItems) {
        this.items.clear();
        if (nuevosItems != null) {
            this.items.addAll(nuevosItems);
        }
        notifyDataSetChanged();
    }

    public void agregarItem(DetallePedido item) {
        // Si el producto ya está en el carrito, sumamos la cantidad
        for (int i = 0; i < items.size(); i++) {
            DetallePedido actual = items.get(i);
            if (actual.getProductoId() == item.getProductoId()) {
                actual.setCantidad(actual.getCantidad() + item.getCantidad());
                notifyItemChanged(i);
                return;
            }
        }
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }

    public void removerItem(int position) {
        if (position >= 0 && position < items.size()) {
            items.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, items.size());
        }
    }

    public void limpiar() {
        this.items.clear();
        notifyDataSetChanged();
    }

    public double calcularTotal() {
        double total = 0.0;
        for (DetallePedido item : items) {
            total += item.getSubtotal();
        }
        return Math.round(total * 100.0) / 100.0;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        DetallePedido item = items.get(position);

        holder.tvNombre.setText(item.getProductoNombre());
        holder.tvCalculo.setText(String.format(Locale.getDefault(), "%d x Q %.2f", item.getCantidad(), item.getPrecio()));
        holder.tvSubtotal.setText(String.format(Locale.getDefault(), "Q %.2f", item.getSubtotal()));

        holder.btnEliminar.setOnClickListener(v -> {
            int currentPos = holder.getAdapterPosition();
            if (currentPos != RecyclerView.NO_POSITION && listener != null) {
                listener.onEliminar(currentPos, item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CarritoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvCalculo;
        TextView tvSubtotal;
        ImageButton btnEliminar;

        public CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvCarritoNombre);
            tvCalculo = itemView.findViewById(R.id.tvCarritoCalculo);
            tvSubtotal = itemView.findViewById(R.id.tvCarritoSubtotal);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItemCarrito);
        }
    }
}
