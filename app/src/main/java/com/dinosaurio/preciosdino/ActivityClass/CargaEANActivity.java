package com.dinosaurio.preciosdino.ActivityClass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.TipoEtiqueta;
import com.dinosaurio.preciosdino.Interfaces.AppData;
import com.dinosaurio.preciosdino.R;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class CargaEANActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TextView textViewCodigo;
    private Button buttonGrabar,buttonFinalizar;
    private ImageButton btnSumar,btnRestar;

    private Spinner cboxTipoEtiqueta;
    private TextView textViewCantidad;
    private static int cantidadCopias;
    private String tipoEtiqueta;
    private String codigo,carga;

    private ArrayList<String> arrayEtiquetas = new ArrayList<String>();
    private Realm realm;
    private ConnectivityManager connectivityManager;
    private final Context context = CargaEANActivity.this;
    private boolean cantidadModificada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carga_ean);

        realm = Realm.getDefaultInstance();
        cantidadModificada = false;

        cboxTipoEtiqueta  = (Spinner) findViewById(R.id.spinnerEtiquetas);

        int cantEtiquetas = realm.where(TipoEtiqueta.class).findAll().size();
        if(cantEtiquetas== 0) {
            connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            boolean conexion = AppData.verificarConexion(connectivityManager);
            if(!conexion) {
                TipoEtiqueta etiquetaDefault = new TipoEtiqueta(
                        "Gondola Min",
                        1,
                        "http://reportesdino/ReportServer/Pages/ReportViewer.aspx?%2fPrecios%2fMinorista%2fGondola%2fPrecio+Individual&rs:Command=Render",
                        1
                );
                realm.beginTransaction();
                realm.insertOrUpdate(etiquetaDefault);
                realm.commitTransaction();

                arrayEtiquetas.add(etiquetaDefault.getNombre());
            }
        }else{
            this.loadFormatosEtiquetas();
        }

        // Initializing an ArrayAdapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,arrayEtiquetas);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        cboxTipoEtiqueta.setAdapter(spinnerArrayAdapter);

        cboxTipoEtiqueta.setOnItemSelectedListener(this);

        btnSumar = (ImageButton) findViewById(R.id.btnAumentar);
        btnRestar = (ImageButton) findViewById(R.id.btnDisminuir);
        buttonGrabar = (Button) findViewById(R.id.btnGrabar);
        buttonFinalizar = (Button) findViewById(R.id.btnFinalizar);
        textViewCantidad = (TextView) findViewById(R.id.etCantidad);

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); // Make to run your application only in portrait mode
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Make to run your application only in LANDSCAPE mode

        textViewCodigo = (TextView) findViewById(R.id.tvCodigoBarra);

        cantidadCopias = 1;
        textViewCantidad.setText(String.valueOf(cantidadCopias));

        btnRestar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decrement();
            }
        });
        btnSumar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increment();
            }
        });

        buttonGrabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickGrabar();
            }
        });
        buttonFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFinalizar();
            }
        });

        Bundle parametros = getIntent().getExtras();

        codigo = parametros.getString("EAN");
        carga = parametros.getString("CARGA");

        textViewCodigo.setText(String.valueOf(codigo));

    }

    public void loadFormatosEtiquetas(){

        RealmResults<TipoEtiqueta> listEtiquetas =  realm.where(TipoEtiqueta.class).findAll();

        for(TipoEtiqueta etiqueta : listEtiquetas){
            arrayEtiquetas.add(etiqueta.getNombre());
        }

    }

    public CargaCodigos checkExist(String tipoEtiqueta,String ean){
        CargaCodigos encontrado;

        encontrado = realm.where(CargaCodigos.class)
                .equalTo("codigoBarra",ean)
                .and()
                .equalTo("tipoEtiqueta",tipoEtiqueta).findFirst();
        return encontrado;
    }


    public void grabarDatos(){
        String fecha = AppData.getFechaSystem();

        CargaCodigos codigoNew = new CargaCodigos(
                textViewCodigo.getText().toString(),
                cantidadCopias,
                tipoEtiqueta,
                AppData.getIdUsuario(),
                fecha,
                0
        );
        try {
            realm.beginTransaction();
            realm.insertOrUpdate(codigoNew);
            //realm.commitTransaction();
            //realm.close();
        }catch (RealmPrimaryKeyConstraintException re) {
            showToast("RealmPrimaryKey:" + re.getMessage(), "E");
        } finally {
            if (realm != null) {
                realm.commitTransaction();
                realm.close();
            }
        }
        limpiarCarga();
    }

    public void updateDatos(CargaCodigos codigo){
        try {
            realm.beginTransaction();
            codigo.setCantidadCopias(cantidadCopias);
            realm.copyToRealmOrUpdate(codigo);
            realm.close();
        }catch (RealmException re){
            showToast("RealException:" + re.getMessage(), "E");
        }
        finally {
            if (realm != null) {
                realm.commitTransaction();
                realm.close();
            }
        }
        limpiarCarga();
    }

    public void showInputDialog(final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(CargaEANActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.input_dialog_cantidad,null );

        final TextView editTextCantidad = (TextView) promptView.findViewById(R.id.textViewCantidadIM);

        ImageButton btnDisminuirIM = (ImageButton) promptView.findViewById(R.id.btnDisminuirIM);
        ImageButton btnAumentarIM = (ImageButton) promptView.findViewById(R.id.btnAumentarIM);
        Button buttonAceptar = (Button) promptView.findViewById(R.id.buttonAceptarCopias);

        editTextCantidad.setText(String.valueOf(cantidadCopias));

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CargaEANActivity.this);
        alertDialogBuilder.setView(promptView);
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();


        buttonAceptar.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alert.dismiss();
                        cantidadModificada = true;
                    }
                }
        );

        btnAumentarIM.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        increment();
                        editTextCantidad.setText(String.valueOf(cantidadCopias));
                    }
                }
        );

        btnDisminuirIM.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        decrement();
                        editTextCantidad.setText(String.valueOf(cantidadCopias));
                    }
                }
        );

    }

    public void clickGrabar(){
        String ean= textViewCodigo.getText().toString();

        int digitoEAN = Integer.valueOf(ean.substring(12, 13));
        int digitoCalculado = checkDigitoVerificador(ean.substring(0, 12));

        //Log.i("Digito","digitoEAN: "+ digitoEAN + " - digitoCalculado:" + digitoCalculado);

        if(digitoCalculado == digitoEAN) {
            CargaCodigos codigoExistente;
            codigoExistente = checkExist(tipoEtiqueta, ean);

            if (codigoExistente != null && !cantidadModificada) {
                showToast("El codigo ya fue ingresado, modifique las cantidades.", "I");
                showInputDialog(context);
            } else {
                if (cantidadModificada) {
                    updateDatos(codigoExistente);
                } else {
                    grabarDatos();
                }
                Intent intent = new Intent();
                intent.putExtra("accion", "1");
                setResult(24, intent);
                finish();
            }
        }else{
            showToast("El EAN leido ó ingresado es incorrecto, verifique!!.","E");
        }
    }

    public void clickFinalizar(){
        String ean= textViewCodigo.getText().toString();

        int digitoEAN = Integer.valueOf(ean.substring(12, 13));
        int digitoCalculado = checkDigitoVerificador(ean.substring(0, 12));

        if(digitoCalculado == digitoEAN) {
            CargaCodigos codigoExistente;
            codigoExistente = checkExist(tipoEtiqueta, ean);

            if (codigoExistente != null && !cantidadModificada) {
                showToast("El codigo ya fue ingresado, modifique las cantidades.", "I");
                showInputDialog(context);
            } else {
                if (cantidadModificada) {
                    updateDatos(codigoExistente);
                } else {
                    grabarDatos();
                }
                Intent intent = new Intent();
                intent.putExtra("accion", "2");
                setResult(24, intent);
                finish();
            }
        }else{
            showToast("El EAN leido ó ingresado es incorrecto, verifique!!.","E");
        }

    }

    public void limpiarCarga(){
        textViewCodigo.setText("");
        cantidadCopias = 1;
        textViewCantidad.setText(String.valueOf(cantidadCopias));
        cboxTipoEtiqueta.post(new Runnable() {
            @Override
            public void run() {
                cboxTipoEtiqueta.setSelection(0);
            }
        });
    }

    private void decrement() {
        if (cantidadCopias>1) {
            cantidadCopias--;
            textViewCantidad.setText(String.valueOf(cantidadCopias));
        }
    }

    private void increment() {
        cantidadCopias++;
        if (cantidadCopias>4){
            cantidadCopias=1;
        }
        textViewCantidad.setText(String.valueOf(cantidadCopias));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
         //Toast.makeText(parent.getContext(), "Item seleccionado " + parent.getItemAtPosition(pos).toString(), Toast.LENGTH_LONG).show();
         tipoEtiqueta = parent.getItemAtPosition(pos).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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

    public static int checkDigitoVerificador (String Input) {
        int pares = 0;
        int impares = 0;
        int checkSum;
        int pos = 1;

        for (int i = 0; i < Input.length(); i++) {
            if (pos % 2 == 0) {
               //Log.e("Par","valor: " + Input.substring(i, pos) + " - i: " + i + " - pos: "+ pos);
               pares += Integer.parseInt(Input.substring(i, pos))*3;
            } else {
               //Log.e("Inpar","valor: " + Input.substring(i, pos) + " - i: " + i + " - pos: "+ pos );
               impares += Integer.parseInt(Input.substring(i, pos))*1;
            }
            pos++;
        }

        int total = impares + pares;

        int resto = total % 10;
        int multiplo = 0;

        if(resto == 0){
            checkSum = 0;
        }else{
            int auxMultiplo = 10 - resto;
            multiplo = total + auxMultiplo;
            checkSum = multiplo - total;
        }

        //Log.e("Variables","Input: "+ Input + " - Impares: " + impares + " - Pares: " + pares + " - Resto: " + resto + " - Multiplo: " + multiplo + " - Resto: " + resto);


        return checkSum;
    }


}//end class
