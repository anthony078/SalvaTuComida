package com.example.salvatucomida;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.salvatucomida.DataAcess.DAOListaCompra;
import com.example.salvatucomida.DataAcess.DAOProducto;
import com.example.salvatucomida.DataAcess.DAOReceta;
import com.example.salvatucomida.Models.ListaCompra;
import com.example.salvatucomida.Models.Producto;
import com.example.salvatucomida.Models.Receta;

import java.util.Calendar;
import java.util.List;

public class MainActivity_Inicio extends AppCompatActivity {

    private TextView tvGreeting, tvSum1Num, tvSum2Num, tvSum3Num, tvSum4Num;
    private LinearLayout layoutExpirations;

    private DAOProducto daoProducto;
    private DAOListaCompra daoListaCompra;
    private DAOReceta daoReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvSum1Num = findViewById(R.id.tvSum1Num);
        tvSum2Num = findViewById(R.id.tvSum2Num);
        tvSum3Num = findViewById(R.id.tvSum3Num);
        tvSum4Num = findViewById(R.id.tvSum4Num);
        layoutExpirations = findViewById(R.id.layoutExpirations);

        daoProducto = new DAOProducto(this);
        daoListaCompra = new DAOListaCompra(this);
        daoReceta = new DAOReceta(this);

        configurarSaludo();
        configurarNavegacion();
        configurarBotonesRapidos();

        // En Inicio, el botón "Atrás" por defecto cierra la app, que es el comportamiento esperado.
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosReales();
    }

    private void configurarSaludo() {
        int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hora >= 6 && hora < 12) tvGreeting.setText("☀️ Buenos días");
        else if (hora >= 12 && hora < 18) tvGreeting.setText("🌤️ Buenas tardes");
        else tvGreeting.setText("🌙 Buenas noches");
    }

    private void cargarDatosReales() {
        List<Producto> inventario = daoProducto.obtenerTodos();
        int totalProductos = inventario != null ? inventario.size() : 0;
        int productosPorVencer = 0;

        layoutExpirations.removeAllViews();

        if (inventario != null) {
            int mostrados = 0;
            for (Producto p : inventario) {
                int dias = calcularDiasRestantes(p.getFechaVencimiento());
                if (dias <= 2) productosPorVencer++;

                if (dias <= 7 && mostrados < 3) {
                    agregarVistaVencimiento(p, dias);
                    mostrados++;
                }
            }
        }
        tvSum1Num.setText(String.valueOf(totalProductos));
        tvSum2Num.setText(String.valueOf(productosPorVencer));

        List<Receta> recetas = daoReceta.obtenerTodas();
        tvSum3Num.setText(String.valueOf(recetas != null ? recetas.size() : 0));

        List<ListaCompra> compras = daoListaCompra.obtenerTodas();
        int porComprar = 0;
        if (compras != null) {
            for (ListaCompra c : compras) if (!c.isComprado()) porComprar++;
        }
        tvSum4Num.setText(String.valueOf(porComprar));
    }

    private void agregarVistaVencimiento(Producto producto, int diasRestantes) {
        View view = LayoutInflater.from(this).inflate(R.layout.item_inicio_vencimiento, layoutExpirations, false);

        TextView txtEmoji = view.findViewById(R.id.txtEmojiVencimiento);
        TextView txtNombre = view.findViewById(R.id.txtNombreVencimiento);
        TextView txtDetalle = view.findViewById(R.id.txtDetalleVencimiento);
        TextView txtEstado = view.findViewById(R.id.txtEstadoVencimiento);
        View viewIndicador = view.findViewById(R.id.viewIndicador);

        txtEmoji.setText(producto.getEmoji() != null ? producto.getEmoji() : "🍎");
        txtNombre.setText(producto.getNombre());

        String cant = producto.getCantidadValor() % 1 == 0
                ? String.valueOf((int) producto.getCantidadValor())
                : String.valueOf(producto.getCantidadValor());
        txtDetalle.setText(cant + " " + producto.getUnidad() + " • " + obtenerTextoVencimiento(diasRestantes));

        if (diasRestantes <= 1) {
            txtEstado.setText("URGENTE");
            txtEstado.setBackgroundColor(0xFFFFCDD2);
            txtEstado.setTextColor(0xFFB71C1C);
            viewIndicador.setBackgroundColor(Color.parseColor("#B71C1C"));
        } else if (diasRestantes <= 4) {
            txtEstado.setText("PRONTO");
            txtEstado.setBackgroundColor(0xFFFFE0B2);
            txtEstado.setTextColor(0xFFE65100);
            viewIndicador.setBackgroundColor(Color.parseColor("#E65100"));
        } else {
            txtEstado.setText("BIEN");
            txtEstado.setBackgroundColor(0xFFC8E6C9);
            txtEstado.setTextColor(0xFF1B5E20);
            viewIndicador.setBackgroundColor(Color.parseColor("#1B5E20"));
        }

        layoutExpirations.addView(view);
    }

    private int calcularDiasRestantes(long fechaVencimiento) {
        if (fechaVencimiento <= 0) return 999;
        Calendar hoy = Calendar.getInstance();
        Calendar venc = Calendar.getInstance();
        venc.setTimeInMillis(fechaVencimiento);
        hoy.set(Calendar.HOUR_OF_DAY, 0); hoy.set(Calendar.MINUTE, 0); hoy.set(Calendar.SECOND, 0); hoy.set(Calendar.MILLISECOND, 0);
        venc.set(Calendar.HOUR_OF_DAY, 0); venc.set(Calendar.MINUTE, 0); venc.set(Calendar.SECOND, 0); venc.set(Calendar.MILLISECOND, 0);
        long diff = venc.getTimeInMillis() - hoy.getTimeInMillis();
        return (int) (diff / (1000L * 60L * 60L * 24L));
    }

    private String obtenerTextoVencimiento(int diasRestantes) {
        if (diasRestantes < 0) return "Venció hace " + Math.abs(diasRestantes) + " días";
        if (diasRestantes == 0) return "Vence HOY";
        if (diasRestantes == 1) return "Vence mañana";
        return "Vence en " + diasRestantes + " días";
    }

    private void configurarBotonesRapidos() {
        findViewById(R.id.btnQuickAgregar).setOnClickListener(v -> irA(MainActivity_Lista.class));
        findViewById(R.id.btnQuickReceta).setOnClickListener(v -> irA(MainActivity_Recetas.class));
        findViewById(R.id.btnVerTodosVencimientos).setOnClickListener(v -> irA(MainActivity_Inventario.class));
    }

    private void configurarNavegacion() {
        LinearLayout navInicio = findViewById(R.id.navInicio);
        LinearLayout navInventario = findViewById(R.id.navInventario);
        LinearLayout navRecetas = findViewById(R.id.navRecetas);
        LinearLayout navCompras = findViewById(R.id.navCompras);
        LinearLayout navPerfil = findViewById(R.id.navPerfil);

        navInicio.setOnClickListener(v -> {
            // Ya estamos en Inicio, no hacemos nada
        });
        navInventario.setOnClickListener(v -> irA(MainActivity_Inventario.class));
        navRecetas.setOnClickListener(v -> irA(MainActivity_Recetas.class));
        navCompras.setOnClickListener(v -> irA(MainActivity_Lista.class));
        navPerfil.setOnClickListener(v -> irA(MainActivity_Perfil.class));
    }

    private void irA(Class<?> destino) {
        startActivity(new Intent(this, destino));
        overridePendingTransition(0, 0);
        finish();
    }
}