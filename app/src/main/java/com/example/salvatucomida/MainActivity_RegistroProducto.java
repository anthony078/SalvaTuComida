package com.example.salvatucomida;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.salvatucomida.Models.Producto;
import com.example.salvatucomida.Repository.Repository_Producto;

import java.util.Calendar;

public class MainActivity_RegistroProducto extends AppCompatActivity {

    private EditText edtNombre;
    private EditText edtCantidad;
    private EditText edtUnidad;
    private EditText edtFechaVencimiento;

    private Button btnGuardar;

    private long fechaVencimiento = 0;

    private Repository_Producto repositoryProducto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registroproducto);

        // ==========================================
        // CONECTAR VISTAS
        // ==========================================

        edtNombre = findViewById(R.id.edtNombre);
        edtCantidad = findViewById(R.id.edtCantidad);
        edtUnidad = findViewById(R.id.edtUnidad);
        edtFechaVencimiento = findViewById(R.id.edtFechaVencimiento);
        btnGuardar = findViewById(R.id.btnGuardar);

        // ==========================================
        // CREAR REPOSITORY
        // ==========================================

        repositoryProducto = new Repository_Producto(this);

        // ==========================================
        // SELECCIONAR FECHA
        // ==========================================

        edtFechaVencimiento.setOnClickListener(v -> mostrarCalendario());

        // ==========================================
        // BOTÓN GUARDAR
        // ==========================================

        btnGuardar.setOnClickListener(v -> validarFormulario());
    }

    // ==========================================
    // MOSTRAR CALENDARIO
    // ==========================================

    private void mostrarCalendario() {

        Calendar calendario = Calendar.getInstance();

        int año = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog selectorFecha = new DatePickerDialog(
                this,
                (view, añoSeleccionado, mesSeleccionado, diaSeleccionado) -> {

                    Calendar fecha = Calendar.getInstance();

                    fecha.set(
                            añoSeleccionado,
                            mesSeleccionado,
                            diaSeleccionado,
                            0,
                            0,
                            0
                    );

                    fecha.set(Calendar.MILLISECOND, 0);

                    fechaVencimiento = fecha.getTimeInMillis();

                    String fechaTexto = String.format(
                            "%02d/%02d/%04d",
                            diaSeleccionado,
                            mesSeleccionado + 1,
                            añoSeleccionado
                    );

                    edtFechaVencimiento.setText(fechaTexto);
                },
                año,
                mes,
                dia
        );

        selectorFecha.show();
    }

    // ==========================================
    // VALIDAR FORMULARIO
    // ==========================================

    private void validarFormulario() {

        String nombre = edtNombre.getText()
                .toString()
                .trim();

        String cantidadTexto = edtCantidad.getText()
                .toString()
                .trim();

        String unidad = edtUnidad.getText()
                .toString()
                .trim();

        // ------------------------------------------
        // VALIDAR NOMBRE
        // ------------------------------------------

        if (nombre.isEmpty()) {

            edtNombre.setError(
                    "Ingrese el nombre del alimento"
            );

            edtNombre.requestFocus();

            return;
        }

        // ------------------------------------------
        // VALIDAR CANTIDAD
        // ------------------------------------------

        if (cantidadTexto.isEmpty()) {

            edtCantidad.setError(
                    "Ingrese la cantidad"
            );

            edtCantidad.requestFocus();

            return;
        }

        double cantidad;

        try {

            cantidad = Double.parseDouble(cantidadTexto);

        } catch (NumberFormatException e) {

            edtCantidad.setError(
                    "Ingrese una cantidad válida"
            );

            edtCantidad.requestFocus();

            return;
        }

        if (cantidad <= 0) {

            edtCantidad.setError(
                    "La cantidad debe ser mayor que 0"
            );

            edtCantidad.requestFocus();

            return;
        }

        // ------------------------------------------
        // VALIDAR UNIDAD
        // ------------------------------------------

        if (unidad.isEmpty()) {

            edtUnidad.setError(
                    "Ingrese la unidad"
            );

            edtUnidad.requestFocus();

            return;
        }

        // ------------------------------------------
        // VALIDAR FECHA
        // ------------------------------------------

        if (fechaVencimiento == 0) {

            Toast.makeText(
                    this,
                    "Seleccione la fecha de vencimiento",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ------------------------------------------
        // GUARDAR
        // ------------------------------------------

        guardarProducto(
                nombre,
                cantidad,
                unidad,
                fechaVencimiento
        );
    }

    // ==========================================
    // GUARDAR PRODUCTO EN SQLITE
    // ==========================================

    private void guardarProducto(
            String nombre,
            double cantidad,
            String unidad,
            long fechaVencimiento
    ) {

        // Fecha actual
        long fechaAgregado = System.currentTimeMillis();

        // Usuario temporal
        // Después podemos reemplazarlo por el usuario
        // que inició sesión.
        int usuarioId = 0;

        // Emoji por defecto
        String emoji = "🍎";

        // Crear objeto Producto
        Producto producto = new Producto(
                usuarioId,
                nombre,
                cantidad,
                unidad,
                fechaVencimiento,
                fechaAgregado,
                emoji
        );

        // Insertar en SQLite
        long resultado = repositoryProducto.insertar(producto);

        // ==========================================
        // COMPROBAR SI SE GUARDÓ
        // ==========================================

        if (resultado != -1) {

            Toast.makeText(
                    this,
                    "Alimento registrado correctamente",
                    Toast.LENGTH_SHORT
            ).show();

            // Regresar al inventario
            finish();

        } else {

            Toast.makeText(
                    this,
                    "No se pudo guardar el alimento",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}