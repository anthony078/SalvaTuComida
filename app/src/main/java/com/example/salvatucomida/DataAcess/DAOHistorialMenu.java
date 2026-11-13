package com.example.salvatucomida.DataAcess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.salvatucomida.Models.HistorialMenu;

import java.util.ArrayList;
import java.util.List;

public class DAOHistorialMenu {

    private BDHelper dbHelper;

    public DAOHistorialMenu(Context context) {
        dbHelper = new BDHelper(context);
    }

    public long insertar(HistorialMenu historial) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("usuarioId", historial.getUsuarioId());
        valores.put("recetaId", historial.getRecetaId());
        valores.put("fecha", historial.getFecha());
        valores.put("tipoComida", historial.getTipoComida());

        long id = db.insert("historial_menu", null, valores);

        db.close();

        return id;
    }

    public List<HistorialMenu> obtenerTodos() {

        List<HistorialMenu> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM historial_menu ORDER BY fecha DESC",
                null
        );

        if (cursor.moveToFirst()) {

            do {
                lista.add(cursorToHistorial(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public List<HistorialMenu> obtenerPorFecha(String fecha) {

        List<HistorialMenu> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM historial_menu WHERE fecha = ?",
                new String[]{fecha}
        );

        if (cursor.moveToFirst()) {

            do {
                lista.add(cursorToHistorial(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public int eliminar(int id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int filas = db.delete(
                "historial_menu",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas;
    }

    private HistorialMenu cursorToHistorial(Cursor cursor) {

        HistorialMenu historial = new HistorialMenu();

        historial.setId(
                cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        );

        historial.setUsuarioId(
                cursor.getInt(cursor.getColumnIndexOrThrow("usuarioId"))
        );

        historial.setRecetaId(
                cursor.getInt(cursor.getColumnIndexOrThrow("recetaId"))
        );

        historial.setFecha(
                cursor.getString(cursor.getColumnIndexOrThrow("fecha"))
        );

        historial.setTipoComida(
                cursor.getString(cursor.getColumnIndexOrThrow("tipoComida"))
        );

        return historial;
    }
}