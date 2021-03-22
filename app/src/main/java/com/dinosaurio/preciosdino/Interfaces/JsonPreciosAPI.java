package com.dinosaurio.preciosdino.Interfaces;

import com.dinosaurio.preciosdino.Entidades.Carga;
import com.dinosaurio.preciosdino.Entidades.DetalleCarga;
import com.dinosaurio.preciosdino.Entidades.Etiqueta;
import com.dinosaurio.preciosdino.Entidades.RespuestaEstado;
import com.dinosaurio.preciosdino.Entidades.SucursalAPI;
import com.dinosaurio.preciosdino.Entidades.UsrLogin;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface JsonPreciosAPI {

    @GET("etiquetas")
    Call<List<Etiqueta>> getEtiquetas();

    @GET("usuarios/{dni}")
    Call<List<UsrLogin>> getUsuario(@Path("dni") String dni);

    @GET("sucursales")
    Call<List<SucursalAPI>> getSucursales();

    @POST("pmcollect")
    Call<RespuestaEstado> guardarCarga(@Body Carga carga);

}
