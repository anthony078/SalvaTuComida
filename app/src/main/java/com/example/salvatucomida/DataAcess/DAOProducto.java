package com.example.salvatucomida.DataAcess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.salvatucomida.Models.Producto;

import java.util.ArrayList;
import java.util.List;

public class DAOProducto {

    private BDHelper dbHelper;

    public DAOProducto(Context context) {
        dbHelper = new BDHelper(context);
    }

    public long insertar(Producto producto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("usuarioId", producto.getUsuarioId());
        valores.put("nombre", producto.getNombre());
        valores.put("cantidadValor", producto.getCantidadValor());
        valores.put("unidad", producto.getUnidad());
        valores.put("fechaVencimiento", producto.getFechaVencimiento());
        valores.put("fechaAgregado", producto.getFechaAgregado());
        valores.put("emoji", producto.getEmoji());
        valores.put("fotoPath", producto.getFotoPath()); // 🆕

        long id = db.insert("productos", null, valores);
        db.close();
        return id;
    }

    public List<Producto> obtenerTodos() {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos ORDER BY fechaVencimiento ASC", null);
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToProducto(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public Producto obtenerPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos WHERE id = ?", new String[]{String.valueOf(id)});
        Producto producto = null;
        if (cursor.moveToFirst()) {
            producto = cursorToProducto(cursor);
        }
        cursor.close();
        db.close();
        return producto;
    }

    public List<Producto> buscar(String texto) {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM productos WHERE nombre LIKE ? ORDER BY fechaVencimiento ASC",
                new String[]{"%" + texto + "%"}
        );
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToProducto(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public int actualizar(Producto producto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("usuarioId", producto.getUsuarioId());
        valores.put("nombre", producto.getNombre());
        valores.put("cantidadValor", producto.getCantidadValor());
        valores.put("unidad", producto.getUnidad());
        valores.put("fechaVencimiento", producto.getFechaVencimiento());
        valores.put("fechaAgregado", producto.getFechaAgregado());
        valores.put("emoji", producto.getEmoji());
        valores.put("fotoPath", producto.getFotoPath()); // 🆕

        int filas = db.update("productos", valores, "id = ?", new String[]{String.valueOf(producto.getId())});
        db.close();
        return filas;
    }

    public int eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("productos", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return filas;
    }


    public List<Producto> obtenerPorUsuario(int usuarioId) {
        List<Producto> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM productos WHERE usuarioId = ? ORDER BY fechaVencimiento ASC",
                new String[]{String.valueOf(usuarioId)}
        );
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToProducto(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    private Producto cursorToProducto(Cursor cursor) {
        Producto producto = new Producto();
        producto.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        producto.setUsuarioId(cursor.getInt(cursor.getColumnIndexOrThrow("usuarioId")));
        producto.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        producto.setCantidadValor(cursor.getDouble(cursor.getColumnIndexOrThrow("cantidadValor")));
        producto.setUnidad(cursor.getString(cursor.getColumnIndexOrThrow("unidad")));
        producto.setFechaVencimiento(cursor.getLong(cursor.getColumnIndexOrThrow("fechaVencimiento")));
        producto.setFechaAgregado(cursor.getLong(cursor.getColumnIndexOrThrow("fechaAgregado")));
        producto.setEmoji(cursor.getString(cursor.getColumnIndexOrThrow("emoji")));
        producto.setFotoPath(cursor.getString(cursor.getColumnIndexOrThrow("fotoPath"))); // 🆕
        return producto;
    }
}