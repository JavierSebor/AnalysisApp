package com.mundiaurum.analysisapp;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mundiaurum.analysisapp.api.Api;
import com.mundiaurum.analysisapp.databinding.ExpandableItemBinding;
import com.mundiaurum.analysisapp.databinding.ListPackagesBinding;
import com.mundiaurum.analysisapp.domain.MAurumItem;
import com.mundiaurum.analysisapp.events.SendProposalEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MAurumItemsExpandableListAdapter extends BaseExpandableListAdapter {

    private LayoutInflater mLayoutInflater;
    private FragmentActivity context;
    NavController navController;
    //private List<MAurumItem> items;
    private Map<String, List<MAurumItem>> itemsMap;
    //private List<String> expandableListTitle;
    //private Map<String, List<MAurumItem>> expandableListDetail;

    private EventBus eventbus;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MAurumItemsExpandableListAdapter(EventBus eventbus, FragmentActivity activity, NavController navController, Map<String, List<MAurumItem>> itemsMap) {
        this.context = activity;
        this.navController = navController;
        //this.items = items;
        this.itemsMap = itemsMap;
        this.eventbus = eventbus;
        //this.expandableListTitle = new ArrayList<String>(this.itemsMap.keySet());


        //System.out.println("SE GENERA UN NUEVO ADAPTER!!!!!!!_______");
        //Map<String, List<MAurumItem>> mapOfItems = getMap(items);
        //List<String> expListTitle = new ArrayList<String>(mapOfItems.keySet());
        //this.expandableListTitle = expListTitle;
        //this.expandableListDetail = getMap(items);
        //System.out.println("MAurumItemsExpandableListAdapter -> constructor -> EL MAP DE RECOGIDAS DEL ADAPTER: " + mapOfItems.size());
    }

    @Override
    public Object getChild(int listPosition, int expandedListPosition) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());

        List<MAurumItem> items = itemsMap.get(keys.get(listPosition));
        //System.out.println("ENTRA EN GET CHILD...ADAPTER: expandedListPosition: " + expandedListPosition + itemsMap.size());
        //System.out.println("TAMAÑO DE LISTITEMS: " + items.size());
        return  items.get((expandedListPosition));

    }

    @Override
    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }

    @Override
    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final String expandedItemId = ((MAurumItem) getChild(listPosition, expandedListPosition)).getIdPieza();
        ExpandableItemBinding bindingPackages;
        if (convertView == null) {
            if(mLayoutInflater == null){
                mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            bindingPackages = ExpandableItemBinding.inflate(mLayoutInflater, parent, false);
            convertView = bindingPackages.getRoot();
            convertView.setTag(bindingPackages);
        }
        else{
            bindingPackages = (ExpandableItemBinding) convertView.getTag();
        }
        bindingPackages.setMAurumItem((MAurumItem)getChild(listPosition, expandedListPosition));
        bindingPackages.textViewItemEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("Click en el boton de Edit Item..." + expandedItemId);
                select(((MAurumItem) getChild(listPosition, expandedListPosition)));
                Bundle args = new Bundle();
                args.putString("itemId", expandedItemId);
                navController.navigate(R.id.action_FirstFragment_to_SecondFragment, args);
            }
        });
        return bindingPackages.getRoot();
    }

    @Override
    public int getChildrenCount(int listPosition) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        return  itemsMap.get(keys.get(listPosition)).size();
    }

    @Override
    public Object getGroup(int listPosition) {
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        return itemsMap.get(keys.get(listPosition));
    }

    @Override
    public int getGroupCount() {
        return itemsMap.size();
    }

    @Override
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        ListPackagesBinding bindingPackages;
        if (convertView == null) {
            if(mLayoutInflater == null){
                mLayoutInflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            bindingPackages = ListPackagesBinding.inflate(mLayoutInflater, parent, false);
            convertView = bindingPackages.getRoot();
            convertView.setTag(bindingPackages);
        }
        else{
            bindingPackages = (ListPackagesBinding) convertView.getTag();
        }
        //TODO
        //Estas 3 lineas son para mostrar siempre los items expandidos
        //ExpandableListView mExpandableListView = (ExpandableListView) parent;
        //mExpandableListView.expandGroup(listPosition);

        bindingPackages.setMAurumItem((MAurumItem)getChild(listPosition, 0));


        if(isPackageReady(listPosition)){
            bindingPackages.sendProposalButton.setVisibility(View.VISIBLE);


            List<String> keys = new ArrayList<>(itemsMap.keySet());
            List<MAurumItem> envio = itemsMap.get(keys.get(listPosition));


            //List<MAurumItem> envio = itemsMap.get( ((List<String>) itemsMap.keySet()).get(listPosition));
            double finalPrice = 0.0;
            for(MAurumItem item : envio){
                finalPrice += Double.parseDouble(item.getIntPiezaPrecioFinal());
            }
            bindingPackages.packagePrice.setText(String.valueOf(finalPrice));
        }
        else{
            bindingPackages.sendProposalButton.setVisibility(View.INVISIBLE);
            bindingPackages.packagePrice.setText("#.##");
        }


        bindingPackages.sendProposalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //System.out.println("MAurumExpandableListAdapter.CLICK ON SEND PROPOSAL...");
                List<String> keys = new ArrayList<>(itemsMap.keySet());

                List<MAurumItem> items = itemsMap.get(keys.get(listPosition));

                sendProposal(items.get(0).getIdRecogida(), bindingPackages.packagePrice.getText().toString(), listPosition);
            }
        });

        return bindingPackages.getRoot();
    }

    private void sendProposal(String envioId, String propuestaImporte, int listPosition) {
        Map<String,String> map=new HashMap<String, String>();
        map.put("strEnvioId", envioId);
        map.put("strPropuestaImporte", propuestaImporte);

        String params = Utils.generateParams(map);
        StringRequest request=new StringRequest(Request.Method.GET, Api.URL_SEND_PROPOSAL + params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response){
                System.out.println("RESPUESTA DEL SERVIDOR A SEND PROPOSAL: " + response.toString());
                try {
                    JSONObject object = new JSONObject(response);
                    if (!object.getBoolean("error")) {
                        //TODO HACER TOAST:
                        List<String> keys = new ArrayList<>(itemsMap.keySet());
                        List<MAurumItem> items = itemsMap.get(keys.get(listPosition));
                        eventbus.post(new SendProposalEvent(items.get(0).getIdRecogida()));
                        //System.out.println("RESPUESTA DEL SERVIDOR A SEND PROPOSAL: @@@@@@@     ES CORRECTO!!!!!");
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
        RequestQueue queue= Volley.newRequestQueue(context);
        queue.add(request);
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }

    /*
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Map<String, List<MAurumItem>> getMap(List<MAurumItem> items) {
        return items.stream().collect(Collectors.toMap(
                        item -> {return((MAurumItem)item).getIdRecogida();},
                        item -> {List<MAurumItem> list = new ArrayList<>(); list.add(item); return list;},
                        (list1, list2) -> {{list1.addAll(list2); return list1;}},
                        HashMap::new));
    }
*/
    private boolean isPackageReady(int listPosition) {
        boolean isReadyToSendProposal = true;
        List<String> keys = new ArrayList<>(itemsMap.keySet());
        //List<MAurumItem> items = itemsMap.get(keys.get(listPosition));

        List<MAurumItem> items = itemsMap.get(keys.get(listPosition));
        for (MAurumItem item : items) {
            boolean itemReady = item.getIntPiezaProcesoTerminado().equals("1");
            isReadyToSendProposal = isReadyToSendProposal && itemReady;
        }
        return isReadyToSendProposal;
    }

    private final MutableLiveData<MAurumItem> selected = new MutableLiveData<MAurumItem>();

    public void select(MAurumItem item) {
        selected.setValue(item);
    }

    public LiveData getSelected() {
        return selected;
    }


    public void setMAurumItemsList(List<MAurumItem> items) {
        //this.items = items;
        notifyDataSetChanged();
    }
}
