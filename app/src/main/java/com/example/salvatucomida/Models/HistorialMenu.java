package com.example.salvatucomida.Models;

public class HistorialMenu {

    private int id;
    private int usuarioId;
    private int recetaId;
    private String fecha;
    private String tipoComida;

    public HistorialMenu() {
    }

    public HistorialMenu(int id, int usuarioId, int recetaId,
                         String fecha, String tipoComida) {

        this.id = id;
        this.usuarioId = usuarioId;
        this.recetaId = recetaId;
        this.fecha = fecha;
        this.tipoComida = tipoComida;
    }

    public HistorialMenu(int usuarioId, int recetaId,
                         String fecha, String tipoComida) {

        this.usuarioId = usuarioId;
        this.recetaId = recetaId;
        this.fecha = fecha;
        this.tipoComida = tipoComida;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getRecetaId() {
        return recetaId;
    }

    public void setRecetaId(int recetaId) {
        this.recetaId = recetaId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipoComida() {
        return tipoComida;
    }

    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }
}