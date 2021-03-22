package com.dinosaurio.preciosdino.Entidades;

import com.dinosaurio.preciosdino.Main.InitApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Parametro extends RealmObject {

        @PrimaryKey
        private int id;
        @Required
        private String descripcion;
        @Required
        private String valor;

    public Parametro() {
    }

    public Parametro(String descripcion, String valor) {
        this.id = InitApplication.ParametroID.getAndIncrement();
        this.descripcion = descripcion;
        this.valor = valor;
    }

    public int getId() {
        return id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
}

