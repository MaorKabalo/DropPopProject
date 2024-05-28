package com.example.droppopproject.game;

import android.graphics.Bitmap;

public class BallSize
{

    public float width, height;
    public Bitmap image;

    public BallSize(float width, float height, Bitmap image) {
        this.width = width;
        this.height = height;
        this.image = image;
    }

    public BallSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

}
