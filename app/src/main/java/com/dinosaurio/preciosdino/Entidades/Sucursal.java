package com.dinosaurio.preciosdino.Entidades;
import com.dinosaurio.preciosdino.Main.InitApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Sucursal extends RealmObject {
    @PrimaryKey
    private int id;
    private String descripcion;
    @Required
    private String abreviatura;
    @Required
    private String idSucursal;
    @Required
    private Integer orden;


    public Sucursal() {
    }

    public Sucursal(String descripcion, String abreviatura, String idSucursal,Integer orden) {
        this.id = InitApplication.SucursalID.getAndIncrement();;
        this.descripcion = descripcion;
        this.abreviatura = abreviatura;
        this.idSucursal = idSucursal;
        this.orden = orden;

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

    public String getAbreviatura() {
        return abreviatura;
    }

    public void setAbreviatura(String abreviatura) {
        this.abreviatura = abreviatura;
    }

    public String getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(String idSucursal) {
        this.idSucursal = idSucursal;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }
}
