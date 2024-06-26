package com.example.droppopproject.activities;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.droppopproject.BallsSharedPreferences;
import com.example.droppopproject.R;
import com.example.droppopproject.create_new_ball.CameraOptions;
import com.example.droppopproject.create_new_ball.CreateBallsCanvas;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This activity allows users to create new balls on a canvas.
 */
public class CreateNewBallsActivity extends AppCompatActivity {

    // Constants for ball colors
    public static final int RED = R.color.red;
    public static final int YELLOW = R.color.yellow;
    public static final int GREEN = R.color.green;
    public static final int BLUE = R.color.blue;
    public static final int MIN_CREATED_BALLS = 4;

    private CreateBallsCanvas createBallsCanvas;
    private final HashMap<Integer, FloatingActionButton> colorButtons = new HashMap<>();
    private Button resetButton;
    private Button saveButton;
    private Button nextButton;
    private ImageView imageTestView;
    private ImageButton undoButton;
    private ImageButton fillButton;
    private ImageButton cameraButton;
    private ProgressBar progressBar;
    private SeekBar widthSeekBar;

    private static ArrayList<Bitmap> mCreatedCustomBalls;
    private CameraOptions mCameraOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_balls);

        initViews();
        setButtonClickListeners();
    }

    /**
     * Initializes views and sets up the activity.
     */
    private void initViews() {
        createBallsCanvas = findViewById(R.id.MyCanvas);
        createBallsCanvas.circleView = findViewById(R.id.circleImageView);
        resetButton = findViewById(R.id.resetButton);
        nextButton = findViewById(R.id.nextButton);
        saveButton = findViewById(R.id.saveButton);
        imageTestView = findViewById(R.id.testImageView);
        undoButton = findViewById(R.id.undoButton);
        fillButton = findViewById(R.id.fillButton);
        cameraButton = findViewById(R.id.cameraButton);
        widthSeekBar = findViewById(R.id.seekBarWidth);
        progressBar = findViewById(R.id.progressBarCustom);
        progressBar.setVisibility(View.GONE);

        mCreatedCustomBalls = new ArrayList<>();
        mCameraOptions = new CameraOptions(this, createBallsCanvas.circleView);

        BallsSharedPreferences.getInstance(this).resetSharedPreferences(false);
        BallsSharedPreferences.getInstance(this).resetScore();

        initializeColorButtons();
    }

    /**
     * Initializes color buttons and maps them to view IDs.
     */
    @SuppressLint("FindViewByIdCast")
    private void initializeColorButtons() {
        colorButtons.put(RED, findViewById(R.id.RedButton));
        colorButtons.put(YELLOW, findViewById(R.id.YellowButton));
        colorButtons.put(GREEN, findViewById(R.id.GreenButton));
        colorButtons.put(BLUE, findViewById(R.id.BlueButton));
    }

    /**
     * Sets click listeners for buttons in the activity.
     */
    private void setButtonClickListeners() {
        resetButton.setOnClickListener(v -> {
            createBallsCanvas.reset(true);
            imageTestView.setImageBitmap(createBallsCanvas.getBitmapCanvas());
        });

        undoButton.setOnClickListener(v -> createBallsCanvas.undo());

        nextButton.setOnClickListener(v -> {
            Bitmap createdBall = createBallsCanvas.getBitmapCanvas();
            imageTestView.setImageBitmap(createdBall);
            mCreatedCustomBalls.add(createdBall);
            Toast.makeText(this, "Ball Number " + mCreatedCustomBalls.size() + " Is Saved", Toast.LENGTH_SHORT).show();
            createBallsCanvas.reset(true);

            if (mCreatedCustomBalls.size() == CreateBallsCanvas.MAX_CREATED_BALLS) {
                Toast.makeText(this, this.getString(R.string.MAX_BALLS_CREATED), Toast.LENGTH_SHORT).show();
                BallsSharedPreferences.getInstance(this).saveCustomBallsToSharedPreferences(mCreatedCustomBalls);
                BallsSharedPreferences.getInstance(this).setEnableCustomBalls(true);
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        saveButton.setOnClickListener(v -> {
            if (mCreatedCustomBalls.size() >= MIN_CREATED_BALLS) {
                progressBar.setVisibility(View.VISIBLE);
                BallsSharedPreferences.getInstance(this).setEnableCustomBalls(true);
                BallsSharedPreferences.getInstance(this).saveCustomBallsToSharedPreferences(mCreatedCustomBalls);
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "At least " + MIN_CREATED_BALLS + " created balls are required", Toast.LENGTH_SHORT).show();
            }
        });

        cameraButton.setOnClickListener(v -> showPhotoDialog());

        for (Integer color : colorButtons.keySet()) {
            FloatingActionButton button = colorButtons.get(color);
            if (button != null) {
                button.setOnClickListener(v -> createBallsCanvas.setDrawColor(color));
            }
        }

        widthSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int value;

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                value = i;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not implemented
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                createBallsCanvas.setWidthOfPaint(value);
            }
        });

        fillButton.setOnClickListener(view -> createBallsCanvas.toggleFillMode());
    }

    /**
     * Retrieves the list of created custom balls.
     *
     * @return a new ArrayList containing the created custom balls,
     *         or null if no custom balls have been created.
     */
    public static ArrayList<Bitmap> getCreatedCustomBalls() {
        if (mCreatedCustomBalls == null || mCreatedCustomBalls.size() < MIN_CREATED_BALLS)
            return null;
        return new ArrayList<>(mCreatedCustomBalls);
    }

    /**
     * Sets the list of created custom balls.
     *
     * @param balls the ArrayList of Bitmap objects representing the custom balls.
     */
    public static void setCreatedCustomBalls(ArrayList<Bitmap> balls) {
        mCreatedCustomBalls = new ArrayList<>(balls);
    }

    /**
     * Erases all created custom balls.
     * Clears the list if it is not null.
     */
    public static void eraseCreatedCustomBalls() {
        if (mCreatedCustomBalls != null) {
            mCreatedCustomBalls.clear();
        }
    }

    /**
     * Displays a dialog for choosing between camera and gallery options for adding a photo.
     * Upon selecting an option, the corresponding action is triggered.
     */
    private void showPhotoDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.choose_photo_option_dialog);

        dialog.findViewById(R.id.use_camera).setOnClickListener(view -> {
            mCameraOptions.takePhoto();
            dialog.cancel();
        });

        dialog.findViewById(R.id.use_gallery).setOnClickListener(view -> {
            mCameraOptions.chooseFromGallery();
            dialog.cancel();
        });

        dialog.show();
    }
}
