package com.dinosaurio.preciosdino.Entidades;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Carga {

    @SerializedName("Sesion")
    private String sesion;
    @SerializedName("Usuario")
    private String usuario;
    @SerializedName("Sucursal")
    private String sucursal;
    @SerializedName("Dispositivo")
    private String dispositivo;
    @SerializedName("Fecha")
    private String fecha;
    @SerializedName("Detalle")
    private List<DetalleCarga> detalleCarga;

    public Carga() {
        super();
    }

    public Carga(String sesion, String usuario, String sucursal, String dispositivo, String fecha, List<DetalleCarga> detalleCarga) {
        this.sesion = sesion;
        this.usuario = usuario;
        this.sucursal = sucursal;
        this.dispositivo = dispositivo;
        this.fecha = fecha;
        this.detalleCarga = detalleCarga;
    }

    public String getSesion() {
        return sesion;
    }

    public void setSesion(String sesion) {
        this.sesion = sesion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }

    public String getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(String dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public List<DetalleCarga> getDetalleCarga() {
        return detalleCarga;
    }


    public void setDetalle(List<DetalleCarga> detalleCarga) {
        this.detalleCarga = detalleCarga;
    }


}
