package com.dinosaurio.preciosdino.Entidades;

import com.dinosaurio.preciosdino.Main.InitApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class CargaCodigos extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String codigoBarra;
    private int cantidadCopias;
    @Required
    private String tipoEtiqueta;
    private int idUsuario;
    @Required
    private String fecha;
    private int uploaded;

    public CargaCodigos() {
    }

    public CargaCodigos(String codigoBarra, int cantidadCopias, String tipoEtiqueta, int idUsuario, String fecha, int uploaded) {
        this.id = InitApplication.CargaCodigosID.getAndIncrement();
        this.codigoBarra = codigoBarra;
        this.cantidadCopias = cantidadCopias;
        this.tipoEtiqueta = tipoEtiqueta;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.uploaded = uploaded;
    }

    public int getId() {
        return id;
    }

    public String getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(String codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public int getCantidadCopias() {
        return cantidadCopias;
    }

    public void setCantidadCopias(int cantidadCopias) {
        this.cantidadCopias = cantidadCopias;
    }

    public String getTipoEtiqueta() {
        return tipoEtiqueta;
    }

    public void setTipoEtiqueta(String tipoEtiqueta) {
        this.tipoEtiqueta = tipoEtiqueta;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getUploaded() {
        return uploaded;
    }

    public void setUploaded(int uploaded) {
        this.uploaded = uploaded;
    }
}
