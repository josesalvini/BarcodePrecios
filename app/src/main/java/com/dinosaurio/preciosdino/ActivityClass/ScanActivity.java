package com.dinosaurio.preciosdino.ActivityClass;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.dinosaurio.preciosdino.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.Formatter;
import java.util.Random;


public class ScanActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener {

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private Button switchFlashlightButton,btnCargaManual;
    private ViewfinderView viewfinderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scanner);

        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.zxing_barcode_scanner);

        barcodeScannerView.setTorchListener(this);

        switchFlashlightButton = (Button)findViewById(R.id.switch_flashlight);
        btnCargaManual = (Button)findViewById(R.id.btnCargaManual);

        final Context context = ScanActivity.this;
        btnCargaManual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(context);
            }
        });



        viewfinderView = (ViewfinderView) findViewById(R.id.zxing_viewfinder_view);

        // if the device does not have flashlight in its camera,
        // then remove the switch flashlight button...
        if (!hasFlash()) {
            switchFlashlightButton.setVisibility(View.GONE);
        }

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        changeMaskColor(null);
    }


    public void showInputDialog(final Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(ScanActivity.this);
        final View promptView = layoutInflater.inflate(R.layout.input_dialog_ean,null );

        final EditText editText = (EditText) promptView.findViewById(R.id.etCodigoManual);
        Button buttonAceptar = (Button) promptView.findViewById(R.id.buttonAceptarCM);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ScanActivity.this);
        alertDialogBuilder.setView(promptView);
        final AlertDialog alert = alertDialogBuilder.create();
        alert.show();

        buttonAceptar.setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Long codigoManual = Long.parseLong(editText.getText().toString());
                    Formatter obj = new Formatter();
                    String numeroCeros = String.valueOf(obj.format("%013d", codigoManual));
                    if (!numeroCeros.isEmpty()) {
                        alert.dismiss();
                        Intent intent = new Intent(context, CargaEANActivity.class);
                        intent.putExtra("EAN", numeroCeros);
                        startActivityForResult(intent, 30);
                    }else{
                        showToast("No se ingreso un código." + numeroCeros, "E");
                    }
                }
            }
        );

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }
        //showToast("Result code: " + resultCode + " Resultado: " + data.getExtras().getString("accion") + " Request code: " + requestCode  , "I");

        if (requestCode == 30 && data != null && data.getExtras().getString("accion").equals("2")) {
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        capture.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        capture.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        capture.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        capture.onSaveInstanceState(outState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return barcodeScannerView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    /**
     * Check if the device's camera has a Flashlight.
     * @return true if there is Flashlight, otherwise false.
     */
    private boolean hasFlash() {
        return getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
    }

    public void switchFlashlight(View view) {
        if (getString(R.string.turn_on_flashlight).equals(switchFlashlightButton.getText())) {
            barcodeScannerView.setTorchOn();
        } else {
            barcodeScannerView.setTorchOff();
        }
    }

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
        //viewfinderView.setMaskColor(color);
        //viewfinderView.setBackgroundColor(color);
    }

    @Override
    public void onTorchOn() {
        switchFlashlightButton.setText(R.string.turn_off_flashlight);
    }

    @Override
    public void onTorchOff() {
        switchFlashlightButton.setText(R.string.turn_on_flashlight);
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


}// end class ScanActivity
