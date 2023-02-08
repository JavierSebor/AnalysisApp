package com.mundiaurum.analysisapp.api;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MetalPrices {

    private static MetalPrices instance;

    private static Map<String, String> prices = new HashMap<String,String>();

    public static MetalPrices getInstance() {
        if (instance == null)
            instance = new MetalPrices();
        return instance;
    }

    public void updateMetalPrices() {
        GetMetalPricesRequest request = new GetMetalPricesRequest(Api.URL_GET_METAL_PRICES);
        request.execute();

    }

    public Map getPrices(){ return prices; }



    private class GetMetalPricesRequest extends AsyncTask<Void, Void, String> {
        String url;

        public GetMetalPricesRequest(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //System.out.println("GetMetalPricesRequest -> OnPostExecute()...\nla String a pasar es: " + s);
            try {
                JSONObject object = new JSONObject(s);
                if (!object.getBoolean("error")) {
                    //System.out.println("Entra a refrescar los datos...");
                    refreshMetalPrices(object.getJSONArray("metalprices"));
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

    private void refreshMetalPrices(JSONArray metalprices) throws JSONException {
        for (int i = 0; i < metalprices.length(); i++) {
            JSONObject obj = metalprices.getJSONObject(i);
            prices.put(obj.getString("strCotizacionMetal"), obj.getString("intCotizacionPrecio"));
        }
        System.out.println("MetalPrices.refreshMetalPrices(JSONArray metalprices). Numero de metales obtenido: \n" + prices.size());
    }
}
