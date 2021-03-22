package com.dinosaurio.preciosdino.Entidades;

import com.google.gson.annotations.SerializedName;

public class DetalleCarga {

    @SerializedName("TipoEtiqueta")
    private String tipoEtiqueta;
    @SerializedName("Ean")
    private String ean;
    @SerializedName("Cantidad")
    private Integer cantidad;

    public DetalleCarga(){
        super();
    }

    public DetalleCarga(String tipoEtiqueta, String ean, Integer cantidad) {
        this.tipoEtiqueta = tipoEtiqueta;
        this.ean = ean;
        this.cantidad = cantidad;
    }

    public String getTipoEtiqueta() {
        return tipoEtiqueta;
    }

    public void setTipoEtiqueta(String tipoEtiqueta) {
        this.tipoEtiqueta = tipoEtiqueta;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }


}