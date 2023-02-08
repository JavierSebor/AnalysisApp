package com.mundiaurum.analysisapp;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.mundiaurum.analysisapp.databinding.FragmentPackagesListBinding;
import com.mundiaurum.analysisapp.domain.MAurumItem;
import com.mundiaurum.analysisapp.events.SendProposalEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

public class MAurumPackagesListFragment extends Fragment {

    private FragmentPackagesListBinding binding;
    private MAurumItemsViewModel mAurumItemsViewModel;
    private MAurumItemsExpandableListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPackagesListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAurumItemsViewModel = new ViewModelProvider(getActivity()).get(MAurumItemsViewModel.class);
        binding.setLifecycleOwner(getViewLifecycleOwner());
        mAurumItemsViewModel.getMAurumItemsMap().observe(getViewLifecycleOwner(), new Observer<Map<String, List<MAurumItem>>>() {
            @Override
            public void onChanged(Map<String, List<MAurumItem>> stringListMap) {
                //System.out.println("ENTRA EN OBSERVE DEL MAP !!!!!!.. NUMERO DE RECOGIDAS del MAP!!!!!: " + stringListMap.size());
                adapter = new MAurumItemsExpandableListAdapter(EventBus.getDefault(),getActivity(), NavHostFragment.findNavController(MAurumPackagesListFragment.this), stringListMap);
                binding.expandableListPackages.setAdapter(adapter);
                binding.textviewFirst.setText("Number of pending packages: " + mAurumItemsViewModel.getMAurumItemsMap().getValue().size());
            }
        });
        binding.textviewFirst.setVisibility(View.VISIBLE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSendProposalEvent(SendProposalEvent event) {
        System.out.println("DENTRO DEL EVENTO onSendProposal..... " + event.getEnvioId());
        Map<String, List<MAurumItem>> theMap = mAurumItemsViewModel.getMAurumItemsMap().getValue();
        theMap.remove(event.getEnvioId());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}