package com.example.salvatucomida;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salvatucomida.Adapters.ProductoAdapter;
import com.example.salvatucomida.DataAcess.DAOProducto;
import com.example.salvatucomida.Models.Producto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity_Inventario extends AppCompatActivity {

    private RecyclerView recyclerProductos;
    private EditText edtBuscarProducto;
    private Button btnTodos, btnUrgentes, btnPronto, btnBien;
    private TextView txtCantidadProductos;
    private LinearLayout navInicio, navInventario, navRecetas, navCompras, navPerfil;
    private Button btnExportarPDF;

    private DAOProducto daoProducto;
    private ProductoAdapter adapter;

    private List<Producto> listaCompleta = new ArrayList<>();
    private List<Producto> listaFiltrada = new ArrayList<>();
    private String filtroActual = "todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventario);

        recyclerProductos = findViewById(R.id.recyclerProductos);
        edtBuscarProducto = findViewById(R.id.edtBuscarProducto);
        btnTodos = findViewById(R.id.btnTodos);
        btnUrgentes = findViewById(R.id.btnUrgentes);
        btnPronto = findViewById(R.id.btnPronto);
        btnBien = findViewById(R.id.btnBien);
        txtCantidadProductos = findViewById(R.id.txtCantidadProductos);
        btnExportarPDF = findViewById(R.id.btnExportarPDF);

        navInicio = findViewById(R.id.navInicio);
        navInventario = findViewById(R.id.navInventario);
        navRecetas = findViewById(R.id.navRecetas);
        navCompras = findViewById(R.id.navCompras);
        navPerfil = findViewById(R.id.navPerfil);

        daoProducto = new DAOProducto(this);

        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoAdapter(listaFiltrada, producto -> confirmarEliminar(producto));
        recyclerProductos.setAdapter(adapter);

        edtBuscarProducto.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                aplicarFiltros(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnTodos.setOnClickListener(v -> { filtroActual = "todos"; aplicarFiltros(edtBuscarProducto.getText().toString()); actualizarBotonActivo(btnTodos); });
        btnUrgentes.setOnClickListener(v -> { filtroActual = "urgentes"; aplicarFiltros(edtBuscarProducto.getText().toString()); actualizarBotonActivo(btnUrgentes); });
        btnPronto.setOnClickListener(v -> { filtroActual = "pronto"; aplicarFiltros(edtBuscarProducto.getText().toString()); actualizarBotonActivo(btnPronto); });
        btnBien.setOnClickListener(v -> { filtroActual = "bien"; aplicarFiltros(edtBuscarProducto.getText().toString()); actualizarBotonActivo(btnBien); });

        btnExportarPDF.setOnClickListener(v -> exportarInventarioPDF());

        configurarNavegacion();
        actualizarBotonActivo(btnTodos);

        // 🆕 CONTROL DEL BOTÓN ATRÁS
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(getApplicationContext(), MainActivity_Inicio.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }

    private void cargarProductos() {
        try {
            List<Producto> productos = daoProducto.obtenerTodos();
            listaCompleta.clear();
            if (productos != null) listaCompleta.addAll(productos);
            aplicarFiltros(edtBuscarProducto.getText().toString().trim());
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void aplicarFiltros(String textoBusqueda) {
        listaFiltrada.clear();
        String texto = textoBusqueda.toLowerCase();

        for (Producto p : listaCompleta) {
            boolean coincideTexto = p.getNombre().toLowerCase().contains(texto);
            int dias = calcularDiasRestantes(p.getFechaVencimiento());
            boolean coincideEstado = false;

            switch (filtroActual) {
                case "urgentes": coincideEstado = dias <= 1; break;
                case "pronto": coincideEstado = (dias >= 2 && dias <= 4); break;
                case "bien": coincideEstado = dias > 4; break;
                case "todos": default: coincideEstado = true; break;
            }

            if (coincideTexto && coincideEstado) {
                listaFiltrada.add(p);
            }
        }

        adapter.actualizarLista(listaFiltrada);
        actualizarContador();
    }

    private void actualizarContador() {
        int cantidad = listaFiltrada.size();
        txtCantidadProductos.setText(cantidad + " prod");
    }

    private int calcularDiasRestantes(long fechaVencimiento) {
        Calendar hoy = Calendar.getInstance();
        Calendar venc = Calendar.getInstance();
        venc.setTimeInMillis(fechaVencimiento);
        hoy.set(Calendar.HOUR_OF_DAY, 0); hoy.set(Calendar.MINUTE, 0); hoy.set(Calendar.SECOND, 0);
        venc.set(Calendar.HOUR_OF_DAY, 0); venc.set(Calendar.MINUTE, 0); venc.set(Calendar.SECOND, 0);
        long diff = venc.getTimeInMillis() - hoy.getTimeInMillis();
        return (int) (diff / (1000L * 60L * 60L * 24L));
    }

    private void actualizarBotonActivo(Button botonActivo) {
        btnTodos.setBackgroundResource(R.drawable.bg_filtro);
        btnUrgentes.setBackgroundResource(R.drawable.bg_filtro);
        btnPronto.setBackgroundResource(R.drawable.bg_filtro);
        btnBien.setBackgroundResource(R.drawable.bg_filtro);
        botonActivo.setBackgroundResource(R.drawable.bg_filtro_activo);
    }

    private void confirmarEliminar(Producto producto) {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de eliminar " + producto.getNombre() + "?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    daoProducto.eliminar(producto.getId());
                    cargarProductos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void exportarInventarioPDF() {
        if (listaCompleta.isEmpty()) {
            Toast.makeText(this, "No hay productos para exportar", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            Canvas canvas = page.getCanvas();
            Paint paint = new Paint();

            paint.setTextSize(20f);
            paint.setFakeBoldText(true);
            paint.setColor(Color.parseColor("#1D4327"));
            canvas.drawText("Mi Inventario - SalvaTuComida", 40, 60, paint);

            paint.setTextSize(12f);
            paint.setFakeBoldText(false);
            paint.setColor(Color.BLACK);
            canvas.drawText("Fecha: " + new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date()), 40, 85, paint);

            int y = 130;
            paint.setTextSize(14f);

            for (Producto p : listaCompleta) {
                String texto = "• " + p.getNombre() + " - " + p.getCantidadValor() + " " + p.getUnidad();
                canvas.drawText(texto, 40, y, paint);
                y += 25;
                if (y > 800) break;
            }

            document.finishPage(page);

            String nombreArchivo = "Inventario_" + System.currentTimeMillis() + ".pdf";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                android.content.ContentValues values = new android.content.ContentValues();
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, nombreArchivo);
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    OutputStream outputStream = getContentResolver().openOutputStream(uri);
                    document.writeTo(outputStream);
                    outputStream.close();
                    Toast.makeText(this, "✅ PDF guardado en Descargas", Toast.LENGTH_LONG).show();
                }
            } else {
                File carpetaDescargas = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File archivoFinal = new File(carpetaDescargas, nombreArchivo);
                document.writeTo(new FileOutputStream(archivoFinal));
                Toast.makeText(this, "✅ PDF guardado en Descargas", Toast.LENGTH_LONG).show();
            }

            document.close();

        } catch (Exception e) {
            Toast.makeText(this, "Error al crear PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void configurarNavegacion() {
        if (navInicio == null) return;
        navInicio.setOnClickListener(v -> irA(MainActivity_Inicio.class));
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