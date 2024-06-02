package com.example.droppopproject.create_new_ball;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.droppopproject.R;
import com.example.droppopproject.game.BallSize;

import java.util.ArrayList;


/**
 * Custom view for drawing shapes on a canvas.
 */
public class CreateBallsCanvas extends View {

    private static final float TOUCH_TOLERANCE = 4f;

    private Rect mFrame;
    private Paint mPaint;
    private Paint mFramePaint;
    private int mBackgroundColor;
    private Canvas mExtraCanvas;
    private Bitmap mExtraBitmap;
    private float prevX, prevY;
    private Shape shapeToDraw;
    private ArrayList<Shape> shapes;

    private boolean fillMode = false;

    public ImageView circleView;

    private static ArrayList<BallSize> sizesOfCustomBalls;

    private static int numOfBall = 0;

    public static final int MAX_CREATED_BALLS = 10;


    /***Constructor and Initialization***/

    public CreateBallsCanvas(Context context) {
        this(context, null);
    }

    public CreateBallsCanvas(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        initialize();
    }


    /**
     * Initialize the view with default values and resources.
     */
    private void initialize() {
        mBackgroundColor = ResourcesCompat.getColor(getResources(), R.color.teal_200, null);
        int drawColor = ResourcesCompat.getColor(getResources(), R.color.yellow, null);
        int frameColor = ResourcesCompat.getColor(getResources(), R.color.colorAccent, null);

        mPaint = createPaint(drawColor, 12, Paint.Style.STROKE);
        mFramePaint = createPaint(frameColor, 12, Paint.Style.STROKE);

        shapeToDraw = null;
        shapes = new ArrayList<>();

        setSizesOfCustomBalls();

    }


    /**
     * Sets the sizes of custom balls based on the default drawable resources.
     * This method populates the 'sizesOfCustomBalls' list with the width and height
     * dimensions of custom ball images loaded from drawable resources.
     * Each custom ball is identified by its resource ID, and its width and height
     * are retrieved from the corresponding bitmap.
     */
    @SuppressLint("DiscouragedApi")
    private void setSizesOfCustomBalls(){
        if (sizesOfCustomBalls == null){
            sizesOfCustomBalls = new ArrayList<>();
            Bitmap bitmap;
            for (int i = 0; i < MAX_CREATED_BALLS; i++){
                int resourceId = getContext().getResources().getIdentifier("ball_" + i , "drawable", getContext().getPackageName());
                bitmap = BitmapFactory.decodeResource(getContext().getResources(), resourceId);
                sizesOfCustomBalls.add(new BallSize(bitmap.getWidth(), bitmap.getHeight()));
            }
        }
    }



    /***Paint and Drawing Setup:***/


    /**
     * Create a new Paint object with specified color, width, and style.
     *
     * @param color Color for the paint
     * @param width Width of the paint stroke
     * @param style Style of the paint (e.g., FILL, STROKE)
     * @return The configured Paint object
     */
    private Paint createPaint(int color, float width, Paint.Style style) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(style);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(width);
        //paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    /**
     * Set the drawing color for shapes.
     *
     * @param color Color resource ID to set
     */
    public void setDrawColor(int color) {
        int drawColor = ResourcesCompat.getColor(getResources(), color, null);
        mPaint.setColor(drawColor);
    }

    /**
     * Sets the stroke width of the drawing paint.
     *
     * @param width The width of the stroke in pixels.
     */
    public void setWidthOfPaint(int width) {mPaint.setStrokeWidth(width);}



    /***Canvas and Bitmap Handling:***/

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        createCanvasAndBitmap(width, height);
        calculateFrame(width, height);
    }

    private void createCanvasAndBitmap(int width, int height) {
        mExtraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mExtraCanvas = new Canvas(mExtraBitmap);
        mExtraCanvas.drawColor(mBackgroundColor);
    }

    private void calculateFrame(int width, int height) {
        int inset = 40;
        mFrame = new Rect(inset, inset, width - inset, height - inset);
    }


    /***Drawing Methods:***/


    /**
     * Called when the custom view is being drawn. This method draws the bitmap,
     * rectangle frame, and any additional circle outline on the canvas.
     *
     * @param canvas The canvas on which to draw the view.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(mExtraBitmap, 0, 0, null);
        canvas.drawRect(mFrame, mFramePaint);
        drawCircleOutlineOnCanvas(mExtraCanvas);
    }


    /**
     * Draw an outline of a circle around the specified ImageView on the canvas.
     *
     * @param canvas Canvas to draw on
     */
    public void drawCircleOutlineOnCanvas(Canvas canvas) {
        int viewWidth = circleView.getWidth();
        int viewHeight = circleView.getHeight();
        float centerX = circleView.getX() + viewWidth / 2f;
        float centerY = circleView.getY() + viewHeight / 2f;
        float radius = viewWidth / 2f;

        int colorResId = R.color.black;
        int colorValue = ContextCompat.getColor(getContext(), colorResId);

        Paint strokePaint = new Paint();
        strokePaint.setColor(colorValue);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(5);

        canvas.drawCircle(centerX, centerY, radius, strokePaint); // Draw the outlined circle

        //IF YOU WANT TO TEST DRAW SOMETHING SO IN HERE

        invalidate();

    }

    /***Touch Event Handling:***/


    /**
     * Handles touch events for the custom view. Depending on the action type,
     * this method triggers drawing or filling operations.
     *
     * @param event The motion event that triggered this method.
     * @return True if the event was handled, otherwise false.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if(!fillMode)
                {
                    touchStartDrawing(x, y);
                }
                else {touchStartFilling(x,y);}
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                break;
            case MotionEvent.ACTION_UP:
                touchUp(x,y);
                break;
            default:
                break;
        }
        return true;
    }



    /**
     * Initiates the drawing operation at the given touch coordinates.
     *
     * @param x The x-coordinate of the touch.
     * @param y The y-coordinate of the touch.
     */
    private void touchStartDrawing(float x, float y) {

        Paint newPaint = createPaint(mPaint.getColor(), mPaint.getStrokeWidth(), mPaint.getStyle());
        //newPaint.setStyle(Paint.Style.FILL);

        if (shapeToDraw == null || shapeToDraw instanceof Freedom) {
            shapeToDraw = new Freedom(x, y, newPaint);
        }
    }

    /**
     * Handles the movement of touch events for drawing operations.
     *
     * @param x The x-coordinate of the touch.
     * @param y The y-coordinate of the touch.
     */
    private void touchMove(float x, float y) {
        if (fillMode) return;


        // Only draw if within the circle bounds
        if (isWithinCircleBounds(x, y)) {

            if (shapeToDraw instanceof Freedom) {
                float dx = Math.abs(x - prevX);
                float dy = Math.abs(y - prevY);

                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    shapeToDraw.setStartX(x);
                    shapeToDraw.setStartY(y);
                    prevX = x;
                    prevY = y;
                    shapeToDraw.draw(mExtraCanvas);
                }
            }

            invalidate(); // Request redraw
        }
    }

    /**
     * Handles the end of touch events, particularly for drawing operations.
     *
     * @param x The x-coordinate of the touch.
     * @param y The y-coordinate of the touch.
     */
    private void touchUp(float x, float y) {
        if (!fillMode && isWithinCircleBounds(x, y)) {
            shapes.add(shapeToDraw);
        }
    }


    /***Filling and Reset Operations:***/

    /**
     * Initiates the filling operation at the given touch coordinates if they are
     * within the circle bounds.
     *
     * @param x The x-coordinate of the touch.
     * @param y The y-coordinate of the touch.
     */
    private void touchStartFilling(float x, float y) {
        if (isWithinCircleBounds(x, y)) {
            int targetColor = mExtraBitmap.getPixel((int) x, (int) y);

            // Perform flood fill operation within the circle bounds
            QueueLinearFloodFiller queueLinearFloodFiller =
                    new QueueLinearFloodFiller(mExtraBitmap, targetColor, mPaint.getColor());

            queueLinearFloodFiller.setStartX((int) x);
            queueLinearFloodFiller.setStartY((int) y);
            queueLinearFloodFiller.draw(mExtraCanvas);

            // Add the flood fill operation (shape) to the shapes list
            shapes.add(queueLinearFloodFiller);

            // Reset and redraw all shapes on the canvas
            reset(false);
            if (!shapes.isEmpty()) {
                for (Shape shape : shapes) {
                    shape.draw(mExtraCanvas);
                }
            }

            // Request redraw of the canvas
            invalidate();
        }
    }


    /**
     * Resets the canvas, optionally clearing all shapes.
     *
     * @param realReset True to clear all shapes; false to keep shapes.
     */
    public void reset(boolean realReset) {
        if(realReset)
        {
            shapes.clear();
        }
        mExtraCanvas.drawColor(mBackgroundColor);
        invalidate();
    }




    /**
     * Undoes the last shape added to the canvas.
     */
    public void undo()
    {
        if(!shapes.isEmpty())
        {
            shapes.remove(shapes.size() - 1);
            reset(false);
            if(!shapes.isEmpty())
                for(Shape shape : shapes)
                    shape.draw(mExtraCanvas);
        }
    }



    /**
     * Toggles the fill mode for the canvas. When fill mode is on, the canvas fills
     * enclosed areas on touch; otherwise, it draws freeform shapes.
     */
    public void toggleFillMode() {
        fillMode = !fillMode;
        Toast.makeText(getContext(),"Fill Mode Set On " + fillMode, Toast.LENGTH_SHORT).show();
    }


    /***Utility Methods:***/


    /**
     * Checks if a given point (x, y) is within the bounds of the circle.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @return True if the point is within the circle bounds, otherwise false.
     */
    private boolean isWithinCircleBounds(float x, float y) {
        // Get the center coordinates and radius of the circle
        int circleCenterX = circleView.getLeft() + circleView.getWidth() / 2;
        int circleCenterY = circleView.getTop() + circleView.getHeight() / 2;
        int circleRadius = circleView.getWidth() / 2;

        // Calculate the distance between the touch point and the circle center
        float distanceToCenter = (float) Math.sqrt(Math.pow(x - circleCenterX, 2) + Math.pow(y - circleCenterY, 2));

        // Check if the distance is within the radius of the circle
        return distanceToCenter <= circleRadius;
    }



    /**
     * Crops the drawn circle from the canvas as a bitmap
     *
     * used when user chooses to draw
     *
     * @param bitmap The original bitmap to be cropped into a circular shape.
     * @return A new Bitmap containing the cropped circular region.
     */
    public Bitmap cropCircle(Bitmap bitmap) {
        // Calculate the rectangle that contains the circle
        int circleViewX = (int) circleView.getX();
        int circleViewY = (int) circleView.getY();
        int width = circleView.getWidth();
        int height = circleView.getHeight();
        float centerX = circleViewX + width / 2f;
        float centerY = circleViewY + height / 2f;
        float radius = width / 2f;

        int left = (int) (centerX - radius);
        int top = (int) (centerY - radius);
        int right = (int) (centerX + radius);
        int bottom = (int) (centerY + radius);

        // Create a new bitmap with the same configuration as the original bitmap
        Bitmap outputBitmap = Bitmap.createBitmap((int) (radius * 2), (int) (radius * 2), Bitmap.Config.ARGB_8888);

        // Create a canvas for the new bitmap
        Canvas canvas = new Canvas(outputBitmap);

        // Prepare a paint object to use for drawing
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        // Create a path representing the circle
        Path path = new Path();
        path.addCircle(radius, radius, radius, Path.Direction.CW);

        // Set the circle as a clipping region on the canvas
        canvas.clipPath(path);

        // Clear the background to make it transparent outside the circle
        canvas.drawColor(0, PorterDuff.Mode.CLEAR);

        // Draw the original bitmap onto the canvas at the specified rectangle
        Rect srcRect = new Rect(left, top, right, bottom);
        Rect destRect = new Rect(0, 0, (int) (radius * 2), (int) (radius * 2));
        canvas.drawBitmap(bitmap, srcRect, destRect, paint);

        return Bitmap.createScaledBitmap(outputBitmap, (int) sizesOfCustomBalls.get(numOfBall).width, (int) sizesOfCustomBalls.get(numOfBall++).height, false);
    }


    /**
     * Retrieves the final bitmap which is played in the game
     *
     * @return A Bitmap representing a circular shape, typically used for graphical purposes.
     */
    public Bitmap getBitmapCanvas() {
        if(CameraOptions.photoTaken){
            Bitmap bitmap = getBitmapFromImageView(circleView);
            circleView.setImageBitmap(null);
            CameraOptions.photoTaken = false;
            return bitmap;
        }
        else {
            return cropCircle(mExtraBitmap);
        }

    }


    /**
     * Get the bitmap on imageView.
     *
     * used when choosing a photo or capturing, scaling it appropriately to default game balls
     *
     * @param imageView The imageView to fetch.
     * @return The resulting Bitmap.
     */
    public Bitmap getBitmapFromImageView(ImageView imageView) {
        Drawable drawable = imageView.getDrawable();
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        return Bitmap.createScaledBitmap(bitmap, (int) sizesOfCustomBalls.get(numOfBall).width, (int) sizesOfCustomBalls.get(numOfBall++).height, false);
    }



}
