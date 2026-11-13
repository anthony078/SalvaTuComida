package com.example.salvatucomida.DataAcess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.salvatucomida.Models.Tienda;

import java.util.ArrayList;
import java.util.List;

public class DAOTienda {
    private BDHelper dbHelper;

    public DAOTienda(Context context) {
        this.dbHelper = new BDHelper(context);
    }

    public long insertar(Tienda t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("usuarioId", t.getUsuarioId());
        values.put("nombre", t.getNombre());
        values.put("ubicacion", t.getUbicacion());
        values.put("telefono", t.getTelefono());
        values.put("distanciaKm", t.getDistanciaKm());
        values.put("precioTotalEstimado", t.getPrecioTotalEstimado());
        values.put("fotoPath", t.getFotoPath()); // Si agregaste esta columna
        long id = db.insert("tiendas", null, values);
        db.close();
        return id;
    }

    public int actualizar(Tienda t) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", t.getNombre());
        values.put("ubicacion", t.getUbicacion());
        values.put("telefono", t.getTelefono());
        values.put("distanciaKm", t.getDistanciaKm());
        values.put("precioTotalEstimado", t.getPrecioTotalEstimado());
        values.put("fotoPath", t.getFotoPath());
        int rows = db.update("tiendas", values, "id = ?", new String[]{String.valueOf(t.getId())});
        db.close();
        return rows;
    }

    public void eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("tiendas", "id = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Tienda> obtenerTodas() {
        List<Tienda> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("tiendas", null, null, null, null, null, "distanciaKm ASC");

        if (cursor != null && cursor.moveToFirst()) {
            do {
                Tienda t = new Tienda();
                t.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                t.setUsuarioId(cursor.getInt(cursor.getColumnIndexOrThrow("usuarioId")));
                t.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
                t.setUbicacion(cursor.getString(cursor.getColumnIndexOrThrow("ubicacion")));
                t.setTelefono(cursor.getString(cursor.getColumnIndexOrThrow("telefono")));
                t.setDistanciaKm(cursor.getDouble(cursor.getColumnIndexOrThrow("distanciaKm")));
                t.setPrecioTotalEstimado(cursor.getDouble(cursor.getColumnIndexOrThrow("precioTotalEstimado")));

                // Si tienes la columna fotoPath en tu BD:
                int indexFoto = cursor.getColumnIndex("fotoPath");
                if (indexFoto != -1) {
                    t.setFotoPath(cursor.getString(indexFoto));
                }

                lista.add(t);
            } while (cursor.moveToNext());
            cursor.close();
        }
        db.close();
        return lista;
    }
}