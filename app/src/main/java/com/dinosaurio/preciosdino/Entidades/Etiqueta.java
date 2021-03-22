package com.dinosaurio.preciosdino.Entidades;

import com.google.gson.annotations.SerializedName;

public class Etiqueta {

    @SerializedName("Id")
    private int id;
    @SerializedName("Nombre")
    private String nombre;
    @SerializedName("Defecto")
    private Integer defecto;
    @SerializedName("URL")
    private String  url;
    @SerializedName("Habilitado")
    private Integer habilitado;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getDefecto() {
        return defecto;
    }

    public void setDefecto(Integer defecto) {
        this.defecto = defecto;
    }

    public Integer getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(Integer habilitado) {
        this.habilitado = habilitado;
    }


}

