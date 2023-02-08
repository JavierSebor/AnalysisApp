package com.mundiaurum.analysisapp;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mundiaurum.analysisapp.api.Api;
import com.mundiaurum.analysisapp.api.MetalPrices;
import com.mundiaurum.analysisapp.api.RequestHandler;
import com.mundiaurum.analysisapp.data.DataBaseMAurumItemsRepository;
import com.mundiaurum.analysisapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private static String androidId;
    private boolean validDevice = true;

    private MAurumItemsViewModel mAurumItemsViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Get device permissions
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        //System.out.println("MainActivity.onCreate()...El UUID es: " + androidId);

        //Uncommnent in production
        getUuidPermission(androidId);

        // Obtener instancia de view model
        MAurumItemsViewModel.Factory factory = new MAurumItemsViewModel.Factory(
                this,
                null,
                DataBaseMAurumItemsRepository.getInstance(), getApplicationContext());
                //TestMAurumItemsRepository.getInstance(), getApplicationContext());
        mAurumItemsViewModel = new ViewModelProvider(this, factory).get(MAurumItemsViewModel.class);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //Obtain metal prices from web
        MetalPrices metalPrices = MetalPrices.getInstance();
        metalPrices.updateMetalPrices();
/*
        //Floating action button.
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Updating prices...", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                MetalPrices metalPrices = MetalPrices.getInstance();
                metalPrices.updateMetalPrices();
            }
        });
 */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //HE COMENTADO LO DE ABAJO QUE HACE DESAPARCER EL MENU => SETTINGS DEL ACTION BAR
        //getMenuInflater().inflate(R.menu.menu_main, menu);

        //Estas dos lineas eliminan la flecha del toolbar de la la pantalla inicial
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                //System.out.println("MainActivity.onOptionsItemSelected()...  BACK BUTTON PRESSED DEL TOOLBAR");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    protected void onDestroy() {
        System.out.println("MainActivity -> onDestroy()...");
        //if(validDevice) {
            //To uncomment after tests
        //NO SE IMPLEMENTA logDeviceToDB -> logout,  ya que onDestroy está marcado como killable,
        //por o que no se ejecuta siempre, onStop() pasa lo mismo.
         //   logDeviceToDB(androidId, "logout");
       // }
        super.onDestroy();
    }

    private void getUuidPermission(String androidId) {
        PerformPermissionRequest request = new PerformPermissionRequest(Api.URL_GET_UUID_PERMISSION + androidId, null);
        request.execute();
    }

    public class PerformPermissionRequest extends AsyncTask<Void, Void, String> {
        String url;
        HashMap<String, String> params;

        PerformPermissionRequest(String url, HashMap<String, String> params) {
            this.url = url;
            this.params = params;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //System.out.println("PerformPermissionRequest.OnPostExecute()... Datos recibidos: " + s);
            try {
                JSONObject object = new JSONObject(s);
                if (object.getBoolean("error")) {
                    validDevice = false;
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    //System.out.println("PerformPermissionRequest.OnPostExecute()...LA APLICACION SE CERRARA...");
                    //To uncomment after tests
                    logDeviceToDB(androidId, "denied");
                    //
                    if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 21) {
                        finishAffinity();
                    } else if (Build.VERSION.SDK_INT >= 21) {
                        finishAndRemoveTask();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    //System.out.println("PerformPermissionRequest.onPostExecute()...La aplicación ha chequeado el UUID y es correcto...");
                    //To uncomment after tests
                    logDeviceToDB(androidId, "login");
                    //
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendGetRequest(url);
        }
    }

    void logDeviceToDB(String uuid, String deviceAction){
        //System.out.println("MainActivitiy.logDeviceToDB. ########  ENTRA EN logDevice  #######");
        String url = Api.URL_POST_LOG_DEVICE + "&deviceuuid=" + uuid + "&deviceaction=" + deviceAction;
        StringRequest logRequest = new StringRequest(
                Request.Method.GET,
                url,new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean("error")) {
                        Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Logged error", Toast.LENGTH_SHORT).show();
                    }
                    //System.out.println("MainActivitiy.logDeviceToDB. Mensaje recibido: " +object.getString("message"));
                }catch(JSONException e) {e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue logQueue = Volley.newRequestQueue(getApplicationContext());
        logQueue.add(logRequest);

    }
}