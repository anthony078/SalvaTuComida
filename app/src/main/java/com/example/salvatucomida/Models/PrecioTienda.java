package com.example.salvatucomida.Models;

public class PrecioTienda {

    private int id;
    private int tiendaId;
    private int listaCompraId;
    private double precio;

    public PrecioTienda() {}

    public PrecioTienda(int tiendaId, int listaCompraId, double precio) {
        this.tiendaId = tiendaId;
        this.listaCompraId = listaCompraId;
        this.precio = precio;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getTiendaId() { return tiendaId; }
    public void setTiendaId(int tiendaId) { this.tiendaId = tiendaId; }

    public int getListaCompraId() { return listaCompraId; }
    public void setListaCompraId(int listaCompraId) { this.listaCompraId = listaCompraId; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }
}