package com.example.salvatucomida.Models;

public class Tienda {
    private int id;
    private int usuarioId;
    private String nombre;
    private String ubicacion;
    private String telefono;
    private double latitud;
    private double longitud;
    private double distanciaKm;
    private double precioTotalEstimado;
    private int productoReferenciaId;
    private String fotoPath; // 🆕

    public Tienda() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }
    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }
    public double getDistanciaKm() { return distanciaKm; }
    public void setDistanciaKm(double distanciaKm) { this.distanciaKm = distanciaKm; }
    public double getPrecioTotalEstimado() { return precioTotalEstimado; }
    public void setPrecioTotalEstimado(double p) { this.precioTotalEstimado = p; }
    public int getProductoReferenciaId() { return productoReferenciaId; }
    public void setProductoReferenciaId(int id) { this.productoReferenciaId = id; }
    public String getFotoPath() { return fotoPath; } // 🆕
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; } // 🆕
}