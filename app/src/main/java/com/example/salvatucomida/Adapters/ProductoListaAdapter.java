package com.example.salvatucomida.Adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salvatucomida.Models.ListaCompra;
import com.example.salvatucomida.R;

import java.util.List;
import java.util.Locale;

public class ProductoListaAdapter
        extends RecyclerView.Adapter<ProductoListaAdapter.ViewHolder> {

    public interface OnItemListener {
        void onMarcar(ListaCompra producto);
        void onDesmarcar(ListaCompra producto);
        void onEliminar(ListaCompra producto);
        void onPasarAlInventario(ListaCompra producto);
    }

    private List<ListaCompra> lista;
    private OnItemListener listener;

    public ProductoListaAdapter(List<ListaCompra> lista, OnItemListener listener) {
        this.lista = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_listacompra, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ListaCompra p = lista.get(position);

        h.txtNombre.setText(p.getNombre());
        h.txtEmoji.setText(obtenerEmoji(p.getNombre()));

        if (p.getPrecio() <= 0) {
            if (p.isComprado()) {
                h.txtPrecio.setText("Gratis");
                h.txtPrecio.setTextColor(0xFF999999);
            } else {
                h.txtPrecio.setText("S/ --");
                h.txtPrecio.setTextColor(0xFFAAAAAA);
            }
        } else {
            h.txtPrecio.setText(String.format(Locale.getDefault(), "S/ %.2f", p.getPrecio()));
            h.txtPrecio.setTextColor(p.isComprado() ? 0xFF1D4327 : 0xFF333333);
        }

        if (p.isComprado()) {
            h.txtNombre.setPaintFlags(h.txtNombre.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            h.txtNombre.setTextColor(0xFF999999);
        } else {
            h.txtNombre.setPaintFlags(h.txtNombre.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            h.txtNombre.setTextColor(0xFF222222);
        }

        h.chkComprado.setOnCheckedChangeListener(null);
        h.chkComprado.setChecked(p.isComprado());

        View.OnClickListener toggle = v -> {
            if (listener == null) return;
            if (p.isComprado()) listener.onDesmarcar(p);
            else listener.onMarcar(p);
        };
        h.itemView.setOnClickListener(toggle);
        h.chkComprado.setOnClickListener(toggle);

        if (p.isComprado()) {
            h.btnInventario.setVisibility(View.VISIBLE);
            h.btnInventario.setOnClickListener(v -> {
                if (listener != null) listener.onPasarAlInventario(p);
            });
        } else {
            h.btnInventario.setVisibility(View.GONE);
            h.btnInventario.setOnClickListener(null);
        }

        h.btnEliminar.setOnClickListener(v -> {
            if (listener != null) listener.onEliminar(p);
        });
    }

    @Override
    public int getItemCount() {
        return lista == null ? 0 : lista.size();
    }

    private String obtenerEmoji(String nombre) {
        if (nombre == null) return "🛒";
        String n = nombre.toLowerCase();
        if (n.contains("huevo"))   return "🥚";
        if (n.contains("arroz"))   return "🍚";
        if (n.contains("leche"))   return "🥛";
        if (n.contains("manzana")) return "🍎";
        if (n.contains("pan"))     return "🍞";
        if (n.contains("pollo"))   return "🍗";
        if (n.contains("carne"))   return "🥩";
        if (n.contains("pescado")) return "🐟";
        if (n.contains("agua"))    return "💧";
        if (n.contains("tomate"))  return "🍅";
        if (n.contains("cebolla")) return "🧅";
        if (n.contains("plátano") || n.contains("platano")) return "🍌";
        if (n.contains("naranja")) return "🍊";
        if (n.contains("queso"))   return "🧀";
        if (n.contains("yogur"))   return "🥛";
        return "🛒";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox chkComprado;
        TextView txtEmoji, txtNombre, txtPrecio, btnInventario, btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chkComprado   = itemView.findViewById(R.id.chkComprado);
            txtEmoji      = itemView.findViewById(R.id.txtEmoji);
            txtNombre     = itemView.findViewById(R.id.txtNombre);
            txtPrecio     = itemView.findViewById(R.id.txtPrecio);
            btnInventario = itemView.findViewById(R.id.btnInventario);
            btnEliminar   = itemView.findViewById(R.id.btnEliminar);
        }
    }
}