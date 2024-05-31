package com.example.droppopproject;

import android.content.Context;
import android.media.MediaPlayer;

/**
 * A class for controlling music playback in the application.
 */
public class MusicControl {

    private static MediaPlayer gameMediaPlayer;
    private static MediaPlayer popMediaPlayer;
    private static int currentGameResourceId = -1;
    private static int currentPopResourceId = -1;

    /**
     * Plays the main music.
     *
     * @param resourceId The resource ID of the main music.
     * @param context    The context to use for creating the MediaPlayer.
     * @param loop       Whether to loop the main music.
     */
    public static void playMainMusic(int resourceId, Context context, boolean loop) {
        // If the requested resource ID is different or there is no existing MediaPlayer,
        // stop the current main music, create a new MediaPlayer, start playback, and update current resource ID.
        if (currentGameResourceId != resourceId || gameMediaPlayer == null) {
            stopMainMusic();
            gameMediaPlayer = MediaPlayer.create(context, resourceId);
            gameMediaPlayer.setLooping(loop);
            gameMediaPlayer.start();
            currentGameResourceId = resourceId;
        }
        // If the MediaPlayer is not null and is paused, resume playback.
        else if (gameMediaPlayer != null && !gameMediaPlayer.isPlaying()) {
            gameMediaPlayer.start();
        }
    }

    /**
     * Plays the background sound.
     *
     * @param resourceId The resource ID of the background sound.
     * @param context    The context to use for creating the MediaPlayer.
     * @param loop       Whether to loop the background sound.
     */
    public static void playBackgroundSound(int resourceId, Context context, boolean loop) {
        // If the requested resource ID is different or there is no existing MediaPlayer,
        // stop the current background sound, create a new MediaPlayer, start playback, and update current resource ID.
        if (currentPopResourceId != resourceId || popMediaPlayer == null) {
            stopBackgroundSound();
            popMediaPlayer = MediaPlayer.create(context, resourceId);
            popMediaPlayer.setLooping(loop);
            popMediaPlayer.start();
            currentPopResourceId = resourceId;
        }
        // If the MediaPlayer is not null and is paused, resume playback.
        else if (popMediaPlayer != null && !popMediaPlayer.isPlaying()) {
            popMediaPlayer.start();
        }
    }

    /**
     * Stops the main music playback.
     */
    private static void stopMainMusic() {
        // If the main music MediaPlayer is playing, stop playback, release resources,
        // set the MediaPlayer to null, and reset the current resource ID.
        if (gameMediaPlayer != null && gameMediaPlayer.isPlaying()) {
            gameMediaPlayer.stop();
            gameMediaPlayer.release();
            gameMediaPlayer = null;
            currentGameResourceId = -1;
        }
    }

    /**
     * Stops the background sound playback.
     */
    private static void stopBackgroundSound() {
        // If the background sound MediaPlayer is playing, stop playback, release resources,
        // set the MediaPlayer to null, and reset the current resource ID.
        if (popMediaPlayer != null && popMediaPlayer.isPlaying()) {
            popMediaPlayer.stop();
            popMediaPlayer.release();
            popMediaPlayer = null;
            currentPopResourceId = -1;
        }
    }
}
