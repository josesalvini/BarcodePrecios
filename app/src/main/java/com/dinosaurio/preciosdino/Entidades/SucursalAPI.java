package com.dinosaurio.preciosdino.Entidades;

import com.google.gson.annotations.SerializedName;

public class SucursalAPI {

    @SerializedName("IdSucursal")
    private String idSucursal;
    @SerializedName("Descripcion")
    private String descripcion;
    @SerializedName("Orden")
    private Integer orden;

    public SucursalAPI() {
    }

    public SucursalAPI(String idSucursal, String descripcion, Integer orden) {
        this.idSucursal = idSucursal;
        this.descripcion = descripcion;
        this.orden = orden;
    }

    public String getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(String idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Integer getOrden() {
        return orden;
    }

    public void setOrden(Integer orden) {
        this.orden = orden;
    }
}
