package com.example.covid199monitor;

public class Padecimiento {
    String nombre;
    int casos_t;
    int casos_p;

    public int getCasos_t() {
        return casos_t;
    }

    public void setCasos_t(int casos_t) {
        this.casos_t = casos_t;
    }

    public int getCasos_p() {
        return casos_p;
    }

    public void setCasos_p(int casos_p) {
        this.casos_p = casos_p;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

}