package com.example.salvatucomida;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.example.salvatucomida.DataAcess.DAOTienda;
import com.example.salvatucomida.Models.Tienda;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity_Tienda extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LatLng ubicacionSeleccionada = null;
    private DAOTienda daoTienda;
    private int tiendaIdEditar = -1;
    private String modo = "";
    private SharedPreferences prefs;

    private ActivityResultLauncher<String> imagePickerLauncher;
    private String fotoSeleccionadaUri = null;
    private ImageView currentImageViewInDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tienda);

        prefs = getSharedPreferences("SalvaTuComidaPrefs", Context.MODE_PRIVATE);
        daoTienda = new DAOTienda(this);

        tiendaIdEditar = getIntent().getIntExtra("tienda_id", -1);
        modo = getIntent().getStringExtra("modo");
        if (modo == null) modo = "";

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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        findViewById(R.id.btnAgregarTiendaMapa).setOnClickListener(v -> {
            if (ubicacionSeleccionada == null) {
                Toast.makeText(this, "Toca el mapa para seleccionar la ubicación", Toast.LENGTH_SHORT).show();
                return;
            }

            if (modo.equals("seleccionar_casa")) {
                guardarCasa();
            } else {
                mostrarDialogoGuardarTienda();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        LatLng cajamarca = new LatLng(-7.16378, -78.50027);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cajamarca, 14f));

        if (modo.equals("seleccionar_casa")) {
            float lat = prefs.getFloat("casa_lat", -7.16378f);
            float lon = prefs.getFloat("casa_lon", -78.50027f);
            ubicacionSeleccionada = new LatLng(lat, lon);
            mMap.addMarker(new MarkerOptions().position(ubicacionSeleccionada).title("Mi Casa"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionSeleccionada, 15f));
        } else if (tiendaIdEditar != -1) {
            Tienda t = buscarTiendaLocal(tiendaIdEditar);
            if (t != null) {
                ubicacionSeleccionada = new LatLng(t.getLatitud(), t.getLongitud());
                mMap.addMarker(new MarkerOptions().position(ubicacionSeleccionada).title(t.getNombre()));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionSeleccionada, 15f));
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
                fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(miUbicacion, 16f));
                    }
                });
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }

        mMap.setOnMapClickListener(latLng -> {
            ubicacionSeleccionada = latLng;
            mMap.clear();
            String titulo = modo.equals("seleccionar_casa") ? "Mi Casa" : "Tienda aquí";
            mMap.addMarker(new MarkerOptions().position(latLng).title(titulo));
        });
    }

    private void guardarCasa() {
        prefs.edit()
                .putFloat("casa_lat", (float) ubicacionSeleccionada.latitude)
                .putFloat("casa_lon", (float) ubicacionSeleccionada.longitude)
                .apply();
        Toast.makeText(this, "✅ Ubicación de casa guardada", Toast.LENGTH_LONG).show();
        finish();
    }

    private void mostrarDialogoGuardarTienda() {
        LinearLayout cont = new LinearLayout(this);
        cont.setOrientation(LinearLayout.VERTICAL);
        int pad = (int) (16 * getResources().getDisplayMetrics().density);
        cont.setPadding(pad, pad, pad, pad);

        final EditText input = new EditText(this);
        input.setHint("Nombre de la tienda (Ej. Mercado)");
        cont.addView(input);

        currentImageViewInDialog = new ImageView(this);
        currentImageViewInDialog.setLayoutParams(new LinearLayout.LayoutParams(150, 150));
        currentImageViewInDialog.setScaleType(ImageView.ScaleType.CENTER_CROP);
        currentImageViewInDialog.setImageResource(R.mipmap.ic_launcher_round);

        if (tiendaIdEditar != -1) {
            Tienda t = buscarTiendaLocal(tiendaIdEditar);
            if (t != null) {
                if (t.getFotoPath() != null) {
                    fotoSeleccionadaUri = t.getFotoPath();
                    Glide.with(this).load(Uri.parse(t.getFotoPath())).circleCrop().into(currentImageViewInDialog);
                }
                input.setText(t.getNombre());
            }
        } else {
            fotoSeleccionadaUri = null;
        }

        Button btnFoto = new Button(this);
        btnFoto.setText("📸 Elegir Foto");
        btnFoto.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        cont.addView(btnFoto);
        cont.addView(currentImageViewInDialog);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(tiendaIdEditar != -1 ? "✏️ Editar Tienda" : "🏪 Guardar Tienda");
        builder.setView(cont);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nombre = input.getText().toString().trim();
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Escribe un nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calcular distancia desde la casa actual
            float latCasa = prefs.getFloat("casa_lat", -7.16378f);
            float lonCasa = prefs.getFloat("casa_lon", -78.50027f);
            float[] results = new float[1];
            Location.distanceBetween(latCasa, lonCasa, ubicacionSeleccionada.latitude, ubicacionSeleccionada.longitude, results);
            double distanciaKm = results[0] / 1000.0;

            if (tiendaIdEditar != -1) {
                Tienda t = buscarTiendaLocal(tiendaIdEditar);
                if (t != null) {
                    t.setNombre(nombre);
                    t.setDistanciaKm(distanciaKm);
                    t.setLatitud(ubicacionSeleccionada.latitude);  // 👈 GUARDAR COORDENADAS
                    t.setLongitud(ubicacionSeleccionada.longitude); // 👈 GUARDAR COORDENADAS
                    t.setFotoPath(fotoSeleccionadaUri);
                    daoTienda.actualizar(t);
                    Toast.makeText(this, "Tienda actualizada", Toast.LENGTH_SHORT).show();
                }
            } else {
                Tienda t = new Tienda();
                t.setUsuarioId(0);
                t.setNombre(nombre);
                t.setDistanciaKm(distanciaKm);
                t.setLatitud(ubicacionSeleccionada.latitude);  // 👈 GUARDAR COORDENADAS
                t.setLongitud(ubicacionSeleccionada.longitude); // 👈 GUARDAR COORDENADAS
                t.setPrecioTotalEstimado(0);
                t.setProductoReferenciaId(-1);
                t.setFotoPath(fotoSeleccionadaUri);
                daoTienda.insertar(t);
                Toast.makeText(this, "Tienda guardada", Toast.LENGTH_SHORT).show();
            }
            finish();
        });
        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private Tienda buscarTiendaLocal(int id) {
        for (Tienda t : daoTienda.obtenerTodas()) {
            if (t.getId() == id) return t;
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onMapReady(mMap);
            }
        }
    }
}