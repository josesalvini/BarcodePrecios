package com.dinosaurio.preciosdino.Main;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.dinosaurio.preciosdino.AccesoDatos.ModeloBD;
import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.Parametro;
import com.dinosaurio.preciosdino.Entidades.Sucursal;
import com.dinosaurio.preciosdino.Entidades.TipoEtiqueta;
import com.dinosaurio.preciosdino.Entidades.Usuario;
import com.dinosaurio.preciosdino.Interfaces.AppData;
import com.dinosaurio.preciosdino.Interfaces.DatosAPI;
import com.dinosaurio.preciosdino.Interfaces.DatosPreference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.RealmModule;

import static com.dinosaurio.preciosdino.Interfaces.DatosPreference.guardarDBPreferencias;

public class InitApplication extends Application {

    private SharedPreferences preferencesSystem,sharedPreferences;

    private Realm realm;
    private RealmConfiguration configuracionBD;

    public static AtomicInteger UsuarioID;
    public static AtomicInteger TipoetiquetaID;
    public static AtomicInteger ParametroID;
    public static AtomicInteger CargaCodigosID;
    public static AtomicInteger SucursalID;

    public InitApplication() {
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.initClasesID();

        preferencesSystem = getSharedPreferences("PreferencesApp", Context.MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        int existBD = DatosPreference.getBDDefault(preferencesSystem);
        Realm.init(getApplicationContext());

        if(existBD==0) {
            Log.d("CREAR BD","SE CREA EL MODELO DE LA BASE DE DATOS.");
            sharedPreferences.edit().clear().apply();
            this.crearModelo();
            this.crearAPI();
            realm.close();
            DatosPreference.guardarDBPreferencias(preferencesSystem,1);
        }else {
            Log.d("EXISTE BD","LA BASE DE DATOS EXISTE NO SE CREA.");
        }

    }

    public void initClasesID(){
        UsuarioID = new AtomicInteger();
        TipoetiquetaID = new AtomicInteger();
        ParametroID = new AtomicInteger();
        CargaCodigosID = new AtomicInteger();
        SucursalID = new AtomicInteger();
    }


    public void crearModelo(){
        // Set the module in the RealmConfiguration to allow only classes defined by the module.
        configuracionBD = new RealmConfiguration.Builder()
                .name("etiquetas_dino.realm")
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .modules(new ModeloBD())
                .build();
        // Use the config
        //realm = Realm.getInstance(configuracionBD);
        Realm.setDefaultConfiguration(configuracionBD);

        realm = Realm.getDefaultInstance();

        UsuarioID = getIDbyTabla(realm,Usuario.class);
        TipoetiquetaID = getIDbyTabla(realm,TipoEtiqueta.class);
        ParametroID = getIDbyTabla(realm,Parametro.class);
        CargaCodigosID = getIDbyTabla(realm,CargaCodigos.class);
        SucursalID = getIDbyTabla(realm, Sucursal.class);
    }

    public void crearAPI(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Parametro valorAPI;
                valorAPI = new Parametro("RESTAPI",
                        DatosAPI.getUrlAPI());

                realm.copyToRealmOrUpdate(valorAPI);
                Log.d(null,"Se crea parametro para API REST por defecto.");
            }
        });
    }

    /*Retorna el maximo id de la tabla*/
    private <T extends RealmObject> AtomicInteger getIDbyTabla(Realm realm,Class<T> clase){
        RealmResults<T> results = realm.where(clase).findAll();
        return (results.size()>0) ? new AtomicInteger(results.max("id").intValue()) : new AtomicInteger();

    }



}
