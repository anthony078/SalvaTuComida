package com.example.salvatucomida.DataAcess;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDHelper extends SQLiteOpenHelper {

    private static final String NOMBRE_BD = "SalvaTuComida.db";
    private static final int VERSION_BD = 4;

    public BDHelper(Context context) {
        super(context, NOMBRE_BD, null, VERSION_BD);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE productos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuarioId INTEGER DEFAULT 0, " +
                "nombre TEXT NOT NULL, " +
                "cantidadValor REAL NOT NULL, " +
                "unidad TEXT NOT NULL, " +
                "fechaVencimiento INTEGER NOT NULL, " +
                "fechaAgregado INTEGER NOT NULL, " +
                "emoji TEXT, " +
                "fotoPath TEXT)"); // 🆕 Nueva columna

        db.execSQL("CREATE TABLE ingredientes (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL UNIQUE, emoji TEXT)");
        db.execSQL("CREATE TABLE recetas (id INTEGER PRIMARY KEY AUTOINCREMENT, nombre TEXT NOT NULL, tipoComida TEXT, tiempoPreparacion INTEGER, emoji TEXT, color TEXT)");
        db.execSQL("CREATE TABLE receta_ingrediente (id INTEGER PRIMARY KEY AUTOINCREMENT, recetaId INTEGER NOT NULL, ingredienteId INTEGER NOT NULL, FOREIGN KEY(recetaId) REFERENCES recetas(id) ON DELETE CASCADE, FOREIGN KEY(ingredienteId) REFERENCES ingredientes(id) ON DELETE CASCADE, UNIQUE(recetaId, ingredienteId))");
        db.execSQL("CREATE TABLE lista_compra (id INTEGER PRIMARY KEY AUTOINCREMENT, usuarioId INTEGER DEFAULT 0, nombre TEXT NOT NULL, precio REAL DEFAULT 0, comprado INTEGER DEFAULT 0, fechaAgregado INTEGER NOT NULL)");

        db.execSQL("CREATE TABLE tiendas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "usuarioId INTEGER DEFAULT 0, " +
                "nombre TEXT NOT NULL, " +
                "ubicacion TEXT, " +
                "telefono TEXT, " +
                "latitud REAL, " +
                "longitud REAL, " +
                "distanciaKm REAL DEFAULT 0, " +
                "precioTotalEstimado REAL DEFAULT 0, " +
                "productoReferenciaId INTEGER, " +
                "fotoPath TEXT, " + // 🆕 Nueva columna
                "FOREIGN KEY(productoReferenciaId) REFERENCES lista_compra(id) ON DELETE SET NULL)");

        db.execSQL("CREATE TABLE precios_tienda (id INTEGER PRIMARY KEY AUTOINCREMENT, tiendaId INTEGER NOT NULL, listaCompraId INTEGER NOT NULL, precio REAL NOT NULL DEFAULT 0, FOREIGN KEY(tiendaId) REFERENCES tiendas(id) ON DELETE CASCADE, FOREIGN KEY(listaCompraId) REFERENCES lista_compra(id) ON DELETE CASCADE, UNIQUE(tiendaId, listaCompraId))");
        db.execSQL("CREATE TABLE historial_menu (id INTEGER PRIMARY KEY AUTOINCREMENT, usuarioId INTEGER DEFAULT 0, recetaId INTEGER NOT NULL, fecha TEXT NOT NULL, tipoComida TEXT, FOREIGN KEY(recetaId) REFERENCES recetas(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS historial_menu");
        db.execSQL("DROP TABLE IF EXISTS precios_tienda");
        db.execSQL("DROP TABLE IF EXISTS tiendas");
        db.execSQL("DROP TABLE IF EXISTS lista_compra");
        db.execSQL("DROP TABLE IF EXISTS receta_ingrediente");
        db.execSQL("DROP TABLE IF EXISTS recetas");
        db.execSQL("DROP TABLE IF EXISTS ingredientes");
        db.execSQL("DROP TABLE IF EXISTS productos");
        onCreate(db);
    }
}