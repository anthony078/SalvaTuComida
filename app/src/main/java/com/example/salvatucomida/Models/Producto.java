package com.example.salvatucomida.Models;

public class Producto {
    private int id;
    private int usuarioId;
    private String nombre;
    private double cantidadValor;
    private String unidad;
    private long fechaVencimiento;
    private long fechaAgregado;
    private String emoji;
    private String fotoPath; // 🆕

    public Producto() {}

    public Producto(int usuarioId, String nombre, double cantidadValor, String unidad, long fechaVencimiento, long fechaAgregado, String emoji) {
        this.usuarioId = usuarioId;
        this.nombre = nombre;
        this.cantidadValor = cantidadValor;
        this.unidad = unidad;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaAgregado = fechaAgregado;
        this.emoji = emoji;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public double getCantidadValor() { return cantidadValor; }
    public void setCantidadValor(double cantidadValor) { this.cantidadValor = cantidadValor; }
    public String getUnidad() { return unidad; }
    public void setUnidad(String unidad) { this.unidad = unidad; }
    public long getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(long fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public long getFechaAgregado() { return fechaAgregado; }
    public void setFechaAgregado(long fechaAgregado) { this.fechaAgregado = fechaAgregado; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public String getFotoPath() { return fotoPath; } // 🆕
    public void setFotoPath(String fotoPath) { this.fotoPath = fotoPath; } // 🆕
}