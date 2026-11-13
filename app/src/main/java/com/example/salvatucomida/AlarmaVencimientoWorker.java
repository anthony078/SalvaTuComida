package com.example.salvatucomida;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.salvatucomida.DataAcess.DAOProducto;
import com.example.salvatucomida.Models.Producto;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlarmaVencimientoWorker extends Worker {

    public AlarmaVencimientoWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        DAOProducto dao = new DAOProducto(getApplicationContext());
        List<Producto> inventario = dao.obtenerTodos();
        int porVencer = 0;

        for (Producto p : inventario) {
            Calendar hoy = Calendar.getInstance();
            Calendar venc = Calendar.getInstance();
            venc.setTimeInMillis(p.getFechaVencimiento());

            long diff = venc.getTimeInMillis() - hoy.getTimeInMillis();
            long dias = TimeUnit.MILLISECONDS.toDays(diff);

            if (dias <= 1) porVencer++; // Vence hoy o mañana
        }

        if (porVencer > 0) {
            lanzarNotificacion(porVencer);
        }
        return Result.success();
    }

    private void lanzarNotificacion(int cantidad) {
        NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String canalId = "alarma_vencimiento";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel canal = new NotificationChannel(canalId, "Vencimientos", NotificationManager.IMPORTANCE_HIGH);
            manager.createNotificationChannel(canal);
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity_Inventario.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), canalId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("¡Buenos días! 👨‍🍳")
                .setContentText("Tienes " + cantidad + " producto(s) que vencen hoy o mañana. ¡Revísalos!")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(1, builder.build());
    }
}