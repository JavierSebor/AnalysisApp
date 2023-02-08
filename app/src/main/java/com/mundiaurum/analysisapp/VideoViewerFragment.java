package com.mundiaurum.analysisapp;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.mundiaurum.analysisapp.MainActivity;
import com.mundiaurum.analysisapp.api.Api;
import com.mundiaurum.analysisapp.databinding.FragmentVideoviewerBinding;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class VideoViewerFragment extends Fragment {
    FragmentVideoviewerBinding binding;
    MediaController mediaControls;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        Bundle args = getArguments();
        binding = FragmentVideoviewerBinding.inflate(inflater, container, false);
        if (mediaControls == null) {
            // create an object of media controller class
            mediaControls = new MediaController(getActivity());
            mediaControls.setAnchorView(binding.videoViewer);
        }
        // set the media controller for video view
        binding.videoViewer.setMediaController(mediaControls);
        // set the uri for the video view
        binding.videoViewer.setVideoURI(Uri.parse(Api.ANALYSIS_FILE_URL + args.getString("fileName")));
        // start a video
        binding.videoViewer.start();
        binding.videoViewer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                Toast.makeText(getActivity(), "Video finished", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
            }});
        binding.videoViewer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Toast.makeText(getActivity(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
                return false;
            }
        });

        ((MainActivity)getActivity()).getSupportActionBar().setTitle(args.getString("fileName"));;
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);


        return binding.getRoot();
    }
}
