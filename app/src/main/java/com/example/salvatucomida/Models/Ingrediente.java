package com.example.salvatucomida.Models;

public class Ingrediente {

    private int id;
    private String nombre;
    private String emoji;

    public Ingrediente() {
    }

    public Ingrediente(int id, String nombre, String emoji) {
        this.id = id;
        this.nombre = nombre;
        this.emoji = emoji;
    }

    public Ingrediente(String nombre, String emoji) {
        this.nombre = nombre;
        this.emoji = emoji;
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

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }
}