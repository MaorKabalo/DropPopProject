package com.example.droppopproject.create_new_ball;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * This class provides camera options for capturing images or selecting images from the gallery,
 *
 * using the new format of ForActivityResult
 */
public class CameraOptions extends AppCompatActivity {
    private final AppCompatActivity activity;
    private final ImageView imageTestView;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    public static boolean photoTaken = false;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;

    /**
     * Constructs a CameraOptions object.
     *
     * @param activity       The activity where camera options are used.
     * @param imageTestView  The ImageView where the captured or selected image will be displayed.
     */
    public CameraOptions(AppCompatActivity activity, ImageView imageTestView) {
        this.activity = activity;
        this.imageTestView = imageTestView;
        initLaunchers();
    }


    /**
     * Initializes the ActivityResultLaunchers for camera and gallery actions.
     */
    private void initLaunchers() {
        cameraLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                            Intent data = result.getData();
                            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                            if (bitmap != null) {
                                photoTaken = true;
                                imageTestView.setImageBitmap(getCircularBitmap(bitmap));
                            }
                        }
                    }
                });

        galleryLauncher = activity.registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                new ActivityResultCallback<Uri>() {
                    @Override
                    public void onActivityResult(Uri uri) {
                        Bitmap bitmap = getBitmapFromUri(uri);
                        if (bitmap != null) {
                            photoTaken = true;
                            imageTestView.setImageBitmap(getCircularBitmap(bitmap));
                        }
                    }
                });
    }

    /**
     * Launches the gallery to select an image.
     */
    public void chooseFromGallery() {
        galleryLauncher.launch("image/*");
    }

    /**
     * Launches the camera to capture an image.
     */
    public void takePhoto() {
        if (checkCameraPermission()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            cameraLauncher.launch(intent);
        } else {
            requestCameraPermission();
        }
    }

    /**
     * Checks if the camera permission is granted.
     *
     * @return true if the camera permission is granted, false otherwise.
     */
    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Requests the camera permission if it is not granted.
     */
    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    /**
     * Retrieves a bitmap from the given URI.
     *
     * @param uri The URI of the image.
     * @return The bitmap obtained from the URI, or null if an error occurs.
     */
    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            return MediaStore.Images.Media.getBitmap(activity.getContentResolver(), uri);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * Converts a given bitmap into a circular bitmap (to put in circleView).
     *
     * @param bitmap The input bitmap to be converted into a circular bitmap.
     * @return A circular bitmap generated from the input bitmap.
     */
    private Bitmap getCircularBitmap(Bitmap bitmap) {
        // Get the width and height of the input bitmap
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // Determine the minimum dimension between width and height
        int minDimension = Math.min(width, height);

        // Create a new bitmap with the same width and height
        Bitmap output = Bitmap.createBitmap(minDimension, minDimension, Bitmap.Config.ARGB_8888);

        // Create a canvas for drawing on the output bitmap
        Canvas canvas = new Canvas(output);
        // Create a paint object for drawing
        final Paint paint = new Paint();
        // Create a rectangle representing the bounds of the circular bitmap
        final Rect rect = new Rect(0, 0, minDimension, minDimension);

        // Calculate the radius of the circle
        float radius = minDimension / 2f;

        // Set paint properties for anti-aliasing and filtering
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // Clear the canvas with transparent color
        canvas.drawARGB(0, 0, 0, 0);
        // Set the color for drawing the circle (optional)
        paint.setColor(Color.parseColor("#BAB399"));

        // Draw a circle on the canvas
        canvas.drawCircle(radius, radius, radius, paint);

        // Use SRC_IN mode to retain only the intersection of the circle and the bitmap
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        // Draw the input bitmap onto the canvas
        canvas.drawBitmap(bitmap, rect, rect, paint);

        // Return the circular bitmap
        return output;
    }


}
