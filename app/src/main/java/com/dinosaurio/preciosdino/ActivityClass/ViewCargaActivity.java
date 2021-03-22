package com.dinosaurio.preciosdino.ActivityClass;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ListView;

import com.dinosaurio.preciosdino.ComplementosClass.QrItem;
import com.dinosaurio.preciosdino.ComplementosClass.ViewQRAdapter;
import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Entidades.Sucursal;
import com.dinosaurio.preciosdino.Entidades.TipoEtiqueta;
import com.dinosaurio.preciosdino.Interfaces.DatosPreference;
import com.dinosaurio.preciosdino.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class ViewCargaActivity extends AppCompatActivity {

    private ListView mListView;
    private QrItem itemData;
    private Bitmap bitmap;
    private ViewQRAdapter listAdapter;
    private List<QrItem> qrList;
    private RealmResults<CargaCodigos> listCodigos;
    private Toolbar viewCargaToolbar;
    private Realm realm;
    private RealmResults<TipoEtiqueta> listEtiquetas;
    private SharedPreferences sharedPreferences;
    private String sucursalUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_carga);

        mListView = (ListView) findViewById(R.id.listViewQR);

        viewCargaToolbar = findViewById(R.id.toolbarViewCarga);

        viewCargaToolbar.setTitle("Codigos ingresados");

        qrList = new ArrayList<>();
        realm = Realm.getDefaultInstance();

        listCodigos = realm.where(CargaCodigos.class).findAll();
        listEtiquetas = realm.where(TipoEtiqueta.class).equalTo("habilitado",1).findAll();

        String sucursalId;
        String urlReporte;
        sharedPreferences = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        sucursalUser = DatosPreference.getSucursal(sharedPreferences);

        sucursalId = this.getIdSucursal(sucursalUser);

        for (CargaCodigos carga : listCodigos)
        {
            urlReporte = this.getURL_Etiqueta(carga.getTipoEtiqueta());
            //Concatenar url con parametros
            //urlReporte += "&Sucursal=" + sucursalId.toLowerCase() + "&EAN=" + carga.getCodigoBarra() + "&cant=" + carga.getCantidadCopias()+ ".jscan";

            urlReporte += sucursalId.toLowerCase() + "/" + carga.getCodigoBarra() + "/" + carga.getCantidadCopias()+ "/.jscan";

            if(TextUtils.isEmpty(urlReporte)){urlReporte="Error";}

            bitmap = this.getBitMapImage(urlReporte);

            itemData = new QrItem(bitmap,
                    carga.getCodigoBarra(),
                    carga.getTipoEtiqueta(),
                    String.valueOf(carga.getCantidadCopias()));
            qrList.add(itemData);
        }

        listAdapter = new ViewQRAdapter(ViewCargaActivity.this,  qrList);
        mListView.setAdapter(listAdapter);

    }

    private String getIdSucursal(String sucursalUser) {
        Sucursal sucursal = realm.where(Sucursal.class).equalTo("abreviatura",sucursalUser).findFirst();
        return sucursal.getIdSucursal();

    }


    public String getURL_Etiqueta(String tipoEtiqueta){
        String urlEtiqueta="";
        for (TipoEtiqueta etiqueta : listEtiquetas) {
            if (etiqueta.getNombre().equals(tipoEtiqueta)){
                urlEtiqueta = etiqueta.getUrl();
                break;
            }
        }
        return urlEtiqueta;
    }

    /*
    public Bitmap getBitMapImage(String datos){
        Bitmap bitmap=null;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{

            Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

            hintMap.put(EncodeHintType.MARGIN, 1);
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

            BitMatrix bitMatrix = multiFormatWriter.encode(datos, BarcodeFormat.QR_CODE,300,300,hintMap);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            bitmap = barcodeEncoder.createBitmap(bitMatrix);

        }catch (WriterException e){
                e.printStackTrace();
        }
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        return bitmap;
    }
    */


    public Bitmap getBitMapImage(String datos){
        BitMatrix result;

        Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        hintMap.put(EncodeHintType.MARGIN, 1);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        try {
            result = new MultiFormatWriter().encode(datos, BarcodeFormat.QR_CODE, 300, 300, hintMap);
        } catch (WriterException iae) {
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 300, 0, 0, w, h);
        return bitmap;

    }


}
