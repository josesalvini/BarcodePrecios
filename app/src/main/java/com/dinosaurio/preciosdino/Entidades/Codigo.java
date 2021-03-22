package com.dinosaurio.preciosdino.Entidades;

public class Codigo {

    private  int ID;
    private  String CODIGO_BARRA;
    private  int CANTIDAD;
    private  String TIPO_ETIQUETA;
    private  int USUARIO_ID;
    private  String FECHA;
    private int UPLOADED;

    public Codigo(){
        super();
    }


    public Codigo(String CODIGO_BARRA, int CANTIDAD, String TIPO_ETIQUETA, int USUARIO_ID, String FECHA,int UPLOADED) {
        this.CODIGO_BARRA = CODIGO_BARRA;
        this.CANTIDAD = CANTIDAD;
        this.TIPO_ETIQUETA = TIPO_ETIQUETA;
        this.USUARIO_ID = USUARIO_ID;
        this.FECHA = FECHA;
        this.UPLOADED = UPLOADED;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getCODIGO_BARRA() {
        return CODIGO_BARRA;
    }

    public void setCODIGO_BARRA(String CODIGO_BARRA) {
        this.CODIGO_BARRA = CODIGO_BARRA;
    }

    public int getCANTIDAD() {
        return CANTIDAD;
    }

    public void setCANTIDAD(int CANTIDAD) {
        this.CANTIDAD = CANTIDAD;
    }

    public String getTIPO_ETIQUETA() {
        return TIPO_ETIQUETA;
    }

    public void setTIPO_ETIQUETA(String TIPO_ETIQUETA) {
        this.TIPO_ETIQUETA = TIPO_ETIQUETA;
    }

    public int getUSUARIO_ID() {
        return USUARIO_ID;
    }

    public void setUSUARIO_ID(int USUARIO_ID) {
        this.USUARIO_ID = USUARIO_ID;
    }

    public String getFECHA() {
        return FECHA;
    }

    public void setFECHA(String FECHA) {
        this.FECHA = FECHA;
    }

    public int getUPLOADED() {
        return UPLOADED;
    }

    public void setUPLOADED(int UPLOADED) {
        this.UPLOADED = UPLOADED;
    }
}
