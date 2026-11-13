package com.example.salvatucomida.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.salvatucomida.DataAcess.DAOPrecioTienda;
import com.example.salvatucomida.Models.Tienda;
import com.example.salvatucomida.R;

import java.util.List;
import java.util.Locale;

public class TiendaAdapter extends RecyclerView.Adapter<TiendaAdapter.ViewHolder> {

    // Agregamos onAsignarPrecios a la interfaz
    public interface OnTiendaListener {
        void onEditar(Tienda tienda);
        void onEliminar(Tienda tienda);
        void onAsignarPrecios(Tienda tienda);
    }

    private List<Tienda> lista;
    private OnTiendaListener listener;
    private DAOPrecioTienda daoPrecioTienda;

    public TiendaAdapter(List<Tienda> lista, Context contexto, OnTiendaListener listener) {
        this.lista = lista;
        this.listener = listener;
        this.daoPrecioTienda = new DAOPrecioTienda(contexto);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tienda, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Tienda t = lista.get(position);

        h.txtNombreTienda.setText(t.getNombre());
        h.txtPrecioTienda.setText(String.format(Locale.getDefault(), "S/ %.2f", t.getPrecioTotalEstimado()));

        if (t.getFotoPath() != null && !t.getFotoPath().isEmpty()) {
            h.imgFotoTienda.setVisibility(View.VISIBLE);
            h.txtIconoTienda.setVisibility(View.GONE);
            Glide.with(h.itemView.getContext())
                    .load(Uri.parse(t.getFotoPath()))
                    .circleCrop()
                    .into(h.imgFotoTienda);
        } else {
            h.imgFotoTienda.setVisibility(View.GONE);
            h.txtIconoTienda.setVisibility(View.VISIBLE);
            h.txtIconoTienda.setText("🏪");
        }

        int numProductos = 0;
        try { numProductos = daoPrecioTienda.obtenerPorTienda(t.getId()).size(); } catch (Exception ignored) {}

        StringBuilder detalle = new StringBuilder();
        detalle.append(String.format(Locale.getDefault(), "📍 %.1f km", t.getDistanciaKm()));

        if (numProductos > 0) {
            detalle.append(" · ").append(numProductos).append(" producto").append(numProductos == 1 ? "" : "s");
        }

        if (position == 0 && t.getPrecioTotalEstimado() > 0 && numProductos > 0) {
            detalle.append(" · MEJOR OPCIÓN");
            h.txtDetalleTienda.setTextColor(0xFFB07C00);
            h.txtDetalleTienda.setTypeface(null, Typeface.BOLD);
        } else {
            h.txtDetalleTienda.setTextColor(0xFF77736A);
            h.txtDetalleTienda.setTypeface(null, Typeface.NORMAL);
        }

        h.txtDetalleTienda.setText(detalle.toString());

        // 👉 CLIC CORTO EN LA TARJETA: IR AL MAPA A EDITAR
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onEditar(t);
        });

        // 👉 CLIC LARGO EN LA TARJETA: ELIMINAR
        h.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onEliminar(t);
            return true;
        });

        // 👉 CLIC EN EL BOTÓN DE PRECIOS
        h.btnAsignarPrecios.setOnClickListener(v -> {
            if (listener != null) listener.onAsignarPrecios(t);
        });
    }

    @Override
    public int getItemCount() {
        return lista == null ? 0 : lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtIconoTienda, txtNombreTienda, txtDetalleTienda, txtPrecioTienda, btnAsignarPrecios;
        ImageView imgFotoTienda;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtIconoTienda    = itemView.findViewById(R.id.txtIconoTienda);
            txtNombreTienda   = itemView.findViewById(R.id.txtNombreTienda);
            txtDetalleTienda  = itemView.findViewById(R.id.txtDetalleTienda);
            txtPrecioTienda   = itemView.findViewById(R.id.txtPrecioTienda);
            imgFotoTienda     = itemView.findViewById(R.id.imgFotoTienda);
            btnAsignarPrecios = itemView.findViewById(R.id.btnAsignarPrecios);
        }
    }
}