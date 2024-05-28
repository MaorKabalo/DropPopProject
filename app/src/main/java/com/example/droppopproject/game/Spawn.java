package com.example.droppopproject.game;

import android.graphics.PointF;

import java.util.ArrayList;

public class Spawn
{
    public float x, y, vx, vy, radius, angle, mass;


    public boolean applyPhysics;

    public Spawn()
    {
        this.x = 0;
        this.y = 0;
        this.vx = 0;
        this.vy = 0;
        this.radius = 0;
        this.mass = 0;
        this.angle = 0.0f;
    }


    /**
     * Applies drag to the ball's velocity components.
     * This method reduces the ball's velocity by multiplying it with the drag coefficient.
     *
     * @param drag The drag coefficient to be applied.
     */
    public void applyDrag(float drag)
    {
        this.vx = (drag * this.vx);
        this.vy = (drag * this.vy);
    }



    public void updateVelocity(float vx, float vy)
    {
        this.vx = vx;
        this.vy = vy;
    }

    public void updatePos(float newX, float newY)
    {
        this.x = newX;
        this.y = newY;
    }

    public float getDiameter() //diameter
    {
        return this.radius * 2;
    }


    public PointF getCenter()
    {
        return new PointF(this.x + radius, this.y + radius);
    }

}
