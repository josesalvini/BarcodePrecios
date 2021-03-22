package com.dinosaurio.preciosdino.Entidades;

import com.google.gson.annotations.SerializedName;

public class RespuestaEstado {

    @SerializedName("Resultado")
    private String estado;

    public RespuestaEstado(String estado) {
        this.estado = estado;
    }

    public RespuestaEstado() {
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString(){
        return "Respuesta: " + this.estado;
    }
}
