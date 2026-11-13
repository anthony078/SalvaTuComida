package com.example.salvatucomida;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.salvatucomida.Adapters.RecetaAdapter;
import com.example.salvatucomida.DataAcess.DAOListaCompra;
import com.example.salvatucomida.DataAcess.DAOProducto;
import com.example.salvatucomida.DataAcess.DAOReceta;
import com.example.salvatucomida.Models.ListaCompra;
import com.example.salvatucomida.Models.Producto;
import com.example.salvatucomida.Models.Receta;
import com.example.salvatucomida.ModelsIA.IARequest;
import com.example.salvatucomida.ModelsIA.IAResponse;
import com.example.salvatucomida.NetWork.APIServicioIA;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity_Recetas extends AppCompatActivity {

    private TextView txtSugerenciaTitulo, txtSugerenciaDetalle, txtHoraSugerencia;
    private Button btnRecomendarIA, btnBuscarOtraIA, btnGuardarRecetaIA, btnAgregarBebida;

    // 🆕 Ahora son TextView para que se vean como en el inventario
    private TextView btnDesayuno, btnAlmuerzo, btnCena;

    private String tipoComidaActual = "";
    private String recetaActualTexto = "";

    private RecyclerView recyclerRecetas;
    private RecetaAdapter recetaAdapter;

    private List<Receta> todasLasRecetas = new ArrayList<>();
    private List<Receta> recetasMostradas = new ArrayList<>();
    private List<Integer> listaFaltantes = new ArrayList<>();

    private String filtroActual = "Todos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recetas);

        txtSugerenciaTitulo = findViewById(R.id.txtSugerenciaTitulo);
        txtSugerenciaDetalle = findViewById(R.id.txtSugerenciaDetalle);
        txtHoraSugerencia = findViewById(R.id.txtHoraSugerencia);

        btnRecomendarIA = findViewById(R.id.btnRecomendarIA);
        btnBuscarOtraIA = findViewById(R.id.btnBuscarOtraIA);
        btnGuardarRecetaIA = findViewById(R.id.btnGuardarRecetaIA);
        btnAgregarBebida = findViewById(R.id.btnAgregarBebida);

        btnDesayuno = findViewById(R.id.btnDesayuno);
        btnAlmuerzo = findViewById(R.id.btnAlmuerzo);
        btnCena = findViewById(R.id.btnCena);

        recyclerRecetas = findViewById(R.id.recyclerRecetas);
        recyclerRecetas.setLayoutManager(new LinearLayoutManager(this));

        recetaAdapter = new RecetaAdapter(recetasMostradas, listaFaltantes, new RecetaAdapter.OnRecetaListener() {
            @Override
            public void onRecetaClick(Receta receta) {
                Toast.makeText(MainActivity_Recetas.this, "Receta: " + receta.getNombre(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRecetaLongClick(Receta receta) {
                mostrarMenuEditarEliminar(receta);
            }
        });
        recyclerRecetas.setAdapter(recetaAdapter);

        if (btnBuscarOtraIA != null) btnBuscarOtraIA.setVisibility(View.GONE);
        if (btnGuardarRecetaIA != null) btnGuardarRecetaIA.setVisibility(View.GONE);
        if (btnAgregarBebida != null) btnAgregarBebida.setVisibility(View.GONE);

        configurarHoraYComida();

        // Configurar Clics de los Filtros
        if (btnDesayuno != null) {
            btnDesayuno.setOnClickListener(v -> {
                filtroActual = "Desayuno";
                actualizarEstiloFiltros();
                cargarRecetasGuardadas();
            });
        }
        if (btnAlmuerzo != null) {
            btnAlmuerzo.setOnClickListener(v -> {
                filtroActual = "Almuerzo";
                actualizarEstiloFiltros();
                cargarRecetasGuardadas();
            });
        }
        if (btnCena != null) {
            btnCena.setOnClickListener(v -> {
                filtroActual = "Cena";
                actualizarEstiloFiltros();
                cargarRecetasGuardadas();
            });
        }

        actualizarEstiloFiltros();

        if (btnRecomendarIA != null) btnRecomendarIA.setOnClickListener(v -> generarRecetaConIA());
        if (btnBuscarOtraIA != null) btnBuscarOtraIA.setOnClickListener(v -> generarRecetaConIA());
        if (btnGuardarRecetaIA != null) btnGuardarRecetaIA.setOnClickListener(v -> guardarRecetaEnBD());
        if (btnAgregarBebida != null) btnAgregarBebida.setOnClickListener(v -> sugerirBebida());

        TextView tituloHeader = findViewById(R.id.txtInfoRecetas);
        if (tituloHeader != null) {
            tituloHeader.setOnLongClickListener(v -> {
                mostrarDialogoAgregarReceta(null);
                return true;
            });
        }

        configurarNavegacion();
        cargarRecetasGuardadas();
    }

    // 🆕 MÉTODO PARA PINTAR LOS FILTROS (Estilo Inventario)
    private void actualizarEstiloFiltros() {
        int colorActivoFondo = Color.parseColor("#1D4327"); // Verde Oscuro
        int colorActivoTexto = Color.WHITE;
        int colorInactivoFondo = Color.WHITE;
        int colorInactivoTexto = Color.parseColor("#333333");

        if (btnDesayuno != null) {
            boolean activo = filtroActual.equals("Desayuno");
            btnDesayuno.setBackgroundTintList(ColorStateList.valueOf(activo ? colorActivoFondo : colorInactivoFondo));
            btnDesayuno.setTextColor(activo ? colorActivoTexto : colorInactivoTexto);
        }
        if (btnAlmuerzo != null) {
            boolean activo = filtroActual.equals("Almuerzo");
            btnAlmuerzo.setBackgroundTintList(ColorStateList.valueOf(activo ? colorActivoFondo : colorInactivoFondo));
            btnAlmuerzo.setTextColor(activo ? colorActivoTexto : colorInactivoTexto);
        }
        if (btnCena != null) {
            boolean activo = filtroActual.equals("Cena");
            btnCena.setBackgroundTintList(ColorStateList.valueOf(activo ? colorActivoFondo : colorInactivoFondo));
            btnCena.setTextColor(activo ? colorActivoTexto : colorInactivoTexto);
        }
    }

    private void sugerirBebida() {
        DAOProducto daoProducto = new DAOProducto(this);
        List<Producto> inventario = daoProducto.obtenerTodos();

        boolean tieneBebida = false;
        String nombreBebida = "";

        if (inventario != null) {
            for (Producto p : inventario) {
                String nombre = p.getNombre().toLowerCase();
                if (nombre.contains("jugo") || nombre.contains("agua") || nombre.contains("leche") || nombre.contains("refresco") || nombre.contains("cafe") || nombre.contains("té")) {
                    tieneBebida = true;
                    nombreBebida = p.getNombre();
                    break;
                }
            }
        }

        if (tieneBebida) {
            new AlertDialog.Builder(this)
                    .setTitle("🥤 Bebida sugerida")
                    .setMessage("Tienes " + nombreBebida + " en tu inventario. ¡Te queda perfecto para acompañar!")
                    .setPositiveButton("Genial", null)
                    .show();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle("🥤 No tienes bebidas")
                    .setMessage("No encontramos bebidas en tu inventario. ¿Quieres agregar 'Jugo de Naranja' a tu lista de compras?")
                    .setPositiveButton("Sí, agregar", (d, w) -> {
                        DAOListaCompra daoLista = new DAOListaCompra(this);
                        ListaCompra nueva = new ListaCompra();
                        nueva.setUsuarioId(0);
                        nueva.setNombre("Jugo de Naranja");
                        nueva.setPrecio(0);
                        nueva.setComprado(false);
                        nueva.setFechaAgregado(System.currentTimeMillis());
                        daoLista.insertar(nueva);

                        Toast.makeText(this, "✅ Agregado a la lista de compras", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, MainActivity_Lista.class));
                        finish();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        }
    }

    private void cargarRecetasGuardadas() {
        DAOReceta daoReceta = new DAOReceta(this);
        todasLasRecetas.clear();
        List<Receta> recetasBD = daoReceta.obtenerTodas();
        if (recetasBD != null) todasLasRecetas.addAll(recetasBD);

        recetasMostradas.clear();
        listaFaltantes.clear();

        for (Receta r : todasLasRecetas) {
            if (filtroActual.equals("Todos") || (r.getTipoComida() != null && r.getTipoComida().equals(filtroActual))) {
                recetasMostradas.add(r);
                listaFaltantes.add(0);
            }
        }
        recetaAdapter.notifyDataSetChanged();
    }

    private void mostrarMenuEditarEliminar(Receta receta) {
        String[] opciones = {"Editar receta", "Eliminar receta"};
        new AlertDialog.Builder(this)
                .setTitle(receta.getNombre())
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        mostrarDialogoAgregarReceta(receta);
                    } else {
                        confirmarEliminar(receta);
                    }
                }).show();
    }

    private void confirmarEliminar(Receta receta) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("¿Seguro que quieres eliminar " + receta.getNombre() + "?")
                .setPositiveButton("Sí", (d, w) -> {
                    DAOReceta dao = new DAOReceta(this);
                    dao.eliminar(receta.getId());
                    cargarRecetasGuardadas();
                }).setNegativeButton("No", null).show();
    }

    private void mostrarDialogoAgregarReceta(Receta recetaAEditar) {
        boolean esEdicion = (recetaAEditar != null);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 40, 50, 40);

        final EditText etNombre = new EditText(this);
        etNombre.setHint("Ej. Jugo de Naranja / Torta");
        if (esEdicion) etNombre.setText(recetaAEditar.getNombre());
        layout.addView(etNombre);

        final EditText etTipo = new EditText(this);
        etTipo.setHint("Ej. Desayuno, Almuerzo, Cena, Bebida");
        if (esEdicion) etTipo.setText(recetaAEditar.getTipoComida());
        layout.addView(etTipo);

        final EditText etTiempo = new EditText(this);
        etTiempo.setHint("Tiempo en minutos (Ej. 15)");
        etTiempo.setInputType(InputType.TYPE_CLASS_NUMBER);
        if (esEdicion) etTiempo.setText(String.valueOf(recetaAEditar.getTiempoPreparacion()));
        layout.addView(etTiempo);

        new AlertDialog.Builder(this)
                .setTitle(esEdicion ? "Editar Receta/Bebida" : "Agregar Receta/Bebida")
                .setView(layout)
                .setPositiveButton("Guardar", (d, w) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String tipo = etTipo.getText().toString().trim();
                    String tiempoStr = etTiempo.getText().toString().trim();

                    if (nombre.isEmpty() || tipo.isEmpty()) {
                        Toast.makeText(this, "El nombre y el tipo son obligatorios", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int tiempo = tiempoStr.isEmpty() ? 0 : Integer.parseInt(tiempoStr);

                    DAOReceta dao = new DAOReceta(this);
                    if (esEdicion) {
                        recetaAEditar.setNombre(nombre);
                        recetaAEditar.setTipoComida(tipo);
                        recetaAEditar.setTiempoPreparacion(tiempo);
                        dao.actualizar(recetaAEditar);
                    } else {
                        Receta nueva = new Receta();
                        nueva.setNombre(nombre);
                        nueva.setTipoComida(tipo);
                        nueva.setTiempoPreparacion(tiempo);
                        nueva.setEmoji("🥤");
                        dao.insertar(nueva);
                    }
                    cargarRecetasGuardadas();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void configurarHoraYComida() {
        Calendar ahora = Calendar.getInstance();
        int hora = ahora.get(Calendar.HOUR_OF_DAY);
        String horaFormateada = String.format(Locale.getDefault(), "%02d:%02d", hora, ahora.get(Calendar.MINUTE));
        if (txtHoraSugerencia != null) txtHoraSugerencia.setText("🕒 " + horaFormateada);

        if (hora >= 6 && hora < 12) tipoComidaActual = "Desayuno";
        else if (hora >= 12 && hora < 17) tipoComidaActual = "Almuerzo";
        else tipoComidaActual = "Cena";
    }

    private void generarRecetaConIA() {
        DAOProducto daoProducto = new DAOProducto(this);
        List<Producto> inventario = daoProducto.obtenerTodos();

        StringBuilder ingredientes = new StringBuilder();
        if (inventario != null) {
            for (Producto p : inventario) ingredientes.append(p.getNombre()).append(", ");
        }

        if (ingredientes.length() == 0) {
            Toast.makeText(this, "Primero agrega productos a tu inventario", Toast.LENGTH_SHORT).show();
            return;
        }

        if (txtSugerenciaTitulo != null) txtSugerenciaTitulo.setText("Cocinando " + tipoComidaActual.toLowerCase() + "... 🤔");
        if (txtSugerenciaDetalle != null) txtSugerenciaDetalle.setText("La IA está buscando la mejor opción...");

        if (btnRecomendarIA != null) btnRecomendarIA.setEnabled(false);
        if (btnBuscarOtraIA != null) btnBuscarOtraIA.setVisibility(View.GONE);
        if (btnGuardarRecetaIA != null) btnGuardarRecetaIA.setVisibility(View.GONE);
        if (btnAgregarBebida != null) btnAgregarBebida.setVisibility(View.GONE);

        String promptSistema = "Eres un chef experto. Responde usando etiquetas HTML básicas. " +
                "Usa <b> para el título. Usa <br> para saltos de línea. " +
                "Estructura: <b>Título</b><br><br><b>Ingredientes:</b><br>- Ingrediente 1<br>- Ingrediente 2<br><br><b>Preparación:</b><br>1. Paso uno<br>2. Paso dos";

        String promptUsuario = "Tengo: " + ingredientes.toString() + ". Dame una receta para el " + tipoComidaActual + " rápida y fácil.";

        IARequest request = new IARequest(promptSistema, promptUsuario);

        Retrofit retrofitIA = new Retrofit.Builder()
                .baseUrl("https://api.groq.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIServicioIA api = retrofitIA.create(APIServicioIA.class);
        String apiKey = "Bearer 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";

        api.generarReceta(apiKey, request).enqueue(new Callback<IAResponse>() {
            @Override
            public void onResponse(Call<IAResponse> call, Response<IAResponse> response) {
                if (btnRecomendarIA != null) btnRecomendarIA.setEnabled(true);

                if (response.isSuccessful() && response.body() != null && response.body().choices != null && !response.body().choices.isEmpty()) {
                    recetaActualTexto = response.body().choices.get(0).message.content;

                    if (txtSugerenciaTitulo != null) txtSugerenciaTitulo.setText("Receta para " + tipoComidaActual);
                    if (txtSugerenciaDetalle != null) txtSugerenciaDetalle.setText(Html.fromHtml(recetaActualTexto, Html.FROM_HTML_MODE_COMPACT));

                    if (btnBuscarOtraIA != null) btnBuscarOtraIA.setVisibility(View.VISIBLE);
                    if (btnGuardarRecetaIA != null) {
                        btnGuardarRecetaIA.setVisibility(View.VISIBLE);
                        btnGuardarRecetaIA.setEnabled(true);
                    }

                    if (btnAgregarBebida != null) {
                        if (tipoComidaActual.equals("Almuerzo") || tipoComidaActual.equals("Cena")) {
                            btnAgregarBebida.setVisibility(View.VISIBLE);
                        }
                    }

                } else {
                    if (txtSugerenciaTitulo != null) txtSugerenciaTitulo.setText("Error de IA");
                    if (txtSugerenciaDetalle != null) txtSugerenciaDetalle.setText("Código: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<IAResponse> call, Throwable t) {
                if (btnRecomendarIA != null) btnRecomendarIA.setEnabled(true);
                if (txtSugerenciaTitulo != null) txtSugerenciaTitulo.setText("Error de conexión");
                if (txtSugerenciaDetalle != null) txtSugerenciaDetalle.setText(t.getMessage());
            }
        });
    }

    private void guardarRecetaEnBD() {
        if (recetaActualTexto == null || recetaActualTexto.isEmpty()) return;

        String textoLimpio = Html.fromHtml(recetaActualTexto, Html.FROM_HTML_MODE_COMPACT).toString();
        String[] lineas = textoLimpio.split("\n");
        String tituloReceta = lineas.length > 0 ? lineas[0].trim() : "Receta IA";

        DAOReceta daoReceta = new DAOReceta(this);

        Receta nuevaReceta = new Receta();
        nuevaReceta.setNombre(tituloReceta);
        nuevaReceta.setTipoComida(tipoComidaActual);
        nuevaReceta.setEmoji("🤖");
        nuevaReceta.setTiempoPreparacion(20);
        nuevaReceta.setColor("#1D4327");

        long id = daoReceta.insertar(nuevaReceta);

        if (id > 0) {
            Toast.makeText(this, "⭐ ¡Receta guardada!", Toast.LENGTH_LONG).show();
            if (btnGuardarRecetaIA != null) btnGuardarRecetaIA.setEnabled(false);
            cargarRecetasGuardadas();
        } else {
            Toast.makeText(this, "Error al guardar la receta", Toast.LENGTH_SHORT).show();
        }
    }

    private void configurarNavegacion() {
        View navInicio = findViewById(R.id.navInicio);
        View navInventario = findViewById(R.id.navInventario);
        View navCompras = findViewById(R.id.navCompras);
        View navPerfil = findViewById(R.id.navPerfil);

        if (navInicio != null) navInicio.setOnClickListener(v -> { startActivity(new android.content.Intent(this, MainActivity_Inicio.class)); finish(); });
        if (navInventario != null) navInventario.setOnClickListener(v -> { startActivity(new android.content.Intent(this, MainActivity_Inventario.class)); finish(); });
        if (navCompras != null) navCompras.setOnClickListener(v -> { startActivity(new android.content.Intent(this, MainActivity_Lista.class)); finish(); });
        if (navPerfil != null) navPerfil.setOnClickListener(v -> { startActivity(new android.content.Intent(this, MainActivity_Perfil.class)); finish(); });
    }
}