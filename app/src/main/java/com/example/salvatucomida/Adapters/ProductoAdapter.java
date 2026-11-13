package com.example.salvatucomida.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.salvatucomida.Models.Producto;
import com.example.salvatucomida.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    public interface OnItemListener {
        void onEliminar(Producto producto);
    }

    private List<Producto> listaProductos;
    private OnItemListener listener;

    public ProductoAdapter(List<Producto> listaProductos, OnItemListener listener) {
        this.listaProductos = listaProductos != null ? listaProductos : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto = listaProductos.get(position);
        holder.txtNombreProducto.setText(producto.getNombre());

        // 🆕 LÓGICA FOTO vs EMOJI
        if (producto.getFotoPath() != null && !producto.getFotoPath().isEmpty()) {
            holder.imgFotoProducto.setVisibility(View.VISIBLE);
            holder.txtEmoji.setVisibility(View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(Uri.parse(producto.getFotoPath()))
                    .circleCrop()
                    .into(holder.imgFotoProducto);
        } else {
            holder.imgFotoProducto.setVisibility(View.GONE);
            holder.txtEmoji.setVisibility(View.VISIBLE);
            holder.txtEmoji.setText(producto.getEmoji() != null ? producto.getEmoji() : "🍎");
        }

        String cant = producto.getCantidadValor() % 1 == 0
                ? String.valueOf((int) producto.getCantidadValor())
                : String.valueOf(producto.getCantidadValor());
        holder.txtCantidad.setText(cant + " " + producto.getUnidad());

        int diasRestantes = calcularDiasRestantes(producto.getFechaVencimiento());
        holder.txtInformacionProducto.setText(obtenerTextoVencimiento(diasRestantes));
        configurarEstado(holder.txtEstado, diasRestantes);

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onEliminar(producto);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaProductos.size();
    }

    private int calcularDiasRestantes(long fechaVencimiento) {
        if (fechaVencimiento <= 0) return 999;
        Calendar hoy = Calendar.getInstance();
        Calendar vencimiento = Calendar.getInstance();
        vencimiento.setTimeInMillis(fechaVencimiento);
        hoy.set(Calendar.HOUR_OF_DAY, 0); hoy.set(Calendar.MINUTE, 0); hoy.set(Calendar.SECOND, 0); hoy.set(Calendar.MILLISECOND, 0);
        vencimiento.set(Calendar.HOUR_OF_DAY, 0); vencimiento.set(Calendar.MINUTE, 0); vencimiento.set(Calendar.SECOND, 0); vencimiento.set(Calendar.MILLISECOND, 0);
        long diferencia = vencimiento.getTimeInMillis() - hoy.getTimeInMillis();
        return (int) (diferencia / (24 * 60 * 60 * 1000));
    }

    private String obtenerTextoVencimiento(int diasRestantes) {
        if (diasRestantes < 0) return "⚠️ Venció hace " + Math.abs(diasRestantes) + " días";
        if (diasRestantes == 0) return "⚠️ Vence HOY";
        if (diasRestantes == 1) return "Vence mañana";
        return "Vence en " + diasRestantes + " días";
    }

    private void configurarEstado(TextView textEstado, int diasRestantes) {
        if (diasRestantes <= 1) {
            textEstado.setText("URGENTE");
            aplicarEstiloEstado(textEstado, "#FFF0ED", "#C0392B");
        } else if (diasRestantes <= 4) {
            textEstado.setText("PRONTO");
            aplicarEstiloEstado(textEstado, "#FFF7DD", "#A66A00");
        } else {
            textEstado.setText("BIEN");
            aplicarEstiloEstado(textEstado, "#EAF3EA", "#245B35");
        }
    }

    private void aplicarEstiloEstado(TextView textView, String colorFondo, String colorTexto) {
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.parseColor(colorFondo));
        fondo.setStroke(1, Color.parseColor(colorTexto));
        fondo.setCornerRadius(12);
        textView.setBackground(fondo);
        textView.setTextColor(Color.parseColor(colorTexto));
    }

    public void actualizarLista(List<Producto> nuevaLista) {
        this.listaProductos = nuevaLista != null ? nuevaLista : new ArrayList<>();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmoji, txtNombreProducto, txtCantidad, txtInformacionProducto, txtEstado;
        ImageView imgFotoProducto; // 🆕

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmoji = itemView.findViewById(R.id.txtEmoji);
            txtNombreProducto = itemView.findViewById(R.id.txtNombreProducto);
            txtCantidad = itemView.findViewById(R.id.txtCantidad);
            txtInformacionProducto = itemView.findViewById(R.id.txtInformacionProducto);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            imgFotoProducto = itemView.findViewById(R.id.imgFotoProducto); // 🆕
        }
    }
}