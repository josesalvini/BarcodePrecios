package com.dinosaurio.preciosdino.Entidades;

import com.dinosaurio.preciosdino.Main.InitApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class TipoEtiqueta extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String nombre;
    private int defecto;
    @Required
    private String url;
    private int habilitado;

    public TipoEtiqueta() {
    }

    public TipoEtiqueta( String nombre, int defecto, String url, int habilitado) {
        this.id = InitApplication.TipoetiquetaID.getAndIncrement();
        this.nombre = nombre;
        this.defecto = defecto;
        this.url = url;
        this.habilitado = habilitado;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getDefecto() {
        return defecto;
    }

    public void setDefecto(int defecto) {
        this.defecto = defecto;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(int habilitado) {
        this.habilitado = habilitado;
    }
}
