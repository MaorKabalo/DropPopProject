package com.example.droppopproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.example.droppopproject.game.Ball;

import org.checkerframework.checker.units.qual.A;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class BallsSharedPreferences {

    @SuppressLint("StaticFieldLeak")
    private static BallsSharedPreferences instance;
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor sharedPreferencesEditor;
    private final Context context;

    private BallsSharedPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.Shared_Pref_Balls_Name), Context.MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();
        this.context = context;
    }

    public static synchronized BallsSharedPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new BallsSharedPreferences(context.getApplicationContext());
        }
        return instance;
    }



    /**
     * Saves the state of balls to SharedPreferences.
     * This method converts the properties of each ball into a JSON array
     * and stores it in SharedPreferences using the key "BALLS".
     */
    public void saveBallsToSharedPreferences(ArrayList<Ball> balls) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < balls.size(); i++) {
                Ball ball = balls.get(i);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(context.getString(R.string.BALL_JSON_X), ball.x);
                jsonObject.put(context.getString(R.string.BALL_JSON_Y), ball.y);
                jsonObject.put(context.getString(R.string.BALL_JSON_VX), ball.vx);
                jsonObject.put(context.getString(R.string.BALL_JSON_VY), ball.vy);
                jsonObject.put(context.getString(R.string.BALL_JSON_TYPE), ball.getBallNum());
                jsonObject.put(context.getString(R.string.BALL_JSON_APPLY_PHYSICS), ball.applyPhysics);
                jsonObject.put(context.getString(R.string.BALL_JSON_CHECK_GAME_OVER), ball.checkGameOver);
                jsonObject.put(context.getString(R.string.BALL_JSON_ANGLE), ball.angle);
                // Add more properties as needed
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sharedPreferencesEditor.putString(context.getString(R.string.Shared_Pref_Balls_Name), jsonArray.toString());
        sharedPreferencesEditor.apply();
    }



    /**
     * Retrieves the state of balls from SharedPreferences.
     * This method reads the JSON array stored in SharedPreferences with the key "BALLS"
     * and reconstructs the balls with their properties.
     */
    public boolean getBallsFromSharedPreferences(ArrayList<Ball> balls, ArrayList<Bitmap> bitmaps) {
        String json = sharedPreferences.getString(context.getString(R.string.Shared_Pref_Balls_Name), null);
        if (json != null) {
            try {
                Bitmap bitmap = null;
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    float x = (float) jsonObject.getDouble(context.getString(R.string.BALL_JSON_X));
                    float y = (float) jsonObject.getDouble(context.getString(R.string.BALL_JSON_Y));
                    float vx = (float) jsonObject.getDouble(context.getString(R.string.BALL_JSON_VX));
                    float vy = (float) jsonObject.getDouble(context.getString(R.string.BALL_JSON_VY));
                    int type = jsonObject.getInt(context.getString(R.string.BALL_JSON_TYPE));
                    boolean applyPhysics = jsonObject.getBoolean(context.getString(R.string.BALL_JSON_APPLY_PHYSICS));
                    boolean checkGameOver = jsonObject.getBoolean(context.getString(R.string.BALL_JSON_CHECK_GAME_OVER));
                    float angle = (float) jsonObject.getDouble(context.getString(R.string.BALL_JSON_ANGLE));
                    if(bitmaps != null && !bitmaps.isEmpty())
                        bitmap = bitmaps.get(type);
                    Ball ball = new Ball(context, x, y, vx, vy, type, applyPhysics, checkGameOver, angle, bitmap);
                    balls.add(ball);
                }
                resetSharedPreferences(false);
                saveBallsToSharedPreferences(balls);
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }



    /**
     * Saves a list of custom ball bitmaps to SharedPreferences.
     * Each bitmap is converted to a Base64 string before saving.
     * @param customBalls The list of custom ball bitmaps to be saved.
     */
    public void saveCustomBallsToSharedPreferences(ArrayList<Bitmap> customBalls) {
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < customBalls.size(); i++) {
                JSONObject jsonObject = new JSONObject();
                String bitmapBase64 = convertBitmapToBase64(customBalls.get(i));
                jsonObject.put("bitmap", bitmapBase64);
                // Add more properties as needed
                jsonArray.put(jsonObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sharedPreferencesEditor.putString("customBalls", jsonArray.toString());
        sharedPreferencesEditor.apply();
    }

    /**
     * Retrieves a list of custom ball bitmaps from SharedPreferences.
     * Each bitmap is retrieved as a Base64 string and converted back to a Bitmap object.
     * @return The list of custom ball bitmaps retrieved from SharedPreferences.
     */
    public ArrayList<Bitmap> getCustomBallsFromSharedPreferences() {
        ArrayList<Bitmap> customBalls = null;
        String customBallsJsonString = sharedPreferences.getString("customBalls", null);

        if (customBallsJsonString != null) {
            customBalls = new ArrayList<>();
            try {
                JSONArray jsonArray = new JSONArray(customBallsJsonString);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String bitmapBase64 = jsonObject.getString("bitmap");
                    Bitmap bitmap = convertBase64ToBitmap(bitmapBase64);
                    customBalls.add(bitmap);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return customBalls;
    }




    /**
     * Resets the SharedPreferences, clearing all stored data.
     */
    public void resetSharedPreferences(boolean removeIsGuest) {
        if(removeIsGuest){
            sharedPreferencesEditor.remove(context.getString(R.string.IS_GUEST)).apply();
        }
         sharedPreferencesEditor.remove(context.getString(R.string.RESET_SP))
                                .remove(context.getString(R.string.Shared_Pref_Balls_Name))
                                .apply();
    }

    /**
     * Sets the status of whether the user is a guest or not in the SharedPreferences.
     *
     * @param isGuest {@code true} if the user is a guest, {@code false} otherwise.
     */
    public void setIsGuest(boolean isGuest) {
        sharedPreferencesEditor.putBoolean(context.getString(R.string.IS_GUEST), isGuest).apply();
    }

    /**
     * Retrieves the status indicating whether the user is a guest from the SharedPreferences.
     *
     * @return {@code true} if the user is a guest, {@code false} otherwise.
     */
    public boolean getIsGuest() {
        return sharedPreferences.getBoolean(context.getString(R.string.IS_GUEST), false);
    }



    /**
     * Checks if the game is in progress by verifying if there are any saved balls in the SharedPreferences.
     *
     * @return {@code true} if the game is in progress (i.e., there are saved balls), {@code false} otherwise.
     */
    public boolean isInMidGame(){
        return !sharedPreferences.getString(context.getString(R.string.Shared_Pref_Balls_Name), "").isEmpty();
    }


    public void setScore(int score) {
        sharedPreferencesEditor.putInt(context.getString(R.string.SCORE), score).apply();
    }

    public int getScore() {
        return sharedPreferences.getInt(context.getString(R.string.SCORE), -1);
    }

    public void resetScore() {
        sharedPreferencesEditor.remove(context.getString(R.string.SCORE)).apply();
    }


    /**
     * Converts a Base64 encoded string to a Bitmap object.
     *
     * @param base64Str the Base64 encoded string
     * @return the decoded Bitmap object
     * @throws IllegalArgumentException if the input string is not properly Base64 encoded
     */
    public static Bitmap convertBase64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    /**
     * Converts a Bitmap object to a Base64 encoded string.
     *
     * @param bitmap the Bitmap object to encode
     * @return the Base64 encoded string representation of the Bitmap
     */
    public static String convertBitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Enables or disables custom balls.
     *
     * @param enableCustom true to enable custom balls, false to disable
     */
    public void setEnableCustomBalls(boolean enableCustom) {
        sharedPreferencesEditor.putBoolean(context.getString(R.string.ENABLE), enableCustom).apply();
    }

    /**
     * Checks whether custom balls are enabled.
     *
     * @return true if custom balls are enabled, false otherwise
     */
    public boolean getEnableCustomBalls() {
        return sharedPreferences.getBoolean(context.getString(R.string.ENABLE), false);
    }



}
