package com.dinosaurio.preciosdino.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.dinosaurio.preciosdino.ActivityClass.MainActivity;
import com.dinosaurio.preciosdino.BuildConfig;
import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.Etiqueta;
import com.dinosaurio.preciosdino.Entidades.Sucursal;
import com.dinosaurio.preciosdino.Entidades.SucursalAPI;
import com.dinosaurio.preciosdino.Entidades.TipoEtiqueta;
import com.dinosaurio.preciosdino.Entidades.Usuario;
import com.dinosaurio.preciosdino.Interfaces.AppData;
import com.dinosaurio.preciosdino.Interfaces.DatosAPI;
import com.dinosaurio.preciosdino.Interfaces.DatosPreference;
import com.dinosaurio.preciosdino.Interfaces.JsonPreciosAPI;
import com.dinosaurio.preciosdino.R;


import java.util.List;
import io.realm.Realm;
import io.realm.exceptions.RealmException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences,preferencesSystem;
    private ImageView imageView;
    private TextView versionApp;
    private Animation animation;
    private static int SPLASH_TIME_OUT = 2000;
    private Intent intentMain,intentLogin;
    private Realm realm;
    private JsonPreciosAPI jsonPreciosAPI;
    private Retrofit retrofit;
    private ConnectivityManager connectivityManager;
    private List<Etiqueta> listEtiquetas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView = (ImageView) findViewById(R.id.imageViewCart);
        versionApp = (TextView) findViewById(R.id.textViewVersion);

        animation = AnimationUtils.loadAnimation(this,R.anim.left_to_right);
        imageView.startAnimation(animation);

        realm = Realm.getDefaultInstance();

        sharedPreferences =  getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        preferencesSystem = getSharedPreferences("PreferencesApp", Context.MODE_PRIVATE);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;

        String autor = getResources().getString(R.string.created_by);

        versionApp.setText("Versión name: "+ versionName + " - Codigo: " + versionCode + " - " + autor);

        String fechaInicio = AppData.getFechaSesion(sharedPreferences);
        if(!AppData.getTiempoExpiracion(fechaInicio)){
            //Borro los datos de las cargas si la sesion ya expiro
            try{
            realm.beginTransaction();
            realm.delete(CargaCodigos.class);
            }catch (RealmException re){
                Log.e("RealmException","deleteCodigos: " + re.getMessage());
            }finally {
                if (realm != null) {
                    realm.commitTransaction();
                }
            }
            this.clearSharedPreferences(sharedPreferences);
        }

        this.existeAPIDefault();

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean conexion = AppData.verificarConexion(connectivityManager);

        crearUsuarioDefecto();

        if(conexion){
            this.crearConexionAPI();
            this.loadEtiquetas();
            this.loadSucursales();
        }else{
            Log.e("Conexion","Sin conexion a Internet.");
            crearSucursalDefecto();
        }

        //intentLogin = new Intent(this, LoginActivity.class);
        intentMain = new Intent(this, MainActivity.class);
        //intentSesionMode = new Intent(this, SesionModeActivity.class);
        intentLogin = new Intent(this, LoginActivity.class);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!TextUtils.isEmpty(DatosPreference.getUsuarioPreference(sharedPreferences))
                        && !TextUtils.isEmpty(DatosPreference.getPasswordPreference(sharedPreferences) )){

                    Usuario usuarioLogin =  realm.where(Usuario.class)
                            .equalTo("nombre", DatosPreference.getUsuarioPreference(sharedPreferences))
                            .and()
                            .equalTo("password", DatosPreference.getPasswordPreference(sharedPreferences))
                            .findFirst();

                    if(usuarioLogin!=null) {
                        AppData.setUsuario(usuarioLogin);
                        AppData.setSucursal(usuarioLogin.getSucursal());

                        intentMain.putExtra("NOMBREUSER", usuarioLogin.getNombre());
                        intentMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intentMain);
                    }else{
                        Log.i("Usuario","USUARIO NO ENCONTRADO.");
                        //startActivity(intentSesionMode);
                        startActivity(intentLogin);
                    }

                }else{
                    //startActivity(intentSesionMode);
                    startActivity(intentLogin);
                }
                finish();
                }
        }, SPLASH_TIME_OUT);

    }// end metodo onCreate()

    private void clearSharedPreferences(SharedPreferences shared) {
        shared.edit().clear().apply();
    }

    public void existeAPIDefault(){
        String urlAPI = DatosPreference.getAPIDefault(preferencesSystem);

        if(TextUtils.isEmpty(urlAPI)){
            DatosPreference.guardarAPIPreferenciasDefault(preferencesSystem);
        }
    }

    public void crearConexionAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(DatosAPI.getUrlAPI())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPreciosAPI = retrofit.create(JsonPreciosAPI.class);
    }

    public void loadEtiquetas() {
        Call<List<Etiqueta>> callEtiquetas = jsonPreciosAPI.getEtiquetas();

        callEtiquetas.enqueue(new Callback<List<Etiqueta>>() {
            @Override
            public void onResponse(Call<List<Etiqueta>> call, Response<List<Etiqueta>> response) {
                if (!response.isSuccessful()){
                    switch (response.code()){
                        case 404:
                            break;
                        default:
                            break;
                    }
                }else{
                    Log.d("Respuesta","Response " + response.code());
                }

                if(response.code() == 200) {
                    realm.beginTransaction();
                    realm.delete(TipoEtiqueta.class);
                    realm.commitTransaction();

                    listEtiquetas = response.body();

                    for (Etiqueta etiqueta : listEtiquetas) {
                        if(etiqueta.getHabilitado() == 1){

                            TipoEtiqueta etiquetaADD = new TipoEtiqueta(etiqueta.getNombre(),
                                    etiqueta.getDefecto(),
                                    etiqueta.getUrl(),
                                    etiqueta.getHabilitado());
                            try {
                                realm.beginTransaction();
                                realm.insertOrUpdate(etiquetaADD);
                            } catch (RealmException re){
                                Log.e("RealmException","loadEtiquetas: " + re.getMessage());
                            }finally {
                                if (realm != null) {
                                    realm.commitTransaction();
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public void onFailure(Call<List<Etiqueta>> call, Throwable t) {
            }
        });

    }

    public void loadSucursales(){
        Call<List<SucursalAPI>> callSucursales = jsonPreciosAPI.getSucursales();

        callSucursales.enqueue(new Callback<List<SucursalAPI>>() {
            @Override
            public void onResponse(Call<List<SucursalAPI>> call, Response<List<SucursalAPI>> response) {
                if (!response.isSuccessful()){
                    switch (response.code()){
                        case 404:
                            break;
                        default:
                            break;
                    }
                }else{
                    Log.d("Respuesta","Response " + response.code());
                }
                try {
                    realm.beginTransaction();
                    realm.delete(Sucursal.class);
                }catch (RealmException re){
                    Log.e("RealmException","delete: " + re.getMessage());
                }finally {
                    if (realm != null) {
                        realm.commitTransaction();
                    }
                }

                if(response.code() == 200) {
                    List<SucursalAPI> listSucursales = response.body();
                    for (SucursalAPI sucursal : listSucursales) {
                        Sucursal sucursalADD = new Sucursal("",
                                sucursal.getDescripcion(),
                                sucursal.getIdSucursal(),
                                sucursal.getOrden());
                        try {
                            realm.beginTransaction();
                            realm.insertOrUpdate(sucursalADD);

                        }catch (RealmException re){
                            Log.e("RealmException","loadSucursales: " + re.getMessage());
                        }finally {
                            if (realm != null) {
                                realm.commitTransaction();
                            }
                        }
                    }
                }else if(response.code() == 500){
                    Log.e("Error:500","Error al obtener sucursales.");
                    crearSucursalDefecto();
                }
            }

            @Override
            public void onFailure(Call<List<SucursalAPI>> call, Throwable t) {
            }
        });
    }

    public void crearUsuarioDefecto(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

                Usuario userDefaultAV,userDefaultR20,userDefaultSV,userDefaultCVL,
                        userDefaultSP,userDefault60C,userDefaultAG,userDefaultTLH,userDefaultTSM;
                userDefaultAV= new Usuario("PRECIO_AV", "AV1234", "DINOSAURIO", "AV");
                userDefaultR20= new Usuario("PRECIO_R20", "R201234", "DINOSAURIO", "R20");
                userDefaultSV= new Usuario("PRECIO_SV", "SV1234", "DINOSAURIO", "SV");
                userDefaultCVL= new Usuario("PRECIO_CVL", "CVL1234", "DINOSAURIO", "CVL");
                userDefaultSP= new Usuario("PRECIO_SP", "SP1234", "DINOSAURIO", "SP");
                userDefault60C= new Usuario("PRECIO_60C", "60C1234", "DINOSAURIO", "60C");
                userDefaultAG= new Usuario("PRECIO_AG", "AG1234", "DINOSAURIO", "AG");
                userDefaultTLH= new Usuario("PRECIO_TLH", "TLH1234", "DINOSAURIO", "TLH");
                userDefaultTSM= new Usuario("PRECIO_TSM", "TSM234", "DINOSAURIO", "TSM");

                realm.copyToRealmOrUpdate(userDefaultAV);
                realm.copyToRealmOrUpdate(userDefaultR20);
                realm.copyToRealmOrUpdate(userDefaultSV);
                realm.copyToRealmOrUpdate(userDefaultCVL);
                realm.copyToRealmOrUpdate(userDefaultSP);
                realm.copyToRealmOrUpdate(userDefault60C);
                realm.copyToRealmOrUpdate(userDefaultAG);
                realm.copyToRealmOrUpdate(userDefaultTLH);
                realm.copyToRealmOrUpdate(userDefaultTSM);
                Log.d(null,"Se crean usuarios por defecto.");
            }
        });
    }

    public void crearSucursalDefecto(){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Sucursal sucursalAV,sucursalR20,sucursalSV,sucursalCVL,sucursalSP,
                        sucursal60C,sucursalAG,sucursalTLH,sucursalTSM;
                sucursalAV = new Sucursal("", "AV", "0AC9A4BA-7642-422C-903F-6FAACBE43249", 1);
                sucursalR20 = new Sucursal("", "R20", "63500A06-E6FD-4B35-9829-ADF2E73133D8", 2);
                sucursalSV = new Sucursal("", "SV", "4AE45237-C030-4E38-89F8-77BC63D60A06", 3);
                sucursalCVL = new Sucursal("", "CVL", "A5FE6B0A-D045-4D77-8D50-09816FFAA65C", 4);
                sucursalSP = new Sucursal("", "SP", "34E82DEA-3931-47C3-B988-49CBDA31829D", 5);
                sucursal60C = new Sucursal("", "60C", "1A9AFB97-CE1A-4901-AD5F-17974BB60EC3", 6);
                sucursalAG = new Sucursal("", "AG", "BAB1A7E9-6924-4873-8447-779D79A7D057", 7);
                sucursalTLH = new Sucursal("", "TLH", "CBC6A936-F354-4102-AE2A-B4301C1E6DF5", 8);
                sucursalTSM = new Sucursal("", "TSM", "F906455C-4D5C-41FD-9841-E4FFF031BF42", 9);


                realm.copyToRealmOrUpdate(sucursalAV);
                realm.copyToRealmOrUpdate(sucursalR20);
                realm.copyToRealmOrUpdate(sucursalSV);
                realm.copyToRealmOrUpdate(sucursalCVL);
                realm.copyToRealmOrUpdate(sucursalSP);
                realm.copyToRealmOrUpdate(sucursal60C);
                realm.copyToRealmOrUpdate(sucursalAG);
                realm.copyToRealmOrUpdate(sucursalTLH);
                realm.copyToRealmOrUpdate(sucursalTSM);
                Log.d(null,"Se crean sucursales por defecto.");
            }
        });
    }

}
