package com.example.droppopproject.create_new_ball;
import android.graphics.Canvas;
import android.graphics.Paint;

public abstract class Shape {
    private float startX;
    private float startY;
    private Paint paint;

    public Shape(float startX, float startY, Paint paint)
    {
        this.startX = startX;
        this.startY = startY;
        this.paint = paint;
    }
    public Shape()
    {

    }

    public void setStartX(float startX) {
        this.startX = startX;
    }

    public float getStartX() {
        return startX;
    }

    public void setStartY(float startY) {
        this.startY = startY;
    }

    public float getStartY() {
        return startY;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

    public Paint getPaint() {
        return paint;
    }


    public abstract void draw(Canvas canvas);
}
