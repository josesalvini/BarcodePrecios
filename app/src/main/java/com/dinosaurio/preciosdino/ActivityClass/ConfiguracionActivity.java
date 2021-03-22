package com.dinosaurio.preciosdino.ActivityClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dinosaurio.preciosdino.ComplementosClass.InputValidation;
import com.dinosaurio.preciosdino.Entidades.Carga;
import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.DetalleCarga;
import com.dinosaurio.preciosdino.Entidades.Etiqueta;
import com.dinosaurio.preciosdino.Entidades.RespuestaEstado;
import com.dinosaurio.preciosdino.Entidades.Sucursal;
import com.dinosaurio.preciosdino.Entidades.SucursalAPI;
import com.dinosaurio.preciosdino.Entidades.TipoEtiqueta;
import com.dinosaurio.preciosdino.Interfaces.AppData;
import com.dinosaurio.preciosdino.Interfaces.DatosAPI;
import com.dinosaurio.preciosdino.Interfaces.DatosPreference;
import com.dinosaurio.preciosdino.Interfaces.JsonPreciosAPI;
import com.dinosaurio.preciosdino.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfiguracionActivity extends AppCompatActivity {

    private ImageButton btnLimpiarCarga,btnUpdateEtiquetas,btnUpdateSucursales,btnSubirCarga;
    private Button btnGrabarURL, btnEditarUrl,btnDefaultURL;
    private TextInputEditText urlAPI;
    private TextInputLayout textViewURL;
    private Toolbar configToolbar;
    private Retrofit retrofit;
    private String valorURL;
    private Carga carga;
    private SharedPreferences preferencesSystem;
    private Realm realm;
    private JsonPreciosAPI jsonPreciosAPI;
    private InputValidation inputValidation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        realm = Realm.getDefaultInstance();
        inputValidation = new InputValidation(ConfiguracionActivity.this);

        btnLimpiarCarga = (ImageButton) findViewById(R.id.iButtonConfigReset);
        btnUpdateEtiquetas = (ImageButton) findViewById(R.id.iButtonUpdateEtiquetas);
        btnSubirCarga = (ImageButton) findViewById(R.id.iButtonSubirCarga);
        btnUpdateSucursales = (ImageButton) findViewById(R.id.iButtonUpdateSucursales);
        btnGrabarURL =  (Button) findViewById(R.id.buttonGuardarURL);
        btnEditarUrl =  (Button) findViewById(R.id.buttonEditarURL);
        btnDefaultURL =  (Button) findViewById(R.id.buttonDefaultURL);

        urlAPI = (TextInputEditText) findViewById(R.id.editTextURL);
        textViewURL = (TextInputLayout) findViewById(R.id.textViewURL);

        urlAPI.setFocusable(false);
        urlAPI.setFocusableInTouchMode(false);
        urlAPI.setInputType(InputType.TYPE_NULL);
        urlAPI.setKeyListener(null);

        configToolbar = findViewById(R.id.toolbarConfig);

        configToolbar.setTitle("Opciones del sistema");

        preferencesSystem = getSharedPreferences("PreferencesApp", Context.MODE_PRIVATE);
        valorURL = DatosPreference.getAPIDefault(preferencesSystem);

        urlAPI.setText(valorURL);

        this.crearConexionAPI();

        btnLimpiarCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCarga();
            }
        });
        btnUpdateEtiquetas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEtiquetas();
            }
        });
        btnSubirCarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarCarga();
            }
        });
        btnUpdateSucursales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSucursales();
            }
        });
        btnGrabarURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarURL();
            }
        });
        btnEditarUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editarURL();
            }
        });
        btnDefaultURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefaultURL();
            }
        });
    }

    public void setDefaultURL(){
        urlAPI.setText(DatosAPI.getUrlAPI());
        guardarURL();
    }

    public void editarURL(){
        urlAPI.setText("http://");
        urlAPI.setFocusable(true);
        urlAPI.setFocusableInTouchMode(true);
        urlAPI.setInputType(InputType.TYPE_TEXT_VARIATION_URI);

    }

    public void guardarURL(){

        if (!inputValidation.isInputEditTextURL(urlAPI, textViewURL, getString(R.string.error_message_url))) {
            return;
        }

        DatosPreference.guardarAPIPreferencias(preferencesSystem,urlAPI.getText().toString().trim());
        valorURL = DatosPreference.getAPIDefault(preferencesSystem);
        updateConexionAPI();

        urlAPI.setFocusable(false);
        urlAPI.setFocusableInTouchMode(false);
        urlAPI.setInputType(InputType.TYPE_NULL);
        urlAPI.setKeyListener(null);

    }

    public void deleteCarga(){
        long row = realm.where(CargaCodigos.class).findAll().size();
        if(row>0){
            realm.beginTransaction();
            realm.delete(CargaCodigos.class);
            realm.commitTransaction();
            showToast("Se elimino correctamente la carga. Items eliminados: " + row,"I");
        }else{
            showToast("No existen datos cargados para ser eliminados.","W");
        }
    }

    public void crearConexionAPI(){
        retrofit = new Retrofit.Builder()
                .baseUrl(valorURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPreciosAPI = retrofit.create(JsonPreciosAPI.class);
    }

    public void updateConexionAPI(){
        retrofit = null;
        valorURL = DatosPreference.getAPIDefault(preferencesSystem);
        retrofit = new Retrofit.Builder()
                .baseUrl(valorURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPreciosAPI = retrofit.create(JsonPreciosAPI.class);
    }

    public void updateEtiquetas() {
         Call<List<Etiqueta>> callEtiquetas = jsonPreciosAPI.getEtiquetas();

         callEtiquetas.enqueue(new Callback<List<Etiqueta>>() {
             @Override
             public void onResponse(Call<List<Etiqueta>> call, Response<List<Etiqueta>> response) {
                 if (!response.isSuccessful()){
                     switch (response.code()){

                         case 404:
                             showToast(response.code() + " - Recurso no disponible." ,"E");
                             break;
                         default:
                             showToast("Error al obtener formatos de etiquetas. " + response.message() ,"E");
                                 break;
                     }
                 }else{
                     Log.d("Respuesta","Response " + response.code());
                 }

                 if(response.code() == 200) {
                     realm.beginTransaction();
                     realm.delete(TipoEtiqueta.class);
                     realm.commitTransaction();

                     List<Etiqueta> listEtiquetas = response.body();
                     int formatosActualizados=0;
                     for (Etiqueta etiqueta : listEtiquetas) {
                         if(etiqueta.getHabilitado() == 1){

                             TipoEtiqueta etiquetaADD = new TipoEtiqueta(etiqueta.getNombre(),
                                     etiqueta.getDefecto(),
                                     etiqueta.getUrl(),
                                     etiqueta.getHabilitado());

                             realm.beginTransaction();
                             realm.insert(etiquetaADD);
                             realm.commitTransaction();

                             formatosActualizados+=1;
                         }
                     }

                     showToast("Se actualizarón " + formatosActualizados + " formato/s de etiqueta/s.", "I");
                     realm.close();
                 }

             }

             @Override
             public void onFailure(Call<List<Etiqueta>> call, Throwable t) {
                 showToast("Error " + t.getMessage(),"E");
             }
         });

    }

    public void updateSucursales(){
        Call<List<SucursalAPI>> callSucursales = jsonPreciosAPI.getSucursales();

        callSucursales.enqueue(new Callback<List<SucursalAPI>>() {
            @Override
            public void onResponse(Call<List<SucursalAPI>> call, Response<List<SucursalAPI>> response) {
                if (!response.isSuccessful()){
                    switch (response.code()){
                        case 404:
                            showToast(response.code() + " - Recurso no disponible." ,"E");
                            break;
                        default:
                            showToast("Error al obtener formatos de etiquetas. " + response.message() ,"E");
                            break;
                    }
                }else{
                    Log.d("Respuesta","Response " + response.code());
                }

                if(response.code() == 200) {
                    realm.beginTransaction();
                    realm.delete(Sucursal.class);
                    realm.commitTransaction();

                    List<SucursalAPI> listSucursales = response.body();
                    int sucursalesActualizados=0;
                    for (SucursalAPI sucursal : listSucursales) {
                        Sucursal sucursalADD = new Sucursal("",
                                sucursal.getDescripcion(),
                                sucursal.getIdSucursal(),
                                sucursal.getOrden());

                        realm.beginTransaction();
                        realm.insert(sucursalADD);
                        realm.commitTransaction();

                        sucursalesActualizados+=1;

                    }

                    showToast("Se actualizarón " + sucursalesActualizados + " sucursal/es.", "I");
                    realm.close();
                }
            }

            @Override
            public void onFailure(Call<List<SucursalAPI>> call, Throwable t) {
                showToast("Error " + t.getMessage(),"E");
            }
        });
    }

    public void grabarCarga(){
        boolean result = this.setCarga();

        if(result){
            Call<RespuestaEstado> callCarga = jsonPreciosAPI.guardarCarga(carga);

            callCarga.enqueue(new Callback<RespuestaEstado>() {
                @Override
                public void onResponse(Call<RespuestaEstado> call, Response<RespuestaEstado> response) {
                    if (response.isSuccessful()) {

                        //RealmResults<CargaCodigos> codigosSubidos = realm.where(CargaCodigos.class).equalTo("uploaded", 0).findAll();
                        RealmResults<CargaCodigos> codigosDelete = realm.where(CargaCodigos.class).equalTo("uploaded",0).findAll();
                        long row = codigosDelete.size();
                        //Elimino todos los codigos despues de subirlos
                        realm.beginTransaction();
                        //codigosSubidos.setInt("uploaded", 1);
                        codigosDelete.deleteAllFromRealm();
                        realm.commitTransaction();

                        showToast( response.body().getEstado() + ". Registros subidos: " + row, "I");
                    }else{
                        try {
                            showToast("ERROR. Codigo - " + response.errorBody().string(), "E");
                        } catch (IOException e) {
                            showToast("Exception - " + e.getMessage(), "E");
                        }
                    }
                }
                @Override
                public void onFailure(Call<RespuestaEstado> call, Throwable t) {
                    showToast(t.getMessage(), "E");
                }
            });
        }else{
            showToast("No existen datos por subir, o los mismos ya fueron subidos.","W");
        }
    }

    public boolean setCarga(){
        carga=null;
        boolean respuestaCarga = false;

        RealmResults<CargaCodigos> cargaCodigos = realm.where(CargaCodigos.class).equalTo("uploaded",0).findAll();

        if (cargaCodigos.size()>0) {

            carga=new Carga();

            String sesion = AppData.getSesionID();
            String usuario = AppData.getNombreUsuario();
            String sucursal = AppData.getSucursal();
            String dispositivo = AppData.getDeviceName();
            String fecha = AppData.getFechaCarga();

            carga.setSesion(sesion);
            carga.setUsuario(usuario);
            carga.setSucursal(sucursal);
            carga.setDispositivo(dispositivo);
            carga.setFecha(fecha);

            List<DetalleCarga> detalleCarga =  new ArrayList<>();
            DetalleCarga detalleAuxiliar;

            for (CargaCodigos codigo : cargaCodigos) {
                detalleAuxiliar = new DetalleCarga();

                detalleAuxiliar.setTipoEtiqueta(codigo.getTipoEtiqueta());
                detalleAuxiliar.setEan(codigo.getCodigoBarra());
                detalleAuxiliar.setCantidad(codigo.getCantidadCopias());

                detalleCarga.add(detalleAuxiliar);
            }

            carga.setDetalle(detalleCarga);
            respuestaCarga = true;
        }
        return respuestaCarga;
    }

    public void showToast(String message,String tipo){
        LayoutInflater inflater = getLayoutInflater();
        View layout=null;
        switch (tipo){
            case "I":
                layout = inflater.inflate(R.layout.custom_toastok, (ViewGroup) findViewById(R.id.customToastOK));
                break;
            case "W":
                layout = inflater.inflate(R.layout.custom_toastwarning, (ViewGroup) findViewById(R.id.customToastWarning));
                break;
            case "E":
                layout = inflater.inflate(R.layout.custom_toasterror, (ViewGroup) findViewById(R.id.customToastError));
                break;
        }

        if(layout!=null) {
            TextView text = (TextView) layout.findViewById(R.id.textViewToast);
            text.setText(message);

            Toast toast = new Toast(getApplicationContext());
            toast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, 0);
            //toast.setGravity(Gravity.TOP| Gravity.LEFT, 10, 10);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
    }

}
