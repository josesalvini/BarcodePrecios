package com.dinosaurio.preciosdino.Entidades;

import com.google.gson.annotations.SerializedName;

public class UsrLogin {

    @SerializedName("UserId")
    private Integer userId;
    @SerializedName("Dni")
    private String nroDNI;
    @SerializedName("Nombre")
    private String nombre;
    @SerializedName("Empresa")
    private String empresa;

    public UsrLogin(){}

    public UsrLogin(int userId, String nroDNI, String nombre, String empresa) {
        this.userId = userId;
        this.nroDNI = nroDNI;
        this.nombre = nombre;
        this.empresa = empresa;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNroDNI() {
        return nroDNI;
    }

    public void setNroDNI(String nroDNI) {
        this.nroDNI = nroDNI;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }
}
