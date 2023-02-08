package com.mundiaurum.analysisapp.data;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.mundiaurum.analysisapp.MAurumItemsExpandableListAdapter;
import com.mundiaurum.analysisapp.domain.AnalysisFile;
import com.mundiaurum.analysisapp.domain.MAurumItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface MAurumItemsRepository {

    LiveData<MAurumItem> observeMAurumItem(String itemId);

    LiveData<Map<String, List<MAurumItem>>> observeMAurumItemsMap();

    //void refreshMAurumItems();

    //ArrayList<MAurumItem> getMAurumItems();

    //LiveData<List<MAurumItem>> observeMAurumItems();

    LiveData<List<AnalysisFile>> observeAnalysisFileList(String itemId);

    void setContext(Context ctx);

    void loadData();

}
