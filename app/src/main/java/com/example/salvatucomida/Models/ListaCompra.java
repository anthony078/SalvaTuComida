package com.example.salvatucomida.Models;

public class ListaCompra {

    private int id;
    private int usuarioId;
    private String nombre;
    private double precio;
    private boolean comprado;
    private long fechaAgregado;

    public ListaCompra() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public boolean isComprado() { return comprado; }
    public void setComprado(boolean comprado) { this.comprado = comprado; }

    public long getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(long fechaAgregado) { this.fechaAgregado = fechaAgregado; }
}