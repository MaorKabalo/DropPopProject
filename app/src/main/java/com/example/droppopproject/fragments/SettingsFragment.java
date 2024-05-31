package com.example.droppopproject.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;
import androidx.fragment.app.Fragment;

import com.example.droppopproject.BallsSharedPreferences;
import com.example.droppopproject.R;
import com.example.droppopproject.activities.CreateNewBallsActivity;
import com.example.droppopproject.activities.GameActivity;

import java.util.ArrayList;

/**
 * Fragment responsible for managing game settings.
 * This fragment allows users to enable/disable custom balls and navigate to create new custom balls.
 */
public class SettingsFragment extends Fragment {

    /** List of saved custom ball bitmaps. */
    private static ArrayList<Bitmap> savedCustomBalls;

    /** Flag to indicate whether custom balls are enabled. */
    public static boolean enableSwitch;

    /** Switch control for enabling/disabling custom balls. */
    private Switch switchEnable;

    /** Instance of BallsSharedPreferences for managing custom balls. */
    private BallsSharedPreferences mBallsSP;

    /**
     * Default constructor for SettingsFragment.
     * Constructs a new instance of SettingsFragment.
     */
    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Called to create the view hierarchy associated with the fragment.
     * @param inflater The LayoutInflater object that can inflate any views in the fragment.
     * @param container The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState This fragment is being re-constructed from a previous saved state.
     * @return Returns the root view of the fragment.
     */
    @Override
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Initialize views and preferences
        switchEnable = view.findViewById(R.id.enableCustom);
        mBallsSP = BallsSharedPreferences.getInstance(getContext());
        savedCustomBalls = mBallsSP.getCustomBallsFromSharedPreferences();

        // Navigate to CreateNewBallsActivity when custom balls button is clicked
        view.findViewById(R.id.customBalls).setOnClickListener(v -> {
            // Check if a game is in progress
            if(mBallsSP.isInMidGame()) {
                showRestartDialog();
                return;
            }
            // Disable custom balls and navigate to CreateNewBallsActivity
            switchEnable.setChecked(false);
            BallsSharedPreferences.getInstance(getContext()).setEnableCustomBalls(false);
            savedCustomBalls = null;
            Intent intent = new Intent(getContext(), CreateNewBallsActivity.class);
            startActivity(intent);
        });

        // Check if custom balls have been previously created
        if (savedCustomBalls == null) {
            savedCustomBalls = CreateNewBallsActivity.getCreatedCustomBalls();
        }

        // Enable/disable custom balls based on their existence
        switchEnable.setChecked(BallsSharedPreferences.getInstance(getContext()).getEnableCustomBalls());
        switchEnable.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableCustomBalls(switchEnable);
            } else {
                disableCustomBalls();
            }
        });

        return view;
    }

    /**
     * Shows a dialog to confirm restarting the game.
     */
    private void showRestartDialog() {
        Dialog dialog = new Dialog(requireContext());
        dialog.setContentView(R.layout.restart_dialog);

        dialog.findViewById(R.id.yes_button).setOnClickListener(view -> {
            mBallsSP.resetSharedPreferences(false);
            mBallsSP.resetScore();
            Intent intent = new Intent(getContext(), CreateNewBallsActivity.class);
            requireContext().startActivity(intent);
            savedCustomBalls.clear();
        });

        dialog.findViewById(R.id.no_button).setOnClickListener(view -> {
            dialog.cancel();
        });

        dialog.show();
    }

    /**
     * Enable custom balls if they exist; otherwise, show a toast message.
     * @param switchEnable The Switch control for enabling custom balls.
     */
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private void enableCustomBalls(Switch switchEnable) {
        if(mBallsSP.isInMidGame()) {
            showRestartDialog();
            switchEnable.setChecked(false);
            mBallsSP.setEnableCustomBalls(false);
            return;
        }

        if (savedCustomBalls == null) {
            Toast.makeText(getContext(), "First create your own custom balls!", Toast.LENGTH_SHORT).show();
            switchEnable.setChecked(false);
            mBallsSP.setEnableCustomBalls(false);
        } else {
            CreateNewBallsActivity.setCreatedCustomBalls(savedCustomBalls);
            mBallsSP.setEnableCustomBalls(true);
        }
    }

    /**
     * Disable custom balls and perform necessary cleanup.
     */
    private void disableCustomBalls() {
        CreateNewBallsActivity.eraseCreatedCustomBalls();
        mBallsSP.setEnableCustomBalls(false);
    }
}
