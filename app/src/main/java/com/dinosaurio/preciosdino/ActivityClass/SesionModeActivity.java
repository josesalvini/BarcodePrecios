package com.dinosaurio.preciosdino.ActivityClass;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.Sucursal;
import com.dinosaurio.preciosdino.Entidades.UsrLogin;
import com.dinosaurio.preciosdino.Entidades.Usuario;
import com.dinosaurio.preciosdino.Interfaces.AppData;
import com.dinosaurio.preciosdino.Interfaces.DatosAPI;
import com.dinosaurio.preciosdino.Interfaces.DatosPreference;
import com.dinosaurio.preciosdino.Interfaces.JsonPreciosAPI;
import com.dinosaurio.preciosdino.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SesionModeActivity extends AppCompatActivity{

    private ImageButton btnModoOnLine,btnModoOffLine;
    private IntentIntegrator scan;
    private Realm realm;
    private SharedPreferences sharedPreferences,preferencesSystem;
    private Intent mainIntent;
    private ArrayList<String> arraySucursales = new ArrayList<String>();
    private String sucursal;
    private JsonPreciosAPI jsonPreciosAPI;
    private ConnectivityManager connectivityManager;
    private Retrofit retrofit;
    private List<UsrLogin> listUsuarioLogin;
    private String nombreUsuario;
    private String empresaUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sesion_mode);

        realm = Realm.getDefaultInstance();
        this.loadSucursales();
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        preferencesSystem = getSharedPreferences("PreferencesApp", Context.MODE_PRIVATE);

        btnModoOnLine = (ImageButton) findViewById(R.id.btnOnLine);
        btnModoOffLine = (ImageButton) findViewById(R.id.btnOffLine);

        btnModoOnLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //clickOnLine();
                connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                boolean conexion = AppData.verificarConexion(connectivityManager);
                if(conexion){
                    crearConexionAPI();
                    showDialogLogin();
                }else{
                    showToast("Sin conexion a Internet.","E");
                }

            }
        });

        btnModoOffLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clickOffLine();
                showDialogSucursales();
            }
        });

        this.existeAPIDefault();
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

    public void getUsuario(final String nroDNI) {
        try {
            Call<List<UsrLogin>> callUsuario = jsonPreciosAPI.getUsuario(nroDNI);

            callUsuario.enqueue(new Callback<List<UsrLogin>>() {
                @Override
                public void onResponse(Call<List<UsrLogin>> call, Response<List<UsrLogin>> response) {
                    showToast(response.message(), "E");
                    if (!response.isSuccessful()) {
                        switch (response.code()) {
                            case 404:
                                break;
                            default:
                                showToast(response.message(), "E");
                                break;
                        }
                    } else {
                        Log.d("Respuesta", "Response " + response.code());
                    }
                    if (response.code() == 200) {
                        listUsuarioLogin = response.body();
                        for (UsrLogin user : listUsuarioLogin) {
                            nombreUsuario = user.getNombre();
                            empresaUsuario = user.getEmpresa();
                            Log.e("Datos Usr:", "Nombre: " + nombreUsuario + " empresa: " + empresaUsuario);
                            crearUsuario(nombreUsuario, nroDNI, empresaUsuario, sucursal);
                        }
                    } else {
                        showToast(response.message(), "E");
                    }

                }

                @Override
                public void onFailure(Call<List<UsrLogin>> call, Throwable t) {
                }
            });
        }catch (Exception e){
            showToast(e.getMessage(),"E");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result.getContents() == null) {
                showToast("Cancelado por el usuario.", "W");
            } else {
                if (resultCode == RESULT_OK) {
                    // 5541|33893081|ZAPATA MAXIMILIANO|SANCOR|CVL
                    Log.i("Contenido:",result.getContents());
                    if (result.getContents().contains("|")) {
                        String[] datos = result.getContents().split("\\|");
                        if (datos.length>3) {
                            int dni = 0;
                            try {
                                dni = Integer.parseInt(datos[1]);
                                String dniUsuario = String.valueOf(dni);
                                String nombreUsuario = datos[2];
                                String empresaUsuario = datos[3];
                                String sucursalUsuario = datos[4];

                                Sucursal suc = realm.where(Sucursal.class).equalTo("idSucursal", sucursalUsuario.toUpperCase()).findFirst();
                                this.crearUsuario(nombreUsuario, dniUsuario, empresaUsuario, sucursalUsuario);

                                Log.i("sucursal:", suc.getDescripcion());

                                this.loginUsuario(nombreUsuario, dniUsuario, suc.getAbreviatura(), "ONLINE");

                            } catch (NumberFormatException nfe) {
                                showToast("El número de DNI no es valido.", "E");
                            }
                        }else {
                            showToast("El codigo QR leido no contiene los datos requeridos.", "E");
                        }
                    } else{
                        showToast("El codigo QR leido es invalido.","E");
                    }
                }
            }
        }

    }// end metodo

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

    public void showDialogLogin(){
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(SesionModeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog_usuario);
        dialog.setCancelable(true);

        // set the custom dialog components - text, image and button
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerSucursales);
        final EditText editTextDNI = (EditText) dialog.findViewById(R.id.etNroDNI);

        Button button = (Button) dialog.findViewById(R.id.buttonAceptarLogin);

        ArrayAdapter adapter = new ArrayAdapter(
                getApplicationContext(),R.layout.spinner_sucursales ,arraySucursales);

        adapter.setDropDownViewResource(R.layout.spinner_sucursales);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                sucursal = arraySucursales.get(position);
                Log.e("Sucursal:",sucursal);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                String dniUsuario = editTextDNI.getText().toString();
                if (dniUsuario.length()>0){
                    try {
                        getUsuario(dniUsuario);
                        Sucursal suc = realm.where(Sucursal.class).equalTo("abreviatura", sucursal.toUpperCase()).findFirst();
                        //Log.e("Encontrada:",suc.getIdSucursal());
                        Log.e("Login:","Nombre: " + nombreUsuario + " empresa: " + empresaUsuario );
                        if(!nombreUsuario.isEmpty() && !empresaUsuario.isEmpty() ) {
                            loginUsuario(nombreUsuario, dniUsuario, suc.getAbreviatura(), "ONLINE");
                        }else {
                            showToast("Usuario no encontrado.", "E");
                        }
                    } catch (NumberFormatException nfe) {
                        showToast("Error al obtener datos de la persona.", "E");
                    }
                }else{
                    showToast("No ingreso un número de DNI valido.","E");
                }
            }
        });
        dialog.show();

    }

    public void showDialogSucursales(){
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(SesionModeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.input_dialog_sucursal);
        dialog.setCancelable(true);

        // set the custom dialog components - text, image and button
        final Spinner spinner = (Spinner) dialog.findViewById(R.id.spinnerSucursales);

        Button button = (Button) dialog.findViewById(R.id.buttonAceptarSuc);

        ArrayAdapter adapter = new ArrayAdapter(
                getApplicationContext(),R.layout.spinner_sucursales ,arraySucursales);

        adapter.setDropDownViewResource(R.layout.spinner_sucursales);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                sucursal = arraySucursales.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                Usuario user = getUsuarioSucursal(sucursal);
                AppData.setUsuario(user);
                loginUsuario(user.getNombre(),user.getPassword(),sucursal,"OFFLINE");

            }
        });
        dialog.show();

    }

    public Usuario getUsuarioSucursal(String suc){
        Usuario usuarioLogin =  realm.where(Usuario.class)
                .equalTo("sucursal", suc)
                .findFirst();
        return usuarioLogin;
    }


}
