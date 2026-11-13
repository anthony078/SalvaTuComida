package com.example.salvatucomida.Adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salvatucomida.Models.Receta;
import com.example.salvatucomida.R;

import java.util.List;

public class RecetaAdapter extends RecyclerView.Adapter<RecetaAdapter.ViewHolder> {

    // Agregamos las opciones de editar y eliminar
    public interface OnRecetaListener {
        void onRecetaClick(Receta receta);
        void onRecetaLongClick(Receta receta);
    }

    private List<Receta> listaRecetas;
    private List<Integer> faltantes;
    private OnRecetaListener listener;

    public RecetaAdapter(List<Receta> listaRecetas, List<Integer> faltantes, OnRecetaListener listener) {
        this.listaRecetas = listaRecetas;
        this.faltantes = faltantes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receta, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Receta receta = listaRecetas.get(position);
        int faltan = (faltantes != null && faltantes.size() > position) ? faltantes.get(position) : 0;

        h.txtNombreReceta.setText(receta.getNombre());
        h.txtEmojiReceta.setText(receta.getEmoji() != null ? receta.getEmoji() : "🍲");

        String tiempo = receta.getTiempoPreparacion() > 0 ? receta.getTiempoPreparacion() + " min" : "-- min";
        h.txtTiempo.setText("⏱️ " + tiempo + " · " + (receta.getTipoComida() != null ? receta.getTipoComida() : "Comida"));

        if (faltan == 0) {
            h.txtIngredientesFaltantes.setText("✅ Tienes todos los ingredientes");
            h.txtIngredientesFaltantes.setTextColor(0xFF245B35);
            h.txtEstadoReceta.setText("¡LISTO!");
            aplicarEstilo(h.txtEstadoReceta, "#EAF3EA", "#245B35");
        } else {
            h.txtIngredientesFaltantes.setText("❌ Te faltan " + faltan + " ingrediente" + (faltan > 1 ? "s" : ""));
            h.txtIngredientesFaltantes.setTextColor(0xFFC0392B);
            h.txtEstadoReceta.setText("FALTAN");
            aplicarEstilo(h.txtEstadoReceta, "#FFF0ED", "#C0392B");
        }

        // Clic normal
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecetaClick(receta);
        });

        // 🆕 Clic largo (Mantener presionado)
        h.itemView.setOnLongClickListener(v -> {
            if (listener != null) listener.onRecetaLongClick(receta);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return listaRecetas.size();
    }

    private void aplicarEstilo(TextView textView, String colorFondo, String colorTexto) {
        GradientDrawable fondo = new GradientDrawable();
        fondo.setColor(Color.parseColor(colorFondo));
        fondo.setStroke(1, Color.parseColor(colorTexto));
        fondo.setCornerRadius(12);
        textView.setBackground(fondo);
        textView.setTextColor(Color.parseColor(colorTexto));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtEmojiReceta, txtNombreReceta, txtTiempo, txtIngredientesFaltantes, txtEstadoReceta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtEmojiReceta = itemView.findViewById(R.id.txtEmojiReceta);
            txtNombreReceta = itemView.findViewById(R.id.txtNombreReceta);
            txtTiempo = itemView.findViewById(R.id.txtTiempo);
            txtIngredientesFaltantes = itemView.findViewById(R.id.txtIngredientesFaltantes);
            txtEstadoReceta = itemView.findViewById(R.id.txtEstadoReceta);
        }
    }
}