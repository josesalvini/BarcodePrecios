package com.dinosaurio.preciosdino.ActivityClass;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.dinosaurio.preciosdino.Entidades.CargaCodigos;
import com.dinosaurio.preciosdino.Interfaces.AppData;
import com.dinosaurio.preciosdino.R;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;


import io.realm.Realm;
import io.realm.RealmResults;

public class ExportFileActivity extends AppCompatActivity {

    private ImageButton btnExportar;
    private Toolbar toolbarExportar;
    private Context context;

    private String nombreArchivo;
    private File directorioArchivo;
    private String pathDescarga;
    private String nombreHoja="Carga etiquetas";
    private RealmResults<CargaCodigos> listCodigos;
    private Realm realm;
    private static final int FILE_SELECT_CODE = 0;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        realm = Realm.getDefaultInstance();

        context = getBaseContext();

        btnExportar = (ImageButton) findViewById(R.id.btnExportarCarga);
        toolbarExportar = (Toolbar) findViewById(R.id.toolbarExportar);

        pathDescarga = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        directorioArchivo = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        toolbarExportar.setTitle("Exportación a carga");

        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombreArchivo = AppData.getNombreUsuario() + "_" +  AppData.getFechaCarga() + ".xls";
                saveExcelFile(nombreArchivo);
            }
        });

    }

    public void openFile2(){

        String[] mimeTypes =
                {"application/vnd.ms-excel"}; //xls
        //"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"   //xlsx
        Intent intentFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intentFile.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intentFile.setType(mimeTypes.length == 1 ? mimeTypes[0] : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            if (mimeTypes.length > 0) {
                intentFile.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intentFile.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intentFile,"ChooseFile") ,FILE_SELECT_CODE);

    }


    public static void openFile(Context context, File url) throws IOException {
        // Create URI
        File file=url;
        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Check what kind of file you are trying to open, by comparing the url with extensions.
        // When the if condition is matched, plugin sets the correct intent (mime) type,
        // so Android knew what application to use to open the file
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if(url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if(url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if(url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if(url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if(url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if(url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/x-wav");
        } else if(url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if(url.toString().contains(".jpg") || url.toString().contains(".jpeg") || url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if(url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if(url.toString().contains(".3gp") || url.toString().contains(".mpg") || url.toString().contains(".mpeg") || url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");
        } else {
            //if you want you can also define the intent type for any other file

            //additionally use else clause below, to manage other unknown extensions
            //in this case, Android will show all applications installed on the device
            //so you can choose which application to use
            intent.setDataAndType(uri, "*/*");
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent datos) {
        if (datos!=null) {
            switch (requestCode) {
                case FILE_SELECT_CODE:
                    if (resultCode == RESULT_OK) {
                        Uri uri = datos.getData();

                        Log.i("Test", "Datos " + datos.getData());
                        directorioArchivo = new File(uri.getPath());
                        Log.i("Test", "PATH " + directorioArchivo.getPath());

                        Intent xlsOpenintent = new Intent(Intent.ACTION_VIEW);
                        xlsOpenintent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        xlsOpenintent.setDataAndType(uri, "application/vnd.ms-excel");
                        try {
                            startActivity(xlsOpenintent);
                        }
                        catch (ActivityNotFoundException e) {

                        }

                    }
                    break;
            }
        }
    }

    private boolean saveExcelFile(String fileName) {

        listCodigos = realm.where(CargaCodigos.class).findAll();

        //dataBase.getViewCarga(false,0);
        boolean success = false;

        if(listCodigos.size()>0) {

            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Request for permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        1);
            }else {
                if (!isExternalStorageAvailable() || isExternalStorageReadOnly()) {
                    showToast("La unidad de almacenamiento no esta disponible ó es de solo lectura.", "E");
                    return false;
                }

                // Create a path where we will place our List of objects on external storage
                file = new File(directorioArchivo, fileName);

                //XSSFWorkbook workbook = null;

                HSSFWorkbook workbook = null;
                workbook = new HSSFWorkbook();

                HSSFCell celda = null;
                HSSFSheet hoja = workbook.createSheet(nombreHoja);
                HSSFRow row = hoja.createRow(0);

                celda = row.createCell(0);
                celda.setCellValue("CODIGO");

                celda = row.createCell(1);
                celda.setCellValue("CANTIDAD");

                celda = row.createCell(2);
                celda.setCellValue("TIPO ETIQUETA");

                int fila = 1;
                for (CargaCodigos codigos : listCodigos) {
                    HSSFRow rowDato = hoja.createRow(fila);
                    HSSFCell r2c1 = rowDato.createCell(0);
                    r2c1.setCellValue((String) codigos.getCodigoBarra());
                    HSSFCell r2c2 = rowDato.createCell(1);
                    r2c2.setCellValue((Integer) codigos.getCantidadCopias());
                    HSSFCell r2c3 = rowDato.createCell(2);
                    r2c3.setCellValue((String) codigos.getTipoEtiqueta());
                    fila += 1;
                }

                hoja.setColumnWidth(0, (15 * 500));
                hoja.setColumnWidth(1, (15 * 500));
                hoja.setColumnWidth(2, (15 * 500));


                FileOutputStream outputStream = null;

                try {
                    outputStream = new FileOutputStream(file);
                    workbook.write(outputStream);
                    showToast("Archivo guardado: " + file.getPath(), "I");
                    success = true;
                    workbook.close();
                    outputStream.close();

                } catch (IOException e) {
                    showToast(e.getMessage(), "E");
                    Log.e("IOException",  e.getMessage());
                } catch (Exception e) {
                    showToast(e.getMessage(), "E");
                    Log.e("Exception",  e.getMessage());
                } finally {
                    try {
                        if (null != outputStream)
                            outputStream.close();
                    } catch (Exception ex) {
                    }
                }
            }
        }else{
            showToast("No existen datos para exportar.","W");
            success = false;
        }
        return success;
    }

    private void openXLS(final File file) {

        //Uri fileURI = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

        String davUrl = "application/vnd.ms-excel" + file.toString();
        Uri fileURI =  Uri.parse(davUrl);

        Intent excelIntent = new Intent(Intent.ACTION_VIEW, fileURI);

        //Intent excelIntent = new Intent(Intent.ACTION_VIEW);
        //excelIntent.setDataAndType(fileURI, "application/vnd.ms-excel");
        excelIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        excelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        try {
            startActivity(excelIntent);
        } catch (ActivityNotFoundException e) {
            showToast("No Application available to viewExcel", "E");
        }




    }

    private void ReadXLSX(String fileName, File file) {

        //Uri photoURI = FileProvider.getUriForFile(context, "com.dinosaurio.preciosdino", file);
        Uri contentUri = FileProvider.getUriForFile(getApplicationContext(), "com.dinosaurio.preciosdino", file);

        Intent openExcel = new Intent(Intent.ACTION_VIEW);
        openExcel.setDataAndType(contentUri, "application/vnd.ms-excel");
        openExcel.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        openExcel.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Log.i("OPEN_FILE_PATH",  fileName);
        startActivity(openExcel);
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState)) {
            return true;
        }
        return false;
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(extStorageState)) {
            return true;
        }
        return false;
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
