package com.mundiaurum.analysisapp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.mundiaurum.analysisapp.api.Api;
import com.mundiaurum.analysisapp.databinding.FragmentImageviewerBinding;
import com.mundiaurum.analysisapp.databinding.FragmentMaurumitemDetailsBinding;

public class ImageviewerFragment extends Fragment {
    FragmentImageviewerBinding binding;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Bundle args = getArguments();
        //System.out.println("ARGS QUE SE LE PASAN AL FRAGMENT: " + args.toString());
        //System.out.println("LA RUTA DE LA IMAGE TO CARGAR EN EL IMAGE VIEWER FRAGMENT");
        //System.out.println(args.getString("apiUrl") +  args.getString("fileName"));
        binding = FragmentImageviewerBinding.inflate(inflater, container, false);
        Glide.with(this).load(args.getString("apiUrl") + args.getString("fileName")).into(binding.imageViewer);
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(args.getString("imageUrl"));
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);

        return binding.getRoot();
    }

    /*
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }
    */


}
