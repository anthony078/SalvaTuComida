package com.example.salvatucomida.DataAcess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.salvatucomida.Models.PrecioTienda;

import java.util.ArrayList;
import java.util.List;

public class DAOPrecioTienda {

    private BDHelper dbHelper;

    public DAOPrecioTienda(Context context) {
        dbHelper = new BDHelper(context);
    }

    public long insertar(PrecioTienda precio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("tiendaId", precio.getTiendaId());
        valores.put("listaCompraId", precio.getListaCompraId());
        valores.put("precio", precio.getPrecio());

        long id = db.insertWithOnConflict(
                "precios_tienda", null, valores, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
        return id;
    }

    public List<PrecioTienda> obtenerPorTienda(int tiendaId) {
        List<PrecioTienda> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM precios_tienda WHERE tiendaId = ?",
                new String[]{String.valueOf(tiendaId)});
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToPrecio(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public List<PrecioTienda> obtenerPorProducto(int listaCompraId) {
        List<PrecioTienda> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM precios_tienda WHERE listaCompraId = ? ORDER BY precio ASC",
                new String[]{String.valueOf(listaCompraId)});
        if (cursor.moveToFirst()) {
            do { lista.add(cursorToPrecio(cursor)); } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public double calcularTotalTienda(int tiendaId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(precio) FROM precios_tienda WHERE tiendaId = ?",
                new String[]{String.valueOf(tiendaId)});
        double total = 0;
        if (cursor.moveToFirst() && !cursor.isNull(0)) total = cursor.getDouble(0);
        cursor.close();
        db.close();
        return total;
    }

    public int eliminarPorTienda(int tiendaId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("precios_tienda", "tiendaId = ?",
                new String[]{String.valueOf(tiendaId)});
        db.close();
        return filas;
    }

    public int eliminarPorPrecio(int tiendaId, int listaCompraId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete("precios_tienda",
                "tiendaId = ? AND listaCompraId = ?",
                new String[]{
                        String.valueOf(tiendaId),
                        String.valueOf(listaCompraId)
                });
        db.close();
        return filas;
    }

    private PrecioTienda cursorToPrecio(Cursor cursor) {
        PrecioTienda p = new PrecioTienda();
        p.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        p.setTiendaId(cursor.getInt(cursor.getColumnIndexOrThrow("tiendaId")));
        p.setListaCompraId(cursor.getInt(cursor.getColumnIndexOrThrow("listaCompraId")));
        p.setPrecio(cursor.getDouble(cursor.getColumnIndexOrThrow("precio")));
        return p;
    }
}