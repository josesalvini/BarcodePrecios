package com.dinosaurio.preciosdino.Interfaces;


import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.dinosaurio.preciosdino.Entidades.Usuario;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;


public abstract class AppData {

private static String sesionID;
private static Usuario usuario;
private static String sucursal;
private static String deviceName;
private static String fecha;


public static boolean verificarConexion(ConnectivityManager connectivityManager ){
    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

    if (networkInfo != null && networkInfo.isConnected()) {
        return true;
    } else {
        // No hay conexión a Internet en este momento
        return false;
    }
}


public static String getSesionID() {
    return sesionID;
}

public static void setSesionID(String sesionID) {
    AppData.sesionID = sesionID;
}

public static String getSucursal() {
    return sucursal;
}

public static void setSucursal(String sucursal) {
    AppData.sucursal = sucursal;
}

public static String getDeviceName() {
    return deviceName;
}

public static void setDeviceName(String deviceName) {
    AppData.deviceName = deviceName;
}

public static String getFecha() {
    return fecha;
}

public static void setFecha(String fecha) {
    AppData.fecha = fecha;
}

public static void setUsuario(Usuario usuario){
    AppData.usuario = usuario;
}

public static int getIdUsuario(){
    return usuario.getId();
}

public static String getNombreUsuario(){
    return usuario.getNombre();
}

public static String getFechaSystem() {
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    Date date = new Date();
    return dateFormat.format(date);
}

public static String getFechaCarga() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
}

public static String getFechaSesion() {
        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }


public static String getFechaSesion(SharedPreferences sharedPreferences){
    String initSesionDate = DatosPreference.getInitSesionDate(sharedPreferences);

    if(TextUtils.isEmpty(initSesionDate)) {
        DatosPreference.guardarInitSesionDate(sharedPreferences);
    }

    initSesionDate = DatosPreference.getInitSesionDate(sharedPreferences);
    return initSesionDate;
}

public static boolean getTiempoExpiracion(String fecha){
    boolean sesionOk;
    try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        Date dateStart = dateFormat.parse(fecha);
        Date dateActual = new Date(); //dateFormat.parse("2019/06/09");

        //obtienes la diferencia de las fechas
        //long diferencia = Math.abs(dateActual.getTime() - dateStart.getTime());
        //obtienes la diferencia en horas ya que la diferencia anterior esta en milisegundos
        //long dias = TimeUnit.MILLISECONDS.toDays(diferencia);
        //diferencia = diferencia / (60 * 60 * 1000);
        //long days=(dias/(1000*60*60*24))%365;

        int dias = numeroDiasEntreDosFechas(dateStart,dateActual);

        if(dias>=3){
            sesionOk = false;
            Log.i("Sesion: " ,"Expirada, se borrar los datos, dias de actividad: " + dias);
        }else{
            sesionOk = true;
            Log.i("Sesion: " ,"Sesion ok: " + " Dias: "+ dias);
        }
    }catch(Exception e){
        e.printStackTrace();
        sesionOk = false;
    }
    return sesionOk;
}

public static int numeroDiasEntreDosFechas(Date fecha1, Date fecha2){
        long startTime = fecha1.getTime();
        long endTime = fecha2.getTime();
        long diffTime = endTime - startTime;
        return (int)TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);
    }

}
