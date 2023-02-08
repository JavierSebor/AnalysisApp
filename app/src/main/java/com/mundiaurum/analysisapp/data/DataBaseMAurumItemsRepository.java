package com.mundiaurum.analysisapp.data;

import android.content.Context;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mundiaurum.analysisapp.api.Api;
import com.mundiaurum.analysisapp.domain.AnalysisFile;
import com.mundiaurum.analysisapp.domain.MAurumItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DataBaseMAurumItemsRepository implements  MAurumItemsRepository{

    private  Context context;

    private static DataBaseMAurumItemsRepository sInstance;

    private final Map<String, List<MAurumItem>> map = new HashMap<>();

    private final MutableLiveData<Map<String, List<MAurumItem>>> observableMAurumItemsMap= new MutableLiveData<>();

    private final MutableLiveData<MAurumItem> observableDetailMAurumItem = new MutableLiveData<>();

    private final MutableLiveData<List<AnalysisFile>> observableAnalysisFileList = new MutableLiveData<>();

    private static List<AnalysisFile> listOfAnalysisfiles = new ArrayList<AnalysisFile>();


    public  static MAurumItemsRepository getInstance() {
        if (sInstance == null) {
            sInstance = new DataBaseMAurumItemsRepository();
        }
        return sInstance;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void loadData() {
        StringRequest request = getMAurumItemsRequest();
        RequestQueue queue= Volley.newRequestQueue(this.context);
        queue.add(request);
    }

    @NonNull
    private StringRequest getMAurumItemsRequest() {
        StringRequest request=new StringRequest(Request.Method.GET, Api.URL_GET_PENDING_PACKAGES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean("error")) {
                        //System.out.println(response);
                        parsePendingPackages(object.getJSONArray("pending_packages"));
                    }
                    else{
                        System.out.println("DataBaseMAurumItemsRepository.getMAurumItemsRequest():");
                        System.out.println(object.getString("message"));
                    }
                }catch(JSONException e) {e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        return request;
    }

    private void parsePendingPackages(JSONArray pendingPackages) throws JSONException {
        if(pendingPackages.length() > 0){
            String itemsString = "";
            for (int i = 0; i < pendingPackages.length(); i++) {
                JSONObject obj = pendingPackages.getJSONObject(i);
                itemsString = itemsString + obj.getString("idPieza") + "_";

            MAurumItem item = new MAurumItem(obj.getString("idPieza"),
                    obj.getString("idRecogida"),
                    obj.has("strPiezaMetal") ? (obj.getString("strPiezaMetal").equals("null") ? "" : obj.getString("strPiezaMetal")): "",
                    obj.has("strPiezaMetalReal") ? (obj.getString("strPiezaMetalReal").equals("null") ? "" : obj.getString("strPiezaMetalReal")): "",
                    obj.has("strPiezaPureza") ? (obj.getString("strPiezaPureza").equals("null") ? "" : obj.getString("strPiezaPureza")) : "",
                    obj.has("strPiezaPurezaReal") ? (obj.getString("strPiezaPurezaReal").equals("null") ? "0" : obj.getString("strPiezaPurezaReal")) : "0",
                    obj.has("intPiezaPeso") ? (obj.getString("intPiezaPeso").equals("null") ? "" : obj.getString("intPiezaPeso")) : "",
                    obj.has("intPiezaPesoReal") ? (obj.getString("intPiezaPesoReal").equals("null") ? "0" : obj.getString("intPiezaPesoReal")) : "0",
                    obj.has("intPiezaPrecioFinal") ? (obj.getString("intPiezaPrecioFinal").equals("null") ? "0" : obj.getString("intPiezaPrecioFinal")) : "0",
                    obj.has("intPiezaPrecioCalculadoInd") ? (obj.getString("intPiezaPrecioCalculadoInd").equals("null") ? "" : obj.getString("intPiezaPrecioCalculadoInd")) : "",
                    obj.has("intPiezaPrecioCalculadoGrupo") ? (obj.getString("intPiezaPrecioCalculadoGrupo").equals("null") ? "" : obj.getString("intPiezaPrecioCalculadoGrupo")) : "",
                    obj.has("intPiezaProcesoTerminado") ? (obj.getString("intPiezaProcesoTerminado").equals("null") ? "" : obj.getString("intPiezaProcesoTerminado")) : "",
                    obj.has("intPiezaProcesoGrupoTerminado") ? (obj.getString("intPiezaProcesoGrupoTerminado").equals("null") ? "" : obj.getString("intPiezaProcesoGrupoTerminado")) : "",
                    obj.has("strPiezaGrupo") ? (obj.getString("strPiezaGrupo").equals("null") ? "" : obj.getString("strPiezaGrupo")) : "",
                    obj.has("strPiezaTipo") ? (obj.getString("strPiezaTipo").equals("null") ? "" : obj.getString("strPiezaTipo")) : "",
                    obj.has("strPiezaDescripcionReal") ? (obj.getString("strPiezaDescripcionReal").equals("null") ? "" : obj.getString("strPiezaDescripcionReal")) : "",
                    obj.has("strPiezaFoto1") ? (obj.getString("strPiezaFoto1").equals("null") ? "" : obj.getString("strPiezaFoto1")) : "",
                    obj.has("strPiezaFoto2") ? (obj.getString("strPiezaFoto2").equals("null") ? "" : obj.getString("strPiezaFoto2")) : "",
                    obj.has("dateFechaRecogida") ? (obj.getString("dateFechaRecogida").equals("null") ? "" : obj.getString("dateFechaRecogida")) : "");

                if (!map.containsKey(item.getIdRecogida())) {
                    List<MAurumItem> mAurumItems = new ArrayList<>();
                    mAurumItems.add(item);
                    map.put(item.getIdRecogida(), mAurumItems);
                } else {
                    map.get(item.getIdRecogida()).add(item);
                }
            }
            System.out.println("DataBaseMAurumItemsRepository.parsePendingPackages(JSONArray pendingPackages). Numero de keys en el map:\n" + map.size());
            String resStrig = removeLastChar(itemsString);
            StringRequest request = getAnalysisFileRequest(resStrig);
            RequestQueue queue= Volley.newRequestQueue(this.context);
            queue.add(request);

        }
        else{
            //TODO imprimir TOAST...
        }
    }
    public static String removeLastChar(String s) {
        return (s == null || s.length() == 0)
                ? null
                : (s.substring(0, s.length() - 1));
    }

    @NonNull
    private StringRequest getAnalysisFileRequest(String inputItems) {
        StringRequest request=new StringRequest(Request.Method.GET, Api.URL_GET_ANALYSIS_FILES + inputItems, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                try {
                    JSONObject object = new JSONObject(response);
                    System.out.println(response);
                    if(object.has("analysisfiles"))
                            parseAnalysisFile(object.getJSONArray("analysisfiles"));
                    System.out.println("DataBaseMAurumItemsRepository.parseAnalysisFile(JSONArray analysisfiles). Size:\n" + listOfAnalysisfiles.size());
                    for(AnalysisFile analysisFile : listOfAnalysisfiles) {
                        for (Map.Entry<String, List<MAurumItem>> entry : map.entrySet()) {
                            for(MAurumItem ite : entry.getValue()){
                                if(ite.getIdPieza().equals(analysisFile.getItemId())){
                                    ite.getAnalysisFileList().add(analysisFile);
                                }
                            }
                        }
                    }

                    observeMAurumItemsMap();

                }catch(JSONException e) {e.printStackTrace();}
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        return request;
    }

    private void parseAnalysisFile(JSONArray analysisfiles) throws JSONException {
        for (int i = 0; i < analysisfiles.length(); i++) {
            JSONObject obj = analysisfiles.getJSONObject(i);
            AnalysisFile af = new AnalysisFile(
                    obj.getString("datetime"),
                    obj.getString("uuid"),
                    obj.getString("itemid"),
                    obj.getString("filename"),
                    obj.getString("status")
            );
            listOfAnalysisfiles.add(af);

            for (Map.Entry<String, List<MAurumItem>> entry : map.entrySet()) {
                for(MAurumItem ite : entry.getValue()){
                    if(ite.getIdPieza().equals(af.getItemId())){
                        ite.getAnalysisFileList().add(af);
                    }
                }
            }
        }
    }

    @Override
    public LiveData<Map<String, List<MAurumItem>>> observeMAurumItemsMap() {
        observableMAurumItemsMap.setValue(map);
        return observableMAurumItemsMap;
    }

    @Override
    public LiveData<MAurumItem> observeMAurumItem(String itemId) {
        for (Map.Entry<String, List<MAurumItem>> entry : map.entrySet()) {
            for(MAurumItem ite : entry.getValue()){
                if(ite.getIdPieza().equals(itemId)){
                    observableDetailMAurumItem.setValue(ite);
                    break;
                }
            }
        }
        return observableDetailMAurumItem;
    }


    public LiveData<List<AnalysisFile>> observeAnalysisFileList(String itemId){
        for (Map.Entry<String, List<MAurumItem>> entry : map.entrySet()) {
            for(MAurumItem ite : entry.getValue()){
                if(ite.getIdPieza().equals(itemId)){
                    observableAnalysisFileList.setValue(ite.getAnalysisFileList());
                    break;
                }
            }
        }
        return observableAnalysisFileList;
    }
}
