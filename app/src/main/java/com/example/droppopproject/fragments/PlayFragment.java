package com.example.droppopproject.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.droppopproject.BallsSharedPreferences;
import com.example.droppopproject.R;
import com.example.droppopproject.activities.GameActivity;
import com.example.droppopproject.activities.HomeActivity;
import com.example.droppopproject.activities.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;


public class PlayFragment extends Fragment {


    public PlayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_play, container, false);


        view.findViewById(R.id.playButton).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), GameActivity.class);
            startActivity(intent);
        });


        return view;
    }



}