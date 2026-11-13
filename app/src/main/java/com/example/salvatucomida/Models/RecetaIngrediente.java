package com.example.salvatucomida.Models;

public class RecetaIngrediente {

    private int id;
    private int recetaId;
    private int ingredienteId;

    public RecetaIngrediente() {
    }

    public RecetaIngrediente(int id, int recetaId, int ingredienteId) {
        this.id = id;
        this.recetaId = recetaId;
        this.ingredienteId = ingredienteId;
    }

    public RecetaIngrediente(int recetaId, int ingredienteId) {
        this.recetaId = recetaId;
        this.ingredienteId = ingredienteId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecetaId() {
        return recetaId;
    }

    public void setRecetaId(int recetaId) {
        this.recetaId = recetaId;
    }

    public int getIngredienteId() {
        return ingredienteId;
    }

    public void setIngredienteId(int ingredienteId) {
        this.ingredienteId = ingredienteId;
    }
}