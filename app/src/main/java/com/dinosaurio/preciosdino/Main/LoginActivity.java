package com.dinosaurio.preciosdino.Main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.Sucursal;
import com.dinosaurio.preciosdino.Entidades.UsrLogin;
import com.dinosaurio.preciosdino.Interfaces.AppData;
import com.dinosaurio.preciosdino.ComplementosClass.InputValidation;
import com.dinosaurio.preciosdino.ActivityClass.MainActivity;
import com.dinosaurio.preciosdino.Entidades.Usuario;
import com.dinosaurio.preciosdino.Interfaces.DatosAPI;
import com.dinosaurio.preciosdino.Interfaces.DatosPreference;
import com.dinosaurio.preciosdino.Interfaces.JsonPreciosAPI;
import com.dinosaurio.preciosdino.R;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private final AppCompatActivity activity = LoginActivity.this;

    private TextInputLayout lblUsuario;

    private TextInputEditText txtUsuario;
    private Button btnLogin;
    private Spinner spinnerSucursales;

    private Realm realm;
    private InputValidation inputValidation;
    private SharedPreferences sharedPreferences,preferencesSystem;

    private ArrayList<String> arraySucursales = new ArrayList<String>();
    private ConnectivityManager connectivityManager;
    private Retrofit retrofit;
    private JsonPreciosAPI jsonPreciosAPI;
    private String sucursal;
    private List<UsrLogin> listUsuarioLogin;
    private String nombreUsuario;
    private String empresaUsuario;
    private Intent mainIntent;
    private boolean usrEncontrado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        realm = Realm.getDefaultInstance();
        this.loadSucursales();
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        preferencesSystem = getSharedPreferences("PreferencesApp", Context.MODE_PRIVATE);
        usrEncontrado = false;

        //loadSharedPreferences();
        this.existeAPIDefault();

        initViews();
        initListeners();
        initObjects();

        String fechaInicio = AppData.getFechaSesion(sharedPreferences);
        if(!AppData.getTiempoExpiracion(fechaInicio)){
            //Borro los datos de las cargas si la sesion ya expiro
            realm.beginTransaction();
            realm.delete(CargaCodigos.class);
            realm.commitTransaction();
            realm.close();
        }


    }

    private void initViews() {

        lblUsuario = (TextInputLayout) findViewById(R.id.textLabelUsuario);
        txtUsuario = (TextInputEditText) findViewById(R.id.textInputEditTextUsuario);

        btnLogin = (Button) findViewById(R.id.btnLogin);

        spinnerSucursales = (Spinner) findViewById(R.id.spinnerSucursalesLogin);
        ArrayAdapter adapter = new ArrayAdapter(
                getApplicationContext(),R.layout.spinner_sucursales ,arraySucursales);

        adapter.setDropDownViewResource(R.layout.spinner_sucursales);
        spinnerSucursales.setAdapter(adapter);
        spinnerSucursales.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sucursal = arraySucursales.get(position);
                //Log.e("Sucursal:",sucursal);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

    }

    private void initListeners() {
        btnLogin.setOnClickListener(this);
    }

    private void initObjects() {
        inputValidation = new InputValidation(activity);
    }

    /**
     * This implemented method is to listen the click on view
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                if(verifyCredentials()) {
                    String dniUsuario = txtUsuario.getText().toString().trim();

                    Usuario usuarioLogin = realm.where(Usuario.class)
                            .equalTo("password", dniUsuario)
                            .findFirst();

                    if (usuarioLogin != null) {
                        AppData.setUsuario(usuarioLogin);
                        loginUsuario(usuarioLogin.getNombre(),usuarioLogin.getPassword(),sucursal,"ONLINE");
                    } else {
                        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        boolean conexion = AppData.verificarConexion(connectivityManager);
                        if (conexion) {
                            crearConexionAPI();
                            getUsuarioByDNI(dniUsuario);
                            if (usrEncontrado){
                                showToast("No se encontro la persona, se usará el usuario por defecto.", "W");
                                loginUsuario(nombreUsuario, dniUsuario, sucursal, "ONLINE");
                            }else{
                                loadUsuarioDefault("ONLINE");
                            }
                        } else {
                            showToast("Sin conexión a Internet, se usará el usuario por defecto. ", "W");
                            //Snackbar.make(v, "Sin conexión a Internet, se usará el usuario por defecto.", Snackbar.LENGTH_LONG).show();
                            loadUsuarioDefault("OFFLINE");
                        }
                    }
                }
                break;
        }
    }//end metodo onClick

    public void loadUsuarioDefault(String modo){
        Usuario user = getUsuarioSucursal(sucursal);
        AppData.setUsuario(user);
        loginUsuario(user.getNombre(),user.getPassword(),sucursal,modo);
    }

    public void loginUsuario(String nombre, String password,String suc,String modo){
        String fechaInicio = AppData.getFechaSesion(sharedPreferences);
        if(!AppData.getTiempoExpiracion(fechaInicio)){
            //Borro los datos de las cargas si la sesion ya expiro
            realm.beginTransaction();
            realm.delete(CargaCodigos.class);
            realm.commitTransaction();
        }

        DatosPreference.guardarPreferencias(sharedPreferences,nombre,password);
        DatosPreference.guardarSesionMode(sharedPreferences,modo);
        DatosPreference.guardarSucursal(sharedPreferences, suc);

        mainIntent = new Intent(this, MainActivity.class);
        mainIntent.putExtra("NOMBREUSER", nombre);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
    }

    public void getUsuarioByDNI(final String nroDNI) {
        try {
            Call<List<UsrLogin>> callUsuario = jsonPreciosAPI.getUsuario(nroDNI);

            nombreUsuario="";
            empresaUsuario="";

            callUsuario.enqueue(new Callback<List<UsrLogin>>() {
                @Override
                public void onResponse(Call<List<UsrLogin>> call, Response<List<UsrLogin>> response) {
                    if (!response.isSuccessful()) {
                        switch (response.code()) {
                            case 404:
                                break;
                            default:
                                showToast("J1 - Metodo getUsuarioByDNI: Codigo: " + response.code() + "Mensaje : " + response.message(), "E");
                                break;
                        }
                    } else {
                        //Log.e("Respuesta", "Response " + response.code());
                    }
                    if (response.code() == 200) {
                        listUsuarioLogin = response.body();

                        for (UsrLogin user : listUsuarioLogin) {
                            nombreUsuario = user.getNombre();
                            empresaUsuario = user.getEmpresa();
                            //Log.e("Encontrado", "Usuario: " + nombreUsuario + " DNI: " + nroDNI + " EMPRESA: " + empresaUsuario + " SUCURSAL: " + sucursal);
                            crearUsuario(nombreUsuario, nroDNI, empresaUsuario, sucursal);
                            usrEncontrado = true;
                        }
                    } else {
                        showToast("J2 - Metodo getUsuarioByDNI: " + response.message(), "E");
                    }

                }

                @Override
                public void onFailure(Call<List<UsrLogin>> call, Throwable t) {
                }
            });
        }catch (Exception e){
            showToast("J3 - Exception: " + e.getMessage(),"E");
        }
    }

    public void crearUsuario(final String nombre, final String password, final String empresa, final String sucursal){
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Usuario user = new Usuario(nombre,
                        password,
                        empresa,
                        sucursal);
                realm.copyToRealmOrUpdate(user);
                AppData.setUsuario(user);
            }
        });
    }

    public Usuario getUsuarioSucursal(String suc){
        Usuario usuarioLogin =  realm.where(Usuario.class)
                .equalTo("sucursal", suc)
                .findFirst();
        return usuarioLogin;
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

    /**
     * This method is to validate the input text fields and verify login credentials from SQLite
     */
    private boolean verifyCredentials() {
        boolean valido=true;
        if (!inputValidation.isInputEditTextUsuario(txtUsuario, lblUsuario, getString(R.string.error_message_usuario))) {
            valido=false;
        }
        return valido;
    }


    /**
     * This method is to empty all input edit text
     */
    private void emptyInputEditText() {
        txtUsuario.setText(null);
    }

    public void loadSharedPreferences(){
        String usuario = DatosPreference.getUsuarioPreference(sharedPreferences);
        String password = DatosPreference.getPasswordPreference(sharedPreferences);

        Log.e("Load shared","usuario: " + usuario + " password: " + password );

        if(!TextUtils.isEmpty(usuario) && !TextUtils.isEmpty(password)  ){
            txtUsuario.setText(usuario);

        }
    }

    public void loadSucursales(){
        RealmResults<Sucursal> listSucursales =  realm.where(Sucursal.class).findAll().sort("orden");
        if(listSucursales.size()> 0){
            for(Sucursal sucursal : listSucursales){
                arraySucursales.add(sucursal.getAbreviatura());
            }
        }else{
            //SUCURSAL POR DEFECTO
            Sucursal sucursalADD = new Sucursal("",
                    "AV",
                    "0AC9A4BA-7642-422C-903F-6FAACBE43249",
                    1);
            realm.beginTransaction();
            realm.insertOrUpdate(sucursalADD);
            realm.commitTransaction();
            arraySucursales.add(sucursalADD.getDescripcion());
        }
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