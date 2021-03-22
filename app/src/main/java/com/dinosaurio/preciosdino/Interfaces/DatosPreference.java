package com.dinosaurio.preciosdino.Interfaces;

import android.content.SharedPreferences;

public class DatosPreference {

    public static String getUsuarioPreference(SharedPreferences preferences){
        return preferences.getString("usuario","");
    }

    public static String getPasswordPreference(SharedPreferences preferences){
        return preferences.getString("password","");
    }

    public static String getInitSesionDate(SharedPreferences preferences){
        return preferences.getString("InitSesionDate",null);
    }

    public static void guardarInitSesionDate(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("InitSesionDate",AppData.getFechaSesion());
        editor.commit();
        editor.apply();
    }

    public static void setInitSesionDate(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("InitSesionDate",null);
        editor.commit();
        editor.apply();
    }


    public static void guardarPreferencias(SharedPreferences preferences,String usuario,String password){
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("usuario",usuario);
            editor.putString("password",password);
            editor.commit();
            editor.apply();
    }

    public static int getBDDefault(SharedPreferences preferences){
        return preferences.getInt("bdDefault",0);
    }

    public static void guardarDBPreferencias(SharedPreferences preferences,int valor){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("bdDefault",valor);
        editor.commit();
        editor.apply();
    }


    public static String getAPIDefault(SharedPreferences preferences){
        return preferences.getString("RESTAPI",null);
    }

    public static void guardarAPIPreferencias(SharedPreferences preferences,String valor){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("RESTAPI",valor);
        editor.commit();
        editor.apply();
    }

    public static void guardarAPIPreferenciasDefault(SharedPreferences preferences){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("RESTAPI",DatosAPI.getUrlAPI());
        editor.commit();
        editor.apply();
    }

    public static String getSesionMode(SharedPreferences preferences){
        return preferences.getString("sesionmode",null);
    }

    public static void guardarSesionMode(SharedPreferences preferences,String modo){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sesionmode",modo);
        editor.commit();
        editor.apply();
    }

    public static String getSucursal(SharedPreferences preferences){
        return preferences.getString("sucursal",null);
    }

    public static void guardarSucursal(SharedPreferences preferences,String sucursal){
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sucursal",sucursal);
        editor.commit();
        editor.apply();
    }

}
