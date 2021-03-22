package com.dinosaurio.preciosdino.ComplementosClass;

import android.graphics.Bitmap;

public class QrItem {

private Bitmap qrImagen;
private String codigoBarra;
private String tipoEtiqueta;
private String cantidadCopias;

    public QrItem() {
    }

    public QrItem(Bitmap qrImage, String codigoBarra, String tipoEtiqueta, String cantidadCopias) {
        this.qrImagen = qrImage;
        this.codigoBarra = codigoBarra;
        this.tipoEtiqueta = tipoEtiqueta;
        this.cantidadCopias = cantidadCopias;
    }

    public Bitmap getQrImagen() {
        return qrImagen;
    }

    public void setQrImagen(Bitmap qrImage) {
        this.qrImagen = qrImage;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getTipoEtiqueta() {
        return tipoEtiqueta;
    }

    public void setTipoEtiqueta(String tipoEtiqueta) {
        this.tipoEtiqueta = tipoEtiqueta;
    }

    public String getCantidadCopias() {
        return cantidadCopias;
    }

    public void setCantidadCopias(String cantidadCopias) {
        this.cantidadCopias = cantidadCopias;
    }
}
