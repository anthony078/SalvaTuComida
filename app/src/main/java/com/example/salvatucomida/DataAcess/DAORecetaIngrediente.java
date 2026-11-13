package com.example.salvatucomida.DataAcess;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.salvatucomida.Models.RecetaIngrediente;

import java.util.ArrayList;
import java.util.List;

public class DAORecetaIngrediente {

    private BDHelper dbHelper;

    public DAORecetaIngrediente(Context context) {
        dbHelper = new BDHelper(context);
    }

    public long insertar(RecetaIngrediente relacion) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues valores = new ContentValues();

        valores.put("recetaId", relacion.getRecetaId());
        valores.put("ingredienteId", relacion.getIngredienteId());

        long id = db.insert("receta_ingrediente", null, valores);

        db.close();

        return id;
    }

    public List<RecetaIngrediente> obtenerIngredientesDeReceta(int recetaId) {

        List<RecetaIngrediente> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM receta_ingrediente WHERE recetaId = ?",
                new String[]{String.valueOf(recetaId)}
        );

        if (cursor.moveToFirst()) {

            do {
                lista.add(cursorToRelacion(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public List<RecetaIngrediente> obtenerRecetasDeIngrediente(int ingredienteId) {

        List<RecetaIngrediente> lista = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM receta_ingrediente WHERE ingredienteId = ?",
                new String[]{String.valueOf(ingredienteId)}
        );

        if (cursor.moveToFirst()) {

            do {
                lista.add(cursorToRelacion(cursor));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

    public int eliminar(int id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int filas = db.delete(
                "receta_ingrediente",
                "id = ?",
                new String[]{String.valueOf(id)}
        );

        db.close();

        return filas;
    }

    public int eliminarRelacion(int recetaId, int ingredienteId) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int filas = db.delete(
                "receta_ingrediente",
                "recetaId = ? AND ingredienteId = ?",
                new String[]{
                        String.valueOf(recetaId),
                        String.valueOf(ingredienteId)
                }
        );

        db.close();

        return filas;
    }

    private RecetaIngrediente cursorToRelacion(Cursor cursor) {

        RecetaIngrediente relacion = new RecetaIngrediente();

        relacion.setId(
                cursor.getInt(cursor.getColumnIndexOrThrow("id"))
        );

        relacion.setRecetaId(
                cursor.getInt(cursor.getColumnIndexOrThrow("recetaId"))
        );

        relacion.setIngredienteId(
                cursor.getInt(cursor.getColumnIndexOrThrow("ingredienteId"))
        );

        return relacion;
    }
}