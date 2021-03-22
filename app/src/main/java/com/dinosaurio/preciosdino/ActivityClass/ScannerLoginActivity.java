package com.dinosaurio.preciosdino.ActivityClass;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;

import com.dinosaurio.preciosdino.R;
import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.ViewfinderView;

import java.util.Random;

public class ScannerLoginActivity extends AppCompatActivity implements DecoratedBarcodeView.TorchListener{

    private CaptureManager capture;
    private DecoratedBarcodeView barcodeScannerView;
    private ViewfinderView viewfinderView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner_login);

        barcodeScannerView = (DecoratedBarcodeView)findViewById(R.id.barcode_scanner_login);

        barcodeScannerView.setTorchListener(this);

        viewfinderView = (ViewfinderView) findViewById(R.id.zxing_viewfinder_view);

        capture = new CaptureManager(this, barcodeScannerView);
        capture.initializeFromIntent(getIntent(), savedInstanceState);
        capture.decode();

        changeMaskColor(null);

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

    public void changeMaskColor(View view) {
        Random rnd = new Random();
        int color = Color.argb(100, rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255));
        //viewfinderView.setMaskColor(color);
        viewfinderView.setBackgroundColor(color);
    }

    @Override
    public void onTorchOn() {}

    @Override
    public void onTorchOff() {}
}
