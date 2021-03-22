package com.dinosaurio.preciosdino.Entidades;

import com.dinosaurio.preciosdino.Main.InitApplication;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Usuario extends RealmObject {

    @PrimaryKey
    private int id;
    @Required
    private String nombre;
    @Required
    private String password;
    @Required
    private String empresa;
    @Required
    private String sucursal;


    public Usuario() {
    }

    public Usuario( String nombre, String password,String empresa,  String sucursal) {
        this.id = InitApplication.UsuarioID.getAndDecrement();
        this.nombre = nombre;
        this.password = password;
        this.empresa = empresa;
        this.sucursal = sucursal;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getSucursal() {
        return sucursal;
    }

    public void setSucursal(String sucursal) {
        this.sucursal = sucursal;
    }
}
