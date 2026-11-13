package com.example.salvatucomida;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final long SPLASH_MS = 800;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1. Pedir permiso de notificaciones (Android 13+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        // 2. Programar alarma diaria (WorkManager)
        programarAlarmaDiaria();

        // 3. Decidir a dónde ir: Login o Inicio (según sesión Firebase)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                // Ya hay sesión activa → directo al inicio
                intent = new Intent(MainActivity.this, MainActivity_Inicio.class);
            } else {
                // No hay sesión → al login
                intent = new Intent(MainActivity.this, MainActivity_Login.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_MS);
    }

    private void programarAlarmaDiaria() {
        PeriodicWorkRequest request = new PeriodicWorkRequest.Builder(
                AlarmaVencimientoWorker.class, 24, TimeUnit.HOURS)
                .setInitialDelay(calcularRetrasoHastaLas8AM(), TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "AlarmaVencimiento",
                ExistingPeriodicWorkPolicy.KEEP,
                request);
    }

    private long calcularRetrasoHastaLas8AM() {
        Calendar c = Calendar.getInstance();
        Calendar target = Calendar.getInstance();
        target.set(Calendar.HOUR_OF_DAY, 8);
        target.set(Calendar.MINUTE, 0);
        target.set(Calendar.SECOND, 0);

        if (target.before(c)) {
            target.add(Calendar.DAY_OF_MONTH, 1);
        }
        return target.getTimeInMillis() - c.getTimeInMillis();
    }
}