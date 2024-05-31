package com.example.droppopproject.create_new_ball;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import java.util.LinkedList;
import java.util.Queue;



/**
 * Implements a queue-based flood fill algorithm to fill contiguous areas of a bitmap with a specified color.
 */
public class QueueLinearFloodFiller extends Shape {

    protected Bitmap image = null;
    protected int[] tolerance = new int[] { 0, 0, 0 };
    protected int width = 0;
    protected int height = 0;
    protected int[] pixels = null;
    protected int fillColor = 0;
    protected int[] startColor = new int[] { 0, 0, 0 };
    protected boolean[] pixelsChecked;
    protected Queue<FloodFillRange> ranges;


    /**
     * Constructs a QueueLinearFloodFiller object using the provided image and sets the target color and fill color.
     *
     * @param img         The Bitmap image to be filled.
     * @param targetColor The target color to be filled.
     * @param newColor    The fill color to be used.
     */
    public QueueLinearFloodFiller(Bitmap img, int targetColor, int newColor) {
        useImage(img);
        setFillColor(newColor);
        setTargetColor(targetColor);
    }

    /**
     * Sets the target color for the flood fill operation.
     *
     * @param targetColor The target color to be filled.
     */
    public void setTargetColor(int targetColor) {
        startColor[0] = Color.red(targetColor);
        startColor[1] = Color.green(targetColor);
        startColor[2] = Color.blue(targetColor);
    }

    /**
     * Gets the fill color used by the flood fill operation.
     *
     * @return The fill color.
     */
    public int getFillColor() {
        return fillColor;
    }

    /**
     * Sets the fill color for the flood fill operation.
     *
     * @param value The fill color to be used.
     */
    public void setFillColor(int value) {
        fillColor = value;
    }


    /**
     * Uses the provided image bitmap for the flood fill operation.
     *
     * @param img The image bitmap to be used.
     */
    public void useImage(Bitmap img) {

        width = img.getWidth();
        height = img.getHeight();
        image = img;

        pixels = new int[width * height];

        image.getPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
    }

    /**
     * Prepares the flood fill operation by initializing required variables.
     */
    protected void prepare() {
        // Called before starting flood-fill
        pixelsChecked = new boolean[pixels.length];
        ranges = new LinkedList<FloodFillRange>();
    }

    /**
     * Fills the specified point on the bitmap with the currently selected fill color.
     *
     * @param x The x-coordinate of the starting point for flood fill
     * @param y The y-coordinate of the starting point for flood fill
     */
    public void floodFill(int x, int y) {
        // Setup
        prepare();

        if (startColor[0] == 0) {
            // Get starting color.
            int startPixel = pixels[(width * y) + x];
            startColor[0] = (startPixel >> 16) & 0xff;
            startColor[1] = (startPixel >> 8) & 0xff;
            startColor[2] = startPixel & 0xff;
        }

        // Do first call to flood fill
        linearFill(x, y);

        // Call flood fill routine while flood fill ranges still exist on the queue
        while (!ranges.isEmpty()) {
            FloodFillRange range = ranges.remove();

            // Check Above and Below Each Pixel in the Flood Fill Range
            int downPxIdx = (width * (range.Y + 1)) + range.startX;
            int upPxIdx = (width * (range.Y - 1)) + range.startX;
            int upY = range.Y - 1;
            int downY = range.Y + 1;

            for (int i = range.startX; i <= range.endX; i++) {
                // Start Fill Upwards
                if (range.Y > 0 && (!pixelsChecked[upPxIdx]) && !checkPixel(upPxIdx))
                    linearFill(i, upY);

                // Start Fill Downwards
                if (range.Y < (height - 1) && (!pixelsChecked[downPxIdx]) && !checkPixel(downPxIdx))
                    linearFill(i, downY);

                downPxIdx++;
                upPxIdx++;
            }
        }

        image.setPixels(pixels, 0, width, 1, 1, width - 1, height - 1);
    }

    /**
     * Finds the furthermost left and right boundaries of the fill area on a given y coordinate,
     * starting from a given x coordinate, filling as it goes.
     * Adds the resulting horizontal range to the queue of flood fill ranges, to be processed in the main loop.
     *
     * @param x The starting x-coordinate
     * @param y The starting y-coordinate
     */
    protected void linearFill(int x, int y) {
        int lFillLoc = x;
        int pxIdx = (width * y) + x;

        do {
            pixels[pxIdx] = fillColor;
            pixelsChecked[pxIdx] = true;

            lFillLoc--;
            pxIdx--;

        } while (lFillLoc >= 0 && !pixelsChecked[pxIdx] && !checkPixel(pxIdx));

        lFillLoc++;

        int rFillLoc = x;
        pxIdx = (width * y) + x;

        do {
            pixels[pxIdx] = fillColor;
            pixelsChecked[pxIdx] = true;

            rFillLoc++;
            pxIdx++;

        } while (rFillLoc < width && !pixelsChecked[pxIdx] && !checkPixel(pxIdx));

        rFillLoc--;

        FloodFillRange r = new FloodFillRange(lFillLoc, rFillLoc, y);
        ranges.offer(r);
    }

    /**
     * Checks if a pixel is within the color tolerance range.
     *
     * @param px The index of the pixel to check
     * @return True if the pixel is within the color tolerance range, false otherwise
     */
    protected boolean checkPixel(int px) {
        int red = (pixels[px] >>> 16) & 0xff;
        int green = (pixels[px] >>> 8) & 0xff;
        int blue = pixels[px] & 0xff;

        return (red < (startColor[0] - tolerance[0])
                || red > (startColor[0] + tolerance[0])
                || green < (startColor[1] - tolerance[1])
                || green > (startColor[1] + tolerance[1])
                || blue < (startColor[2] - tolerance[2]) || blue > (startColor[2] + tolerance[2]));
    }


    /**
     * Draws the flood-filled bitmap on the canvas.
     *
     * @param canvas The canvas to draw on
     */
    @Override
    public void draw(Canvas canvas) {
        floodFill((int) getStartX(), (int) getStartY());
    }

    /**
     * Represents a linear range to be filled and branched from.
     */
    protected class FloodFillRange {
        public int startX;
        public int endX;
        public int Y;

        /**
         * Constructor for FloodFillRange.
         *
         * @param startX The starting x-coordinate of the range
         * @param endX   The ending x-coordinate of the range
         * @param y      The y-coordinate of the range
         */
        public FloodFillRange(int startX, int endX, int y) {
            this.startX = startX;
            this.endX = endX;
            this.Y = y;
        }
    }

}
