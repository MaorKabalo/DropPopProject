package com.example.droppopproject.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.droppopproject.BallsSharedPreferences;
import com.example.droppopproject.MusicControl;
import com.example.droppopproject.R;
import com.example.droppopproject.fragments.SettingsFragment;
import com.example.droppopproject.game.GameView;

public class GameActivity extends AppCompatActivity {

    private GameView mGameView;

    private BallsSharedPreferences mBallsSharedPreferences;

    private MusicControl musicControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);



        mBallsSharedPreferences = BallsSharedPreferences.getInstance(this);
//        if(getIntent().getBooleanExtra(getString(R.string.RESET_SP), false)){
//            mBallsSharedPreferences.resetSharedPreferences();
//            mBallsSharedPreferences.resetScore();
//
//        }

        MusicControl.playMainMusic(R.raw.game_music, this, true);



        mGameView = findViewById(R.id.GameView);
        mGameView.mScoreView = findViewById(R.id.ScoreText);
        mGameView.mScoreView.setText(String.valueOf(mGameView.mScore));
        mGameView.mHomeButton = findViewById(R.id.HomeButton);
        mGameView.mRestartButton = findViewById(R.id.RestartButton);

        //mGameView.mCloseButton = findViewById(R.id.closeGameButton);



        mGameView.mHomeButton.setOnClickListener(v -> {
            MusicControl.stopMainMusic();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        mGameView.mRestartButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
            mBallsSharedPreferences.resetSharedPreferences(false);
            mBallsSharedPreferences.resetScore();
            finish();
        });





    }

    @Override
    protected void onPause() {
        super.onPause();
        mGameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGameView.resume();
    }




}