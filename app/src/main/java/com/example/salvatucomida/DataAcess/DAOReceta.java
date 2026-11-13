package com.example.salvatucomida.DataAcess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.salvatucomida.Models.Receta;

import java.util.ArrayList;
import java.util.List;

public class DAOReceta {

    private BDHelper dbHelper;

    public DAOReceta(Context context) {
        dbHelper = new BDHelper(context);
    }

    public long insertar(Receta receta) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("nombre", receta.getNombre());
        valores.put("tipoComida", receta.getTipoComida());
        valores.put("tiempoPreparacion", receta.getTiempoPreparacion());
        valores.put("emoji", receta.getEmoji());
        valores.put("color", receta.getColor());
        long id = db.insert("recetas", null, valores);
        db.close();
        return id;
    }

    public List<Receta> obtenerTodas() {

        List<Receta> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM recetas ORDER BY nombre ASC",
                null
        );
        if (cursor.moveToFirst()) {
            do {
                lista.add(cursorToReceta(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return lista;
    }

    public Receta obtenerPorId(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM recetas WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
        Receta receta = null;
        if (cursor.moveToFirst()) {
            receta = cursorToReceta(cursor);
        }
        cursor.close();
        db.close();
        return receta;
    }

    public List<Receta> buscar(String texto) {

        List<Receta> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM recetas WHERE nombre LIKE ?",
                new String[]{"%" + texto + "%"}
        );

        if (cursor.moveToFirst()) {

            do {
                lista.add(cursorToReceta(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return lista;
    }

    public int actualizar(Receta receta) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("nombre", receta.getNombre());
        valores.put("tipoComida", receta.getTipoComida());
        valores.put("tiempoPreparacion", receta.getTiempoPreparacion());
        valores.put("emoji", receta.getEmoji());
        valores.put("color", receta.getColor());

        int filas = db.update(
                "recetas",
                valores,
                "id = ?",
                new String[]{String.valueOf(receta.getId())}
        );
        db.close();
        return filas;
    }

    public int eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int filas = db.delete(
                "recetas",
                "id = ?",
                new String[]{String.valueOf(id)});
        db.close();
        return filas;
    }

    private Receta cursorToReceta(Cursor cursor) {

        Receta receta = new Receta();
        receta.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        receta.setNombre(cursor.getString(cursor.getColumnIndexOrThrow("nombre")));
        receta.setTipoComida(cursor.getString(cursor.getColumnIndexOrThrow("tipoComida")));
        receta.setTiempoPreparacion(cursor.getInt(cursor.getColumnIndexOrThrow("tiempoPreparacion")));
        receta.setEmoji(cursor.getString(cursor.getColumnIndexOrThrow("emoji")));
        receta.setColor(cursor.getString(cursor.getColumnIndexOrThrow("color")));

        return receta;
    }

}