package com.dinosaurio.preciosdino.Interfaces;

import android.content.Context;
import android.content.SharedPreferences;

public abstract class DatosAPI {

    private static String urlAPI="http://190.226.250.99:12380/";
    private static String urlAPIPrueba="https://jsonplaceholder.typicode.com/";

    public static String getUrlAPI() {
        return urlAPI;
    }


    public static void setUrlAPI(String urlAPI) {
        DatosAPI.urlAPI = urlAPI;
    }

    public static String getUrlAPIPrueba() {
        return urlAPIPrueba;
    }

    public static void setUrlAPIPrueba(String urlAPIPrueba) {
        DatosAPI.urlAPIPrueba = urlAPIPrueba;
    }
}
