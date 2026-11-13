package com.example.salvatucomida.Repository;


import android.content.Context;

import com.example.salvatucomida.DataAcess.DAOListaCompra;
import com.example.salvatucomida.Models.ListaCompra;

import java.util.List;

public class Repository_ListaCompra {

    private final DAOListaCompra dao;

    public Repository_ListaCompra(Context context) {
        dao = new DAOListaCompra(context.getApplicationContext());
    }

    public long insertar(ListaCompra producto) {
        return dao.insertar(producto);
    }

    public List<ListaCompra> obtenerTodas() {
        return dao.obtenerTodas();
    }

    public int actualizar(ListaCompra producto) {
        return dao.actualizar(producto);
    }

    public int marcarComprado(int id, boolean comprado) {
        return dao.marcarComprado(id, comprado);
    }

    public int eliminar(int id) {
        return dao.eliminar(id);
    }

    public double obtenerTotalPendiente() {
        return dao.obtenerTotalPendiente();
    }
}
