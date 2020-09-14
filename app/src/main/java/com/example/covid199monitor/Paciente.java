package com.example.covid199monitor;

public class Paciente {
    //FECHA_ACTUALIZACION,ID_REGISTRO,ORIGEN,SECTOR,ENTIDAD_UM,SEXO,ENTIDAD_NAC,ENTIDAD_
    // RES,MUNICIPIO_RES,TIPO_PACIENTE,FECHA_INGRESO,FECHA_SINTOMAS,FECHA_DEF,INTUBADO,NEUMONIA,EDAD,
    // NACIONALIDAD,EMBARAZO,HABLA_LENGUA_INDIG,DIABETES,EPOC,ASMA,INMUSUPR,HIPERTENSION,OTRA_COM,
    // CARDIOVASCULAR,OBESIDAD,RENAL_CRONICA,TABAQUISMO,OTRO_CASO,RESULTADO,MIGRANTE,PAIS_NACIONALIDAD,
    // PAIS_ORIGEN,UCI

    private String id;
    private int entidad_i;
    private String entidad;
    private Character sexo;
    private String fecha_ingreso;
    private String fecha_defuncion;
    private int edad;

    private boolean embarazo;
    private boolean intubado;
    private boolean neumonia;
    private boolean diabetes;
    private boolean epoc;
    private boolean asma;
    private boolean inmunosupresion;
    private boolean hipertension;
    private boolean cardiovascular;
    private boolean obesidad;
    private boolean renal_cronica;
    private boolean tabaquismo;

    private boolean resultado;

    private boolean cuidados_intensivos;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEntidad_i() {
        return entidad_i;
    }

    public void setEntidad_i(int entidad_i) {
        this.entidad_i = entidad_i;
    }

    public String getEntidad() {
        return entidad;
    }

    public void setEntidad(String entidad) {
        this.entidad = entidad;
    }

    public Character getSexo() {
        return sexo;
    }

    public void setSexo(Character sexo) {
        this.sexo = sexo;
    }

    public String getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(String fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }

    public String getFecha_defuncion() {
        return fecha_defuncion;
    }

    public void setFecha_defuncion(String fecha_defuncion) {
        this.fecha_defuncion = fecha_defuncion;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public boolean isEmbarazo() {
        return embarazo;
    }

    public void setEmbarazo(boolean embarazo) {
        this.embarazo = embarazo;
    }

    public boolean isIntubado() {
        return intubado;
    }

    public void setIntubado(boolean intubado) {
        this.intubado = intubado;
    }

    public boolean isNeumonia() {
        return neumonia;
    }

    public void setNeumonia(boolean neumonia) {
        this.neumonia = neumonia;
    }

    public boolean isDiabetes() {
        return diabetes;
    }

    public void setDiabetes(boolean diabetes) {
        this.diabetes = diabetes;
    }

    public boolean isEpoc() {
        return epoc;
    }

    public void setEpoc(boolean epoc) {
        this.epoc = epoc;
    }

    public boolean isAsma() {
        return asma;
    }

    public void setAsma(boolean asma) {
        this.asma = asma;
    }

    public boolean isInmunosupresion() {
        return inmunosupresion;
    }

    public void setInmunosupresion(boolean inmunosupresion) {
        this.inmunosupresion = inmunosupresion;
    }

    public boolean isHipertension() {
        return hipertension;
    }

    public void setHipertension(boolean hipertension) {
        this.hipertension = hipertension;
    }

    public boolean isCardiovascular() {
        return cardiovascular;
    }

    public void setCardiovascular(boolean cardiovascular) {
        this.cardiovascular = cardiovascular;
    }

    public boolean isObesidad() {
        return obesidad;
    }

    public void setObesidad(boolean obesidad) {
        this.obesidad = obesidad;
    }

    public boolean isRenal_cronica() {
        return renal_cronica;
    }

    public void setRenal_cronica(boolean renal_cronica) {
        this.renal_cronica = renal_cronica;
    }

    public boolean isTabaquismo() {
        return tabaquismo;
    }

    public void setTabaquismo(boolean tabaquismo) {
        this.tabaquismo = tabaquismo;
    }

    public boolean isResultado() {
        return resultado;
    }

    public void setResultado(boolean resultado) {
        this.resultado = resultado;
    }

    public boolean isCuidados_intensivos() {
        return cuidados_intensivos;
    }

    public void setCuidados_intensivos(boolean cuidados_intensivos) {
        this.cuidados_intensivos = cuidados_intensivos;
    }
}
