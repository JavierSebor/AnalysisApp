package com.mundiaurum.analysisapp.uploadvideo;

import android.content.Context;
import android.net.Uri;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.mundiaurum.analysisapp.Utils;
import com.mundiaurum.analysisapp.api.Api;
import com.mundiaurum.analysisapp.domain.AnalysisFile;
import com.mundiaurum.analysisapp.events.NewVideoEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class UploadFile2Server {

    private static final String uploadServerUrl = Api.URL_UPLOAD_VIDEO_FILE;//"https://www.aurumdatos.com/DEAurum/JavaServices/borra/UploadVideoToServer.php";
    private Context context;
    private final String sourceFileUri;
    private final String uuid;
    private final String datetime;
    private final Uri uri;
    private  final Path path;
    private final String itemId;
    private final String filename;




    public UploadFile2Server(Context ctx, String sourceFileUri, String uuid, String itemId){
        this.context = ctx;
        this.sourceFileUri = sourceFileUri;
        this.uuid = uuid;
        this.itemId = itemId;
        this.uri = Uri.parse(sourceFileUri);
        this.path = Paths.get(uri.getPath());
        this.filename = path.getFileName().toString();
        this.datetime = filename.substring(4, 8) + "-" + filename.substring(8, 10) + "-" + filename.substring(10, 12) +
                " " + filename.substring(13, 15) + ":" + filename.substring(15, 17) + ":" + filename.substring(17, 19);


        //System.out.println("UploadFile2Server2.java CONSTRUCTOR !!!!!!!!!!!!!!!!!!!!!!!!!!...  " + sourceFileUri);
    }



    public void uploadBitmap() {

        final byte[] byteArrayFile;

        byteArrayFile = Utils.convertUriToByteArray(uri);
        //System.out.println("EL PATH DESDE EL CONSTRUCTOR !!!!!!!!!! " + path);
        //System.out.println("UploadFile2Server2.java DESDE EL CONSTRUCTOR !!!!!!!!!! " + datetime);

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, Api.URL_UPLOAD_VIDEO_FILE,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        //System.out.println("UpadateFile2Server onResponse (status code): " + response.statusCode);
                        String st = new String(response.data);
                        //System.out.println("UpadateFile2Server onResponse: " + st);
                        try {
                            JSONObject object = new JSONObject(st);
                            if (!object.getBoolean("error")) {
                                AnalysisFile analysisFile = new AnalysisFile(uuid, datetime, itemId, filename, "OK");
                                EventBus.getDefault().post(new NewVideoEvent(analysisFile));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                        /*
                        if(Boolean.parseBoolean(response)){
                            AnalysisFile analysisFile = new AnalysisFile(uuid, datetime, itemId, filename, "OK");
                            mAurumItemsViewModel.getMAurumItemById().getValue().getAnalysisFileList().add(analysisFile);
                            analysisFileListAdapter.notifyDataSetChanged();
                        }
                        */


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //System.out.println("ERROR AL ENVIAR EL ARCHIVO: " + error.getMessage());
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String,String> map=new HashMap<String, String>();
                map.put("uuid", uuid);
                map.put("datetime",datetime);
                map.put("itemid",itemId);
                map.put("filename",filename);
                map.put("status","OK");
                return map;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                long imagename = System.currentTimeMillis();
                params.put("file", new DataPart(filename, byteArrayFile));
                return params;
            }
        };
        //adding the request to volley
        Volley.newRequestQueue(context).add(volleyMultipartRequest);
    }



}
