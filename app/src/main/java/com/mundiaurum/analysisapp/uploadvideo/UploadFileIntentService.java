package com.mundiaurum.analysisapp.uploadvideo;

import android.Manifest;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.annotation.Nullable;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class UploadFileIntentService extends IntentService {
    private String uri;
    private String itemId;
    /**
     * @deprecated
     */
    public UploadFileIntentService() {
        super("my_intent_thread");
    }
    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String uuid = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        //System.out.println("UploadFileIntentService.java => Enetro en onHandleEvent...URI: "+ uri);
        createReadWritePermission(getApplicationContext());
        //UploadFile2Server up = new UploadFile2Server(uri);
        UploadFile2Server up2 = new UploadFile2Server(getApplicationContext(), uri, uuid, itemId);
        up2.uploadBitmap();
        //int resp = up.uploadFile();
        //System.out.println("La respuesta del servidor: " + resp);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //Esto es si se quiere añadir datos extra...
        //uri = intent.getStringExtra("uri");
        uri = intent.getStringExtra("uri");
        itemId = intent.getStringExtra("itemId");
        //Toast.makeText(UploadFileIntentService.this, "service started", Toast.LENGTH_LONG).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy(){
        //Toast.makeText(UploadFileIntentService.this, "service destroyed", Toast.LENGTH_LONG).show();
        super.onDestroy();
    }

    private void createReadWritePermission(Context ctx) {
        Dexter.withContext(ctx)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        //System.out.println("Leer archivos...Entra en onPermissionGranted...");
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        //System.out.println("Leer archivos...Entra en onPermissionDenied...");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        //System.out.println("Leer archivos...Entra en onPermissionRationaleShouldBeShown...");
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}
