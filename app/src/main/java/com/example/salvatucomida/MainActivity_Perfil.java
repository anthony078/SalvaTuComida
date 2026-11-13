package com.example.salvatucomida;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.salvatucomida.DataAcess.DAOProducto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity_Perfil extends AppCompatActivity {

    private SharedPreferences prefs;
    private TextView txtPresupuestoDetalle;
    private ProgressBar progressPresupuesto;
    private TextView txtStatProductos;
    private TextView txtStatGasto;

    // Vistas del usuario y Firebase
    private TextView txtNombreUsuario, txtCorreoUsuario;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        mAuth = FirebaseAuth.getInstance();
        prefs = getSharedPreferences("SalvaTuComidaPrefs", Context.MODE_PRIVATE);

        // 1. Conectar Vistas
        try {
            txtPresupuestoDetalle = findViewById(R.id.txtPresupuestoDetalle);
            progressPresupuesto = findViewById(R.id.progressPresupuesto);
            txtStatProductos = findViewById(R.id.txtStatProductos);
            txtStatGasto = findViewById(R.id.txtStatGasto);

            // Vistas del usuario (OJO: Asegúrate que estos IDs existan en tu XML)
            txtNombreUsuario = findViewById(R.id.txtNombreUsuario);
            txtCorreoUsuario = findViewById(R.id.txtCorreoUsuario);
        } catch (Exception e) {
            Toast.makeText(this, "Error al cargar vistas", Toast.LENGTH_SHORT).show();
        }

        // 2. Cargar datos de Firebase (Nombre y Correo)
        cargarDatosUsuario();

        // 3. Configurar Botones
        View btnEditarPresupuesto = findViewById(R.id.btnEditarPresupuesto);
        if (btnEditarPresupuesto != null) {
            btnEditarPresupuesto.setOnClickListener(v -> mostrarDialogoConfigurarPresupuesto());
        }

        View btnReiniciarGastos = findViewById(R.id.btnReiniciarGastos);
        if (btnReiniciarGastos != null) {
            btnReiniciarGastos.setOnClickListener(v -> confirmarReiniciarGastos());
        }

        // 4. Configurar Botón de Cerrar Sesión
        View btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        if (btnCerrarSesion != null) {
            btnCerrarSesion.setOnClickListener(v -> cerrarSesion());
        }

        // 5. Navegación inferior
        configurarNavegacionInferior();

        actualizarDatos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarDatos();
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String nombre = user.getDisplayName() != null ? user.getDisplayName() : "Usuario";
            String correo = user.getEmail() != null ? user.getEmail() : "correo@gmail.com";

            if (txtNombreUsuario != null) txtNombreUsuario.setText(nombre);
            if (txtCorreoUsuario != null) txtCorreoUsuario.setText(correo);
        }
    }

    private void cerrarSesion() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro que deseas salir de tu cuenta?")
                .setPositiveButton("Sí, salir", (d, w) -> {
                    mAuth.signOut(); // Cierra sesión en Firebase

                    // Redirigir al Login y limpiar el historial
                    Intent intent = new Intent(this, MainActivity_Login.class); // Asegúrate que tu clase se llame así
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void configurarNavegacionInferior() {
        View navInicio = findViewById(R.id.navInicio);
        View navInventario = findViewById(R.id.navInventario);
        View navRecetas = findViewById(R.id.navRecetas);
        View navCompras = findViewById(R.id.navCompras);
        View navPerfil = findViewById(R.id.navPerfil);

        if (navInicio != null) {
            navInicio.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity_Inicio.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navInventario != null) {
            navInventario.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity_Inventario.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navRecetas != null) {
            navRecetas.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity_Recetas.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navCompras != null) {
            navCompras.setOnClickListener(v -> {
                startActivity(new Intent(this, MainActivity_Lista.class));
                overridePendingTransition(0, 0);
                finish();
            });
        }
        if (navPerfil != null) {
            navPerfil.setOnClickListener(v -> { /* Ya estamos aquí */ });
        }
    }

    private void actualizarDatos() {
        float presupuesto = prefs.getFloat("presupuesto", 0f);
        float gastado = prefs.getFloat("gastado", 0f);

        DAOProducto daoProducto = new DAOProducto(this);
        int numProductos = 0;
        try {
            if (daoProducto.obtenerTodos() != null) {
                numProductos = daoProducto.obtenerTodos().size();
            }
        } catch (Exception ignored) {}

        String txtGastado = String.format(Locale.getDefault(), "S/ %.2f", gastado);
        String txtPresupuestoTotal = String.format(Locale.getDefault(), "S/ %.2f", presupuesto);

        if (txtPresupuestoDetalle != null) {
            txtPresupuestoDetalle.setText("Has gastado " + txtGastado + " de " + txtPresupuestoTotal);
        }
        if (progressPresupuesto != null) {
            int progreso = (presupuesto > 0) ? (int) ((gastado / presupuesto) * 100) : 0;
            if (progreso > 100) progreso = 100;
            progressPresupuesto.setProgress(progreso);
        }
        if (txtStatProductos != null) txtStatProductos.setText(String.valueOf(numProductos));
        if (txtStatGasto != null) txtStatGasto.setText(txtGastado);
    }

    private void mostrarDialogoConfigurarPresupuesto() {
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Ej. 150.00");

        float presupuestoActual = prefs.getFloat("presupuesto", 0f);
        if (presupuestoActual > 0) input.setText(String.format(Locale.getDefault(), "%.2f", presupuestoActual));

        new AlertDialog.Builder(this)
                .setTitle("💰 Configurar Presupuesto")
                .setMessage("Ingresa el monto total que planeas gastar:")
                .setView(input)
                .setPositiveButton("Guardar", (d, w) -> {
                    String texto = input.getText().toString().trim();
                    if (!texto.isEmpty()) {
                        try {
                            float nuevoPresupuesto = Float.parseFloat(texto);
                            prefs.edit().putFloat("presupuesto", nuevoPresupuesto).apply();
                            actualizarDatos();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Número inválido", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("Cancelar", null).show();
    }

    private void confirmarReiniciarGastos() {
        new AlertDialog.Builder(this)
                .setTitle("🔄 Reiniciar Gastos")
                .setMessage("¿Seguro que quieres poner el gasto acumulado en S/ 0.00?")
                .setPositiveButton("Sí, reiniciar", (d, w) -> {
                    prefs.edit().putFloat("gastado", 0f).apply();
                    actualizarDatos();
                }).setNegativeButton("Cancelar", null).show();
    }
}