package com.example.covid199monitor;

public class Estado {
    private String nombre;
    private int casos;
    private int casos_positivos;
    private int fallecimientos;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCasos() {
        return casos;
    }

    public void setCasos(int casos) {
        this.casos = casos;
    }

    public int getCasos_positivos() {
        return casos_positivos;
    }

    public void setCasos_positivos(int casos_positivos) {
        this.casos_positivos = casos_positivos;
    }

    public int getFallecimientos() {
        return fallecimientos;
    }

    public void setFallecimientos(int fallecimientos) {
        this.fallecimientos = fallecimientos;
    }
}
