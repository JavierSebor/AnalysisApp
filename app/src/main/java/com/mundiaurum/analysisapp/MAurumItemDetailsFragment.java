package com.mundiaurum.analysisapp;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mundiaurum.analysisapp.api.Api;
import com.mundiaurum.analysisapp.api.MetalPrices;
import com.mundiaurum.analysisapp.databinding.AnalysisFilesListBinding;
import com.mundiaurum.analysisapp.databinding.FragmentMaurumitemDetailsBinding;
import com.mundiaurum.analysisapp.domain.AnalysisFile;
import com.mundiaurum.analysisapp.uploadvideo.BetterActivityResult;
import com.mundiaurum.analysisapp.events.NewVideoEvent;
import com.mundiaurum.analysisapp.uploadvideo.UploadFileIntentService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MAurumItemDetailsFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private FragmentMaurumitemDetailsBinding binding;
    private MAurumItemsViewModel mAurumItemsViewModel;
    private AnalysisFileListAdapter analysisFileListAdapter;
    private AlertDialog.Builder deleteFileDialogBuilder;
    private static String itemId;
    private Bundle args;
    private String encodedimage;
    private boolean generalDataExpanded;
    private  boolean inputDataExpanded;
    private boolean analysisDataExpanded;
    private String tempDateTime;
    private final BetterActivityResult<Intent, ActivityResult> activityLauncher = BetterActivityResult.registerActivityForResult(this);

    private  String initialMetal = "";
    private  String initialPurity = "";
    private  String initialWeight = "";
    private  String initialComment = "";

    private boolean dirtyMetal = false;
    private boolean dirtyWeight = false;
    private boolean dirtyPurity = false;
    private boolean dirtyComment = false;
    private boolean dirty = false;

    private boolean metalDataValid = false;
    private boolean purityDataValid = false;
    private boolean weightDataValid = false;

    private MetalPrices metalPrices;

    //private MAurumItemsExpandableListAdapter adap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        args = getArguments();
        itemId = args.getString("itemId");
        generalDataExpanded = false;
        inputDataExpanded = true;
        analysisDataExpanded = true;
        ((MainActivity)getActivity()).getSupportActionBar().setTitle("Item " + itemId);
        binding = FragmentMaurumitemDetailsBinding.inflate(inflater, container, false);

        mAurumItemsViewModel = new ViewModelProvider(getActivity()).get(MAurumItemsViewModel.class);

        // Conectar Binding con ViewModel
        binding.setViewModel(mAurumItemsViewModel);
        binding.setLifecycleOwner(getViewLifecycleOwner());

        return binding.getRoot();
    }


    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        metalPrices = MetalPrices.getInstance();
        binding.expandableLayout.setVisibility(View.GONE);
        mAurumItemsViewModel.loadMAurumItemDetail(itemId);
        mAurumItemsViewModel.getAnalysisfiles().observe(getViewLifecycleOwner(), new Observer<List<AnalysisFile>>() {
            @Override
            public void onChanged(List<AnalysisFile> items) {
                analysisFileListAdapter = new AnalysisFileListAdapter(items);
                binding.listViewAnalysisFiles.setAdapter(analysisFileListAdapter);
            }});

        //Spinner with the metal values
        binding.metalRealValue.setOnItemSelectedListener(this);
        deleteFileDialogBuilder = new AlertDialog.Builder(getActivity());
        //saveItemDialogBuilder = new AlertDialog.Builder(getActivity());
        binding.generalData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setGeneralDataExpanded(!generalDataExpanded);
                binding.expandableLayout.setVisibility(generalDataExpanded ? View.VISIBLE : View.GONE);
                binding.imageButton.setImageDrawable(generalDataExpanded ? ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_drop_up_24) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_arrow_drop_down_24));
            }
        });
        binding.inputData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInputDataExpanded(!inputDataExpanded);
                binding.expandableLayoutInputData.setVisibility(inputDataExpanded ? View.VISIBLE : View.GONE);
                binding.imageButtonInputData.setImageDrawable(inputDataExpanded ? ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_drop_up_24) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_arrow_drop_down_24));
            }
        });
        binding.unboxing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAnalysisDataExpanded(!analysisDataExpanded);
                binding.expandableLayoutUnboxing.setVisibility(analysisDataExpanded ? View.VISIBLE : View.GONE);
                binding.imageButtonUnboxing.setImageDrawable(analysisDataExpanded ? ContextCompat.getDrawable(requireActivity(), R.drawable.ic_baseline_arrow_drop_up_24) : ContextCompat.getDrawable(getActivity(), R.drawable.ic_baseline_arrow_drop_down_24));
            }
        });

        binding.picture1Value.setPaintFlags(binding.picture1Value.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        binding.picture1Value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle argsPicture1 = new Bundle();
                argsPicture1.putString("apiUrl", Api.PICTURES_URL);
                argsPicture1.putString("fileName", binding.picture1Value.getText().toString());
                NavHostFragment.findNavController(MAurumItemDetailsFragment.this)
                        .navigate(R.id.action_SecondFragment_to_ImageVierwerFragment, argsPicture1);
            }
        });
        binding.picture2Value.setPaintFlags(binding.picture2Value.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        binding.picture2Value.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle argsPicture2 = new Bundle();
                argsPicture2.putString("apiUrl", Api.PICTURES_URL);
                argsPicture2.putString("fileName", binding.picture2Value.getText().toString());
                NavHostFragment.findNavController(MAurumItemDetailsFragment.this)
                        .navigate(R.id.action_SecondFragment_to_ImageVierwerFragment, argsPicture2);
            }
        });

        binding.weightRealValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //System.out.println("weight => initial: " + initialWeight + "\tCurrent: " + charSequence);
                if(charSequence.toString().equals(initialWeight)){
                    dirtyWeight = false;
                }
                else{
                    dirtyWeight = true;
                }
                checkDirty();
                if(!charSequence.toString().matches("^\\d{1,4}(\\.\\d{0,2})?$")){
                    binding.weightRealValue.setBackgroundResource(R.color.red);
                    weightDataValid = false;
                }
                else{
                    binding.weightRealValue.setBackgroundResource(R.color.gray);
                    weightDataValid = true;
                }
                calculatePrice();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.purityRealValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //System.out.println("Purity => initial: " + initialPurity + "\tCurrent: " + charSequence);
                if(charSequence.toString().equals(initialPurity)){
                    dirtyPurity = false;
                }
                else{
                    dirtyPurity = true;
                }
                checkDirty();
                if(!charSequence.toString().matches("^[0-9]{1,3}?$")){
                    binding.purityRealValue.setBackgroundResource(R.color.red);
                    purityDataValid = false;
                }
                else{
                    binding.purityRealValue.setBackgroundResource(R.color.gray);
                    purityDataValid = true;
                }
                calculatePrice();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        binding.metalRealValue.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String metal = adapterView.getItemAtPosition(i).toString();
                //System.out.println("@@@@ METAL CHANGED: " + metal);
                if(metal.equals(initialMetal)){
                    dirtyMetal = false;
                }
                else{
                    dirtyMetal = true;
                }
                checkDirty();
                metalDataValid = true;
                if(metal.equals(""))
                    metalDataValid = false;
                calculatePrice();

            }
            @Override public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        binding.commentValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //System.out.println("Comment => initial: " + initialComment + "\tCurrent: " + charSequence);
                if(charSequence.toString().equals(initialComment)){
                    dirtyComment = false;
                }
                else{
                    dirtyComment = true;
                }
                checkDirty();
            }
            @Override
            public void afterTextChanged(Editable editable) {}
        });

        //todo
        //TODO
        //TODO
        //TODO
        binding.newVideoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("Entra en onClick...VIDEO");
                createImageVideo(MediaStore.ACTION_VIDEO_CAPTURE);
            }
        });

        binding.newImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("Entra en onClick...CREATE NEW IMAGE");
                createImageVideo(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        });

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveItem();
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);


    }

    private void saveItem() {
        String procesoTerminado =
                (isNumeric(binding.itemPriceValue.getText().toString()) &&
                 isNumeric(binding.weightRealValue.getText().toString()) &&
                 isNumeric(binding.itemPriceValue.getText().toString()) &&
                 binding.metalRealValue.getSelectedItem().toString() != "" )? "1" : "0";
        Map<String,String> map=new HashMap<String, String>();
        map.put("itemId", itemId);
        map.put("strPiezaMetalReal",binding.metalRealValue.getSelectedItem().toString());
        map.put("intPiezaPeso",binding.weightRealValue.getText().toString());
        map.put("intPiezaPureza",binding.purityRealValue.getText().toString());
        map.put("intPiezaCotizacionActual", (String) metalPrices.getPrices().get(binding.metalRealValue.getSelectedItem().toString()));
        map.put("intPiezaPrecioFinal",binding.itemPriceValue.getText().toString());
        map.put("strPiezaDescripcionReal",binding.commentValue.getText().toString());
        map.put("intPiezaProcesoTerminado",procesoTerminado);

        String params = Utils.generateParams(map);
        StringRequest request=new StringRequest(Request.Method.GET, Api.URL_SAVE_ITEM + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                //System.out.println("RESPUESTA DEL SERVIDOR A SAVE ITEM: " + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean("error")) {
                        //todo HACER TOAST:
                        mAurumItemsViewModel.getMAurumItemById().getValue().
                                setStrPiezaMetalReal(binding.metalRealValue.getSelectedItem().toString());
                        mAurumItemsViewModel.getMAurumItemById().getValue().
                                setIntPiezaPesoReal(binding.weightRealValue.getText().toString());
                        mAurumItemsViewModel.getMAurumItemById().getValue().
                                setIntPiezaPurezaReal(binding.purityRealValue.getText().toString());
                        mAurumItemsViewModel.getMAurumItemById().getValue().
                                setIntPiezaPrecioFinal(binding.itemPriceValue.getText().toString());
                        mAurumItemsViewModel.getMAurumItemById().getValue().
                                setStrPiezaDescripcionReal(binding.commentValue.getText().toString());
                        mAurumItemsViewModel.getMAurumItemById().getValue().
                                setIntPiezaProcesoTerminado(procesoTerminado);
                        dirty = false;
                        dirtyPurity = false;
                        dirtyWeight = false;
                        dirtyMetal = false;
                        dirtyComment = false;
                        binding.saveButton.setEnabled(false);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //todo HACER TOAST: Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue= Volley.newRequestQueue(this.getContext());
        queue.add(request);
    }

    private void calculatePrice() {
        if((metalDataValid && purityDataValid && weightDataValid)){
            String res = Utils.calculateItemPrice(metalPrices.getPrices(),
                                     binding.metalRealValue.getSelectedItem().toString(),
                                     binding.weightRealValue.getText().toString(),
                                     binding.purityRealValue.getText().toString());
            binding.itemPriceValue.setText(res);
        }
        else {
            binding.itemPriceValue.setText("No value");
        }
    }

    private Pattern isNumericPattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        return isNumericPattern.matcher(strNum).matches();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        ArrayAdapter ad = ArrayAdapter.createFromResource(this.getContext(), R.array.metals, android.R.layout.simple_spinner_item);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.metalRealValue.setAdapter(ad);
        binding.metalRealValue.setSelection(ad.getPosition(mAurumItemsViewModel.getMAurumItemById().getValue().getStrPiezaMetalReal()));

        initialPurity = binding.purityRealValue.getText().toString();
        initialWeight = binding.weightRealValue.getText().toString();

        initialMetal = binding.metalRealValue.getSelectedItem().toString();
        initialComment = binding.commentValue.getText().toString();
        //Hay que dejar estas dos lineas para el boton SAVE disabled al inicio
        dirty = false;
        dirtyPurity = false;
        dirtyWeight = false;
        dirtyMetal = false;
        dirtyComment = false;
        binding.saveButton.setEnabled(false);
        super.onResume();
    }

    public void setGeneralDataExpanded(boolean generalDataExpanded) {
        this.generalDataExpanded = generalDataExpanded;
    }

    public void setInputDataExpanded(boolean inputDataExpanded) {
        this.inputDataExpanded = inputDataExpanded;
    }

    public void setAnalysisDataExpanded(boolean analysisDataExpanded){
        this.analysisDataExpanded =analysisDataExpanded;
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //System.out.println("ON ITEM SELECTED EN EL SPINNER...  " + i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //System.out.println("MAurumItemDetailsFragment.onNothingSelected: EN EL SPINNER...  ");
    }

    public  class  AnalysisFileListAdapter extends ArrayAdapter<AnalysisFile>{
        List<AnalysisFile> analysisFileList;
        private LayoutInflater mLayoutInflater;
        public AnalysisFileListAdapter(List<AnalysisFile> analysisFileList) {
            super(getActivity(), R.layout.analysis_files_list, analysisFileList);
            this.analysisFileList = analysisFileList;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
          AnalysisFilesListBinding analysisFilesListBinding;
            if (convertView == null) {
                if(mLayoutInflater == null){
                    mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                }
                analysisFilesListBinding = AnalysisFilesListBinding.inflate(mLayoutInflater, parent, false);
                convertView = analysisFilesListBinding.getRoot();
                convertView.setTag(analysisFilesListBinding);
            }
            else{
                analysisFilesListBinding = (AnalysisFilesListBinding) convertView.getTag();
            }
            analysisFilesListBinding.fileName.setText(analysisFileList.get(position).getFileName());
            analysisFilesListBinding.viewAnalysisFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle analysisFileArgs = new Bundle();
                    analysisFileArgs.putString("apiUrl", Api.ANALYSIS_FILE_URL);
                    analysisFileArgs.putString("fileName", analysisFileList.get(position).getFileName());
                    //System.out.println("NOMBRE DE LA IMAGEN A VISUALIZAR: " + analysisFileList.get(position).getFileName());
                    switch (analysisFileList.get(position).getFileName().split("\\.")[1]){
                        case "jpg":
                            //System.out.println("EL ARCHIVO ES JPG");
                            NavHostFragment.findNavController(MAurumItemDetailsFragment.this)
                                    .navigate(R.id.action_SecondFragment_to_ImageVierwerFragment, analysisFileArgs);
                            break;
                        case "mp4":
                            //System.out.println("EL ARCHIVO ES MP4");
                            NavHostFragment.findNavController(MAurumItemDetailsFragment.this)
                                    .navigate(R.id.action_SecondFragment_to_VideoVierwerFragment, analysisFileArgs);
                            break;
                        default:
                            //System.out.println("NO SE SABE QUE TIPO DE ARCHIVO ES...");
                    }
                }
            });
            analysisFilesListBinding.deleteAnalysisFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Uncomment the below code to Set the message and title from the strings.xml file
                    deleteFileDialogBuilder.setMessage("Do you want to delete the Analysis File?") .setTitle("Delete Analysis File");
                    //Setting message manually and performing action on button click
                    deleteFileDialogBuilder.setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    StringRequest request=new StringRequest(Request.Method.POST, Api.URL_DELETE_ANALYSIS_FILE + analysisFileList.get(position).getFileName(), new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response){
                                            try {
                                                JSONObject object = new JSONObject(response);
                                                if (!object.getBoolean("error")) {
                                                    mAurumItemsViewModel.getMAurumItemById().getValue().getAnalysisFileList().remove(position);
                                                    analysisFileListAdapter.notifyDataSetChanged();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Toast.makeText(getActivity(),error.toString(),Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    RequestQueue queue= Volley.newRequestQueue(getActivity());
                                    queue.add(request);
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = deleteFileDialogBuilder.create();
                    //Setting the title manually
                    //alert.setTitle("AlertDialogExample");
                    alert.show();
                }
            });
           return analysisFilesListBinding.getRoot();
        }
    }

    private void createImageVideo(String imageVideoAction) {
        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        Intent intent=new Intent(imageVideoAction);
                        switch(imageVideoAction){
                            case MediaStore.ACTION_VIDEO_CAPTURE:
                                openVideoActivityForResult(intent, imageVideoAction);
                                break;
                            case MediaStore.ACTION_IMAGE_CAPTURE:
                                Intent imageIntent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                //TODO CAMBIAR ESTA FUNCION DEPRECADA...
                                startActivityForResult(imageIntent,111);
                                //openImageActionForResults();
                                break;
                        }
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        //System.out.println("Entra en onPermissionDenied...");
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        //System.out.println("Entra en onPermissionRationaleShouldBeShown...");
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public void openVideoActivityForResult(Intent intent, String imageVideoAction) {
        activityLauncher.launch(intent, result -> {
            Uri imageVideoUri;
            if (result.getResultCode() == RESULT_OK) {
                Intent data = result.getData();
                imageVideoUri = data.getData();
                //System.out.println("Action: MediaStore.ACTION_VIDEO_CAPTURE...");
                String path = Utils.getRealPathFromURI(getActivity(), imageVideoUri);
                //System.out.println("El PATH DEL VIDEO CREADO: " + path);
                Intent imageVideoIntent = new Intent(null, imageVideoUri, getActivity(), UploadFileIntentService.class);
                imageVideoIntent.putExtra("uri", path);
                imageVideoIntent.putExtra("itemId", itemId);

                getActivity().startService(imageVideoIntent);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Bitmap bitmap;
        if(requestCode==111 && resultCode == RESULT_OK){
            bitmap=(Bitmap)data.getExtras().get("data");
            encodebitmap(bitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void encodebitmap(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[] byteofimages=byteArrayOutputStream.toByteArray();
        encodedimage=android.util.Base64.encodeToString(byteofimages, Base64.DEFAULT);
        uploadtoserver();
    }

    private void uploadtoserver(){
        final String uuid = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
        final String datetime = getDateTime();
        final String itemid = itemId;
        final String filename = createImageFileName();
        StringRequest request=new StringRequest(Request.Method.POST, Api.URL_UPLOAD_IMAGE_FILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                System.out.println("RESPUESTA DEL SERVIDO A UPLOAD IMAGE FILE: " + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean("error")) {
                        AnalysisFile analysisFile = new AnalysisFile(uuid, datetime, itemid, filename, "OK");
                        mAurumItemsViewModel.getMAurumItemById().getValue().getAnalysisFileList().add(analysisFile);
                        analysisFileListAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(getApplicationContext(),error.toString(),Toast.LENGTH_SHORT).show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> map=new HashMap<String, String>();
                map.put("uuid", uuid);
                map.put("datetime",datetime);
                map.put("itemid",itemid);
                map.put("filename",filename);
                map.put("status","OK");
                map.put("upload",encodedimage);
                return map;
            }
        };

        RequestQueue queue= Volley.newRequestQueue(this.getContext());
        queue.add(request);
    }

    private String createImageFileName() {
        return "IMG_" + tempDateTime + ".jpg";
    }

    private String getDateTime() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        DateFormat dateFormat2 = new SimpleDateFormat("yyyyMMdd_HHmmss");
        tempDateTime = dateFormat2.format(date);
        return dateFormat.format(date);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNewVideoEvent(NewVideoEvent event) {
        //System.out.println("PROCESANDO EL MENSAJE EVENT!!!!!!!!!!!!!!!!!!!!!!!!!! ¬¬¬¬¬¬¬¬¬¬¬");
        AnalysisFile af = event.getAnalysisFile();
        mAurumItemsViewModel.getMAurumItemById().getValue().getAnalysisFileList().add(af);
        analysisFileListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void checkDirty() {
        //System.out.println("Dirty Purity: " + dirtyPurity + "\tDirty Weight: "+ dirtyWeight + "\tDirty Comment" + dirtyComment + "\tDirty Metal" + dirtyMetal);
        dirty = dirtyPurity || dirtyWeight || dirtyComment || dirtyMetal;
        binding.saveButton.setEnabled(dirty);
    }




}