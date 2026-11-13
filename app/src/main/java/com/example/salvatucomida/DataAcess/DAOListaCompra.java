package com.example.salvatucomida.DataAcess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.salvatucomida.Models.ListaCompra;

import java.util.ArrayList;
import java.util.List;

public class DAOListaCompra {

    private BDHelper dbHelper;

    public DAOListaCompra(Context context) {
        dbHelper = new BDHelper(context);
    }

    public long insertar(ListaCompra producto) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("usuarioId", producto.getUsuarioId());
        valores.put("nombre", producto.getNombre());
        valores.put("precio", producto.getPrecio());
        valores.put("comprado", producto.isComprado() ? 1 : 0);
        valores.put("fechaAgregado", producto.getFechaAgregado());

        long id = db.insert("lista_compra", null, valores);

        db.close();

        return id;
    }

    public List<ListaCompra> obtenerTodas() {

        List<ListaCompra> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM lista_compra ORDER BY comprado ASC, fechaAgregado DESC",
                null
        );

        if (cursor.moveToFirst()) {

            do {
                lista.add(cursorToListaCompra(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public int actualizar(ListaCompra producto) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("usuarioId", producto.getUsuarioId());
        valores.put("nombre", producto.getNombre());
        valores.put("precio", producto.getPrecio());
        valores.put("comprado", producto.isComprado() ? 1 : 0);
        valores.put("fechaAgregado", producto.getFechaAgregado());

        int filas = db.update(
                "lista_compra",
                valores,
                "id = ?",
                new String[]{String.valueOf(producto.getId())}
        );

        db.close();

        return filas;
    }

    public int marcarComprado(int id, boolean comprado) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("comprado", comprado ? 1 : 0);

        int filas = db.update(
                "lista_compra",
                valores,
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas;
    }

    public int eliminar(int id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int filas = db.delete(
                "lista_compra",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas;
    }

    public double obtenerTotalPendiente() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(precio) FROM lista_compra WHERE comprado = 0",
                null
        );

        double total = 0;

        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return total;
    }

    private ListaCompra cursorToListaCompra(Cursor cursor) {

        ListaCompra producto = new ListaCompra();

        producto.setId(
                cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        );

        producto.setUsuarioId(
                cursor.getInt(cursor.getColumnIndexOrThrow("usuarioId"))
        );

        producto.setNombre(
                cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
        );

        producto.setPrecio(
                cursor.getDouble(cursor.getColumnIndexOrThrow("precio"))
        );

        producto.setComprado(
                cursor.getInt(cursor.getColumnIndexOrThrow("comprado")) == 1
        );

        producto.setFechaAgregado(
                cursor.getLong(cursor.getColumnIndexOrThrow("fechaAgregado"))
        );

        return producto;
    }
}