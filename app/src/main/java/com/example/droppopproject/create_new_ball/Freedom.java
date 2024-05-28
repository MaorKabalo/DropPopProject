package com.example.droppopproject.create_new_ball;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * The Freedom class represents a drawable shape that can be drawn freely
 * by the user on a canvas. It extends the Shape class.
 */
public class Freedom extends Shape {

    private Path mPath;
    private float prevX, prevY;

    /**
     * Default constructor that initializes a new Path object.
     */
    public Freedom() {
        mPath = new Path();
    }

    /**
     * Constructor that initializes a new Path object and sets the starting
     * point for the path.
     *
     * @param startX the starting x-coordinate of the path
     * @param startY the starting y-coordinate of the path
     * @param paint the Paint object used to define the path's style and color
     */
    public Freedom(float startX, float startY, Paint paint) {
        super(startX, startY, paint);
        mPath = new Path();
        mPath.moveTo(startX, startY);
        prevX = startX;
        prevY = startY;
    }

    /**
     * Resets the path to an empty state.
     */
    public void resetPath() {
        mPath.reset();
    }

    /**
     * Draws the path on the provided canvas. This method uses quadratic
     * Bezier curves to draw smooth paths based on the current and previous
     * coordinates.
     *
     * @param canvas the Canvas on which to draw the path
     */
    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            mPath.quadTo(prevX, prevY, (getStartX() + prevX) / 2, (getStartY() + prevY) / 2);
            prevX = getStartX();
            prevY = getStartY();
            canvas.drawPath(mPath, getPaint());
        }
    }
}
