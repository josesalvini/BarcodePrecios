package com.dinosaurio.preciosdino.ActivityClass;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.TipoEtiqueta;
import com.dinosaurio.preciosdino.Interfaces.AppData;
import com.dinosaurio.preciosdino.Interfaces.DatosPreference;
import com.dinosaurio.preciosdino.Main.LoginActivity;
import com.dinosaurio.preciosdino.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Formatter;
import java.util.UUID;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity  {

    private Toolbar mToolbar;
    private ImageButton iButtonExportar,iButtonScan,iButtonConfig,iButtonViewQR;
    private TextView deviceNombre,fechaInitSesion,sucursal;
    private IntentIntegrator scan;
    private String deviceName,initSesionDate,sucursalUser;
    private Realm realm;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        mToolbar.inflateMenu(R.menu.menu_sesion);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return clickMenu(item.getItemId());
            }
        });

        iButtonExportar = (ImageButton) findViewById(R.id.iButtonExportar);
        iButtonScan = (ImageButton)findViewById(R.id.iButtonScan);
        iButtonConfig = (ImageButton) findViewById(R.id.iButtonConfig);
        iButtonViewQR = (ImageButton) findViewById(R.id.iButtonViewQR);

        deviceNombre = (TextView)  findViewById(R.id.textViewDevice);
        sucursal = (TextView)  findViewById(R.id.textViewSucursal);
        fechaInitSesion = (TextView)  findViewById(R.id.textViewSesion);

        deviceName = getDeviceName();
        String uniqueID = UUID.randomUUID().toString();

        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);

        initSesionDate = DatosPreference.getInitSesionDate(sharedPreferences);
        sucursalUser = DatosPreference.getSucursal(sharedPreferences);

        AppData.setDeviceName(deviceName);
        AppData.setSesionID(uniqueID);
        AppData.setSucursal(sucursalUser);
        AppData.setFecha(AppData.getFechaCarga());

        realm = Realm.getDefaultInstance();

        Bundle parametros = getIntent().getExtras();
        String nombreUsuario;

        if(parametros!= null){
            nombreUsuario = parametros.getString("NOMBREUSER");
        }else{
            nombreUsuario ="Sin identificar";
        }

        mToolbar.setTitle(nombreUsuario);
        deviceNombre.setText("Equipo: " + deviceName);
        sucursal.setText("Sucursal: " + sucursalUser );
        fechaInitSesion.setText("Inicio sesion: " + initSesionDate);

        iButtonExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickExportar();
            }
        });
        iButtonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickScan();
            }
        });
        iButtonConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickConfiguracion();
            }
        });
        iButtonViewQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickViewQr();
            }
        });

        scan = new IntentIntegrator(this);
        scan.setOrientationLocked(true);
        scan.setBeepEnabled(true);
        scan.setCaptureActivity(ScanActivity.class);

    }

    public boolean clickMenu(int idMenuItem){
        switch (idMenuItem){
            case R.id.menu_logout:
                this.logout();
                this.clearSharedPreferences();
                this.setDatosSesion();
                return true;
            case R.id.menu_off:
                finish();
                return true;
            default:
                return false;
        }
    }

    public void setDatosSesion(){
        DatosPreference.setInitSesionDate(sharedPreferences);
        //Borro los datos de las cargas
        realm.beginTransaction();
        realm.delete(CargaCodigos.class);
        realm.commitTransaction();
        realm.close();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        //showToast("Result code: " + resultCode + " Resultado: " + data.getExtras().getString("accion"), "I");

        if (requestCode == 24 && data!=null){
            String resultado = data.getExtras().getString("accion");
            switch (resultado){
                case "1":
                    //showToast("Accion grabar", "I");
                    clickScan();
                    break;
                case "2":
                    //showToast("Accion finalizar", "I");
                    break;
            }
        }else{
            if (requestCode == IntentIntegrator.REQUEST_CODE) {
                IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                if (result.getContents() == null) {
                    showToast("Cancelado por el usuario.", "W");
                } else {
                    if (resultCode == RESULT_OK) {
                         try{
                             Long codigoLeido = Long.parseLong(result.getContents());
                             Formatter obj = new Formatter();
                             String numeroCeros = String.valueOf(obj.format("%013d", codigoLeido));
                             Intent intent = new Intent(this, CargaEANActivity.class);
                             intent.putExtra("EAN",numeroCeros );
                             startActivityForResult(intent, 24);
                        }catch (NumberFormatException nfe) {
                            showToast("El codigo de barra leido no contiene un EAN valido.", "E");
                        }
                    }
                }
            }
        }
    }// end metodo

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

    public void clickExportar(){
        Intent exportIntent = new Intent(this, ExportFileActivity.class);
        startActivity(exportIntent);
    }

    public void clickScan(){
        long cantidad = realm.where(TipoEtiqueta.class).count();
        String modo = DatosPreference.getSesionMode(sharedPreferences);
        if(modo.equals("ONLINE")) {
            if (cantidad > 0) {
                scan.initiateScan();
            } else {
                showToast("No existen formatos de etiquetas cargados, actualice los mismos antes de iniciar el escaneo.", "W");
            }
        }else{
            scan.initiateScan();
        }
    }

    public void clickConfiguracion(){
        Intent configIntent = new Intent(this, ConfiguracionActivity.class);
        startActivity(configIntent);
    }

    public void clickViewQr(){

        long cantidad = realm.where(CargaCodigos.class).count();

        if(cantidad>0) {
            Intent viewQrIntent = new Intent(this, ViewCargaActivity.class);
            startActivity(viewQrIntent);
        }else{
            showToast("No existen códigos cargados para visualizar.","W");
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }

    }

    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public void logout(){
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    public void clearSharedPreferences(){
        sharedPreferences.edit().clear().apply();
        //preferencesSystem.edit().clear().apply();
    }
}
