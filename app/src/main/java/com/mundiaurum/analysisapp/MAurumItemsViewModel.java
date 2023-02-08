package com.mundiaurum.analysisapp;

import android.content.Context;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.arch.core.util.Function;
import androidx.lifecycle.AbstractSavedStateViewModelFactory;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.savedstate.SavedStateRegistryOwner;

import com.mundiaurum.analysisapp.data.MAurumItemsRepository;
import com.mundiaurum.analysisapp.domain.AnalysisFile;
import com.mundiaurum.analysisapp.domain.MAurumItem;

import java.util.List;
import java.util.Map;

public class MAurumItemsViewModel extends ViewModel {
    // Key para estado de item activado
    private static final String ACTIVATED_MAURUMITEM_KEY = "activated_maurumitem";

    // Conector para guardar estados
    private final SavedStateHandle mState;

    private MAurumItemsRepository mAurumItemsRepository;

    private final MutableLiveData<Boolean> mSync = new MutableLiveData<>(false);

    private final LiveData<Map<String, List<MAurumItem>>> mAurumItemsMap =
            Transformations.switchMap(mSync,
                    new Function<Boolean, LiveData<Map<String,List<MAurumItem>>>>() {
                        @Override
                        public LiveData<Map<String,List<MAurumItem>>> apply(Boolean input) {
                            //System.out.println("ANTES DE SABER SI REFRESCA LOS DATOS   ...");
                            if (input) {
                                //System.out.println("HA ENTTRADO DENTRO DEL IF, REFRESH mAURUM ITEMS...");
                                //mAurumItemsRepository.refreshMAurumItems();
                            }
                            return mAurumItemsRepository.observeMAurumItemsMap();
                        }
                    });

    private final MutableLiveData<String> mAurumItemId = new MutableLiveData<>();

    private final LiveData<MAurumItem> mAurumItemById = Transformations.switchMap(mAurumItemId, itemId -> mAurumItemsRepository.observeMAurumItem(itemId));

    private final LiveData<List<AnalysisFile>> mAnalysisfiles = Transformations.switchMap(mAurumItemId, itemId -> mAurumItemsRepository.observeAnalysisFileList(itemId));


    private boolean mTwoPane = false;


    /**
     * Inicializar view model
     */
    public MAurumItemsViewModel(SavedStateHandle savedStateHandle, MAurumItemsRepository repository) {
        mState = savedStateHandle;
        //mState.set(ACTIVATED_PET_KEY, 0);
        mAurumItemsRepository = repository;
        loadMAurumItems(true);
    }

    public LiveData<List<AnalysisFile>> getAnalysisfiles(){
        return mAnalysisfiles;
    }

    public void loadMAurumItems(boolean pull) {
        mSync.setValue(pull);
    }

    public void loadMAurumItemDetail(String itemId) {

        // Si ya se cargó el item, retornar
         if (itemId.equals(mAurumItemId.getValue())) {
             return;
        }
        mAurumItemId.setValue(itemId);
    }

    public LiveData<Map<String, List<MAurumItem>>> getMAurumItemsMap() {
        return mAurumItemsMap;
    }

    public LiveData<MAurumItem> getMAurumItemById() {
        return mAurumItemById;
    }

    public void setTwoPane(boolean twoPane) {
        mTwoPane = twoPane;
    }

    public boolean isTwoPane() {
        return mTwoPane;
    }

    public void setActivatedItem(int position) {
        mState.set(ACTIVATED_MAURUMITEM_KEY, position);
    }

    public int getActivatedItem() {
        if (mState.contains(ACTIVATED_MAURUMITEM_KEY)) {
            return mState.get(ACTIVATED_MAURUMITEM_KEY);
        } else {
            return 0;
        }
    }


    /**
     * Permite crear insntancias del view model pasando parámetros en su constructor
     */
    public static class Factory extends AbstractSavedStateViewModelFactory {

        private final MAurumItemsRepository mRepository;

        private final Context context;


        public Factory(@NonNull SavedStateRegistryOwner owner,
                       @Nullable Bundle defaultArgs,
                       MAurumItemsRepository mRepository,
                       Context context) {
            super(owner, defaultArgs);
            this.mRepository = mRepository;
            this.context = context;
        }

        @SuppressWarnings("unchecked")
        @NonNull
        @Override
        protected <T extends ViewModel> T create(@NonNull String key,
                                                 @NonNull Class<T> modelClass,
                                                 @NonNull SavedStateHandle handle) {
            mRepository.setContext(context);
            mRepository.loadData();
            return (T) new MAurumItemsViewModel(handle, mRepository);
        }
    }
}
