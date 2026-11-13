package com.example.salvatucomida.DataAcess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.salvatucomida.Models.Ingrediente;

import java.util.ArrayList;
import java.util.List;

public class DAOIngrediente {

    private BDHelper dbHelper;

    public DAOIngrediente(Context context) {
        dbHelper = new BDHelper(context);
    }

    public long insertar(Ingrediente ingrediente) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("nombre", ingrediente.getNombre());
        valores.put("emoji", ingrediente.getEmoji());

        long id = db.insert("ingredientes", null, valores);

        db.close();

        return id;
    }

    public List<Ingrediente> obtenerTodos() {

        List<Ingrediente> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM ingredientes ORDER BY nombre ASC",
                null
        );

        if (cursor.moveToFirst()) {

            do {
                lista.add(cursorToIngrediente(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public Ingrediente obtenerPorId(int id) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM ingredientes WHERE id = ?",
                new String[]{String.valueOf(id)}
        );

        Ingrediente ingrediente = null;

        if (cursor.moveToFirst()) {
            ingrediente = cursorToIngrediente(cursor);
        }

        cursor.close();
        db.close();

        return ingrediente;
    }

    public List<Ingrediente> buscar(String texto) {

        List<Ingrediente> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM ingredientes WHERE nombre LIKE ?",
                new String[]{"%" + texto + "%"}
        );

        if (cursor.moveToFirst()) {

            do {
                lista.add(cursorToIngrediente(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public int actualizar(Ingrediente ingrediente) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("nombre", ingrediente.getNombre());
        valores.put("emoji", ingrediente.getEmoji());

        int filas = db.update(
                "ingredientes",
                valores,
                "id = ?",
                new String[]{String.valueOf(ingrediente.getId())}
        );

        db.close();

        return filas;
    }

    public int eliminar(int id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int filas = db.delete(
                "ingredientes",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas;
    }

    private Ingrediente cursorToIngrediente(Cursor cursor) {

        Ingrediente ingrediente = new Ingrediente();

        ingrediente.setId(
                cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        );

        ingrediente.setNombre(
                cursor.getString(cursor.getColumnIndexOrThrow("nombre"))
        );

        ingrediente.setEmoji(
                cursor.getString(cursor.getColumnIndexOrThrow("emoji"))
        );

        return ingrediente;
    }

}