package com.example.covid199monitor;

import java.io.Serializable;

public class Estado implements Serializable {
    private String nombre;

    private int casos;
    private int casos_positivos;
    private int fallecimientos;

    private int hombres;
    private int mujeres;

    private int edad;

    private int embarazo;
    private int intubado;
    private int neumonia;
    private int diabetes;
    private int epoc;
    private int asma;
    private int inmunosupresion;
    private int hipertension;
    private int cardiovascular;
    private int obesidad;
    private int renal_cronica;
    private int tabaquismo;

    public int getHombres() {
        return hombres;
    }

    public void setHombres(int hombres) {
        this.hombres = hombres;
    }

    public int getMujeres() {
        return mujeres;
    }

    public void setMujeres(int mujeres) {
        this.mujeres = mujeres;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public int getEmbarazo() {
        return embarazo;
    }

    public void setEmbarazo(int embarazo) {
        this.embarazo = embarazo;
    }

    public int getIntubado() {
        return intubado;
    }

    public void setIntubado(int intubado) {
        this.intubado = intubado;
    }

    public int getNeumonia() {
        return neumonia;
    }

    public void setNeumonia(int neumonia) {
        this.neumonia = neumonia;
    }

    public int getDiabetes() {
        return diabetes;
    }

    public void setDiabetes(int diabetes) {
        this.diabetes = diabetes;
    }

    public int getEpoc() {
        return epoc;
    }

    public void setEpoc(int epoc) {
        this.epoc = epoc;
    }

    public int getAsma() {
        return asma;
    }

    public void setAsma(int asma) {
        this.asma = asma;
    }

    public int getInmunosupresion() {
        return inmunosupresion;
    }

    public void setInmunosupresion(int inmunosupresion) {
        this.inmunosupresion = inmunosupresion;
    }

    public int getHipertension() {
        return hipertension;
    }

    public void setHipertension(int hipertension) {
        this.hipertension = hipertension;
    }

    public int getCardiovascular() {
        return cardiovascular;
    }

    public void setCardiovascular(int cardiovascular) {
        this.cardiovascular = cardiovascular;
    }

    public int getObesidad() {
        return obesidad;
    }

    public void setObesidad(int obesidad) {
        this.obesidad = obesidad;
    }

    public int getRenal_cronica() {
        return renal_cronica;
    }

    public void setRenal_cronica(int renal_cronica) {
        this.renal_cronica = renal_cronica;
    }

    public int getTabaquismo() {
        return tabaquismo;
    }

    public void setTabaquismo(int tabaquismo) {
        this.tabaquismo = tabaquismo;
    }

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
