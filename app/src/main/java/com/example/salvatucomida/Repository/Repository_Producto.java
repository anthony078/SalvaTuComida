package com.example.salvatucomida.Repository;

import android.content.Context;

import com.example.salvatucomida.DataAcess.DAOProducto;
import com.example.salvatucomida.Models.Producto;

import java.util.List;

public class Repository_Producto {

    private final DAOProducto daoProducto;
    public Repository_Producto(Context context)
    {daoProducto = new DAOProducto(context);}
    public long insertar(Producto producto)
    {return daoProducto.insertar(producto);}
    public List<Producto> obtenerTodos()
    {return daoProducto.obtenerTodos();}
    public List<Producto> obtenerPorUsuario(int usuarioId)
    {return daoProducto.obtenerPorUsuario(usuarioId);}
    public List<Producto> buscar(String texto)
    {return daoProducto.buscar(texto);}
    public Producto obtenerPorId(int id)
    {return daoProducto.obtenerPorId(id);}
    public int actualizar(Producto producto)
    {return daoProducto.actualizar(producto);}
    public int eliminar(int id)
    {return daoProducto.eliminar(id);}
}