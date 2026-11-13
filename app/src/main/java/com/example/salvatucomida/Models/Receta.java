package com.example.salvatucomida.Models;

public class Receta {

    private int id;
    private String nombre;
    private String tipoComida;
    private int tiempoPreparacion;
    private String emoji;
    private String color;

    public Receta() {
    }
    public Receta(int id, String nombre, String tipoComida,
                  int tiempoPreparacion, String emoji, String color) {

        this.id = id;
        this.nombre = nombre;
        this.tipoComida = tipoComida;
        this.tiempoPreparacion = tiempoPreparacion;
        this.emoji = emoji;
        this.color = color;
    }
    public Receta(String nombre, String tipoComida,
                  int tiempoPreparacion, String emoji, String color) {

        this.nombre = nombre;
        this.tipoComida = tipoComida;
        this.tiempoPreparacion = tiempoPreparacion;
        this.emoji = emoji;
        this.color = color;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getTipoComida() {
        return tipoComida;
    }
    public void setTipoComida(String tipoComida) {
        this.tipoComida = tipoComida;
    }
    public int getTiempoPreparacion() {
        return tiempoPreparacion;
    }
    public void setTiempoPreparacion(int tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }
    public String getEmoji() {
        return emoji;
    }
    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
    public String getColor() {
        return color;
    }
    public void setColor(String color) {
        this.color = color;
    }
}