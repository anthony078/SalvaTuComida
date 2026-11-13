package com.example.salvatucomida;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location; // 👈 IMPORTANTE PARA DISTANCIAS
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.example.salvatucomida.Adapters.ProductoListaAdapter;
import com.example.salvatucomida.Adapters.TiendaAdapter;
import com.example.salvatucomida.DataAcess.DAOListaCompra;
import com.example.salvatucomida.DataAcess.DAOPrecioTienda;
import com.example.salvatucomida.DataAcess.DAOProducto;
import com.example.salvatucomida.DataAcess.DAOTienda;
import com.example.salvatucomida.Models.ListaCompra;
import com.example.salvatucomida.Models.PrecioTienda;
import com.example.salvatucomida.Models.Producto;
import com.example.salvatucomida.Models.Tienda;
import com.example.salvatucomida.Models.TiendaAPI;
import com.example.salvatucomida.NetWork.APIServicioCliente;
import com.example.salvatucomida.NetWork.RetrofitCliente;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity_Lista extends AppCompatActivity {

    private static final int COLOR_ACTIVO   = 0xFF1D4327;
    private static final int COLOR_INACTIVO = 0xFF555555;

    private RecyclerView recyclerProductos, recyclerTiendas;
    private TextView txtPendiente, txtPendienteCard, txtContador, txtSinTiendas;
    private EditText edtNuevoProducto;
    private TextView btnAgregarProducto;
    private Button btnCompararPrecios, btnAgregarTienda, btnRegistrarCompra, btnSincronizarTiendas;
    private FloatingActionButton fabAgregar;
    private LinearLayout navInicio, navInventario, navRecetas, navCompras, navPerfil;
    private LottieAnimationView lottieVacio;

    private DAOListaCompra daoListaCompra;
    private DAOTienda daoTienda;
    private DAOPrecioTienda daoPrecioTienda;
    private DAOProducto daoProducto;

    private ProductoListaAdapter adapterProductos;
    private TiendaAdapter adapterTiendas;
    private List<ListaCompra> listaProductos = new ArrayList<>();
    private List<Tienda> listaTiendas = new ArrayList<>();

    private ActivityResultLauncher<String> imagePickerLauncher;
    private String fotoSeleccionadaUri = null;
    private ImageView currentImageViewInDialog;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listacompra);

        prefs = getSharedPreferences("SalvaTuComidaPrefs", Context.MODE_PRIVATE);

        recyclerProductos   = findViewById(R.id.recyclerProductos);
        recyclerTiendas     = findViewById(R.id.recyclerTiendas);
        txtPendiente        = findViewById(R.id.txtPendiente);
        txtPendienteCard    = findViewById(R.id.txtPendienteCard);
        txtContador         = findViewById(R.id.txtContador);
        txtSinTiendas       = findViewById(R.id.txtSinTiendas);
        edtNuevoProducto    = findViewById(R.id.edtNuevoProducto);
        btnAgregarProducto  = findViewById(R.id.btnAgregarProducto);
        btnCompararPrecios  = findViewById(R.id.btnCompararPrecios);
        btnAgregarTienda    = findViewById(R.id.btnAgregarTienda);
        btnRegistrarCompra  = findViewById(R.id.btnRegistrarCompra);
        btnSincronizarTiendas = findViewById(R.id.btnSincronizarTiendas);
        fabAgregar          = findViewById(R.id.fabAgregar);
        lottieVacio         = findViewById(R.id.lottieVacio);

        navInicio     = findViewById(R.id.navInicio);
        navInventario = findViewById(R.id.navInventario);
        navRecetas    = findViewById(R.id.navRecetas);
        navCompras    = findViewById(R.id.navCompras);
        navPerfil     = findViewById(R.id.navPerfil);

        daoListaCompra  = new DAOListaCompra(this);
        daoTienda       = new DAOTienda(this);
        daoPrecioTienda = new DAOPrecioTienda(this);
        daoProducto     = new DAOProducto(this);

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

        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        fotoSeleccionadaUri = uri.toString();
                        if (currentImageViewInDialog != null) {
                            Glide.with(this).load(uri).circleCrop().into(currentImageViewInDialog);
                        }
                    }
                }
        );

        recyclerProductos.setLayoutManager(new LinearLayoutManager(this));
        adapterProductos = new ProductoListaAdapter(listaProductos, new ProductoListaAdapter.OnItemListener() {
            @Override public void onMarcar(ListaCompra p)             { pedirPrecioYMarcar(p); }
            @Override public void onDesmarcar(ListaCompra p)          { desmarcarProducto(p); }
            @Override public void onEliminar(ListaCompra p)           { eliminarProducto(p); }
            @Override public void onPasarAlInventario(ListaCompra p)  { pasarAlInventario(p); }
        });
        recyclerProductos.setAdapter(adapterProductos);

        recyclerTiendas.setLayoutManager(new LinearLayoutManager(this));
        adapterTiendas = new TiendaAdapter(listaTiendas, this, new TiendaAdapter.OnTiendaListener() {
            @Override public void onEditar(Tienda t)         { editarTienda(t); }
            @Override public void onEliminar(Tienda t)       { confirmarEliminarTienda(t); }
            @Override public void onAsignarPrecios(Tienda t) { asignarPreciosATienda(t); }
        });
        recyclerTiendas.setAdapter(adapterTiendas);

        btnAgregarProducto.setOnClickListener(v -> agregarProducto());
        fabAgregar.setOnClickListener(v -> agregarProducto());
        btnRegistrarCompra.setOnClickListener(v -> dialogoRegistrarCompra(null));
        btnCompararPrecios.setOnClickListener(v -> compararPrecios());

        btnAgregarTienda.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity_Tienda.class);
            startActivity(intent);
        });

        btnSincronizarTiendas.setOnClickListener(v -> sincronizarTiendas());

        configurarNavegacion();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recalcularDistancias(); // 👈 ACTUALIZA LAS DISTANCIAS
        cargarProductos();
        cargarTiendas();
    }

    private void recalcularDistancias() {
        float latCasa = prefs.getFloat("casa_lat", -7.16378f);
        float lonCasa = prefs.getFloat("casa_lon", -78.50027f);

        List<Tienda> tiendas = daoTienda.obtenerTodas();
        boolean huboCambios = false;

        for (Tienda t : tiendas) {
            if (t.getLatitud() != 0 && t.getLongitud() != 0) {
                float[] resultados = new float[1];
                Location.distanceBetween(latCasa, lonCasa, t.getLatitud(), t.getLongitud(), resultados);
                double nuevaDistancia = resultados[0] / 1000.0;

                if (Math.abs(t.getDistanciaKm() - nuevaDistancia) > 0.1) {
                    t.setDistanciaKm(nuevaDistancia);
                    daoTienda.actualizar(t);
                    huboCambios = true;
                }
            }
        }

        if (huboCambios) cargarTiendas();
    }

    private boolean verificarPresupuesto(double nuevoGasto) {
        float presupuesto = prefs.getFloat("presupuesto", 0f);
        float gastadoAcumulado = prefs.getFloat("gastado", 0f);
        if (presupuesto <= 0) return true;
        if ((gastadoAcumulado + nuevoGasto) > presupuesto) {
            float disponible = presupuesto - gastadoAcumulado;
            new AlertDialog.Builder(this)
                    .setTitle("⚠️ Límite de Presupuesto Alcanzado")
                    .setMessage("No puedes realizar esta compra.\n\n" +
                            "Presupuesto Total: S/ " + String.format(Locale.getDefault(), "%.2f", presupuesto) + "\n" +
                            "Ya has gastado: S/ " + String.format(Locale.getDefault(), "%.2f", gastadoAcumulado) + "\n" +
                            "Disponible: S/ " + String.format(Locale.getDefault(), "%.2f", disponible) + "\n\n" +
                            "Ve a tu Perfil para recargar tu presupuesto o reiniciar los gastos.")
                    .setPositiveButton("Ir al Perfil", (d, w) -> {
                        startActivity(new Intent(this, MainActivity_Perfil.class));
                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return false;
        }
        return true;
    }

    private void sumarGasto(double cantidad) {
        float gastadoAcumulado = prefs.getFloat("gastado", 0f);
        prefs.edit().putFloat("gastado", gastadoAcumulado + (float) cantidad).apply();
    }

    private void restarGasto(double cantidad) {
        float gastadoAcumulado = prefs.getFloat("gastado", 0f);
        float nuevoGasto = gastadoAcumulado - (float) cantidad;
        if (nuevoGasto < 0) nuevoGasto = 0;
        prefs.edit().putFloat("gastado", nuevoGasto).apply();
    }

    private void cargarProductos() {
        List<ListaCompra> lista = daoListaCompra.obtenerTodas();
        listaProductos.clear();
        if (lista != null) listaProductos.addAll(lista);
        adapterProductos.notifyDataSetChanged();
        if (lottieVacio != null) {
            lottieVacio.setVisibility(listaProductos.isEmpty() ? View.VISIBLE : View.GONE);
            recyclerProductos.setVisibility(listaProductos.isEmpty() ? View.GONE : View.VISIBLE);
        }
        actualizarResumen();
    }

    private void cargarTiendas() {
        try {
            List<Tienda> lista = daoTienda.obtenerTodas();
            listaTiendas.clear();
            if (lista != null) listaTiendas.addAll(lista);
            listaTiendas.sort((a, b) -> {
                int cmp = Double.compare(a.getPrecioTotalEstimado(), b.getPrecioTotalEstimado());
                if (cmp != 0) return cmp;
                return Double.compare(a.getDistanciaKm(), b.getDistanciaKm());
            });
            if (adapterTiendas != null) adapterTiendas.notifyDataSetChanged();
            if (txtSinTiendas != null) txtSinTiendas.setVisibility(listaTiendas.isEmpty() ? View.VISIBLE : View.GONE);
        } catch (Exception e) {
            Toast.makeText(this, "Error tiendas: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void actualizarResumen() {
        int total = listaProductos.size();
        int comprados = 0;
        for (ListaCompra p : listaProductos) if (p.isComprado()) comprados++;

        float gastadoAcumulado = prefs.getFloat("gastado", 0f);
        String txtG = String.format(Locale.getDefault(), "S/ %.2f", gastadoAcumulado);

        if (txtPendiente != null)     txtPendiente.setText(txtG);
        if (txtPendienteCard != null) txtPendienteCard.setText("Gastado: " + txtG);
        if (txtContador != null)      txtContador.setText(comprados + " de " + total + " comprados");
    }

    private void agregarProducto() {
        String nombre = edtNuevoProducto.getText().toString().trim();
        if (nombre.isEmpty()) {
            edtNuevoProducto.setError("Escribe un nombre");
            return;
        }
        nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1).toLowerCase();
        ListaCompra n = new ListaCompra();
        n.setUsuarioId(0); n.setNombre(nombre); n.setPrecio(0); n.setComprado(false); n.setFechaAgregado(System.currentTimeMillis());
        if (daoListaCompra.insertar(n) != -1) {
            edtNuevoProducto.setText("");
            cargarProductos();
        }
    }

    private void pedirPrecioYMarcar(ListaCompra producto) {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Ej. 8.50"); input.setTextColor(0xFF222222);
        int p = (int) (16 * getResources().getDisplayMetrics().density);
        input.setPadding(p, p, p, p);

        new AlertDialog.Builder(this)
                .setTitle("💰 Precio de " + producto.getNombre())
                .setView(input)
                .setPositiveButton("Confirmar", (d, w) -> {
                    String texto = input.getText().toString().trim();
                    double precio = 0;
                    if (!texto.isEmpty()) {
                        try { precio = Double.parseDouble(texto); } catch (NumberFormatException e) {
                            Toast.makeText(this, "Por favor, ingresa un número válido", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    if (!verificarPresupuesto(precio)) return;
                    sumarGasto(precio);
                    producto.setComprado(true); producto.setPrecio(precio);
                    daoListaCompra.actualizar(producto); cargarProductos();
                }).setNegativeButton("Cancelar", null).show();
    }

    private void desmarcarProducto(ListaCompra producto) {
        if (producto.isComprado() && producto.getPrecio() > 0) restarGasto(producto.getPrecio());
        producto.setComprado(false); producto.setPrecio(0);
        daoListaCompra.actualizar(producto); cargarProductos();
    }

    private void eliminarProducto(ListaCompra producto) {
        new AlertDialog.Builder(this).setTitle("Eliminar").setMessage("¿Quitar \"" + producto.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    if (producto.isComprado() && producto.getPrecio() > 0) restarGasto(producto.getPrecio());
                    daoListaCompra.eliminar(producto.getId());
                    cargarProductos();
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void dialogoRegistrarCompra(Integer tiendaId) {
        List<ListaCompra> pendientes = new ArrayList<>();
        for (ListaCompra p : listaProductos) if (!p.isComprado()) pendientes.add(p);
        if (pendientes.isEmpty()) { Toast.makeText(this, "No hay pendientes", Toast.LENGTH_SHORT).show(); return; }

        Map<Integer, Double> preciosTienda = new HashMap<>();
        if (tiendaId != null) { for (PrecioTienda pt : daoPrecioTienda.obtenerPorTienda(tiendaId)) preciosTienda.put(pt.getListaCompraId(), pt.getPrecio()); }

        int pad = (int) (12 * getResources().getDisplayMetrics().density);
        LinearLayout cont = new LinearLayout(this); cont.setOrientation(LinearLayout.VERTICAL); cont.setPadding(pad, pad, pad, pad);
        final List<FilaCompra> filas = new ArrayList<>();

        for (ListaCompra p : pendientes) {
            LinearLayout fila = new LinearLayout(this); fila.setOrientation(LinearLayout.HORIZONTAL); fila.setGravity(Gravity.CENTER_VERTICAL); fila.setPadding(0, pad / 2, 0, pad / 2);
            CheckBox chk = new CheckBox(this); chk.setButtonTintList(android.content.res.ColorStateList.valueOf(0xFF1D4327));
            TextView tvN = new TextView(this); tvN.setText(p.getNombre()); tvN.setTextColor(0xFF222222); tvN.setTextSize(13);
            tvN.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            EditText edtP = new EditText(this); edtP.setHint("S/"); edtP.setTextSize(12); edtP.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edtP.setLayoutParams(new LinearLayout.LayoutParams((int) (70 * getResources().getDisplayMetrics().density), ViewGroup.LayoutParams.WRAP_CONTENT));

            Double pre = preciosTienda.get(p.getId());
            if (pre != null && pre > 0) { chk.setChecked(true); edtP.setText(String.format(Locale.getDefault(), "%.2f", pre)); edtP.setEnabled(true); }
            else { chk.setChecked(false); edtP.setEnabled(false); }
            chk.setOnCheckedChangeListener((btn, checked) -> { edtP.setEnabled(checked); if (!checked) edtP.setText(""); });

            fila.addView(chk); fila.addView(tvN); fila.addView(edtP); cont.addView(fila);
            filas.add(new FilaCompra(p, chk, edtP));
        }
        ScrollView scroll = new ScrollView(this); scroll.addView(cont);

        new AlertDialog.Builder(this).setTitle("💰 Registrar compra").setView(scroll)
                .setPositiveButton("Confirmar", (d, w) -> {
                    double totalNuevaCompra = 0;
                    for (FilaCompra fr : filas) {
                        if (fr.check.isChecked()) {
                            String t = fr.precio.getText().toString().trim();
                            if (!t.isEmpty()) { try { totalNuevaCompra += Double.parseDouble(t); } catch (NumberFormatException ignored) {} }
                        }
                    }
                    if (!verificarPresupuesto(totalNuevaCompra)) return;
                    int c = 0; double tot = 0;
                    for (FilaCompra fr : filas) {
                        if (!fr.check.isChecked()) continue;
                        String t = fr.precio.getText().toString().trim(); if (t.isEmpty()) continue;
                        double pre = 0;
                        try { pre = Double.parseDouble(t); } catch (NumberFormatException e) {
                            Toast.makeText(this, "Precio inválido en " + fr.producto.getNombre(), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        fr.producto.setComprado(true); fr.producto.setPrecio(pre); daoListaCompra.actualizar(fr.producto); c++; tot += pre;
                    }
                    if (c > 0) { sumarGasto(tot); cargarProductos(); Toast.makeText(this, "✅ " + c + " productos", Toast.LENGTH_SHORT).show(); }
                }).setNegativeButton("Cancelar", null).show();
    }

    private static class FilaCompra {
        ListaCompra producto; CheckBox check; EditText precio;
        FilaCompra(ListaCompra p, CheckBox c, EditText e) { producto = p; check = c; precio = e; }
    }

    private void pasarAlInventario(ListaCompra producto) {
        int pad = (int) (12 * getResources().getDisplayMetrics().density);
        final EditText edtCantidad = new EditText(this); edtCantidad.setHint("Ej. 2"); edtCantidad.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        final EditText edtUnidad = new EditText(this); edtUnidad.setHint("Ej. kg, litros"); edtUnidad.setInputType(InputType.TYPE_CLASS_TEXT);
        final EditText edtFecha = new EditText(this); edtFecha.setHint("Seleccione fecha"); edtFecha.setFocusable(false); edtFecha.setClickable(true);
        edtCantidad.setPadding(pad, pad, pad, pad); edtUnidad.setPadding(pad, pad, pad, pad); edtFecha.setPadding(pad, pad, pad, pad);
        final long[] fecha = {System.currentTimeMillis() + (7L * 24 * 60 * 60 * 1000)};

        edtFecha.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance(); cal.setTimeInMillis(fecha[0]);
            new DatePickerDialog(this, (view, y, m, d) -> {
                Calendar c = Calendar.getInstance(); c.set(y, m, d, 0, 0, 0); fecha[0] = c.getTimeInMillis();
                edtFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", d, m + 1, y));
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
        Calendar def = Calendar.getInstance(); def.setTimeInMillis(fecha[0]);
        edtFecha.setText(String.format(Locale.getDefault(), "%02d/%02d/%04d", def.get(Calendar.DAY_OF_MONTH), def.get(Calendar.MONTH) + 1, def.get(Calendar.YEAR)));

        LinearLayout cont = new LinearLayout(this); cont.setOrientation(LinearLayout.VERTICAL);
        int padExt = (int) (16 * getResources().getDisplayMetrics().density); cont.setPadding(padExt, padExt / 2, padExt, padExt / 2);

        TextView tvT = new TextView(this); tvT.setText("📦 " + producto.getNombre()); tvT.setTextSize(15); tvT.setTypeface(null, Typeface.BOLD); tvT.setTextColor(0xFF1D4327); tvT.setPadding(0, 0, 0, pad); cont.addView(tvT);
        cont.addView(crearLabel("Cantidad *")); cont.addView(edtCantidad);
        cont.addView(crearLabel("Unidad *")); cont.addView(edtUnidad);
        cont.addView(crearLabel("Fecha de vencimiento *")); cont.addView(edtFecha);

        fotoSeleccionadaUri = null;
        currentImageViewInDialog = new ImageView(this);
        currentImageViewInDialog.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        currentImageViewInDialog.setScaleType(ImageView.ScaleType.CENTER_CROP);
        currentImageViewInDialog.setImageResource(R.mipmap.ic_launcher_round);

        Button btnFoto = new Button(this);
        btnFoto.setText("📸 Elegir Foto");
        btnFoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        cont.addView(crearLabel("Foto (Opcional)"));
        cont.addView(btnFoto);
        cont.addView(currentImageViewInDialog);

        new AlertDialog.Builder(this).setTitle("Agregar al inventario").setView(cont)
                .setPositiveButton("Agregar", (d, w) -> {
                    String cantTxt = edtCantidad.getText().toString().trim(); String uni = edtUnidad.getText().toString().trim();
                    if (cantTxt.isEmpty() || uni.isEmpty()) { Toast.makeText(this, "Completa campos", Toast.LENGTH_SHORT).show(); return; }
                    double cant = 0;
                    try { cant = Double.parseDouble(cantTxt); } catch (NumberFormatException e) { Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show(); return; }
                    if (cant <= 0) { Toast.makeText(this, "Cantidad inválida", Toast.LENGTH_SHORT).show(); return; }

                    Producto np = new Producto(0, producto.getNombre(), cant, uni, fecha[0], System.currentTimeMillis(), obtenerEmoji(producto.getNombre()));
                    np.setFotoPath(fotoSeleccionadaUri);
                    if (daoProducto.insertar(np) != -1) {
                        Toast.makeText(this, "✅ Agregado al inventario", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity_Lista.this, MainActivity_Inventario.class));
                        overridePendingTransition(0, 0); finish();
                    }
                }).setNegativeButton("Cancelar", null).show();
    }

    private String obtenerEmoji(String nombre) {
        if (nombre == null) return "🍎"; String n = nombre.toLowerCase();
        if (n.contains("huevo")) return "🥚"; if (n.contains("arroz")) return "🍚"; if (n.contains("leche")) return "🥛";
        if (n.contains("manzana")) return "🍎"; if (n.contains("pan")) return "🍞"; if (n.contains("pollo")) return "🍗";
        if (n.contains("carne")) return "🥩"; if (n.contains("pescado")) return "🐟"; return "🍎";
    }

    private void editarTienda(Tienda tienda) {
        Intent intent = new Intent(this, MainActivity_Tienda.class);
        intent.putExtra("tienda_id", tienda.getId());
        startActivity(intent);
    }

    private void confirmarEliminarTienda(Tienda tienda) {
        new AlertDialog.Builder(this).setTitle("🗑️ Eliminar tienda").setMessage("¿Eliminar \"" + tienda.getNombre() + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    daoPrecioTienda.eliminarPorTienda(tienda.getId());
                    daoTienda.eliminar(tienda.getId());
                    if (tienda.getFotoPath() != null) {
                        try {
                            java.io.File file = new java.io.File(android.net.Uri.parse(tienda.getFotoPath()).getPath());
                            if (file.exists()) file.delete();
                        } catch (Exception ignored) {}
                    }
                    cargarTiendas();
                })
                .setNegativeButton("Cancelar", null).show();
    }

    private void asignarPreciosATienda(Tienda tienda) {
        if (listaProductos.isEmpty()) {
            Toast.makeText(this, "Primero agrega productos a la lista", Toast.LENGTH_SHORT).show();
            return;
        }

        List<PrecioTienda> preciosExistentes = daoPrecioTienda.obtenerPorTienda(tienda.getId());
        Map<Integer, Double> mapaPrecios = new HashMap<>();
        for (PrecioTienda pt : preciosExistentes) mapaPrecios.put(pt.getListaCompraId(), pt.getPrecio());

        int pad = (int) (12 * getResources().getDisplayMetrics().density);
        LinearLayout cont = new LinearLayout(this); cont.setOrientation(LinearLayout.VERTICAL); cont.setPadding(pad, pad, pad, pad);
        final List<FilaPrecio> filas = new ArrayList<>();

        for (ListaCompra p : listaProductos) {
            LinearLayout fila = new LinearLayout(this); fila.setOrientation(LinearLayout.HORIZONTAL); fila.setGravity(Gravity.CENTER_VERTICAL); fila.setPadding(0, pad / 2, 0, pad / 2);
            CheckBox chk = new CheckBox(this); chk.setButtonTintList(android.content.res.ColorStateList.valueOf(0xFF1D4327));
            TextView tvN = new TextView(this); tvN.setText(p.getNombre()); tvN.setTextColor(0xFF222222); tvN.setTextSize(13);
            tvN.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
            EditText edtP = new EditText(this); edtP.setHint("S/"); edtP.setTextSize(12); edtP.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
            edtP.setLayoutParams(new LinearLayout.LayoutParams((int) (70 * getResources().getDisplayMetrics().density), ViewGroup.LayoutParams.WRAP_CONTENT));

            Double precioGuardado = mapaPrecios.get(p.getId());
            if (precioGuardado != null && precioGuardado > 0) { chk.setChecked(true); edtP.setText(String.format(Locale.getDefault(), "%.2f", precioGuardado)); edtP.setEnabled(true); }
            else { chk.setChecked(false); edtP.setEnabled(false); }
            chk.setOnCheckedChangeListener((btn, checked) -> { edtP.setEnabled(checked); if (!checked) edtP.setText(""); });

            fila.addView(chk); fila.addView(tvN); fila.addView(edtP); cont.addView(fila);
            filas.add(new FilaPrecio(p.getId(), chk, edtP));
        }
        ScrollView scroll = new ScrollView(this); scroll.addView(cont);

        new AlertDialog.Builder(this).setTitle("🏪 Precios en " + tienda.getNombre()).setView(scroll)
                .setPositiveButton("Guardar", (d, w) -> {
                    daoPrecioTienda.eliminarPorTienda(tienda.getId());
                    double totalTienda = 0;
                    int guardados = 0;
                    for (FilaPrecio fp : filas) {
                        if (!fp.check.isChecked()) continue;
                        String t = fp.precio.getText().toString().trim(); if (t.isEmpty()) continue;
                        double pre = 0;
                        try { pre = Double.parseDouble(t); } catch (NumberFormatException e) {
                            Toast.makeText(this, "Precio inválido en un producto", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        daoPrecioTienda.insertar(new PrecioTienda(tienda.getId(), fp.listaCompraId, pre));
                        totalTienda += pre; guardados++;
                    }
                    tienda.setPrecioTotalEstimado(totalTienda);
                    daoTienda.actualizar(tienda);
                    cargarTiendas();
                    Toast.makeText(this, "✅ " + guardados + " precios guardados", Toast.LENGTH_SHORT).show();
                }).setNegativeButton("Cancelar", null).show();
    }

    private static class FilaPrecio {
        int listaCompraId; CheckBox check; EditText precio;
        FilaPrecio(int id, CheckBox c, EditText e) { listaCompraId = id; check = c; precio = e; }
    }

    private void compararPrecios() {
        if (listaTiendas.isEmpty()) { Toast.makeText(this, "Primero agrega tiendas", Toast.LENGTH_SHORT).show(); return; }

        List<Tienda> conDatos = new ArrayList<>();
        for (Tienda t : listaTiendas) { if (daoPrecioTienda.calcularTotalTienda(t.getId()) > 0) conDatos.add(t); }
        if (conDatos.isEmpty()) { Toast.makeText(this, "Ninguna tienda tiene precios", Toast.LENGTH_SHORT).show(); return; }

        conDatos.sort((a, b) -> Double.compare(daoPrecioTienda.calcularTotalTienda(a.getId()), daoPrecioTienda.calcularTotalTienda(b.getId())));

        final Tienda mejorTienda = conDatos.get(0);
        double mejorTot = daoPrecioTienda.calcularTotalTienda(mejorTienda.getId());

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_comparacion, null);
        LinearLayout layoutRanking = dialogView.findViewById(R.id.layoutRanking);
        LinearLayout layoutProductos = dialogView.findViewById(R.id.layoutProductos);
        TextView txtSmartBuy = dialogView.findViewById(R.id.txtSmartBuy);

        for (int i = 0; i < conDatos.size(); i++) {
            Tienda t = conDatos.get(i);
            double tot = daoPrecioTienda.calcularTotalTienda(t.getId());
            LinearLayout fila = new LinearLayout(this); fila.setOrientation(LinearLayout.HORIZONTAL); fila.setPadding(0, 8, 0, 8);
            TextView tvMedalla = new TextView(this); tvMedalla.setText(i == 0 ? "🥇 " : i == 1 ? "🥈 " : i == 2 ? "🥉 " : "• "); tvMedalla.setTextSize(16); fila.addView(tvMedalla);
            TextView tvNombre = new TextView(this); tvNombre.setText(t.getNombre()); tvNombre.setTextSize(14); tvNombre.setTextColor(0xFF333333); tvNombre.setTypeface(null, i == 0 ? Typeface.BOLD : Typeface.NORMAL); tvNombre.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)); fila.addView(tvNombre);
            TextView tvPrecio = new TextView(this); tvPrecio.setText(String.format(Locale.getDefault(), "S/ %.2f", tot)); tvPrecio.setTextSize(14); tvPrecio.setTypeface(null, Typeface.BOLD); tvPrecio.setTextColor(i == 0 ? 0xFF1D4327 : 0xFF666666); fila.addView(tvPrecio);
            layoutRanking.addView(fila);
        }

        double totalMezclado = 0;
        for (ListaCompra p : listaProductos) {
            List<PrecioTienda> precios = daoPrecioTienda.obtenerPorProducto(p.getId());
            if (precios.isEmpty()) continue;
            TextView tvProd = new TextView(this); tvProd.setText("📦 " + p.getNombre()); tvProd.setTextSize(14); tvProd.setTypeface(null, Typeface.BOLD); tvProd.setTextColor(0xFF1D4327); tvProd.setPadding(0, 16, 0, 8); layoutProductos.addView(tvProd);
            for (int i = 0; i < precios.size(); i++) {
                PrecioTienda pt = precios.get(i); Tienda t = buscarTienda(pt.getTiendaId()); if (t == null) continue;
                LinearLayout fpp = new LinearLayout(this); fpp.setOrientation(LinearLayout.HORIZONTAL); fpp.setPadding((int)(16 * getResources().getDisplayMetrics().density), 4, 0, 4);
                TextView tvBullet = new TextView(this); tvBullet.setText(i == 0 ? "⭐ " : "   "); tvBullet.setTextSize(12); fpp.addView(tvBullet);
                TextView tvTN = new TextView(this); tvTN.setText(t.getNombre()); tvTN.setTextSize(13); tvTN.setTextColor(i == 0 ? 0xFF1D4327 : 0xFF777777); tvTN.setTypeface(null, i == 0 ? Typeface.BOLD : Typeface.NORMAL); tvTN.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)); fpp.addView(tvTN);
                TextView tvPP = new TextView(this); tvPP.setText(String.format(Locale.getDefault(), "S/ %.2f", pt.getPrecio())); tvPP.setTextSize(13); tvPP.setTextColor(i == 0 ? 0xFF1D4327 : 0xFF777777); tvPP.setTypeface(null, i == 0 ? Typeface.BOLD : Typeface.NORMAL); fpp.addView(tvPP);
                layoutProductos.addView(fpp);
            }
            totalMezclado += precios.get(0).getPrecio();
        }

        double diff = mejorTot - totalMezclado;
        StringBuilder sb = new StringBuilder();
        if (diff > 0.5) {
            sb.append("✅ Mezclando tiendas: S/ ").append(String.format(Locale.getDefault(), "%.2f", totalMezclado)).append("\n🏪 Todo en ").append(mejorTienda.getNombre()).append(": S/ ").append(String.format(Locale.getDefault(), "%.2f", mejorTot)).append("\n\n💰 Ahorras S/ ").append(String.format(Locale.getDefault(), "%.2f", diff)).append(" si mezclas.");
        } else {
            sb.append("✅ Conviene comprar TODO en ").append(mejorTienda.getNombre()).append("\nTotal: S/ ").append(String.format(Locale.getDefault(), "%.2f", mejorTot));
        }
        txtSmartBuy.setText(sb.toString());

        AlertDialog.Builder builder = new AlertDialog.Builder(this); builder.setView(dialogView); AlertDialog dialog = builder.create();
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button btnComprar = dialogView.findViewById(R.id.btnComprarAqui); Button btnLimpiar = dialogView.findViewById(R.id.btnLimpiar); Button btnCerrar = dialogView.findViewById(R.id.btnCerrar);
        btnComprar.setText("✅ Comprar en " + mejorTienda.getNombre());
        btnComprar.setOnClickListener(v -> { dialog.dismiss(); dialogoRegistrarCompra(mejorTienda.getId()); });
        btnLimpiar.setOnClickListener(v -> { dialog.dismiss(); limpiarComprados(); });
        btnCerrar.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private Tienda buscarTienda(int id) { for (Tienda t : listaTiendas) if (t.getId() == id) return t; return null; }

    private void limpiarComprados() {
        int comprados = 0; for (ListaCompra p : listaProductos) if (p.isComprado()) comprados++;
        if (comprados == 0) return;
        new AlertDialog.Builder(this).setTitle("🧹 Limpiar comprados").setMessage("¿Eliminar " + comprados + " productos ya comprados?\n\n(Tu historial de gastos se mantendrá intacto para el presupuesto).")
                .setPositiveButton("Limpiar", (d, w) -> {
                    for (ListaCompra p : new ArrayList<>(listaProductos)) { if (p.isComprado()) daoListaCompra.eliminar(p.getId()); }
                    cargarProductos();
                }).setNegativeButton("Cancelar", null).show();
    }

    private TextView crearLabel(String texto) {
        TextView tv = new TextView(this); tv.setText(texto); tv.setTextColor(0xFF1D4327); tv.setTextSize(12); tv.setTypeface(null, Typeface.BOLD);
        int p = (int) (8 * getResources().getDisplayMetrics().density); tv.setPadding(0, p, 0, 0); return tv;
    }

    private void configurarNavegacion() {
        if (navInicio == null) return;
        setInactivo(navInicio, R.id.textoInicio); setInactivo(navInventario, R.id.textoInventario);
        setInactivo(navRecetas, R.id.textoRecetas); setInactivo(navCompras, R.id.textoCompras); setInactivo(navPerfil, R.id.textoPerfil);
        setActivo(navCompras, R.id.textoCompras);
        navInicio.setOnClickListener(v -> irA(MainActivity_Inicio.class));
        navInventario.setOnClickListener(v -> irA(MainActivity_Inventario.class));
        navRecetas.setOnClickListener(v -> irA(MainActivity_Recetas.class));
        navCompras.setOnClickListener(v -> { });
        navPerfil.setOnClickListener(v -> irA(MainActivity_Perfil.class));
    }
    private void irA(Class<?> destino) { startActivity(new Intent(this, destino)); overridePendingTransition(0, 0); finish(); }
    private void setActivo(LinearLayout contenedor, int textoId) {
        if (contenedor == null) return; TextView t = contenedor.findViewById(textoId);
        if (t != null) { t.setTextColor(COLOR_ACTIVO); t.setTypeface(null, Typeface.BOLD); }
    }
    private void setInactivo(LinearLayout contenedor, int textoId) {
        if (contenedor == null) return; TextView t = contenedor.findViewById(textoId);
        if (t != null) { t.setTextColor(COLOR_INACTIVO); t.setTypeface(null, Typeface.NORMAL); }
    }

    private void sincronizarTiendas() {
        APIServicioCliente api = RetrofitCliente.getCliente().create(APIServicioCliente.class);
        api.GetTiendas().enqueue(new retrofit2.Callback<List<TiendaAPI>>() {
            @Override
            public void onResponse(retrofit2.Call<List<TiendaAPI>> call, retrofit2.Response<List<TiendaAPI>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int guardadas = 0;
                    for (TiendaAPI tNube : response.body()) {
                        Tienda tLocal = new Tienda();
                        tLocal.setNombre(tNube.getNombre());
                        tLocal.setDistanciaKm(tNube.getDistanciaKm());
                        tLocal.setUbicacion(tNube.getUbicacion());
                        tLocal.setFotoPath(tNube.getRuta());
                        daoTienda.insertar(tLocal);
                        guardadas++;
                    }
                    cargarTiendas();
                    Toast.makeText(MainActivity_Lista.this, "✅ " + guardadas + " tiendas descargadas", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity_Lista.this, "Error en la respuesta", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<List<TiendaAPI>> call, Throwable t) {
                Toast.makeText(MainActivity_Lista.this, "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}